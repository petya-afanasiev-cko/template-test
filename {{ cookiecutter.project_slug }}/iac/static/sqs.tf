locals {
  visibility_timeout_seconds = 30
  delay_seconds              = 0
  max_message_size           = 8192
  message_retention_seconds  = 86400
  receive_wait_time_seconds  = 20
}

resource "aws_sqs_queue" "{{ cookiecutter.project_snake }}_dl" {
  name                       = "{{ cookiecutter.project_slug }}-dl"
  kms_master_key_id          = data.aws_kms_key.scheme_settlements_kms_key.arn
  visibility_timeout_seconds = local.visibility_timeout_seconds
  delay_seconds              = local.delay_seconds
  max_message_size           = local.max_message_size
  message_retention_seconds  = local.message_retention_seconds
  receive_wait_time_seconds  = local.receive_wait_time_seconds
}