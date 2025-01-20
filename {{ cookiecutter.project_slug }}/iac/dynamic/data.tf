data "aws_cloudwatch_log_group" "{{ cookiecutter.scheme_slug }}_cloudwatch_group" {
  name = "/aws/batch/${local.application_name}"
}

data "aws_iam_role" "{{ cookiecutter.scheme_slug }}_batch_job_role" {
  name = "${local.application_name}-batch-job-role-${terraform.workspace}"
}

data "aws_iam_role" "{{ cookiecutter.scheme_slug }}_batch_execution_role" {
  name = "${local.application_name}-batch-execution-role-${terraform.workspace}"
}

data "aws_batch_job_queue" "{{ cookiecutter.scheme_slug }}_batch_job_queue" {
  name = "${local.application_name}-batch-job-queue"
}

data "aws_sqs_queue" "{{ cookiecutter.scheme_slug }}_dlq" {
  name = "{{ cookiecutter.project_slug }}-dl"
}

data "aws_s3_bucket" "scheme_settlements_bucket" {
  bucket = "scheme-settlements-${terraform.workspace}"
}