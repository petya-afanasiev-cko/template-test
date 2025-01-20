assume_role_arn = "arn:aws:iam::851392519502:role/spacelift"
aws_environment = "prod"

tags = {
  "repo" : "cko-fort/{{ cookiecutter.project_slug }}",
  "team" : "scheme-settlements",
  "product" : "scheme-settlements",
  "pillar" : "financial-infrastructure",
  "pci" : "out-of-scope",
  "env" : "prod"
}