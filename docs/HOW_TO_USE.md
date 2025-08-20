# How to Use

## Run the Backend
1. Ensure Java 24 installed.
2. Optionally set your AI key: `export OPENAI_API_KEY=sk-...`
3. (Optional) Use a database for persistence; otherwise in-memory storage is used.

MongoDB (local):
```
export MONGODB_URI="mongodb://localhost:27017/learningtool"
```

AWS DocumentDB:
```
export AWS_DOCDB_URI="mongodb://username:password@your-docdb-cluster.cluster-xxxxxx.us-east-1.docdb.amazonaws.com:27017/learningtool?tls=true&replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=false"
```
Notes:
- Ensure network access (VPC/peering/PrivateLink/SSH tunnel) to the cluster endpoint.
- Supply a user with appropriate permissions.
- You can add parameters (e.g., tlsCAFile) to the URI if required by your environment.

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
2. Start dev server:
```
cd frontend
npm install
npm run dev
```
3. Open http://localhost:5173

## Generate Content
- Choose a type (Short Notes, Cheat Sheet, Image, Animation, LeetCode, Voice Explanation, Interview Q&A)
- Enter a topic (e.g., "Java Streams")
- Click Generate

If no OPENAI_API_KEY is configured, you will see mock data so you can explore the UI/flow without external calls.

## Java Version
- The project targets Java 24. Install a Java 24 JDK or configure Gradle toolchains to provision it.
