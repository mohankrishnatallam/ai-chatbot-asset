const AUTH_USER_STORAGE_KEY = 'ai-chatbot-auth-user'

export function getStoredAuthUser() {
  const raw = localStorage.getItem(AUTH_USER_STORAGE_KEY)
  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw)
  } catch {
    localStorage.removeItem(AUTH_USER_STORAGE_KEY)
    return null
  }
}

export function storeAuthUser(user) {
  localStorage.setItem(AUTH_USER_STORAGE_KEY, JSON.stringify(user))
}

export function clearStoredAuthUser() {
  localStorage.removeItem(AUTH_USER_STORAGE_KEY)
}
