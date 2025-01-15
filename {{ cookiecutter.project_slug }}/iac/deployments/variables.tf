variable "region" {
  type        = string
  description = "AWS region"
}

variable "assume_role_arn" {
  type        = string
  description = "The role to assume when deploying this infrastructure"
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
