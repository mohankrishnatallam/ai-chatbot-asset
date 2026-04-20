import { useState } from 'react'
import ChatComposer from '../components/ChatComposer'
import ChatThread from '../components/ChatThread'
import { fetchAssistantResponse } from '../services/assistantApi'
import '../styles/homePage.css'

function HomePage() {
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
      <section className="chat-card">
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
