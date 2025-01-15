locals {
  visibility_timeout_seconds = 30
  delay_seconds              = 0
  max_message_size           = 8192
  message_retention_seconds  = 86400
  receive_wait_time_seconds  = 20
}

resource "aws_sqs_queue" "{{ cookiecutter.scheme_slug }}_settlement" {
  name                       = "{{ cookiecutter.scheme_slug }}-settlement"
  kms_master_key_id          = data.aws_kms_key.scheme_settlements_kms_key.arn
  visibility_timeout_seconds = local.visibility_timeout_seconds
  delay_seconds              = local.delay_seconds
  max_message_size           = local.max_message_size
  message_retention_seconds  = local.message_retention_seconds
  receive_wait_time_seconds  = local.receive_wait_time_seconds

  redrive_policy = <<EOF
    {"deadLetterTargetArn":"${aws_sqs_queue.{{ cookiecutter.scheme_slug }}_settlement_dl.arn}","maxReceiveCount":5}
  EOF
}

resource "aws_sqs_queue" "{{ cookiecutter.scheme_slug }}_settlement_dl" {
  name                       = "{{ cookiecutter.scheme_slug }}-settlement-dl"
  kms_master_key_id          = data.aws_kms_key.scheme_settlements_kms_key.arn
  visibility_timeout_seconds = local.visibility_timeout_seconds
  delay_seconds              = local.delay_seconds
  max_message_size           = local.max_message_size
  message_retention_seconds  = local.message_retention_seconds
  receive_wait_time_seconds  = local.receive_wait_time_seconds
}

data "aws_iam_policy_document" "s3_upload_notification_to_sqs_policy" {
  statement {
    sid    = "AllowS3ToSendNotificationsToSqs"
    effect = "Allow"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions   = ["sqs:SendMessage"]
    resources = [aws_sqs_queue.{{ cookiecutter.scheme_slug }}_settlement.arn]

    condition {
      test     = "ArnEquals"
      variable = "aws:SourceArn"
      values   = ["arn:aws:s3:::scheme-settlements-qa"]
    }
  }
}

resource "aws_sqs_queue_policy" "{{ cookiecutter.scheme_slug }}_settlement_sqs_policy" {
  queue_url = aws_sqs_queue.{{ cookiecutter.scheme_slug }}_settlement.id
  policy    = data.aws_iam_policy_document.s3_upload_notification_to_sqs_policy.json
}