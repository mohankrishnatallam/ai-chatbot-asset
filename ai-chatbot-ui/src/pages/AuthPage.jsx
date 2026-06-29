import { useState } from 'react'
import '../styles/authPage.css'

function AuthPage({ mode, onSubmit, onSwitchMode, onBack }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [errorMessage, setErrorMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const isLogin = mode === 'login'
  const title = isLogin ? 'Login to continue' : 'Create your account'
  const buttonLabel = isLogin ? 'Login' : 'Register'
  const helperText = isLogin
    ? 'Using the app for the first time? Register here.'
    : 'Already have an account? Login here.'

  const handleSubmit = async (event) => {
    event.preventDefault()
    setErrorMessage('')
    setIsSubmitting(true)

    try {
      await onSubmit({ username, password })
      setUsername('')
      setPassword('')
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : 'Unable to complete authentication. Please try again.'
      )
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main
      className="auth-shell"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) {
          onBack()
        }
      }}
    >
      <section className="auth-card">
        <button type="button" className="close-button" onClick={onBack} aria-label="Close popup">
          ×
        </button>

        <h1 className="auth-title">{title}</h1>

        <form className="auth-form" onSubmit={handleSubmit}>
          <label className="auth-field">
            <span>Username</span>
            <input
              type="text"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              autoComplete="username"
              disabled={isSubmitting}
              required
            />
          </label>

          <label className="auth-field">
            <span>Password</span>
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete={isLogin ? 'current-password' : 'new-password'}
              disabled={isSubmitting}
              required
            />
          </label>

          {errorMessage && <p className="auth-error">{errorMessage}</p>}

          <button type="submit" className="auth-submit" disabled={isSubmitting}>
            {isSubmitting ? 'Please wait...' : buttonLabel}
          </button>
        </form>

        <button
          type="button"
          className="auth-switch-link"
          onClick={onSwitchMode}
          disabled={isSubmitting}
        >
          {helperText}
        </button>
      </section>
    </main>
  )
}

export default AuthPage
