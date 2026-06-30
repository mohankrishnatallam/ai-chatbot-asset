import { useEffect, useState } from 'react'
import ChatThread from './ChatThread'
import {
  deleteSession,
  fetchSessionHistory,
  fetchUserSessions,
  truncateSessionHistory,
} from '../services/assistantApi'
import { getApiErrorMessage } from '../utils/apiError'
import '../styles/sessionHistory.css'

function formatSessionDate(value) {
  if (!value) {
    return ''
  }

  return new Date(value).toLocaleString()
}

function SessionHistoryPanel({
  userId,
  currentSessionId,
  onBackToHome,
  onCurrentSessionDeleted,
  onContinueSession,
}) {
  const [sessions, setSessions] = useState([])
  const [selectedSession, setSelectedSession] = useState(null)
  const [selectedConversations, setSelectedConversations] = useState([])
  const [isLoadingSessions, setIsLoadingSessions] = useState(true)
  const [isLoadingHistory, setIsLoadingHistory] = useState(false)
  const [deletingSessionId, setDeletingSessionId] = useState(null)
  const [continuingSessionId, setContinuingSessionId] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')

  const loadSessions = async () => {
    setIsLoadingSessions(true)
    setErrorMessage('')

    try {
      const result = await fetchUserSessions(userId)
      setSessions(result)
    } catch (error) {
      setErrorMessage(
        getApiErrorMessage(error, 'Unable to load your previous sessions. Please try again.')
      )
    } finally {
      setIsLoadingSessions(false)
    }
  }

  useEffect(() => {
    loadSessions()
  }, [userId])

  const handleSelectSession = async (session) => {
    setSelectedSession(session)
    setIsLoadingHistory(true)
    setErrorMessage('')

    try {
      const conversations = await fetchSessionHistory(session.sessionId, userId)
      setSelectedConversations(conversations)
    } catch (error) {
      setSelectedConversations([])
      setErrorMessage(
        getApiErrorMessage(error, 'Unable to load this session. Please try again.')
      )
    } finally {
      setIsLoadingHistory(false)
    }
  }

  const handleDeleteSession = async (session) => {
    const confirmed = window.confirm(
      `Delete "${session.title}"? This cannot be undone.`
    )

    if (!confirmed) {
      return
    }

    setDeletingSessionId(session.sessionId)
    setErrorMessage('')

    try {
      await deleteSession(session.sessionId, userId)

      if (selectedSession?.sessionId === session.sessionId) {
        setSelectedSession(null)
        setSelectedConversations([])
      }

      if (session.sessionId === currentSessionId) {
        onCurrentSessionDeleted?.()
      }

      await loadSessions()
    } catch (error) {
      setErrorMessage(
        getApiErrorMessage(error, 'Unable to delete this session.')
      )
    } finally {
      setDeletingSessionId(null)
    }
  }

  const handleContinueSession = async (session, { fromSequence } = {}) => {
    if (!session?.sessionId) {
      return
    }

    setContinuingSessionId(session.sessionId)
    setErrorMessage('')

    try {
      if (typeof fromSequence === 'number') {
        await truncateSessionHistory(session.sessionId, userId, fromSequence)
      }

      onContinueSession?.(session.sessionId)
    } catch (error) {
      setErrorMessage(
        getApiErrorMessage(error, 'Unable to continue this chat. Please try again.')
      )
    } finally {
      setContinuingSessionId(null)
    }
  }

  if (selectedSession) {
    return (
      <section className="history-panel">
        <div className="history-detail-header">
          <button
            type="button"
            className="history-back-button"
            onClick={() => {
              setSelectedSession(null)
              setSelectedConversations([])
              setErrorMessage('')
            }}
          >
            ← All sessions
          </button>

          <div className="history-detail-actions">
            <button
              type="button"
              className="history-continue-button"
              onClick={() => handleContinueSession(selectedSession)}
              disabled={continuingSessionId === selectedSession.sessionId}
            >
              {continuingSessionId === selectedSession.sessionId ? 'Opening...' : 'Continue chat'}
            </button>

            <button
              type="button"
              className="history-delete-button"
              onClick={() => handleDeleteSession(selectedSession)}
              disabled={deletingSessionId === selectedSession.sessionId}
            >
              {deletingSessionId === selectedSession.sessionId ? 'Deleting...' : 'Delete'}
            </button>
          </div>
        </div>

        <h2 className="history-detail-title">{selectedSession.title}</h2>
        <p className="history-detail-meta">
          {formatSessionDate(selectedSession.updatedAt)} · {selectedSession.turnCount} messages
        </p>
        <p className="history-continue-hint">
          Click a prompt below to continue the chat from that point on Home.
        </p>

        {isLoadingHistory && <p className="history-status">Loading conversation...</p>}
        {errorMessage && <p className="history-error">{errorMessage}</p>}

        {!isLoadingHistory &&
          selectedConversations.map((item, index) => (
            <ChatThread
              key={`${selectedSession.sessionId}-${index}`}
              question={item.question}
              answer={item.answer}
              onQuestionClick={() =>
                handleContinueSession(selectedSession, { fromSequence: item.sequence ?? index })
              }
              className="history-chat-thread"
            />
          ))}
      </section>
    )
  }

  return (
    <section className="history-panel">
      <div className="history-header">
        <h2 className="history-title">Your chat history</h2>
        <button type="button" className="history-home-button" onClick={onBackToHome}>
          Back to current chat
        </button>
      </div>

      {isLoadingSessions && <p className="history-status">Loading sessions...</p>}
      {errorMessage && <p className="history-error">{errorMessage}</p>}

      {!isLoadingSessions && !errorMessage && sessions.length === 0 && (
        <p className="history-empty">No previous sessions yet. Start chatting on Home.</p>
      )}

      <ul className="history-session-list">
        {sessions.map((session) => (
          <li key={session.sessionId} className="history-session-item">
            <button
              type="button"
              className={`history-session-card ${
                session.sessionId === currentSessionId ? 'history-session-card-current' : ''
              }`}
              onClick={() => handleSelectSession(session)}
            >
              <span className="history-session-title">{session.title}</span>
              <span className="history-session-meta">
                {formatSessionDate(session.updatedAt)} · {session.turnCount} messages
              </span>
              {session.sessionId === currentSessionId && (
                <span className="history-session-badge">Current session</span>
              )}
            </button>

            <button
              type="button"
              className="history-continue-button history-continue-button-compact"
              onClick={() => handleContinueSession(session)}
              disabled={continuingSessionId === session.sessionId}
            >
              {continuingSessionId === session.sessionId ? 'Opening...' : 'Continue'}
            </button>

            <button
              type="button"
              className="history-delete-button"
              onClick={() => handleDeleteSession(session)}
              disabled={deletingSessionId === session.sessionId}
              aria-label={`Delete session ${session.title}`}
            >
              {deletingSessionId === session.sessionId ? 'Deleting...' : 'Delete'}
            </button>
          </li>
        ))}
      </ul>
    </section>
  )
}

export default SessionHistoryPanel
