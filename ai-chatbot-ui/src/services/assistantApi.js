import {
  ApiError,
  assertOkResponse,
  assertSuccessfulAiResponse,
} from '../utils/apiError'
import { buildApiHeaders } from '../utils/apiHeaders'

function isEmptyData(data) {
  if (data == null) {
    return true
  }
  if (typeof data !== 'object') {
    return false
  }
  if (Array.isArray(data)) {
    return data.length === 0
  }
  return Object.keys(data).length === 0
}

function formatAssistantAnswer({ message, data } = {}) {
  const text = message?.trim() || ''

  if (isEmptyData(data)) {
    return text || 'No response from assistant.'
  }

  const dataText = typeof data === 'string' ? data : JSON.stringify(data, null, 2)
  return text ? `${text}\n\n${dataText}` : dataText
}

function requireUserId(userId) {
  if (!userId) {
    throw new ApiError('User id is required. Please log in and try again.')
  }
}

export async function fetchAssistantResponse(message, sessionId, userId) {
  requireUserId(userId)

  if (!sessionId) {
    throw new ApiError('Session id is required.')
  }

  const response = await fetch('/assistant', {
    method: 'POST',
    headers: {
      ...buildApiHeaders({ sessionId, userId }),
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ message }),
  })

  const result = await assertOkResponse(response)
  const aiResponse = assertSuccessfulAiResponse(result)
  return formatAssistantAnswer(aiResponse)
}

export async function fetchUserSessions(userId) {
  requireUserId(userId)

  const response = await fetch('/assistant/sessions', {
    headers: buildApiHeaders({ userId }),
  })

  return assertOkResponse(response)
}

export async function fetchSessionHistory(sessionId, userId) {
  requireUserId(userId)

  if (!sessionId) {
    throw new ApiError('Session id is required.')
  }

  const response = await fetch('/assistant/history', {
    headers: buildApiHeaders({ sessionId, userId }),
  })

  const turns = await assertOkResponse(response)
  return turns.map((turn) => ({
    question: turn.question,
    answer: formatAssistantAnswer(
      turn.assistantPayload || { message: turn.answerText, data: null }
    ),
    sequence: turn.sequence,
  }))
}

export async function truncateSessionHistory(sessionId, userId, afterSequence) {
  requireUserId(userId)

  if (!sessionId) {
    throw new ApiError('Session id is required.')
  }

  const params = new URLSearchParams({ afterSequence: String(afterSequence) })
  const response = await fetch(`/assistant/history/truncate?${params}`, {
    method: 'DELETE',
    headers: buildApiHeaders({ sessionId, userId }),
  })

  await assertOkResponse(response)
}

export async function deleteSession(sessionId, userId) {
  requireUserId(userId)

  if (!sessionId) {
    throw new ApiError('Session id is required.')
  }

  const response = await fetch('/assistant/session', {
    method: 'DELETE',
    headers: buildApiHeaders({ sessionId, userId }),
  })

  await assertOkResponse(response)
}
