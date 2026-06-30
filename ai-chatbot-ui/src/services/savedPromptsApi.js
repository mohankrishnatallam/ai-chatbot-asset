import { assertOkResponse } from '../utils/apiError'
import { buildApiHeaders } from '../utils/apiHeaders'

export async function fetchSavedPrompts(userId) {
  const response = await fetch('/prompts', {
    headers: buildApiHeaders({ userId }),
  })

  return assertOkResponse(response)
}

export async function savePrompt(userId, text) {
  const response = await fetch('/prompts', {
    method: 'POST',
    headers: {
      ...buildApiHeaders({ userId }),
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ text }),
  })

  return assertOkResponse(response)
}

export async function deleteSavedPrompt(promptId, userId) {
  const response = await fetch(`/prompts/${promptId}`, {
    method: 'DELETE',
    headers: buildApiHeaders({ userId }),
  })

  return assertOkResponse(response)
}
