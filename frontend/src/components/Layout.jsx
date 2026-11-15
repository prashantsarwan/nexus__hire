import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const seekerNav = [
  { to: '/seeker', label: 'Dashboard', end: true },
  { to: '/seeker/jobs', label: 'Jobs' },
  { to: '/seeker/applications', label: 'Applications' },
  { to: '/seeker/saved', label: 'Saved' },
  { to: '/seeker/assessments', label: 'Assessments' },
  { to: '/seeker/profile', label: 'Profile' }
]

const recruiterNav = [
  { to: '/recruiter', label: 'Dashboard', end: true },
  { to: '/recruiter/jobs', label: 'My Jobs' },
  { to: '/recruiter/candidates', label: 'Candidates' },
  { to: '/recruiter/analytics', label: 'Analytics' },
  { to: '/recruiter/company', label: 'Company' }
]

const adminNav = [
  { to: '/admin', label: 'Overview', end: true },
  { to: '/admin/users', label: 'Users' },
  { to: '/admin/jobs', label: 'Jobs' }
]

export default function Layout({ title = 'NEXUS HIRE' }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const nav = user?.role === 'RECRUITER' ? recruiterNav : user?.role === 'ADMIN' ? adminNav : seekerNav

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen flex">
      <aside className="w-56 border-r border-cyan-400/20 bg-[#001122]/60 backdrop-blur-md p-4 hidden md:flex flex-col">
        <div className="mb-6">
          <div className="text-cyan-300 text-xl font-bold tracking-[0.3em]">NEXUS</div>
          <div className="text-[10px] uppercase tracking-[0.4em] text-cyan-400/50">Hire Platform</div>
        </div>
        <nav className="flex-1 space-y-1">
          {nav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) => `hud-nav ${isActive ? 'hud-nav-active' : ''}`}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="mt-4 pt-4 border-t border-cyan-400/20">
          <div className="text-xs text-cyan-200 truncate">{user?.fullName}</div>
          <div className="text-[10px] uppercase tracking-widest text-cyan-400/50">
            {user?.role?.replace('_', ' ')}
          </div>
          <button onClick={handleLogout} className="hud-btn w-full mt-3">Logout</button>
        </div>
      </aside>
      <main className="flex-1 min-w-0">
        <header className="border-b border-cyan-400/20 px-6 py-4 flex items-center justify-between">
          <div>
            <div className="text-[10px] uppercase tracking-[0.4em] text-cyan-400/60">Nexus Command</div>
            <div className="text-lg text-cyan-200 tracking-widest">{title}</div>
          </div>
          <div className="md:hidden">
            <button onClick={handleLogout} className="hud-btn">Logout</button>
          </div>
        </header>
        <div className="p-6">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
