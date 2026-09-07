import { useEffect, useRef, useState } from 'react'
import ChartsEmbedSDK from '@mongodb-js/charts-embed-dom'
import { fetchChartsEmbedToken } from '../services/assistantApi'
import { getApiErrorMessage } from '../utils/apiError'
import '../styles/atlasChartEmbed.css'

function AtlasChartEmbed({ baseUrl, dashboardId, filter, userId, onOpenLogin }) {
  const containerRef = useRef(null)
  const [status, setStatus] = useState(userId ? 'loading' : 'login')
  const [errorMessage, setErrorMessage] = useState('')
  const filterKey = JSON.stringify(filter ?? {})

  useEffect(() => {
    if (!userId) {
      setStatus('login')
      return
    }

    if (!baseUrl || !dashboardId) {
      setStatus('error')
      setErrorMessage('This report is missing its Atlas Charts dashboard configuration.')
      return
    }

    let cancelled = false
    const container = containerRef.current
    if (!container) {
      return
    }

    container.replaceChildren()
    setStatus('loading')
    setErrorMessage('')

    const sdk = new ChartsEmbedSDK({
      baseUrl,
      getUserToken: () => fetchChartsEmbedToken(userId),
    })

    const dashboard = sdk.createDashboard({
      dashboardId,
      filter: filter && Object.keys(filter).length > 0 ? filter : undefined,
      width: '100%',
      height: '420px',
      background: 'transparent',
      autoRefresh: true,
    })

    dashboard
      .render(container)
      .then(() => {
        if (!cancelled) {
          setStatus('ready')
        }
      })
      .catch((error) => {
        if (!cancelled) {
          setStatus('error')
          setErrorMessage(
            getApiErrorMessage(
              error,
              'Unable to load Atlas Charts. Confirm Charts is configured and you are logged in.'
            )
          )
        }
      })

    return () => {
      cancelled = true
      container.replaceChildren()
    }
  }, [baseUrl, dashboardId, userId, filterKey])

  if (status === 'login') {
    return (
      <div className="atlas-chart-status">
        <p>Log in to view this report.</p>
        {typeof onOpenLogin === 'function' && (
          <button type="button" className="atlas-chart-login-button" onClick={onOpenLogin}>
            Login
          </button>
        )}
      </div>
    )
  }

  return (
    <div className="atlas-chart-embed">
      {status === 'loading' && <p className="atlas-chart-status">Loading report charts...</p>}
      {status === 'error' && <p className="atlas-chart-status atlas-chart-error">{errorMessage}</p>}
      <div ref={containerRef} className="atlas-chart-frame" />
    </div>
  )
}

export default AtlasChartEmbed
