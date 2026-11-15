import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ProtectedRoute({ roles }) {
  const { user, loading } = useAuth()
  if (loading) {
    return <div className="min-h-screen flex items-center justify-center text-cyan-400/60">Initializing session...</div>
  }
  if (!user) return <Navigate to="/login" replace />
  if (roles && !roles.includes(user.role)) {
    const target = user.role === 'JOB_SEEKER' ? '/seeker' : user.role === 'RECRUITER' ? '/recruiter' : '/admin'
    return <Navigate to={target} replace />
  }
  return <Outlet />
}
