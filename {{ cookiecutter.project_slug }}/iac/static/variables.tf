variable "region" {
  type        = string
  description = "AWS region"
}

variable "assume_role_arn" {
  type        = map(string)
  description = "The role to assume when deploying this infrastructure"
  default     = {
    "qa"      = "arn:aws:iam::711533748762:role/spacelift",
    "prod"    = "arn:aws:iam::851392519502:role/spacelift"
  }
}

variable "tags" {
  type = map(string)
  description = "Tags to assign to resources"
}

variable "spacelift_mgmt_role_arn" {
  type        = string
  description = "The ARN of the Spacelift role in the mgmt account"
  default     = "arn:aws:iam::791259062566:role/spacelift"
}
