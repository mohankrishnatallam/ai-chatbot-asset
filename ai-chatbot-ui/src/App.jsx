import { useState } from 'react'
import AuthPage from './pages/AuthPage'
import HomePage from './pages/HomePage'

function App() {
  const [authMode, setAuthMode] = useState(null)

  return (
    <>
      <HomePage onOpenLogin={() => setAuthMode('login')} />

      {authMode && (
        <AuthPage
          mode={authMode}
          onSubmit={() => setAuthMode(null)}
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
