data "aws_caller_identity" "current" {}

data "aws_ec2_managed_prefix_list" "cloudflare_vpn" {
  filter {
    name   = "prefix-list-name"
    values = [local.cloudflare_vpn]
  }
}

data "aws_iam_role" "github_runner_role" {
  name     = "cko-fort-runner-role-prod"
  provider = aws.mgmt
}

data "aws_ecr_repository" "dci_settlement" {
  provider = aws.mgmt
  name     = "cko-fort/dci-settlement"
  depends_on = [module.ecr]
}

data "aws_secretsmanager_secret" "datadog_app_key" {
  provider = aws.mgmt
  name     = "techfinance/datadog-app"
}

data "aws_secretsmanager_secret_version" "datadog_app_key_value" {
  provider  = aws.mgmt
  secret_id = data.aws_secretsmanager_secret.datadog_app_key.id
}

data "aws_secretsmanager_secret" "datadog_api_key" {
  provider = aws.mgmt
  name     = "datadog_api"
}

data "aws_secretsmanager_secret_version" "datadog_api_key_value" {
  provider  = aws.mgmt
  secret_id = data.aws_secretsmanager_secret.datadog_api_key.id
}

data "aws_s3_bucket" "scheme_settlements_bucket" {
  bucket = "scheme-settlements-${terraform.workspace}"
}

data "aws_lambda_function" "datadog_forwarder" {
  function_name = "scheme-settlements-datadog-forwarder"
}

data "aws_kms_key" "scheme_settlements_kms_key" {
  key_id =  "alias/scheme-settlements"
}

data "aws_vpc" "vpc" {
  filter {
    name   = "tag:Name"
    values = ["qa-sdp-vpc"]
  }
}

data "aws_subnets" "subnets" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.vpc.id]
  }
}