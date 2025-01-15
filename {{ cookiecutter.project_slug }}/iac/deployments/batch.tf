resource "aws_batch_compute_environment" "compute_env" {
  compute_environment_name = "${local.application_name}-batch-compute-environment"
  type                     = "MANAGED"

  compute_resources {
    type               = "FARGATE"
    min_vcpus          = 0
    max_vcpus          = 16
    desired_vcpus      = 0
    subnets            = data.aws_subnets.subnets.ids
    security_group_ids = [aws_security_group.batch_sg.id]
  }

  service_role = module.batch_service_role.role_arn
}

resource "aws_batch_job_queue" "job_queue" {
  name                 = "${local.application_name}-batch-job-queue"
  state                = "ENABLED"
  priority             = 1
  compute_environment_order {
    compute_environment = aws_batch_compute_environment.compute_env.arn
    order               = 0
  }
}

resource "aws_batch_job_definition" "job_definition" {
  name        = "${local.application_name}-batch-job-definition"
  type        = "container"

  platform_capabilities = ["FARGATE"]
  
  container_properties = jsonencode({
    image        = "public.ecr.aws/amazonlinux/amazonlinux:2" //TODO this image is for testing. For our real image we need to get the arn from ECR (if we upload container there)
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
    command          = ["echo", "Hello from Batch!"]
    jobRoleArn       = module.batch_job_role.role_arn
    executionRoleArn = module.batch_execution_role.role_arn
    logConfiguration = {
      logDriver = "awslogs",
      options = {
        awslogs-group         = aws_cloudwatch_log_group.{{ cookiecutter.scheme_slug }}_settlement.name
        awslogs-region        = "eu-west-1"
        awslogs-stream-prefix = "batch"
      }
    }
    "environment": [
      {
        "name": "AWS_DEBUG",
        "value": "true"
      }
    ]
  })
}

resource "aws_security_group" "batch_sg" {
  name        = "${local.application_name}-batch-security-group"
  description = "Security group for AWS Batch tasks"
  vpc_id      = data.aws_vpc.vpc.id

  //TODO we need to restrict these inress and egress rules more before going to production but leaving them open now to make sure everything works for the hackaton.
  ingress {
    description = "Allow inbound traffic from EventBridge/S3"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow outbound HTTPS traffic for S3, ECR, and other AWS services"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow outbound DNS traffic"
    from_port   = 53
    to_port     = 53
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
