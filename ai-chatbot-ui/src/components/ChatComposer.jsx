function ChatComposer({ question, isLoading, onQuestionChange, onSubmit }) {
  return (
    <form className="composer" onSubmit={onSubmit}>
      <input
        type="text"
        className="message-input"
        placeholder="Ask anything"
        value={question}
        onChange={(event) => onQuestionChange(event.target.value)}
        disabled={isLoading}
        required
      />
      <button
        type="submit"
        className="submit-button"
        disabled={isLoading || !question.trim()}
      >
        {isLoading ? 'Submitting...' : 'Submit'}
      </button>
    </form>
  )
}

export default ChatComposer
