export async function fetchAssistantResponse(message, sessionId, userId) {
  const params = new URLSearchParams({
    message,
    sessionId,
  })

  if (userId) {
    params.set('userId', userId)
  }

  const response = await fetch(`/assistant?${params}`)

  if (!response.ok) {
    throw new Error(`API request failed with status ${response.status}`)
  }

  const result = await response.json()
  return result?.message || 'No response message found from backend.'
}

export async function fetchUserSessions(userId) {
  const params = new URLSearchParams({ userId })
  const response = await fetch(`/assistant/sessions?${params}`)

  if (!response.ok) {
    throw new Error(`Failed to load sessions with status ${response.status}`)
  }

  return response.json()
}

export async function fetchSessionHistory(sessionId) {
  const params = new URLSearchParams({ sessionId })
  const response = await fetch(`/assistant/history?${params}`)

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
  const params = new URLSearchParams({ userId })
  const response = await fetch(`/assistant/session/${sessionId}?${params}`, {
    method: 'DELETE',
  })

  if (!response.ok) {
    const result = await response.json().catch(() => ({}))
    throw new Error(result?.message || `Failed to delete session with status ${response.status}`)
  }
}
