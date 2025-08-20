# Learning Tool (Spring Boot + React)

A full-stack learning assistant that helps beginners quickly learn programming languages and technologies. It generates:
- Attractive images and animation storyboards
- Short notes and cheat sheets
- Coding practice (LeetCode-style) summaries
- Interview questions and model answers
- Voice explanation scripts (and can integrate with TTS)
- All powered by an AI provider (OpenAI) or a built-in mock for offline demos

Backend: Spring Boot (Gradle). Frontend: React (Vite).

Important: The project is configured to use Java 24.

## Quickstart

1) Backend
- Prerequisites: Java 24, Gradle wrapper, Internet (for deps)
- Set your OpenAI key (optional):
  - export OPENAI_API_KEY=sk-...
- Run:
  - ./gradlew bootRun
- API will be at http://localhost:8080

2) Frontend
- Prerequisites: Node 18+
- Run:
  - cd frontend
  - npm install
  - npm run dev
- Open http://localhost:5173

If OPENAI_API_KEY is not set, the app uses a mock provider with placeholder content (images via placehold.co, sample Q&A, etc.).

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
- This project targets Java 24 for compilation and runtime.
- Ensure your environment provides a Java 24 JDK. If needed, configure Gradle toolchains on your machine to provision JDK 24 automatically.

## Project Layout
- backend: Spring Boot service exposing the /api/generate endpoint
- frontend: Vite React UI that calls the backend and shows results
- docs: Additional architecture and rationale

## Configuration
- OPENAI_API_KEY: OpenAI key (optional; if absent, mock provider is used)
- OPENAI_API_BASE: Custom base URL (optional; defaults to https://api.openai.com)
- MONGODB_URI: If provided, enables MongoDB persistence for generation history (e.g., mongodb://localhost:27017/learningtool)
- AWS_DOCDB_URI: If provided, enables AWS DocumentDB persistence. Example:
  - export AWS_DOCDB_URI="mongodb://username:password@your-docdb-cluster.cluster-xxxxxx.us-east-1.docdb.amazonaws.com:27017/learningtool?tls=true&replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=false"
  Notes:
  - Ensure network access via VPC/peering/SSH tunnel as required.
  - Use a user with proper permissions.
  - You can embed additional parameters (e.g., tlsCAFile) in the URI if needed.

## Development
- Backend tests: `./gradlew test`
- Health check: `GET http://localhost:8080/actuator/health`

## License
MIT (for this template). Check third-party licenses accordingly.
