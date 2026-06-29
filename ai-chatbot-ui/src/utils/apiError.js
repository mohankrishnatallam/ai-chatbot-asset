export function getApiErrorMessage(error, fallbackMessage) {
  if (error instanceof TypeError) {
    return 'Cannot reach the backend. Start the API with .\\mvnw.cmd spring-boot:run in ai-chatbot-api (port 8082).'
  }

  if (error instanceof Error && error.message) {
    return error.message
  }

  return fallbackMessage
}
