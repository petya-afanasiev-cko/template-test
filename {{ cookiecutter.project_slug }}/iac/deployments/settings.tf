terraform {
  required_version = "~> 1.3"

  backend "s3" {
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.7.0"
    }
  }
}

provider "aws" {
  region = var.region
  assume_role {
    role_arn     = var.assume_role_arn
    session_name = local.application_name
  }
  default_tags {
    tags = var.tags
  }
}

provider "aws" {
  alias  = "mgmt"
  region = var.region

  assume_role {
    role_arn     = var.spacelift_mgmt_role_arn
    session_name = "scheme-settlements-vpc-mgmt"
  }
}
