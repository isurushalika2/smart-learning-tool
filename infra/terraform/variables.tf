variable "project_name" {
  description = "Project name prefix for tagging and resource naming"
  type        = string
  default     = "learning-tool"
}

variable "aws_region" {
  description = "AWS region to deploy to"
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidr" {
  description = "CIDR for the public subnet"
  type        = string
  default     = "10.0.1.0/24"
}

variable "allow_ssh_cidr" {
  description = "CIDR allowed to access SSH (22). Use your IP in production."
  type        = string
  default     = "0.0.0.0/0"
}

variable "instance_type" {
  description = "EC2 instance type (Free Tier eligible: t2.micro/t3.micro depending on account)"
  type        = string
  default     = "t3.micro"
}

variable "key_pair_name" {
  description = "Optional existing EC2 Key Pair name for SSH access (leave empty to skip)"
  type        = string
  default     = ""
}

variable "create_s3_website" {
  description = "Create an S3 bucket configured for static website hosting for the React build"
  type        = bool
  default     = true
}

variable "subnet_az" {
  description = "Availability Zone for the public subnet (must be in the selected aws_region and support the chosen instance_type)"
  type        = string
  default     = "us-east-1a"
}

# Free Tier DB option (DynamoDB)
variable "create_dynamodb" {
  description = "Create a DynamoDB table for storing generation history (Free Tier eligible)"
  type        = bool
  default     = true
}

variable "dynamodb_table_name" {
  description = "Name of the DynamoDB table to create when create_dynamodb is true"
  type        = string
  default     = "learningtool-generation-history"
}
