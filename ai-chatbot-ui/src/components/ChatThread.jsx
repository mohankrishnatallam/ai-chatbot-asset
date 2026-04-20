function ChatThread({ question, answer }) {
  return (
    <article className="chat-thread">
      <p className="chat-label">You</p>
      <p className="chat-bubble user-bubble">{question}</p>

      <p className="chat-label">Assistant</p>
      <p className="chat-bubble assistant-bubble">{answer}</p>
    </article>
  )
}

export default ChatThread
