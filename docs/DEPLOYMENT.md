# Deployment and Environment Variables

Your Spring Boot backend reads configuration from environment variables at runtime (see src/main/resources/application.yml). After deploying to AWS, you must provide the following variables to the running process:

- OPENAI_API_KEY — required only if you want real OpenAI responses. If not set, the app uses the mock provider.
- OPENAI_API_BASE — optional; defaults to https://api.openai.com
- DYNAMODB_ENABLED — set to true to enable DynamoDB-backed persistence (default true in this project).
- DYNAMODB_TABLE_NAME — DynamoDB table name (defaults to learningtool-generation-history)
- DYNAMODB_REGION — AWS region for DynamoDB (if omitted, the repository resolves using AWS_REGION or defaults to us-east-1)

Below are common ways to set these variables depending on your deployment method.

---

## Option A: GitHub Actions (deploy.yml) to EC2 via SSH

1) Store secrets in your GitHub repository
- Settings → Secrets and variables → Actions → New repository secret
- Add: OPENAI_API_KEY, OPENAI_API_BASE (optional), DYNAMODB_ENABLED, DYNAMODB_TABLE_NAME, DYNAMODB_REGION. You can also store an SSH key and EC2 host details as secrets.

2) In your deploy.yml, write the env vars to the EC2 instance
- A typical approach is to create an env file on the instance (e.g., /etc/learning-tool.env) and a systemd service that loads it.

Example (snippets inside your GitHub Actions job):

```
- name: Create env file on EC2
  run: |
    ssh -o StrictHostKeyChecking=no ec2-user@${{ secrets.EC2_HOST }} \
      "echo 'OPENAI_API_KEY=${{ secrets.OPENAI_API_KEY }}' | sudo tee /etc/learning-tool.env >/dev/null"
    ssh -o StrictHostKeyChecking=no ec2-user@${{ secrets.EC2_HOST }} \
      "echo 'OPENAI_API_BASE=${{ secrets.OPENAI_API_BASE }}' | sudo tee -a /etc/learning-tool.env >/dev/null"
    ssh -o StrictHostKeyChecking=no ec2-user@${{ secrets.EC2_HOST }} \
      "echo 'DYNAMODB_ENABLED=${{ secrets.DYNAMODB_ENABLED }}' | sudo tee -a /etc/learning-tool.env >/dev/null"
    ssh -o StrictHostKeyChecking=no ec2-user@${{ secrets.EC2_HOST }} \
      "echo 'DYNAMODB_TABLE_NAME=${{ secrets.DYNAMODB_TABLE_NAME }}' | sudo tee -a /etc/learning-tool.env >/dev/null"
    ssh -o StrictHostKeyChecking=no ec2-user@${{ secrets.EC2_HOST }} \
      "echo 'DYNAMODB_REGION=${{ secrets.DYNAMODB_REGION }}' | sudo tee -a /etc/learning-tool.env >/dev/null"
```

3) Manage the app with systemd and EnvironmentFile
Create a systemd unit that references /etc/learning-tool.env so the variables are loaded on service start.

```
- name: Install/Update systemd service
  run: |
    ssh ec2-user@${{ secrets.EC2_HOST }} "sudo bash -c 'cat > /etc/systemd/system/learning-tool.service <<EOF
[Unit]
Description=Learning Tool Backend
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/opt/learning-tool
ExecStart=/usr/bin/java -jar /opt/learning-tool/app.jar
Restart=always
EnvironmentFile=/etc/learning-tool.env
# (Optional) JVM opts: Environment=JAVA_OPTS=-Xms256m -Xmx512m

[Install]
WantedBy=multi-user.target
EOF'"
    ssh ec2-user@${{ secrets.EC2_HOST }} "sudo systemctl daemon-reload && sudo systemctl enable learning-tool && sudo systemctl restart learning-tool && sudo systemctl status --no-pager learning-tool"
```

Notes:
- Keep OPENAI_API_KEY in GitHub Secrets (not in plaintext vars).
- You can also use SSM Parameter Store to fetch secrets inside the instance; see Option C.

---

## Option B: Directly on EC2 (manual or via user_data)

If you log into the instance or extend user_data to bootstrap the app, you can set variables in one of these places:

- System-wide env for service sessions: write key=value lines to /etc/learning-tool.env and reference it from a systemd unit using EnvironmentFile=/etc/learning-tool.env (recommended).
- Shell profile (affects interactive shells only): append export lines to ~/.bash_profile or /etc/profile.d/yourfile.sh (not recommended for services).
- /etc/environment (applies system-wide; requires sudo; affects login environments).

Example manual steps on EC2:

```bash
# Create env file (as root)
echo "OPENAI_API_KEY=sk-..." | sudo tee /etc/learning-tool.env >/dev/null
sudo tee -a /etc/learning-tool.env >/dev/null <<'EOF'
OPENAI_API_BASE=https://api.openai.com
DYNAMODB_ENABLED=true
DYNAMODB_TABLE_NAME=learningtool-generation-history
DYNAMODB_REGION=us-east-1
EOF

# Create/refresh the systemd unit to load this file
sudo bash -c 'cat > /etc/systemd/system/learning-tool.service <<EOF
[Unit]
Description=Learning Tool Backend
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/opt/learning-tool
ExecStart=/usr/bin/java -jar /opt/learning-tool/app.jar
Restart=always
EnvironmentFile=/etc/learning-tool.env

[Install]
WantedBy=multi-user.target
EOF'

sudo systemctl daemon-reload
sudo systemctl enable learning-tool
sudo systemctl restart learning-tool
```

---

## Option C: AWS Systems Manager Parameter Store or Secrets Manager

For better security (especially for OPENAI_API_KEY), store secrets in SSM Parameter Store or Secrets Manager and fetch them at deploy time.

Example pattern in deploy.yml:
- Create parameters /learning-tool/OPENAI_API_KEY, /learning-tool/DYNAMODB_TABLE_NAME, etc.
- In the deploy job, use aws ssm get-parameter to retrieve values and write them into /etc/learning-tool.env on the instance.

```
- name: Fetch secrets from SSM Parameter Store
  env:
    AWS_REGION: us-east-1
  run: |
    OPENAI=$(aws ssm get-parameter --name "/learning-tool/OPENAI_API_KEY" --with-decryption --query Parameter.Value --output text)
    ssh ec2-user@${{ secrets.EC2_HOST }} "echo 'OPENAI_API_KEY=${OPENAI}' | sudo tee /etc/learning-tool.env >/dev/null"
    # Repeat for other parameters...
```

This keeps sensitive values out of your GitHub repository and centralizes secrets in AWS.

---

## Frontend configuration

If you deploy the frontend separately (e.g., S3 static site), set the backend base URL in a .env file during the frontend build:

- frontend/.env with VITE_API_BASE="http://your-backend-host-or-elb:8080"

---

## Summary
- Place these variables where your service process can see them at start.
- For GitHub Actions deploy.yml → Use GitHub Secrets and write to an env file on the instance referenced by a systemd unit.
- For EC2 manual setup → Use /etc/learning-tool.env and EnvironmentFile= in the systemd service.
- For enhanced security → Use AWS SSM Parameter Store or Secrets Manager and fetch at deploy time.

The application will automatically read these variables on startup; no code changes are required to switch values between environments.