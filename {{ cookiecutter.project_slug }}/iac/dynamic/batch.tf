resource "aws_batch_job_definition" "job_definition" {
  name        = "${local.application_name}-batch-job-definition"
  type        = "container"

  platform_capabilities = ["FARGATE"]

  container_properties = jsonencode({
    image        = "791259062566.dkr.ecr.eu-west-1.amazonaws.com/cko-fort/{{ cookiecutter.project_slug }}:${var.git_tag}"
    
    resourceRequirements = [
      {
        type  = "VCPU"
        value = "1"
      },
      {
        type  = "MEMORY"
        value = "2048"
      }
    ]
    
    logConfiguration = {
      logDriver = "awslogs",
      options = {
        awslogs-group         = data.aws_cloudwatch_log_group.{{ cookiecutter.scheme_slug }}_cloudwatch_group.name
        awslogs-region        = var.aws_region
        awslogs-stream-prefix = "batch"
      }
    }
    
    "environment": [
      {
        "name": "application",
        "value": local.application_name
      },
      {
        "name": "team",
        "value": local.team_name
      }
    ]
    
    jobRoleArn       = data.aws_iam_role.{{ cookiecutter.scheme_slug }}_batch_job_role.arn
    executionRoleArn = data.aws_iam_role.{{ cookiecutter.scheme_slug }}_batch_execution_role.arn
  })
}

resource "aws_cloudwatch_event_target" "eventbridge_to_batch" {
  rule      = "${local.application_name}-eventbridge-batch-rule"
  target_id = "${local.application_name}-batch-job"
  arn       = data.aws_batch_job_queue.{{ cookiecutter.scheme_slug }}_batch_job_queue.arn

  role_arn  = module.eventbridge_role.role_arn

  batch_target {
    job_definition = aws_batch_job_definition.job_definition.arn
    job_name       = "${local.application_name}-batch-job"
    job_attempts   = 3
  }   

  dead_letter_config {
    arn = data.aws_sqs_queue.{{ cookiecutter.scheme_slug }}_dlq.arn
  }
}