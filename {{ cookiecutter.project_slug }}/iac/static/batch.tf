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

  service_role = data.aws_iam_role.{{ cookiecutter.scheme_slug }}_batch_service_role.arn
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
