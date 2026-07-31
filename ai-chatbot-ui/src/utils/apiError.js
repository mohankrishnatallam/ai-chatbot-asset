export class ApiError extends Error {
  constructor(message, { status, code, type } = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
    this.type = type
  }
}

export async function parseJsonBody(response) {
  return response.json().catch(() => ({}))
}

export async function assertOkResponse(response) {
  const body = await parseJsonBody(response)

  if (!response.ok) {
    throw new ApiError(body?.message || `Request failed with status ${response.status}`, {
      status: response.status,
    })
  }

  return body
}

function toUserFriendlyAiError(message) {
  if (!message) {
    return 'The assistant could not complete your request.'
  }

  if (
    message.includes('Unexpected end-of-input') ||
    message.includes('Unexpected character') ||
    message.includes('JsonEOFException') ||
    message.includes('was too large')
  ) {
    return 'The assistant response was too large to process. Try a more specific request, such as checking one product by ID.'
  }

  if (message.startsWith('AI error: ')) {
    return message.slice('AI error: '.length)
  }

  return message
}

export function assertSuccessfulAiResponse(result) {
  if (result?.type === 'ERROR' || result?.status === 'FAILED') {
    throw new ApiError(toUserFriendlyAiError(result?.message), {
      code: result?.status,
      type: result?.type,
    })
  }

  return result
}

export function getApiErrorMessage(error, fallbackMessage) {
  if (error instanceof TypeError) {
    return 'Cannot reach the backend. Start the API with .\\mvnw.cmd spring-boot:run in ai-chatbot-api (port 8082).'
  }

  if (error instanceof ApiError) {
    const details = []
    if (error.status) {
      details.push(`HTTP ${error.status}`)
    }
    if (error.code) {
      details.push(error.code)
    }
    if (error.type) {
      details.push(error.type)
    }

    const prefix = details.length > 0 ? `${details.join(' · ')}: ` : ''
    return `${prefix}${error.message}`
  }

  if (error instanceof Error && error.message) {
    return error.message
  }

  return fallbackMessage
}
