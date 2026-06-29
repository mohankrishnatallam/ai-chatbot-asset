async function parseJsonResponse(response) {
  const result = await response.json().catch(() => ({}))

  if (!response.ok) {
    throw new Error(result?.message || `Request failed with status ${response.status}`)
  }

  return result
}

export async function fetchSavedPrompts(userId) {
  const params = new URLSearchParams({ userId })
  const response = await fetch(`/prompts?${params}`)
  return parseJsonResponse(response)
}

export async function savePrompt(userId, text) {
  const response = await fetch('/prompts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ userId, text }),
  })

  return parseJsonResponse(response)
}

export async function deleteSavedPrompt(promptId, userId) {
  const params = new URLSearchParams({ userId })
  const response = await fetch(`/prompts/${promptId}?${params}`, {
    method: 'DELETE',
  })

  return parseJsonResponse(response)
}
