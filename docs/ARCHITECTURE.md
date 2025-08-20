# Architecture and Rationale

```mermaid
flowchart LR
  UI[React UI (Vite)] -- REST /api/generate --> API[Spring Boot Controller]
  API --> SVC[GenerateService]
  SVC --> FACT[ProviderFactory]
  FACT -->|OpenAI configured| OA[ProviderOpenAi]
  FACT -->|Fallback| MOCK[ProviderMock]
  OA --> OpenAI[(OpenAI API)]
```

## Components
- React UI: Simple form to select topic/type/level/language and show generated results. Vite for fast dev.
- Spring Boot Controller: Exposes POST /api/generate with validation and JSON payloads.
- Service: Thin orchestration layer delegating to the provider strategy.
- ProviderFactory: Chooses between OpenAI provider and a mock provider based on env vars.
- ProviderOpenAi: Calls OpenAI Chat Completions for textual content, can be extended to images/audio.
- ProviderMock: Generates deterministic placeholder content for offline demos.

## Why these technologies
- Spring Boot: Mature ecosystem, rapid API development, actuator for ops, validation, WebFlux for calling external APIs.
- Java (21 default): LTS, best support with Spring Boot 3.3; Java 24 can be tested when supported.
- Gradle: Fast, flexible build system with kotlin DSL.
- React + Vite: Modern, fast dev server and build pipeline, good DX.

## Data Flow
1. User submits a form in the React UI.
2. UI sends POST /api/generate to the backend.
3. Controller validates and forwards to GenerateService.
4. Service asks ProviderFactory for the active provider.
5. ProviderOpenAi (if configured) calls AI API; otherwise ProviderMock returns placeholder data.
6. Structured response is sent back and shown in the UI.

## Extending the system
- Add new content type: extend GenerateRequest.ContentType and adjust Provider implementations.
- Add providers: create another ProviderX implementing ContentProvider and update ProviderFactory routing rules.
- Add authentication/rate limiting: Spring Security and filters; API keys per user.
- Persistence: store generated artifacts in a DB. The project includes optional MongoDB/DocumentDB integration (enable with AWS_DOCDB_URI or MONGODB_URI); otherwise it uses an in-memory store. You can swap to RDS (JPA) as needed.

## Deployment
- Backend: Build a fat jar `./gradlew bootJar` and run with Java 24. Containerize with a minimal base (e.g., eclipse-temurin:24-jre-alpine if available).
- Frontend: `npm run build` produces static assets for any web server or can be served by Nginx.
