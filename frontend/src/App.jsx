import React, { useState } from 'react'
import axios from 'axios'

const types = [
  { value: 'SHORT_NOTES', label: 'Short Notes' },
  { value: 'CHEAT_SHEET', label: 'Cheat Sheet' },
  { value: 'IMAGE', label: 'Image' },
  { value: 'ANIMATION', label: 'Animation' },
  { value: 'LEETCODE', label: 'LeetCode' },
  { value: 'VOICE_EXPLANATION', label: 'Voice Explanation' },
  { value: 'INTERVIEW_QA', label: 'Interview Q&A' },
]

export default function App() {
  const [topic, setTopic] = useState('Java Basics')
  const [type, setType] = useState('SHORT_NOTES')
  const [level, setLevel] = useState('beginner')
  const [language, setLanguage] = useState('en')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')

  const submit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setResult(null)
    try {
      const res = await axios.post('/api/generate', { type, topic, level, language })
      setResult(res.data)
    } catch (err) {
      setError(err.response?.data?.message || err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ fontFamily: 'system-ui, sans-serif', padding: 20, maxWidth: 900, margin: '0 auto' }}>
      <h1>Learning Tool</h1>
      <p>Generate learning assets for any technology or programming language.</p>
      <form onSubmit={submit} style={{ display: 'grid', gap: 12 }}>
        <label>
          Topic
          <input value={topic} onChange={(e) => setTopic(e.target.value)} placeholder="e.g., Java Basics" style={{ width: '100%', padding: 8 }} />
        </label>
        <label>
          Type
          <select value={type} onChange={(e) => setType(e.target.value)} style={{ width: '100%', padding: 8 }}>
            {types.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
          </select>
        </label>
        <div style={{ display: 'flex', gap: 12 }}>
          <label style={{ flex: 1 }}>
            Level
            <select value={level} onChange={(e) => setLevel(e.target.value)} style={{ width: '100%', padding: 8 }}>
              <option>beginner</option>
              <option>intermediate</option>
              <option>advanced</option>
            </select>
          </label>
          <label style={{ flex: 1 }}>
            Language
            <select value={language} onChange={(e) => setLanguage(e.target.value)} style={{ width: '100%', padding: 8 }}>
              <option value="en">English</option>
              <option value="es">Spanish</option>
              <option value="fr">French</option>
              <option value="de">German</option>
              <option value="si">Sinhala</option>
              <option value="ta">Tamil</option>
            </select>
          </label>
        </div>
        <button disabled={loading || !topic.trim()} type="submit" style={{ padding: '10px 16px' }}>{loading ? 'Generating...' : 'Generate'}</button>
      </form>

      {error && <p style={{ color: 'red' }}>Error: {error}</p>}

      {result && (
        <div style={{ marginTop: 24 }}>
          <h2>Result: {result.type} - {result.topic}</h2>
          {result.summary && <p>{result.summary}</p>}
          {Array.isArray(result.items) && result.items.length > 0 && (
            <ul>
              {result.items.map((i, idx) => <li key={idx}>{i}</li>)}
            </ul>
          )}
          {result.payload?.imageUrls && (
            <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
              {result.payload.imageUrls.map((u, idx) => <img key={idx} src={u} alt={`img-${idx}`} style={{ maxWidth: 300 }} />)}
            </div>
          )}
          {result.payload?.qa && (
            <div>
              <h3>Interview Q&A</h3>
              <ul>
                {result.payload.qa.map((qa, idx) => <li key={idx}><strong>Q:</strong> {qa.q} <br /><strong>A:</strong> {qa.a}</li>)}
              </ul>
            </div>
          )}
          {result.payload?.problems && (
            <div>
              <h3>Practice Problems</h3>
              <ul>
                {result.payload.problems.map((p, idx) => <li key={idx}>{p.title} - {p.difficulty}</li>)}
              </ul>
            </div>
          )}
        </div>
      )}

      <footer style={{ marginTop: 40, opacity: 0.7 }}>
        <p>Backend: Spring Boot (http://localhost:8080), Frontend: Vite React (http://localhost:5173)</p>
      </footer>
    </div>
  )
}
