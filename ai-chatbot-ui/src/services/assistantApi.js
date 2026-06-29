const SESSION_ID_HEADER = 'X-Session-Id'
const USER_ID_HEADER = 'X-User-Id'

function buildAssistantHeaders(sessionId, userId) {
  const headers = {}
  if (sessionId) {
    headers[SESSION_ID_HEADER] = sessionId
  }
  if (userId) {
    headers[USER_ID_HEADER] = userId
  }
  return headers
}

export async function fetchAssistantResponse(message, sessionId, userId) {
  const params = new URLSearchParams({ message })
  const response = await fetch(`/assistant?${params}`, {
    headers: buildAssistantHeaders(sessionId, userId),
  })

  if (!response.ok) {
    throw new Error(`API request failed with status ${response.status}`)
  }

  const result = await response.json()
  return result?.message || 'No response message found from backend.'
}

export async function fetchUserSessions(userId) {
  const response = await fetch('/assistant/sessions', {
    headers: { [USER_ID_HEADER]: userId },
  })

  if (!response.ok) {
    const result = await response.json().catch(() => ({}))
    throw new Error(result?.message || `Failed to load sessions with status ${response.status}`)
  }

  return response.json()
}

export async function fetchSessionHistory(sessionId, userId) {
  const response = await fetch('/assistant/history', {
    headers: buildAssistantHeaders(sessionId, userId),
  })

  if (!response.ok) {
    throw new Error(`Failed to load session history with status ${response.status}`)
  }

  const turns = await response.json()
  return turns.map((turn) => ({
    question: turn.question,
    answer: turn.answerText || turn.assistantPayload?.message || '',
  }))
}

export async function deleteSession(sessionId, userId) {
  const response = await fetch('/assistant/session', {
    method: 'DELETE',
    headers: buildAssistantHeaders(sessionId, userId),
  })

  if (!response.ok) {
    const result = await response.json().catch(() => ({}))
    throw new Error(result?.message || `Failed to delete session with status ${response.status}`)
  }
}
