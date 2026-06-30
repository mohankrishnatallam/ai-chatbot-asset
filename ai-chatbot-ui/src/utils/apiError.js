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

export function assertSuccessfulAiResponse(result) {
  if (result?.type === 'ERROR' || result?.status === 'FAILED') {
    throw new ApiError(result?.message || 'The assistant could not complete your request.', {
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
