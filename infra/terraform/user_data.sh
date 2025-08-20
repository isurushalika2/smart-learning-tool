#!/bin/bash
set -euxo pipefail
# Update system
sudo dnf -y update

# Install Amazon Corretto 21 (widely available; Spring Boot 3.3 supports 21). The app can be built with Java 24 but runs on 21 if compiled accordingly.
sudo rpm --import https://yum.corretto.aws/corretto.key || true
sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
sudo dnf -y install java-21-amazon-corretto-headless git

# Create app directory
sudo mkdir -p /opt/learning-tool
sudo chown ec2-user:ec2-user /opt/learning-tool

# Placeholder: you can copy your built jar to /opt/learning-tool/app.jar via scp or S3.
echo "Learning Tool instance ready. Upload your Spring Boot JAR to /opt/learning-tool/app.jar and run: 'java -jar /opt/learning-tool/app.jar'" | sudo tee /opt/learning-tool/README_INSTANCE.txt
