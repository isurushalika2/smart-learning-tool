# Terraform for Learning Tool (AWS Free Tier)

This Terraform config provisions a minimal, Free Tier–friendly AWS stack:
- VPC with a public subnet, Internet Gateway, and routing
- Security Group allowing port 8080 (app) and optionally 22 (SSH)
- IAM role/instance profile for AWS Systems Manager Session Manager access
- 1x EC2 instance (default t2.micro) with user_data that installs Amazon Corretto 24
- Optional S3 bucket configured for static website hosting (for the React build)
- Optional DynamoDB table (Free Tier) for storing app data (disabled/enabled via variable)

Notes:
- Only Free Tier–eligible components are used by default. Actual eligibility depends on your account/region and usage.
- Persistence: This project uses DynamoDB only. MongoDB/AWS DocumentDB are not supported by the application.

## Prerequisites
- Terraform >= 1.3
- AWS credentials configured (AWS_PROFILE or environment variables)

## Usage
```
cd infra/terraform
terraform init
terraform apply -auto-approve
```

Variables (see variables.tf):
- aws_region (default: us-east-1)
- instance_type (default: t3.micro)
- allow_ssh_cidr (default: 0.0.0.0/0) — restrict to your IP for production
- key_pair_name (default: "") — set to an existing EC2 key pair name to enable SSH key login
- create_s3_website (default: true)
- subnet_az (default: us-east-1a) — AZ for the public subnet; ensure your chosen instance_type is supported in this AZ.
- create_dynamodb (default: true) — create a Free Tier DynamoDB table to persist data
- dynamodb_table_name (default: learningtool-generation-history) — table name

Outputs:
- backend_instance_public_ip / public_dns
- backend_security_group_id
- s3_website_endpoint (if bucket created)
- dynamodb_table_name (if created)
- dynamodb_table_arn (if created)

## Deploying the app
- Backend: Build your Spring Boot jar, copy it to the instance (e.g., scp to /opt/learning-tool/app.jar), and run: `java -jar /opt/learning-tool/app.jar`.
- Frontend: Build with `npm run build`, then upload `dist/*` files to the created S3 bucket (from outputs) if you enabled it.

### DynamoDB (default persistence)
- This Terraform can create a DynamoDB table (create_dynamodb=true). The backend uses DynamoDB as its only persistence.
- Ensure the application has IAM permissions to PutItem and Scan on the table.

## Cost and Free Tier
This setup is designed for Free Tier usage:
- EC2: t2.micro/t3.micro is Free Tier eligible for 12 months for new accounts.
- S3: 5GB standard storage and certain request/transfer quotas are in Free Tier.
- VPC/IGW/Route tables: no direct hourly charges.
- IAM/SSM: IAM is free; SSM Session Manager typically free for basic usage.
Always monitor your costs in the AWS Billing console.

## Optional: GitHub Actions for Terraform (not included in this repo)
This repository does not ship with CI workflows under .github/workflows. If you want to automate Terraform in GitHub Actions, you can add workflows that:
- Run fmt, init, validate, and plan on pull requests touching infra/terraform
- Run apply on pushes to your main branch or on manual dispatch

CI setup checklist (if you add workflows):
- Configure repository secrets: AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY (and optional AWS_SESSION_TOKEN)
- Optionally add a repository variable AWS_REGION (defaults to us-east-1)
- Use an IAM user or OIDC with least-privilege permissions for the resources in this module
- Protect environments and require approvals for apply jobs as needed

Running locally vs CI:
- Local: ensure your credentials can access the same backend bucket/table.
- CI: export the above secrets/variables for Terraform and AWS CLI in the workflow steps.
