# Learning Tool (Spring Boot + React)

A full-stack learning assistant that helps beginners quickly learn programming languages and technologies. It generates:
- Attractive images and animation storyboards
- Short notes and cheat sheets
- Coding practice (LeetCode-style) summaries
- Interview questions and model answers
- Voice explanation scripts (and can integrate with TTS)
- All powered by an AI provider (OpenAI) or a built-in mock for offline demos

Backend: Spring Boot (Gradle). Frontend: React (Vite).

Important: The project compiles to Java 21 bytecode for Spring compatibility and runs on Java 21+ (tested on Java 24).

## Quickstart

1) Backend
- Prerequisites: Java 21+ (build targets 21; Java 24 runtime is fine), Gradle wrapper, Internet (for deps)
- Set your OpenAI key (optional):
  - export OPENAI_API_KEY=sk-...
- Run:
  - ./gradlew bootRun
- API will be at http://localhost:8080

👉 For a step-by-step guide on generating content with AI (Swagger UI, frontend, and cURL), see docs/GENERATE_WITH_AI.md

2) Frontend
- Prerequisites: Node 18+
- Development (proxy to backend):
  - cd frontend
  - npm install
  - npm run dev
  - Open http://localhost:5173
  - Vite dev server proxies /api to http://localhost:8080 (see frontend/vite.config.js)
- UI Overview:
  - Tabs in header: Generate, History, Admin
  - Generate: form to POST /api/generate and view pretty and raw JSON results
  - History: list of items from /api/history
  - Admin: buttons/forms to replicate Swagger actions (insert sample/custom history, trigger generate, fetch history)
- Production build:
  - npm run build
  - Serve dist/ from any host, and set the backend URL via environment:
    - Copy frontend/.env.example to frontend/.env and set VITE_API_BASE (e.g., http://localhost:8080 or your EC2 public DNS)

If OPENAI_API_KEY is not set, the app uses a mock provider with placeholder content (images via placehold.co, sample Q&A, etc.).

Connectivity notes:
- In dev, requests go through the Vite proxy; no CORS issues expected.
- In prod, the frontend calls VITE_API_BASE + /api/... directly. The backend’s CORS is configured to allow all origins without credentials so cross-origin calls succeed.

## API
POST /api/generate
- Body:
```
{
  "type": "SHORT_NOTES" | "CHEAT_SHEET" | "IMAGE" | "ANIMATION" | "LEETCODE" | "VOICE_EXPLANATION" | "INTERVIEW_QA",
  "topic": "Java Basics",
  "level": "beginner|intermediate|advanced",
  "language": "en|..."
}
```
- Response: structured JSON with summary, items, and payload fields such as `imageUrls`, `qa`, `problems`, etc.

Curl example:
```
curl -X POST http://localhost:8080/api/generate \
  -H 'Content-Type: application/json' \
  -d '{"type":"SHORT_NOTES","topic":"Java Basics","level":"beginner","language":"en"}'
```

## Java Version
- Runs on Java 24 (recommended). Compiles to Java 21 bytecode for Spring compatibility.
- Ensure your environment provides a Java 24 JDK. Gradle toolchains are configured to use JDK 24 automatically if available.

## Project Layout
- backend: Spring Boot service exposing the /api/generate endpoint
- frontend: Vite React UI that calls the backend and shows results
- docs: Additional architecture and rationale

## Configuration
- OPENAI_API_KEY: OpenAI key (optional; if absent, mock provider is used)
- OPENAI_API_BASE: Custom base URL (optional; defaults to https://api.openai.com)
### Persistence (DynamoDB only)
This project now uses AWS DynamoDB as the only persistence backend.

Configure via environment variables:
- DYNAMODB_ENABLED=true (default)
- DYNAMODB_TABLE_NAME=learningtool-generation-history (or your table)
- DYNAMODB_REGION=us-east-1 (or your region)
- Provide AWS credentials via the default AWS SDK v2 chain (IAM role on EC2/ECS/Lambda, or env/credentials file).

Notes:
- If credentials are missing or the table is absent, the app continues to serve requests; save() failures are swallowed to avoid breaking the flow.
- You no longer need MongoDB or AWS DocumentDB for this project.

## Deployment environment variables (AWS / GitHub Actions)
For production deployments, place OPENAI_API_KEY, OPENAI_API_BASE, DYNAMODB_ENABLED, DYNAMODB_TABLE_NAME, and DYNAMODB_REGION as environment variables where the backend process runs. Recommended approaches:
- GitHub Actions deploy.yml → store values in GitHub Secrets, then write them to /etc/learning-tool.env on the EC2 instance and reference it via a systemd service (EnvironmentFile=/etc/learning-tool.env).
- Manual EC2 setup → create /etc/learning-tool.env with key=value lines and load via systemd.
- AWS SSM Parameter Store/Secrets Manager → store the secrets and fetch them during deploy to populate /etc/learning-tool.env.

See docs/DEPLOYMENT.md for step-by-step examples and snippets.

## Development
- Backend tests: `./gradlew test`
- Health check: `GET http://localhost:8080/actuator/health`

## License
MIT (for this template). Check third-party licenses accordingly.
