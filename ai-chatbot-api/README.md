# AI Chatbot API

Java-based backend for the AI Chatbot application. This service exposes API endpoints for chatbot interactions and uses OpenAI via environment configuration.

## Prerequisites

- Java JDK 21
- Maven or the included Maven wrapper (`mvnw` / `mvnw.cmd`)
- OpenAI API key
- MongoDB Atlas account with cluster access (for data persistence)

## API Local Setup

From your repo root or workspace, change into the API directory using a relative path and set the environment variables:

### PowerShell (Recommended)
```powershell
cd ../ai-chatbot-api
$env:JAVA_HOME = "C:\Java\jdk-21.0.11"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:OPENAI_API_KEY = "<key-here>"
$env:MONGO_USERNAME = "<mongodb-username>"
$env:MONGO_PASSWORD = "<mongodb-password>"
```

### Windows Command Prompt
```cmd
cd ../ai-chatbot-api
set JAVA_HOME=C:\Java\jdk-21.0.11
set Path=%JAVA_HOME%\bin;%Path%
set OPENAI_API_KEY=<key-here>
set MONGO_USERNAME=<mongodb-username>
set MONGO_PASSWORD=<mongodb-password>
```

Replace `<key-here>` with your OpenAI API key, and `<mongodb-username>` and `<mongodb-password>` with your MongoDB Atlas credentials.


## Build and Run

```powershell
.\mvnw.cmd spring-boot:run
```

## API documentation (Swagger)

After the server starts, open:

- **Swagger UI:** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)

Use **Auth → login** first to obtain a `userId`, then set `X-User-Id` (and `X-Session-Id` where required) in the assistant and prompts endpoints.

## Available Endpoints

- `POST /auth/register` - creates a user in MongoDB (password stored as BCrypt hash)
- `POST /auth/login` - validates username/password and returns user info
- `POST /assistant` - returns a chat response via the AI assistant service (body: `{ message }`; `X-Session-Id`, `X-User-Id` headers required)
- `GET /assistant/history` - returns persisted chat turns for a session (`X-Session-Id`, `X-User-Id` headers required)
- `GET /assistant/sessions` - returns chat sessions for a user (`X-User-Id` header required)
- `GET /prompts` - returns saved prompts for a user (`X-User-Id` header required)
- `POST /prompts` - saves a prompt (`X-User-Id` header required; body: `{ text }`)
- `DELETE /assistant/session` - deletes session and chat turns from MongoDB (`X-Session-Id`, `X-User-Id` headers required)
- `DELETE /prompts/{promptId}` - deletes a saved prompt (`X-User-Id` header required)
- `GET /assistant/debug/sessions` - returns active in-memory session count
- `GET /model?message={text}` - returns a response using the lower-level chat model API

## Postman (local testing)

Import both files from `postman/`:

1. `AI-Chatbot-API-Local.postman_collection.json`
2. `AI-Chatbot-API-Local.postman_environment.json`

Select the **AI Chatbot API - Local** environment, start the API, then run the requests against `http://localhost:8082`.

## Notes

- Keep sensitive values out of source control by using environment variables (OpenAI API key, MongoDB credentials).
- If you run the service from a different working directory, update the relative path accordingly.
