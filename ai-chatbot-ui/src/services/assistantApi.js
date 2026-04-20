export async function fetchAssistantResponse(message) {
  const response = await fetch(
    `/assistant?message=${encodeURIComponent(message)}`
  )

  if (!response.ok) {
    throw new Error(`API request failed with status ${response.status}`)
  }

  const result = await response.json()
  return result?.message || 'No response message found from backend.'
}
