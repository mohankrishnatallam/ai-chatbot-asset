import { useState } from 'react'
import chatbotGif from '../assets/chatbot-gif.gif'
import ChatComposer from '../components/ChatComposer'
import ChatThread from '../components/ChatThread'
import { fetchAssistantResponse } from '../services/assistantApi'
import '../styles/homePage.css'

const navigationItems = [
  { label: 'Home', icon: '⌂', active: true },
  { label: 'History', icon: '🕘', active: false },
  { label: 'Saved Prompts', icon: '✦', active: false },
  { label: 'Settings', icon: '⚙', active: false },
]

function HomePage({ onOpenLogin }) {
  const [question, setQuestion] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [conversations, setConversations] = useState([])

  const handleSubmit = async (event) => {
    event.preventDefault()

    const trimmedQuestion = question.trim()
    if (!trimmedQuestion || isLoading) {
      return
    }

    setIsLoading(true)

    try {
      const answerText = await fetchAssistantResponse(trimmedQuestion)

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

  return (
    <main className="app-shell">
      <aside className="left-rail">
        <button type="button" className="login-button" onClick={onOpenLogin}>
          Login
        </button>

        <nav className="sidebar-nav" aria-label="Primary">
          {navigationItems.map((item) => (
            <button
              key={item.label}
              type="button"
              className={`nav-item ${item.active ? 'nav-item-active' : ''}`}
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
      </section>
    </main>
  )
}

export default HomePage
