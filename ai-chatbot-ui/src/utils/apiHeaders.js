export const SESSION_ID_HEADER = 'X-Session-Id'
export const USER_ID_HEADER = 'X-User-Id'

export function buildApiHeaders({ sessionId, userId } = {}) {
  const headers = {}
  if (sessionId) {
    headers[SESSION_ID_HEADER] = sessionId
  }
  if (userId) {
    headers[USER_ID_HEADER] = userId
  }
  return headers
}
