# How to Use

## Run the Backend
1. Ensure Java 24 installed (recommended). The app compiles to Java 21 bytecode for Spring compatibility.
2. Optionally set your AI key: `export OPENAI_API_KEY=sk-...`
3. Persistence (DynamoDB only)

Configure AWS DynamoDB via environment variables (examples):
```
export DYNAMODB_ENABLED=true
export DYNAMODB_TABLE_NAME=learningtool-generation-history
export DYNAMODB_REGION=us-east-1
# AWS credentials via IAM role on EC2/ECS/Lambda, or local ~/.aws/credentials, or env vars
```
Notes:
- MongoDB/DocumentDB support has been removed. You only need DynamoDB.
- If credentials or the table are not present, requests still succeed; save() failures are swallowed.
- For production, ensure the table exists (Terraform module can create it) and assign IAM permissions.

4. Start the server:
```
./gradlew bootRun
```
5. Health check: http://localhost:8080/actuator/health

## Infrastructure (AWS Free Tier with Terraform)
You can provision a small Free Tier–friendly stack on AWS using Terraform:

1. Install Terraform and configure AWS credentials (env vars or AWS profile).
2. From the repo root:
```
cd infra/terraform
terraform init
terraform apply
```
3. After apply, note the outputs (EC2 public DNS/IP and optional S3 website endpoint).
4. Copy your Spring Boot jar to the EC2 instance and run it (see infra/terraform/README.md). Optionally upload the frontend build to the S3 bucket.

## Run the Frontend
1. Ensure Node 18+ installed.
2. Development (with proxy):
```
cd frontend
npm install
npm run dev
# open http://localhost:5173 (Vite proxies /api to http://localhost:8080)
```
3. Production build:
```
cd frontend
npm run build
# Copy frontend/.env.example to frontend/.env and set VITE_API_BASE to your backend base URL
# Serve the dist/ directory with any static web server
```

## Generate Content
- Choose a type (Short Notes, Cheat Sheet, Image, Animation, LeetCode, Voice Explanation, Interview Q&A)
- Enter a topic (e.g., "Java Streams")
- Click Generate

If no OPENAI_API_KEY is configured, you will see mock data so you can explore the UI/flow without external calls.

## Java Version
- The project targets Java 24. Install a Java 24 JDK or configure Gradle toolchains to provision it.
