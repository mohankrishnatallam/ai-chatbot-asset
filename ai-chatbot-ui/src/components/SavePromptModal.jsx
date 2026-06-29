import { useState } from 'react'
import '../styles/savePromptModal.css'

function SavePromptModal({ onSave, onCancel }) {
  const [text, setText] = useState('')
  const [errorMessage, setErrorMessage] = useState('')
  const [isSaving, setIsSaving] = useState(false)

  const handleSubmit = async (event) => {
    event.preventDefault()
    setErrorMessage('')
    setIsSaving(true)

    try {
      await onSave(text)
      setText('')
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : 'Unable to save prompt. Please try again.'
      )
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <main
      className="save-prompt-shell"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) {
          onCancel()
        }
      }}
    >
      <section className="save-prompt-card">
        <button
          type="button"
          className="close-button"
          onClick={onCancel}
          aria-label="Close popup"
        >
          ×
        </button>

        <h1 className="save-prompt-title">Save a prompt</h1>

        <form className="save-prompt-form" onSubmit={handleSubmit}>
          <label className="save-prompt-field">
            <span>Prompt</span>
            <textarea
              value={text}
              onChange={(event) => setText(event.target.value)}
              placeholder="Type your prompt here..."
              rows={5}
              disabled={isSaving}
              required
            />
          </label>

          {errorMessage && <p className="save-prompt-error">{errorMessage}</p>}

          <div className="save-prompt-actions">
            <button
              type="button"
              className="save-prompt-cancel"
              onClick={onCancel}
              disabled={isSaving}
            >
              Cancel
            </button>
            <button type="submit" className="save-prompt-save" disabled={isSaving}>
              {isSaving ? 'Saving...' : 'Save'}
            </button>
          </div>
        </form>
      </section>
    </main>
  )
}

export default SavePromptModal
