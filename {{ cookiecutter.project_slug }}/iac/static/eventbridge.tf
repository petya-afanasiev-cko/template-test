resource "aws_cloudwatch_event_rule" "s3_to_batch" {
  name        = "${local.application_name}-eventbridge-batch-rule"
  description = "Triggers AWS Batch jobs from S3 events"
  event_pattern = jsonencode({
    "source": ["aws.s3"],
    "detail-type": ["Object Created"],
    "detail": {
      "bucket": {
        "name": [data.aws_s3_bucket.scheme_settlements_bucket.id]
      },
      "object": {
        "key": [{
          "prefix": "{{ cookiecutter.scheme_slug }}/incoming/"
        }]
      }
    }
  })
}

resource "aws_cloudwatch_log_group" "{{ cookiecutter.project_snake }}_test" { 
  //TODO remove this cloudwatch group and the target below after confirming batch works as expected with our code and ECR image.
  //This cloudwatch group is there to test if the eventbridge target/rule work and you will see s3 upload events in the cloudwatch logs in this group.
  name = "/aws/events/${local.application_name}"
}

resource "aws_cloudwatch_event_target" "log_eventbridge" {
  rule      = aws_cloudwatch_event_rule.s3_to_batch.name
  target_id = "log_eventbridge"
  arn       = aws_cloudwatch_log_group.{{ cookiecutter.project_snake }}_test.arn
}