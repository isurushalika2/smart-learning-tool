# Generate Content with AI

This guide shows exactly what you need to do to generate content with the LearningTool backend, using an AI provider (OpenAI). If you don’t provide an API key, the app returns mock data so you can still try the flow.

## 1) Prerequisites
- Java 21+ runtime (Java 24 recommended; project compiles to Java 21 bytecode)
- Internet access for dependencies
- Optional: OpenAI API key

## 2) Configure your AI provider key
The backend reads the OpenAI API key from an environment variable.

- macOS/Linux (bash/zsh):
```bash
export OPENAI_API_KEY=sk-...your_key...
# Optional if you use a custom compatible endpoint (e.g., Azure OpenAI or proxy)
export OPENAI_API_BASE=https://api.openai.com
```
- Windows (PowerShell):
```powershell
setx OPENAI_API_KEY "sk-...your_key..."
# Re-open terminal or set $env:OPENAI_API_KEY for current session
$env:OPENAI_API_KEY = "sk-...your_key..."
$env:OPENAI_API_BASE = "https://api.openai.com"  # optional
```
Notes:
- If OPENAI_API_KEY is not set, generation still works using a built-in mock provider (useful for demos).
- OPENAI_API_BASE is optional; keep default unless you’re using Azure/OpenAI-compatible gateways.

## 3) Run the backend
From the repository root:
```bash
./gradlew bootRun
```
Health check: http://localhost:8080/actuator/health

## 4) (Optional) Run the frontend
The React UI provides a simple form to call /api/generate and view results.
```bash
cd frontend
npm install
npm run dev
# open http://localhost:5173
```
The dev server proxies /api to http://localhost:8080 automatically.

## 5) How to generate content
You have multiple options:

### A) Swagger UI (easiest, no tools required)
1. Start the backend.
2. Open http://localhost:8080/swagger-ui.html
3. Find POST /api/generate
4. Click Try it out and provide a request body like:
```json
{
  "type": "SHORT_NOTES",
  "topic": "Java Streams",
  "level": "beginner",
  "language": "en"
}
```
5. Execute. You will receive a structured JSON response with fields like summary, items, and payload (images/qa/problems depending on type).

### B) Frontend UI
1. Start backend and `npm run dev` in `frontend/`.
2. Open http://localhost:5173
3. Go to the Generate tab, fill in the form, click Generate.
4. The result is shown in a friendly view as well as raw JSON, and is saved to history.

### C) cURL / HTTP clients
Example request:
```bash
curl -X POST http://localhost:8080/api/generate \
  -H 'Content-Type: application/json' \
  -d '{
    "type":"SHORT_NOTES",
    "topic":"Kubernetes Basics",
    "level":"beginner",
    "language":"en"
  }'
```
See more examples in docs/sample-requests.http

## 6) Saving and viewing history (optional)
- All generate responses are saved through the pluggable repository.
- By default, in-memory history is used. If you enable DynamoDB, items are persisted there.

DynamoDB quick setup (optional):
- Ensure these env vars (defaults exist in application.yml):
```bash
export DYNAMODB_ENABLED=true
export DYNAMODB_TABLE_NAME=learningtool-generation-history
export DYNAMODB_REGION=us-east-1   # or your region
```
- Provide AWS credentials via the default AWS SDK chain (env/profile/role).
- Create table (example):
```bash
aws dynamodb create-table \
  --table-name learningtool-generation-history \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region us-east-1
```
- Endpoints:
  - GET /api/history — list items
  - POST /api/history/sample — insert a sample item
  - POST /api/history — insert a custom item (GenerateResponse schema)

## 7) Troubleshooting
- I get empty or mock-looking results
  - Ensure you exported OPENAI_API_KEY in the same shell before running the app.
  - Restart the backend after changing env vars.
- 401/403 from AI provider
  - Check your OpenAI key validity and account status.
- Corporate proxy or custom gateway
  - Set OPENAI_API_BASE if your environment requires a different base URL.
- CORS errors from a separate frontend host
  - Backend CORS is already configured to allow all origins; ensure the frontend calls the correct base URL (set VITE_API_BASE in frontend/.env for production).
- DynamoDB errors (optional persistence)
  - Verify table exists in the exact region you configured.
  - Ensure IAM permissions allow PutItem and Scan on the table.

## 8) Quick checklist
- [ ] Backend running (./gradlew bootRun)
- [ ] OPENAI_API_KEY exported (optional; mock is used if absent)
- [ ] Using Swagger UI, Frontend, or cURL to call POST /api/generate
- [ ] (Optional) DynamoDB configured and table exists for persistent history

You’re all set. Generate away!