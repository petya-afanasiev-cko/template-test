module "ecr" {
  count  = terraform.workspace == "qa" ? 1 : 0 #This repo is in cko-mgmt env. So only needs to be deployed once.
  source = "github.com/cko-core-terraform/terraform-aws-ecr.git?ref=1.8.0"
  repo_name   = "cko-fort/dci-settlement"
  custom_tags = var.tags

  allowed_roles = [
    data.aws_iam_role.github_runner_role.arn
  ]
  providers = {
    aws = aws.mgmt
  }
  force_delete = true
}