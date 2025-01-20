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
      aws_cloudwatch_log_group.{{ cookiecutter.project_snake }}.arn,
      "${aws_cloudwatch_log_group.{{ cookiecutter.project_snake }}.arn}:*"
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
    resources = [data.aws_ecr_repository.{{ cookiecutter.project_snake }}.arn]
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
      "${data.aws_s3_bucket.scheme_settlements_bucket.arn}/{{ cookiecutter.scheme_slug }}/*"
    ]
  }
}