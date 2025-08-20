terraform {
  backend "s3" {
    bucket         = "tf-state-109598918608-us-east-1"
    key            = "envs/prod/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "tf-state-locks"
    encrypt        = true
  }
}
