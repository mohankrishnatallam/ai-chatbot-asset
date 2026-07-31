import { useState } from 'react'
import AuthPage from './pages/AuthPage'
import HomePage from './pages/HomePage'
import { login, register } from './services/authApi'
import {
  clearStoredAuthUser,
  getStoredAuthUser,
  storeAuthUser,
} from './utils/authUser'
import {
  clearSessionId,
  createNewSessionId,
  getOrCreateSessionId,
  resumeSessionId,
} from './utils/sessionId'

function App() {
  const [authMode, setAuthMode] = useState(null)
  const [authUser, setAuthUser] = useState(() => getStoredAuthUser())
  const [sessionId, setSessionId] = useState(() => {
    const storedUser = getStoredAuthUser()
    return getOrCreateSessionId(storedUser?.userId ?? null)
  })
  const [prevAuthUserId, setPrevAuthUserId] = useState(authUser?.userId ?? null)

  const currentAuthUserId = authUser?.userId ?? null
  if (currentAuthUserId !== prevAuthUserId) {
    setPrevAuthUserId(currentAuthUserId)
    setSessionId(getOrCreateSessionId(currentAuthUserId))
  }

  const handleAuthSubmit = async ({ username, password }) => {
    const authAction = authMode === 'login' ? login : register
    const result = await authAction(username, password)

    const user = {
      userId: result.userId,
      username: result.username,
    }

    const newSessionId = createNewSessionId(user.userId)

    storeAuthUser(user)
    setAuthUser(user)
    setSessionId(newSessionId)
    setAuthMode(null)
  }

  const handleLogout = () => {
    if (authUser?.userId) {
      clearSessionId(authUser.userId)
    }

    clearStoredAuthUser()
    setAuthUser(null)
    setSessionId(getOrCreateSessionId(null))
  }

  const handleCurrentSessionDeleted = () => {
    if (authUser?.userId) {
      setSessionId(createNewSessionId(authUser.userId))
    }
  }

  const handleContinueSession = (sessionIdToResume) => {
    if (!authUser?.userId || !sessionIdToResume) {
      return
    }

    resumeSessionId(authUser.userId, sessionIdToResume)
    setSessionId(sessionIdToResume)
  }

  return (
    <>
      <HomePage
        authUser={authUser}
        sessionId={sessionId}
        onOpenLogin={() => setAuthMode('login')}
        onLogout={handleLogout}
        onCurrentSessionDeleted={handleCurrentSessionDeleted}
        onContinueSession={handleContinueSession}
      />

      {authMode && (
        <AuthPage
          mode={authMode}
          onSubmit={handleAuthSubmit}
          onSwitchMode={() =>
            setAuthMode((previous) => (previous === 'login' ? 'register' : 'login'))
          }
          onBack={() => setAuthMode(null)}
        />
      )}
    </>
  )
}

export default App
