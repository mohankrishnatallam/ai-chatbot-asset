import { useEffect, useState } from 'react'
import SavePromptModal from './SavePromptModal'
import {
  deleteSavedPrompt,
  fetchSavedPrompts,
  savePrompt,
} from '../services/savedPromptsApi'
import { getApiErrorMessage } from '../utils/apiError'
import '../styles/savedPrompts.css'

function formatPromptDate(value) {
  if (!value) {
    return ''
  }

  return new Date(value).toLocaleString()
}

function SavedPromptsPanel({ userId, onBackToHome, onUsePrompt }) {
  const [prompts, setPrompts] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [deletingPromptId, setDeletingPromptId] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const [isModalOpen, setIsModalOpen] = useState(false)

  const loadPrompts = async () => {
    setIsLoading(true)
    setErrorMessage('')

    try {
      const result = await fetchSavedPrompts(userId)
      setPrompts(result)
    } catch (error) {
      setErrorMessage(
        getApiErrorMessage(error, 'Unable to load saved prompts. Please try again.')
      )
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    loadPrompts()
  }, [userId])

  const handleSave = async (text) => {
    await savePrompt(userId, text)
    setIsModalOpen(false)
    await loadPrompts()
  }

  const handleDelete = async (prompt) => {
    const confirmed = window.confirm('Delete this saved prompt? This cannot be undone.')

    if (!confirmed) {
      return
    }

    setDeletingPromptId(prompt.id)
    setErrorMessage('')

    try {
      await deleteSavedPrompt(prompt.id, userId)
      await loadPrompts()
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : 'Unable to delete this prompt.'
      )
    } finally {
      setDeletingPromptId(null)
    }
  }

  return (
    <>
      <section className="saved-prompts-panel">
        <div className="saved-prompts-header">
          <h2 className="saved-prompts-title">Saved prompts</h2>
          <div className="saved-prompts-actions">
            <button type="button" className="saved-prompts-home-button" onClick={onBackToHome}>
              Back to current chat
            </button>
            <button
              type="button"
              className="saved-prompts-new-button"
              onClick={() => setIsModalOpen(true)}
            >
              + New prompt
            </button>
          </div>
        </div>

        {isLoading && <p className="saved-prompts-status">Loading prompts...</p>}
        {errorMessage && <p className="saved-prompts-error">{errorMessage}</p>}

        {!isLoading && !errorMessage && prompts.length === 0 && (
          <p className="saved-prompts-empty">
            No saved prompts yet. Click &quot;New prompt&quot; to save your first one.
          </p>
        )}

        <ul className="saved-prompts-list">
          {prompts.map((prompt) => (
            <li key={prompt.id} className="saved-prompt-item">
              <button
                type="button"
                className="saved-prompt-card"
                onClick={() => onUsePrompt?.(prompt.text)}
              >
                <span className="saved-prompt-text">{prompt.text}</span>
                <span className="saved-prompt-meta">{formatPromptDate(prompt.updatedAt)}</span>
              </button>

              <button
                type="button"
                className="saved-prompt-delete-button"
                onClick={() => handleDelete(prompt)}
                disabled={deletingPromptId === prompt.id}
                aria-label="Delete saved prompt"
              >
                {deletingPromptId === prompt.id ? 'Deleting...' : 'Delete'}
              </button>
            </li>
          ))}
        </ul>
      </section>

      {isModalOpen && (
        <SavePromptModal onSave={handleSave} onCancel={() => setIsModalOpen(false)} />
      )}
    </>
  )
}

export default SavedPromptsPanel
