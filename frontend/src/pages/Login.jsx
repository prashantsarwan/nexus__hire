import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import HUDCard from '../components/HUDCard'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await login(email, password)
      if (user.role === 'JOB_SEEKER') navigate('/seeker')
      else if (user.role === 'RECRUITER') navigate('/recruiter')
      else navigate('/admin')
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <HUDCard className="w-full max-w-md">
        <div className="hud-label mb-1">Nexus Hire</div>
        <h1 className="text-2xl text-cyan-200 tracking-widest mb-6">AUTHENTICATE</h1>
        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label className="hud-label">Email</label>
            <input className="hud-input mt-1" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
          <div>
            <label className="hud-label">Password</label>
            <input className="hud-input mt-1" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
          {error && <div className="text-red-400 text-xs">{error}</div>}
          <button type="submit" disabled={loading} className="hud-btn-primary w-full">
            {loading ? 'Connecting...' : 'Enter Console'}
          </button>
        </form>
        <div className="text-xs text-cyan-400/60 mt-4 text-center">
          No account? <Link to="/register" className="text-cyan-300 underline">Register</Link>
        </div>
        <div className="text-[10px] text-cyan-400/40 mt-6 leading-relaxed">
          Demo: seeker@nexus.dev / recruiter@nexus.dev / admin@nexus.dev<br />Password: Password@123
        </div>
      </HUDCard>
    </div>
  )
}
