import { useState } from 'react'
import '../styles/authPage.css'

function AuthPage({ mode, onSubmit, onSwitchMode, onBack }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const isLogin = mode === 'login'
  const title = isLogin ? 'Login to continue' : 'Create your account'
  const buttonLabel = isLogin ? 'Login' : 'Register'
  const helperText = isLogin
    ? 'Using the app for the first time? Register here.'
    : 'Already have an account? Login here.'

  const handleSubmit = (event) => {
    event.preventDefault()
    onSubmit({ username, password })
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
              required
            />
          </label>

          <label className="auth-field">
            <span>Password</span>
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </label>

          <button type="submit" className="auth-submit">
            {buttonLabel}
          </button>
        </form>

        <button type="button" className="auth-switch-link" onClick={onSwitchMode}>
          {helperText}
        </button>
      </section>
    </main>
  )
}

export default AuthPage
