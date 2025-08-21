# Terraform for Learning Tool (AWS Free Tier)

This Terraform config provisions a minimal, Free Tier–friendly AWS stack:
- VPC with a public subnet, Internet Gateway, and routing
- Security Group allowing port 8080 (app) and optionally 22 (SSH)
- IAM role/instance profile for AWS Systems Manager Session Manager access
- 1x EC2 instance (default t2.micro) with user_data that installs Amazon Corretto 21
- Optional S3 bucket configured for static website hosting (for the React build)

Notes:
- Only Free Tier–eligible components are used by default. Actual eligibility depends on your account/region and usage.
- DocumentDB is not created since it does not have an always-on free tier. If needed, you can later integrate other AWS data stores with free tiers (e.g., DynamoDB) separately.

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

Outputs:
- backend_instance_public_ip / public_dns
- backend_security_group_id
- s3_website_endpoint (if bucket created)

## Deploying the app
- Backend: Build your Spring Boot jar, copy it to the instance (e.g., scp to /opt/learning-tool/app.jar), and run: `java -jar /opt/learning-tool/app.jar`.
- Frontend: Build with `npm run build`, then upload `dist/*` files to the created S3 bucket (from outputs) if you enabled it.

## Cost and Free Tier
This setup is designed for Free Tier usage:
- EC2: t2.micro/t3.micro is Free Tier eligible for 12 months for new accounts.
- S3: 5GB standard storage and certain request/transfer quotas are in Free Tier.
- VPC/IGW/Route tables: no direct hourly charges.
- IAM/SSM: IAM is free; SSM Session Manager typically free for basic usage.
Always monitor your costs in the AWS Billing console.

## GitHub Actions Terraform Workflows
Two workflows are provided:
- .github/workflows/terraform-plan.yml: runs on pull requests touching infra/terraform; performs fmt, init, validate, and plan; uploads plan as an artifact.
- .github/workflows/terraform-apply.yml: runs on push to master and can be triggered manually; performs init, plan (for visibility), and apply.

### Required GitHub configuration
1) Set organization/repo Variables and Secrets:
- Repository Variable AWS_REGION (optional, defaults to us-east-1).
- Repository Secret AWS_ACCESS_KEY_ID with an IAM user's access key ID.
- Repository Secret AWS_SECRET_ACCESS_KEY with the corresponding secret access key.
- Optional Repository Secret AWS_SESSION_TOKEN if you use temporary session credentials.

2) Protect the "production" environment (recommended):
- Add required reviewers or manual approvals so applies are gated.

### Required AWS IAM setup (Access Keys)
Create or choose IAM credentials that have permissions to manage your resources and the Terraform backend:
- Grant access to your S3 backend bucket and DynamoDB lock table (see backend.tf).
- Grant Create/Update/Delete for the AWS resources used in this module (EC2, VPC, IGW, Route Tables, Subnets, Security Groups, IAM roles/instance profiles, S3, etc.). Use least-privilege, ideally scoped by resource ARNs and/or project tags.

Security note: Access keys are long‑lived. Rotate them regularly and restrict their permissions. Consider migrating to OIDC and short‑lived credentials when possible.

### Running locally vs CI
- Local: ensure your credentials can access the same backend bucket/table.
- CI: the workflows read AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY (and optional AWS_SESSION_TOKEN) from repository secrets/variables and export them for Terraform/AWS CLI.
