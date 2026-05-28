# AI Chatbot API

Java-based backend for the AI Chatbot application. This service exposes API endpoints for chatbot interactions and uses OpenAI via environment configuration.

## Prerequisites

- Java JDK 21
- Maven or the included Maven wrapper (`mvnw` / `mvnw.cmd`)
- OpenAI API key

## API Local Setup

From your repo root or workspace, change into the API directory using a relative path and set the Java and OpenAI environment variables:

```powershell
cd ../ai-chatbot-api
$env:JAVA_HOME = "C:\Java\jdk-21.0.11"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:OPENAI_API_KEY = "<key-here>"
```

Replace `<key-here>` with your OpenAI API key.


## Build and Run

```powershell
.
\mvnw.cmd spring-boot:run
```

or if Maven is available globally:

```powershell
mvn spring-boot:run
```

## Available Endpoints

- `GET /assistant?message={text}` - returns a chat response via the AI assistant service
- `GET /model?message={text}` - returns a response using the lower-level chat model API

## Notes

- Keep sensitive values out of source control by using environment variables.
- If you run the service from a different working directory, update the relative path accordingly.
