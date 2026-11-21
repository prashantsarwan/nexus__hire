import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import HUDCard from '../components/HUDCard'

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    password: '',
    role: params.get('role') === 'RECRUITER' ? 'RECRUITER' : 'JOB_SEEKER'
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await register(form)
      if (user.role === 'JOB_SEEKER') navigate('/seeker')
      else navigate('/recruiter')
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <HUDCard className="w-full max-w-md">
        <div className="hud-label mb-1">New Operative</div>
        <h1 className="text-2xl text-cyan-200 tracking-widest mb-6">REGISTER</h1>
        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label className="hud-label">Full Name</label>
            <input className="hud-input mt-1" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} required />
          </div>
          <div>
            <label className="hud-label">Email</label>
            <input className="hud-input mt-1" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          </div>
          <div>
            <label className="hud-label">Password</label>
            <input className="hud-input mt-1" type="password" minLength={6} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
          </div>
          <div>
            <label className="hud-label">Role</label>
            <select className="hud-input mt-1" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
              <option value="JOB_SEEKER">Job Seeker</option>
              <option value="RECRUITER">Recruiter</option>
            </select>
          </div>
          {error && <div className="text-red-400 text-xs">{error}</div>}
          <button type="submit" disabled={loading} className="hud-btn-primary w-full">
            {loading ? 'Creating...' : 'Initialize Profile'}
          </button>
        </form>
        <div className="text-xs text-cyan-400/60 mt-4 text-center">
          Already registered? <Link to="/login" className="text-cyan-300 underline">Login</Link>
        </div>
      </HUDCard>
    </div>
  )
}
