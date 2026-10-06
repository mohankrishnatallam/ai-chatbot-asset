import ReactECharts from 'echarts-for-react'

const colors = ['#6366f1', '#06b6d4', '#f97316', '#ec4899', '#22c55e', '#eab308']

function buildCartesianOption(chart) {
  const isLine = chart.type === 'line'

  return {
    color: colors,
    title: {
      text: chart.title,
      left: 'center',
      textStyle: { color: '#1e1b4b', fontSize: 16, fontWeight: 600 },
    },
    tooltip: { trigger: 'axis', axisPointer: { type: isLine ? 'line' : 'shadow' } },
    legend: { top: 30 },
    grid: { top: 72, right: 24, bottom: 42, left: 52, containLabel: true },
    xAxis: {
      type: 'category',
      data: chart.labels || [],
      axisLine: { lineStyle: { color: '#cbd5e1' } },
      axisLabel: { color: '#475569' },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#475569' },
      splitLine: { lineStyle: { color: '#e2e8f0' } },
    },
    series: (chart.series || []).map((series, index) => ({
      ...series,
      type: chart.type,
      smooth: isLine,
      symbolSize: isLine ? 8 : undefined,
      barMaxWidth: 48,
      itemStyle: {
        color: colors[index % colors.length],
        borderRadius: isLine ? 4 : [8, 8, 0, 0],
      },
      lineStyle: isLine ? { width: 3 } : undefined,
      areaStyle: isLine ? { opacity: 0.12 } : undefined,
    })),
  }
}

function buildPieOption(chart) {
  const series = chart.series?.[0]
  const values = series?.data || []
  const data = (chart.labels || []).map((name, index) => ({
    name,
    value: values[index] ?? 0,
  }))

  return {
    color: colors,
    title: {
      text: chart.title,
      left: 'center',
      textStyle: { color: '#1e1b4b', fontSize: 16, fontWeight: 600 },
    },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { type: 'scroll', bottom: 0, left: 'center' },
    series: [
      {
        name: series?.name || chart.title,
        type: 'pie',
        radius: chart.type === 'donut' ? ['42%', '68%'] : ['0%', '68%'],
        center: ['50%', '52%'],
        data,
        itemStyle: { borderColor: '#ffffff', borderWidth: 3, borderRadius: 6 },
        label: { formatter: '{b}\n{d}%' },
        emphasis: { scale: true, scaleSize: 8 },
      },
    ],
  }
}

function ChartMessage({ chart }) {
  if (!chart || !['bar', 'line', 'pie', 'donut'].includes(chart.type)) {
    return null
  }

  const option =
    chart.type === 'pie' || chart.type === 'donut'
      ? buildPieOption(chart)
      : buildCartesianOption(chart)

  return (
    <div className="chat-chart" role="img" aria-label={chart.title || `${chart.type} chart`}>
      <ReactECharts option={option} style={{ width: '100%', height: 340 }} />
    </div>
  )
}

export default ChartMessage
