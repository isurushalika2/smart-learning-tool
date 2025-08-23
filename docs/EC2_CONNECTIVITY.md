# EC2 Connectivity Troubleshooting (Spring Boot Health Check)

If hitting your instance like:

- http://<EC2_PUBLIC_DNS>:8080/actuator/health

returns "Couldn't connect to server", work through this checklist.

## 1) Is the app listening on the correct interface and port?
- This project explicitly binds to all interfaces now:
  - application.yml contains:
    ```yaml
    server:
      port: 8080
      address: 0.0.0.0
    ```
- Ensure the process is running:
  ```bash
  ps -ef | grep app.jar
  sudo ss -ltnp | grep 8080
  curl -v http://localhost:8080/actuator/health
  ```

## 2) Is the app actually running on the EC2 instance?
- If you manually copied the JAR to /opt/learning-tool/app.jar, start it:
  ```bash
  cd /opt/learning-tool
  java -jar app.jar > app.log 2>&1 &
  tail -f app.log
  ```
- Preferred: run as a systemd service (see systemd unit below).

## 3) Security Group and networking
- The Terraform stack opens TCP 8080 to the world by default:
  - aws_security_group.backend_sg ingress 8080 from 0.0.0.0/0
- Verify your instance uses that SG and that you’re using the correct public DNS/IP.
- NACLs are default open in the provided VPC. If customized, ensure ephemeral ports and 8080 are allowed.

## 4) OS firewall
- Amazon Linux 2023 does not enable firewalld by default. If you enabled additional firewalling, allow 8080/tcp.

## 5) Reverse proxy or ALB
- If you put an ALB or proxy in front, you may need to use port 80 on the ALB and point target group to 8080 on the instance.
- In that case, health check URL should target the ALB DNS without :8080, e.g. http://<ALB_DNS>/actuator/health.

## 6) systemd service (recommended)
Create a unit file on the instance at /etc/systemd/system/learning-tool.service:

```
[Unit]
Description=Learning Tool Spring Boot Service
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/opt/learning-tool
EnvironmentFile=/etc/learning-tool.env
ExecStart=/usr/bin/java -jar /opt/learning-tool/app.jar
Restart=always
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

- Create env file (optional) with your variables:
```
# /etc/learning-tool.env
OPENAI_API_KEY=...
DYNAMODB_ENABLED=true
DYNAMODB_TABLE_NAME=learningtool-generation-history
DYNAMODB_REGION=us-east-1
```

- Reload and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable --now learning-tool
sudo systemctl status learning-tool
journalctl -u learning-tool -f
```

## 7) Quick remote test sequence
From your local machine:
```bash
# Replace with your EC2 DNS
EC2=http://ec2-xx-xx-xx-xx.compute-1.amazonaws.com:8080
curl -v "$EC2/actuator/health"
```

If it still fails:
- Confirm the public DNS/IP hasn’t changed (instance restarted).
- Confirm no corporate proxy/VPN is blocking port 8080.
- Try from a different network.

## 8) Common fixes summary
- App not started: start via systemd or java -jar.
- Wrong bind: server.address set to 0.0.0.0 (already configured in application.yml).
- Wrong port: ensure you are calling :8080 or change server.port to 80 and open SG 80 inbound.
- SG/NACL: confirm inbound 8080 allowed and instance is in the right SG.
- ALB in front: use ALB DNS, not instance DNS; health path stays /actuator/health.
