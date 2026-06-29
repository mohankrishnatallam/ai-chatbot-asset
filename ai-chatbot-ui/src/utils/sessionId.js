const SESSION_STORAGE_PREFIX = 'ai-chatbot-session-id'

function getStorageKey(userId) {
  return userId ? `${SESSION_STORAGE_PREFIX}-${userId}` : `${SESSION_STORAGE_PREFIX}-guest`
}

export function getStoredSessionId(userId) {
  return localStorage.getItem(getStorageKey(userId))
}

export function createNewSessionId(userId) {
  const sessionId = crypto.randomUUID()
  localStorage.setItem(getStorageKey(userId), sessionId)
  return sessionId
}

export function getOrCreateSessionId(userId) {
  const storedSessionId = getStoredSessionId(userId)
  if (storedSessionId) {
    return storedSessionId
  }

  return createNewSessionId(userId)
}

export function clearSessionId(userId) {
  localStorage.removeItem(getStorageKey(userId))
}
