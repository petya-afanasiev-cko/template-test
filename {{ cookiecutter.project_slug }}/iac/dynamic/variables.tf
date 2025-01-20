variable "git_tag" {
  type        = string
  description = "image version"
}

variable "tags" {
  type = map(string)
  description = "Tags to assign to resources"
  default = {
    "team" : "scheme-settlements",
    "product" : "scheme-settlements",
    "pillar" : "financial-infrastructure",
    "pci" : "out-of-scope"
  }
}

variable "aws_region" {
  type        = string
  description = "AWS region"
  default     = "eu-west-1"
}

variable "assume_role_arn" {
  type        = map(string)
  description = "The role to assume when deploying this infrastructure"
  default     = {
    "qa"      = "arn:aws:iam::711533748762:role/spacelift",
    "prod"    = "arn:aws:iam::851392519502:role/spacelift"
  }
}

variable "spacelift_mgmt_role_arn" {
  type        = string
  description = "The ARN of the Spacelift role in the mgmt account"
  default     = "arn:aws:iam::791259062566:role/spacelift"
}