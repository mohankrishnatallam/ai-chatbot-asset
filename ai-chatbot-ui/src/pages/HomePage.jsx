import { useEffect, useState } from 'react'
import chatbotGif from '../assets/chatbot-gif.gif'
import ChatComposer from '../components/ChatComposer'
import ChatThread from '../components/ChatThread'
import SessionHistoryPanel from '../components/SessionHistoryPanel'
import SavedPromptsPanel from '../components/SavedPromptsPanel'
import {
  fetchAssistantResponse,
  fetchSessionHistory,
} from '../services/assistantApi'
import '../styles/homePage.css'

const navigationItems = [
  { id: 'home', label: 'Home', icon: '⌂' },
  { id: 'history', label: 'History', icon: '🕘' },
  { id: 'saved', label: 'Saved Prompts', icon: '✦' },
  { id: 'settings', label: 'Settings', icon: '⚙' },
]

function HomePage({ authUser, sessionId, onOpenLogin, onLogout, onCurrentSessionDeleted }) {
  const [activeView, setActiveView] = useState('home')
  const [question, setQuestion] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [conversations, setConversations] = useState([])

  useEffect(() => {
    if (!sessionId || activeView !== 'home') {
      return
    }

    let isMounted = true

    const loadCurrentSession = async () => {
      try {
        const history = await fetchSessionHistory(sessionId, authUser?.userId)
        if (isMounted) {
          setConversations(history)
        }
      } catch {
        if (isMounted) {
          setConversations([])
        }
      }
    }

    loadCurrentSession()

    return () => {
      isMounted = false
    }
  }, [sessionId, activeView, authUser?.userId])

  const handleSubmit = async (event) => {
    event.preventDefault()

    const trimmedQuestion = question.trim()
    if (!trimmedQuestion || isLoading || !sessionId) {
      return
    }

    setIsLoading(true)

    try {
      const answerText = await fetchAssistantResponse(
        trimmedQuestion,
        sessionId,
        authUser?.userId
      )

      setConversations((previous) => [
        ...previous,
        { question: trimmedQuestion, answer: answerText },
      ])
      setQuestion('')
    } catch {
      setConversations((previous) => [
        ...previous,
        {
          question: trimmedQuestion,
          answer:
            'Unable to fetch a response from backend. Please verify the backend is running and then retry.',
        },
      ])
    } finally {
      setIsLoading(false)
    }
  }

  const handleNavClick = (itemId) => {
    if ((itemId === 'history' || itemId === 'saved') && !authUser) {
      onOpenLogin()
      return
    }

    if (itemId === 'home' || itemId === 'history' || itemId === 'saved') {
      setActiveView(itemId)
    }
  }

  const handleUsePrompt = (promptText) => {
    setQuestion(promptText)
    setActiveView('home')
  }

  return (
    <main className="app-shell">
      <aside className="left-rail">
        {authUser ? (
          <div className="user-panel">
            <span className="user-greeting">Hi, {authUser.username}</span>
            <button type="button" className="logout-button" onClick={onLogout}>
              Logout
            </button>
          </div>
        ) : (
          <button type="button" className="login-button" onClick={onOpenLogin}>
            Login
          </button>
        )}

        <nav className="sidebar-nav" aria-label="Primary">
          {navigationItems.map((item) => (
            <button
              key={item.id}
              type="button"
              className={`nav-item ${activeView === item.id ? 'nav-item-active' : ''}`}
              onClick={() => handleNavClick(item.id)}
            >
              <span className="nav-icon" aria-hidden="true">
                {item.icon}
              </span>
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
      </aside>

      <section className="chat-card">
        {activeView === 'history' && authUser ? (
          <SessionHistoryPanel
            userId={authUser.userId}
            currentSessionId={sessionId}
            onBackToHome={() => setActiveView('home')}
            onCurrentSessionDeleted={onCurrentSessionDeleted}
          />
        ) : activeView === 'saved' && authUser ? (
          <SavedPromptsPanel
            userId={authUser.userId}
            onBackToHome={() => setActiveView('home')}
            onUsePrompt={handleUsePrompt}
          />
        ) : (
          <>
            <img src={chatbotGif} alt="Chatbot illustration" className="hero-image" />
            <h1 className="chat-title">Hey! Ready to dive in?</h1>

            {conversations.map((item, index) => (
              <ChatThread
                key={`${item.question}-${index}`}
                question={item.question}
                answer={item.answer}
              />
            ))}

            <ChatComposer
              question={question}
              isLoading={isLoading}
              onQuestionChange={setQuestion}
              onSubmit={handleSubmit}
            />
          </>
        )}
      </section>
    </main>
  )
}

export default HomePage
