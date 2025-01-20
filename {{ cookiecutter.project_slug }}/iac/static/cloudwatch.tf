resource "aws_cloudwatch_log_group" "{{ cookiecutter.project_snake }}" {
  name              = "/aws/batch/${local.application_name}"
  retention_in_days = 365
}

resource "aws_lambda_permission" "datadog_forwarder_permission" {
  statement_id  = "${local.application_name}-datadog-forwarder"
  action        = "lambda:InvokeFunction"
  function_name = data.aws_lambda_function.datadog_forwarder.function_name
  principal     = "logs.eu-west-1.amazonaws.com"
  source_arn    = "${aws_cloudwatch_log_group.{{ cookiecutter.project_snake }}.arn}:*"
}

resource "aws_cloudwatch_log_subscription_filter" "datadog_forwarder_filter" {
  name            = "${local.application_name}-datadog-forwarder"
  log_group_name  = aws_cloudwatch_log_group.{{ cookiecutter.project_snake }}.name
  destination_arn = data.aws_lambda_function.datadog_forwarder.arn
  filter_pattern  = ""
}