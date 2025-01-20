vpc_cidr_block       = "10.78.156.0/23"
private_subnet_cidrs = ["10.78.156.0/26", "10.78.156.64/26", "10.78.156.128/26"]
public_subnet_cidrs  = ["10.78.156.192/26", "10.78.157.0/26", "10.78.157.64/26"]
db_subnet_cidrs      = ["10.78.157.128/27", "10.78.157.160/27", "10.78.157.192/26"]

aws_environment = "qa"

tags = {
  "repo" : "cko-fort/{{ cookiecutter.project_slug }}",
  "team" : "scheme-settlements",
  "product" : "scheme-settlements",
  "pillar" : "financial-infrastructure",
  "pci" : "out-of-scope",
  "env" : "qa"
}