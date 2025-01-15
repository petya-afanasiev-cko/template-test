module "eventbridge_role" {
  source      = "github.com/cko-core-terraform/terraform-aws-iam-role.git?ref=2.1.0"
  role_name   = "${local.application_name}-eventbridge-role-${terraform.workspace}"
  path        = "/${local.application_name}/"
  description = "IAM Role for the ${local.application_name} Eventbridge"

  assume_role_services = ["events.amazonaws.com"]

  policies = {
    "eventbridge_invoke_batch" = data.aws_iam_policy_document.eventbridge_invoke_batch.json,
    "s3-policy"                = data.aws_iam_policy_document.s3.json,
    "cloud-watch"              = data.aws_iam_policy_document.cloud_watch.json
  }
}

data "aws_iam_policy_document" "eventbridge_invoke_batch" {
  statement {
    actions = [
      "batch:SubmitJob",
      "batch:DescribeJob"
    ]
    resources = [
      aws_batch_job_queue.job_queue.arn,
      aws_batch_job_definition.job_definition.arn
    ]
  }
}

module "batch_job_role" {
  source      = "github.com/cko-core-terraform/terraform-aws-iam-role.git?ref=2.1.0"
  role_name   = "${local.application_name}-batch-job-role-${terraform.workspace}"
  path        = "/${local.application_name}/"
  description = "IAM Role for ${local.application_name} AWS Batch Job"

  assume_role_services = ["batch.amazonaws.com", "ecs-tasks.amazonaws.com"]

  policies = {
    "cloud-watch-policy" = data.aws_iam_policy_document.cloud_watch.json,
    "s3-policy"          = data.aws_iam_policy_document.s3.json
  }
}

module "batch_execution_role" {
  source      = "github.com/cko-core-terraform/terraform-aws-iam-role.git?ref=2.1.0"
  role_name   = "${local.application_name}-batch-execution-role-${terraform.workspace}"
  path        = "/${local.application_name}/"
  description = "IAM Role for ${local.application_name} AWS Batch Fargate Task Execution"

  assume_role_services = ["ecs-tasks.amazonaws.com"]

  policies = {
    "cloud-watch-policy" = data.aws_iam_policy_document.cloud_watch.json,
    "ecr-policy"         = data.aws_iam_policy_document.ecr.json
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = module.batch_execution_role.role_name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

module "batch_service_role" {
  source      = "github.com/cko-core-terraform/terraform-aws-iam-role.git?ref=2.1.0"
  role_name   = "${local.application_name}-batch-service-role-${terraform.workspace}"
  path        = "/${local.application_name}/"
  description = "IAM Role for ${local.application_name} AWS Batch Service"

  assume_role_services = ["batch.amazonaws.com"]

  policies = {
    "batch-service-role" = data.aws_iam_policy_document.batch_service_role.json
  }
}

resource "aws_iam_role_policy_attachment" "aws_batch_service_role" {
  role       = module.batch_service_role.role_name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSBatchServiceRole"
}

data "aws_iam_policy_document" "batch_service_role" {
  statement {
    actions = [
      "batch:SubmitJob",
      "batch:DescribeJob",
      "batch:CancelJob"
    ]
    resources = [
      aws_batch_job_queue.job_queue.arn,
      aws_batch_job_definition.job_definition.arn
    ]
  }
}

data "aws_iam_policy_document" "cloud_watch" {
  statement {
    actions = [
      "logs:CreateLogGroup",
      "logs:PutMetricFilter",
      "logs:PutRetentionPolicy",
      "logs:CreateLogStream",
      "logs:PutLogEvents",
      "logs:DescribeLogGroups",
      "cloudwatch:PutMetricData"
    ]
    resources = [
      aws_cloudwatch_log_group.dci_settlement.arn,
      "${aws_cloudwatch_log_group.dci_settlement.arn}:*"
    ]
  }
}

data "aws_iam_policy_document" "ecr" {
  statement {
    actions = [
      "ecr:GetDownloadUrlForLayer",
      "ecr:BatchGetImage",
      "ecr:BatchCheckLayerAvailability"
    ]
    resources = [data.aws_ecr_repository.dci_settlement.arn]
  }

  statement {
    actions = [
      "ecr:GetAuthorizationToken"
    ]
    resources = ["*"]
  }
}

data "aws_iam_policy_document" "s3" {
  statement {
    actions = [
      "s3:GetObject",
      "s3:PutObject",
      "s3:DeleteObject",
      "s3:ListBucket"
    ]
    resources = [
      data.aws_s3_bucket.scheme_settlements_bucket.arn,
      "${data.aws_s3_bucket.scheme_settlements_bucket.arn}/dci/*"
    ]
  }
}