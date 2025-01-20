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
      data.aws_batch_job_queue.{{ cookiecutter.scheme_slug }}_batch_job_queue.arn,
      aws_batch_job_definition.job_definition.arn
    ]
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
      data.aws_cloudwatch_log_group.{{ cookiecutter.scheme_slug }}_cloudwatch_group.arn,
      "${data.aws_cloudwatch_log_group.{{ cookiecutter.scheme_slug }}_cloudwatch_group.arn}:*"
    ]
  }
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
      data.aws_batch_job_queue.{{ cookiecutter.scheme_slug }}_batch_job_queue.arn,
      aws_batch_job_definition.job_definition.arn
    ]
  }
}