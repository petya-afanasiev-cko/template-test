terraform {
  required_version = ">= 1.3"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.7.0"
    }
  }
  backend "s3" {}
}

provider "aws" {
  region = var.aws_region

  assume_role {
    role_arn     = var.assume_role_arn[terraform.workspace]
    session_name = local.application_name
  }

  default_tags {
    tags = merge(var.tags, {
      env = terraform.workspace
    })
  }
}

provider "aws" {
  alias  = "mgmt"
  region = var.aws_region

  assume_role {
    role_arn     = var.spacelift_mgmt_role_arn
    session_name = local.application_name
  }

  default_tags {
    tags = merge(var.tags, {
      env = terraform.workspace
    })
  }
}

