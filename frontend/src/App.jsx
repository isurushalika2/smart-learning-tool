import React, { useMemo, useState } from 'react'
import axios from 'axios'

const API = axios.create({ baseURL: import.meta.env.VITE_API_BASE || '' })

const types = [
  { value: 'SHORT_NOTES', label: 'Short Notes' },
  { value: 'CHEAT_SHEET', label: 'Cheat Sheet' },
  { value: 'IMAGE', label: 'Image' },
  { value: 'ANIMATION', label: 'Animation' },
  { value: 'LEETCODE', label: 'LeetCode' },
  { value: 'VOICE_EXPLANATION', label: 'Voice Explanation' },
  { value: 'INTERVIEW_QA', label: 'Interview Q&A' },
]

function JsonBlock({ title, data }) {
  if (!data) return null
  return (
    <div className="card">
      {title && <h3>{title}</h3>}
      <pre className="code">{JSON.stringify(data, null, 2)}</pre>
    </div>
  )
}

function Spinner({ size = 'sm' }) {
  return <span className={`spinner ${size}`} aria-hidden="true" />
}

export default function App() {
  const [tab, setTab] = useState('generate')

  // Generate form state
  const [topic, setTopic] = useState('Java Basics')
  const [type, setType] = useState('SHORT_NOTES')
  const [level, setLevel] = useState('beginner')
  const [language, setLanguage] = useState('en')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')

  // History/Admin state
  const [history, setHistory] = useState([])
  const [hLoading, setHLoading] = useState(false)
  const [hError, setHError] = useState('')

  const [adminMsg, setAdminMsg] = useState('')
  const [adminErr, setAdminErr] = useState('')
  const [adminResp, setAdminResp] = useState(null)
  const [adminLoading, setAdminLoading] = useState(false)

  const [customType, setCustomType] = useState('SAMPLE')
  const [customTopic, setCustomTopic] = useState('DynamoDB connectivity test')
  const [customSummary, setCustomSummary] = useState('This is a custom sample record')
  const [customItems, setCustomItems] = useState('bullet-1\nbullet-2\nbullet-3')
  const [customPayload, setCustomPayload] = useState('{"source":"admin-ui","tags":["sample","history"],"difficulty":"easy"}')

  const generateBody = useMemo(() => ({ type, topic, level, language }), [type, topic, level, language])

  const submitGenerate = async (e) => {
    e?.preventDefault()
    setLoading(true)
    setError('')
    setResult(null)
    try {
      const res = await API.post('/api/generate', generateBody)
      setResult(res.data)
    } catch (err) {
      setError(err.response?.data?.message || err.message)
    } finally {
      setLoading(false)
    }
  }

  const fetchHistory = async () => {
    setHLoading(true)
    setHError('')
    try {
      const res = await API.get('/api/history')
      setHistory(Array.isArray(res.data) ? res.data : [])
    } catch (err) {
      setHError(err.response?.data?.message || err.message)
    } finally {
      setHLoading(false)
    }
  }

  const insertSample = async () => {
    setAdminMsg('')
    setAdminErr('')
    setAdminResp(null)
    setAdminLoading(true)
    try {
      const res = await API.post('/api/history/sample')
      setAdminResp(res.data)
      setAdminMsg('Sample record inserted successfully.')
    } catch (err) {
      setAdminErr(err.response?.data?.message || err.message)
    } finally {
      setAdminLoading(false)
    }
  }

  const insertCustom = async () => {
    setAdminMsg('')
    setAdminErr('')
    setAdminResp(null)
    setAdminLoading(true)
    try {
      const payload = customPayload?.trim() ? JSON.parse(customPayload) : undefined
      const items = customItems?.split('\n').map(s => s.trim()).filter(Boolean)
      const body = { type: customType, topic: customTopic, summary: customSummary, items, payload }
      const res = await API.post('/api/history', body)
      setAdminResp(res.data)
      setAdminMsg('Custom record inserted successfully.')
    } catch (err) {
      if (err instanceof SyntaxError) {
        setAdminErr('Invalid JSON in Payload field.')
      } else {
        setAdminErr(err.response?.data?.message || err.message)
      }
    } finally {
      setAdminLoading(false)
    }
  }

  const adminGenerate = async () => {
    setAdminMsg('')
    setAdminErr('')
    setAdminResp(null)
    setAdminLoading(true)
    try {
      const res = await API.post('/api/generate', generateBody)
      setAdminResp(res.data)
      setAdminMsg('Generate request completed.')
    } catch (err) {
      setAdminErr(err.response?.data?.message || err.message)
    } finally {
      setAdminLoading(false)
    }
  }

  return (
    <div>
      {/* Header */}
      <div className="header">
        <div className="header-inner container">
          <div className="brand">
            <div className="logo" />
            LearningTool
          </div>
          <div className="tabs">
            <button className={`tab ${tab==='generate'?'active':''}`} onClick={() => setTab('generate')}>Generate</button>
            <button className={`tab ${tab==='history'?'active':''}`} onClick={() => setTab('history')}>History</button>
            <button className={`tab ${tab==='admin'?'active':''}`} onClick={() => setTab('admin')}>Admin</button>
          </div>
        </div>
      </div>

      <div className="container content">
        {/* Generate */}
        {tab==='generate' && (
          <div className="grid grid-2">
            <div className="card">
              <h2>Generate Learning Content</h2>
              <p className="muted">Fill the form and click Generate to create content. Results also save to history.</p>
              <form onSubmit={submitGenerate} className="row">
                <label>
                  <span className="label">Topic</span>
                  <input className="input" value={topic} onChange={(e) => setTopic(e.target.value)} placeholder="e.g., Java Basics" />
                </label>
                <div className="row row-2">
                  <label>
                    <span className="label">Type</span>
                    <select className="select" value={type} onChange={(e) => setType(e.target.value)}>
                      {types.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select>
                  </label>
                  <label>
                    <span className="label">Level</span>
                    <select className="select" value={level} onChange={(e) => setLevel(e.target.value)}>
                      <option>beginner</option>
                      <option>intermediate</option>
                      <option>advanced</option>
                    </select>
                  </label>
                </div>
                <label>
                  <span className="label">Language</span>
                  <select className="select" value={language} onChange={(e) => setLanguage(e.target.value)}>
                    <option value="en">English</option>
                    <option value="es">Spanish</option>
                    <option value="fr">French</option>
                    <option value="de">German</option>
                    <option value="si">Sinhala</option>
                    <option value="ta">Tamil</option>
                  </select>
                </label>
                <div>
                  <button className="button" disabled={loading || !topic.trim()} type="submit">
                    {loading && <Spinner size="sm" />} <span>{loading ? 'Generating…' : 'Generate'}</span>
                  </button>
                </div>
              </form>
            </div>
            <div className="card">
              <h2>Result</h2>
              {error && <div className="alert error">{error}</div>}
              {!result && !error && <div className="muted">No result yet. Submit the form to see output.</div>}
              {result && (
                <div className="row">
                  <div>
                    <strong>{result.type}</strong> — {result.topic}
                  </div>
                  {result.summary && <div>{result.summary}</div>}
                  {Array.isArray(result.items) && result.items.length > 0 && (
                    <ul className="list">
                      {result.items.map((i, idx) => <li key={idx}>{i}</li>)}
                    </ul>
                  )}
                  {result.payload?.imageUrls && (
                    <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
                      {result.payload.imageUrls.map((u, idx) => <img key={idx} src={u} alt={`img-${idx}`} style={{ maxWidth: 300, borderRadius: 10 }} />)}
                    </div>
                  )}
                  {result.payload?.qa && (
                    <div>
                      <h3>Interview Q&A</h3>
                      <ul className="list">
                        {result.payload.qa.map((qa, idx) => <li key={idx}><strong>Q:</strong> {qa.q} <br /><strong>A:</strong> {qa.a}</li>)}
                      </ul>
                    </div>
                  )}
                  {result.payload?.problems && (
                    <div>
                      <h3>Practice Problems</h3>
                      <ul className="list">
                        {result.payload.problems.map((p, idx) => <li key={idx}>{p.title} — {p.difficulty}</li>)}
                      </ul>
                    </div>
                  )}
                  <JsonBlock title="Raw JSON" data={result} />
                </div>
              )}
            </div>
          </div>
        )}

        {/* History */}
        {tab==='history' && (
          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12 }}>
              <h2>History</h2>
              <div>
                <button className="button secondary" onClick={fetchHistory} disabled={hLoading}>
                  {hLoading && <Spinner size="sm" />} <span>{hLoading ? 'Loading…' : 'Refresh'}</span>
                </button>
              </div>
            </div>
            {hError && <div className="alert error">{hError}</div>}
            {!history.length && !hError && <div className="muted">No items yet. Generate or insert samples to populate history.</div>}
            <div className="row" style={{ marginTop: 8 }}>
              {history.map((item, idx) => (
                <div key={idx} className="card">
                  <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
                    <div><strong>{item.type || 'UNKNOWN'}</strong> — {item.topic || '-'}</div>
                    <span className="kbd">#{idx+1}</span>
                  </div>
                  {item.summary && <div style={{ marginTop: 6 }}>{item.summary}</div>}
                  {Array.isArray(item.items) && item.items.length > 0 && (
                    <ul className="list">
                      {item.items.map((i, iidx) => <li key={iidx}>{i}</li>)}
                    </ul>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Admin */}
        {tab==='admin' && (
          <div className="grid">
            <div className="card">
              <h2>Admin Actions</h2>
              <div className="row row-2">
                <button className="button" onClick={insertSample} disabled={adminLoading}>
                  {adminLoading && <Spinner size="sm" />} <span>Insert Sample (POST /api/history/sample)</span>
                </button>
                <button className="button warn" onClick={fetchHistory} disabled={hLoading}>
                  {hLoading && <Spinner size="sm" />} <span>Fetch History (GET /api/history)</span>
                </button>
              </div>
              <div className="row" style={{ marginTop: 12 }}>
                <h3>Insert Custom History (POST /api/history)</h3>
                <div className="row row-2">
                  <label>
                    <span className="label">Type</span>
                    <input className="input" value={customType} onChange={(e) => setCustomType(e.target.value)} />
                  </label>
                  <label>
                    <span className="label">Topic</span>
                    <input className="input" value={customTopic} onChange={(e) => setCustomTopic(e.target.value)} />
                  </label>
                </div>
                <label>
                  <span className="label">Summary</span>
                  <input className="input" value={customSummary} onChange={(e) => setCustomSummary(e.target.value)} />
                </label>
                <label>
                  <span className="label">Items (one per line)</span>
                  <textarea className="textarea" value={customItems} onChange={(e) => setCustomItems(e.target.value)} />
                </label>
                <label>
                  <span className="label">Payload (JSON)</span>
                  <textarea className="textarea" value={customPayload} onChange={(e) => setCustomPayload(e.target.value)} />
                </label>
                <div>
                  <button className="button" onClick={insertCustom} disabled={adminLoading}>
                    {adminLoading && <Spinner size="sm" />} <span>Insert Custom</span>
                  </button>
                </div>
              </div>

              <div className="row" style={{ marginTop: 10 }}>
                <h3>Trigger Generate (POST /api/generate)</h3>
                <div className="row row-2">
                  <label>
                    <span className="label">Topic</span>
                    <input className="input" value={topic} onChange={(e) => setTopic(e.target.value)} />
                  </label>
                  <label>
                    <span className="label">Type</span>
                    <select className="select" value={type} onChange={(e) => setType(e.target.value)}>
                      {types.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select>
                  </label>
                </div>
                <div className="row row-2">
                  <label>
                    <span className="label">Level</span>
                    <select className="select" value={level} onChange={(e) => setLevel(e.target.value)}>
                      <option>beginner</option>
                      <option>intermediate</option>
                      <option>advanced</option>
                    </select>
                  </label>
                  <label>
                    <span className="label">Language</span>
                    <select className="select" value={language} onChange={(e) => setLanguage(e.target.value)}>
                      <option value="en">English</option>
                      <option value="es">Spanish</option>
                      <option value="fr">French</option>
                      <option value="de">German</option>
                      <option value="si">Sinhala</option>
                      <option value="ta">Tamil</option>
                    </select>
                  </label>
                </div>
                <div>
                  <button className="button" onClick={adminGenerate} disabled={adminLoading}>
                    {adminLoading && <Spinner size="sm" />} <span>Run Generate</span>
                  </button>
                </div>
              </div>

              {adminErr && <div className="alert error" style={{ marginTop: 12 }}>{adminErr}</div>}
              {adminMsg && <div className="alert success" style={{ marginTop: 12 }}>{adminMsg}</div>}
            </div>

            <JsonBlock title="Admin Response" data={adminResp} />
          </div>
        )}

        <div className="footer">Backend: Spring Boot (http://localhost:8080) • Frontend: Vite React (http://localhost:5173) • API Base: {API.defaults.baseURL || '(same origin)'}</div>
      </div>
    </div>
  )
}
