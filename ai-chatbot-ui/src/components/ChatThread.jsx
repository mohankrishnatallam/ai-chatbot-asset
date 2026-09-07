import AtlasChartEmbed from './AtlasChartEmbed'

function ChatThread({
  question,
  answer,
  data,
  userId,
  onOpenLogin,
  onClick,
  onQuestionClick,
  className = '',
}) {
  const isThreadInteractive = typeof onClick === 'function'
  const isPromptInteractive = typeof onQuestionClick === 'function'
  const embed = data?.embed

  return (
    <article
      className={`chat-thread ${isThreadInteractive ? 'chat-thread-interactive' : ''} ${className}`.trim()}
      onClick={isThreadInteractive ? onClick : undefined}
      onKeyDown={
        isThreadInteractive
          ? (event) => {
              if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault()
                onClick()
              }
            }
          : undefined
      }
      role={isThreadInteractive ? 'button' : undefined}
      tabIndex={isThreadInteractive ? 0 : undefined}
      title={isThreadInteractive ? 'Continue this conversation from Home' : undefined}
    >
      <p className="chat-label">You</p>
      <p
        className={`chat-bubble user-bubble ${isPromptInteractive ? 'user-bubble-interactive' : ''}`.trim()}
        onClick={
          isPromptInteractive
            ? (event) => {
                event.stopPropagation()
                onQuestionClick()
              }
            : undefined
        }
        onKeyDown={
          isPromptInteractive
            ? (event) => {
                if (event.key === 'Enter' || event.key === ' ') {
                  event.preventDefault()
                  event.stopPropagation()
                  onQuestionClick()
                }
              }
            : undefined
        }
        role={isPromptInteractive ? 'button' : undefined}
        tabIndex={isPromptInteractive ? 0 : undefined}
        title={isPromptInteractive ? 'Continue chat from this prompt' : undefined}
      >
        {question}
      </p>

      <p className="chat-label">Assistant</p>
      <p className="chat-bubble assistant-bubble">{answer}</p>
      {embed?.dashboardId && embed?.baseUrl ? (
        <AtlasChartEmbed
          baseUrl={embed.baseUrl}
          dashboardId={embed.dashboardId}
          filter={embed.filter}
          userId={userId}
          onOpenLogin={onOpenLogin}
        />
      ) : null}
    </article>
  )
}

export default ChatThread
