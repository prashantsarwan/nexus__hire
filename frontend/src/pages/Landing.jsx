import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import api from '../api/client'
import JobCard from '../components/JobCard'
import HUDCard from '../components/HUDCard'

export default function Landing() {
  const { data: latest } = useQuery({
    queryKey: ['landing-latest'],
    queryFn: () => api.get('/api/jobs/latest').then((r) => r.data)
  })

  return (
    <div className="min-h-screen">
      <header className="border-b border-cyan-400/20 px-8 py-4 flex justify-between items-center">
        <div>
          <div className="text-cyan-300 text-xl font-bold tracking-[0.3em]">NEXUS</div>
          <div className="text-[10px] uppercase tracking-[0.4em] text-cyan-400/50">Hire Platform</div>
        </div>
        <div className="space-x-3">
          <Link to="/login" className="hud-btn">Login</Link>
          <Link to="/register" className="hud-btn-primary">Get Started</Link>
        </div>
      </header>

      <section className="max-w-6xl mx-auto px-8 py-20 text-center">
        <div className="text-[10px] uppercase tracking-[0.5em] text-cyan-400/60 mb-4">Tactical Hiring Interface</div>
        <h1 className="text-5xl md:text-7xl font-bold text-cyan-200 tracking-widest mb-6" style={{ textShadow: '0 0 30px rgba(68,221,255,0.4)' }}>
          HIRE SMARTER.<br />SHIP FASTER.
        </h1>
        <p className="max-w-2xl mx-auto text-cyan-400/70 mb-8">
          AI-ranked candidates, real-time application tracking, verified skill badges, and a full recruiter command center - all in one sci-fi HUD.
        </p>
        <div className="space-x-3">
          <Link to="/register?role=JOB_SEEKER" className="hud-btn-primary">I'm a Job Seeker</Link>
          <Link to="/register?role=RECRUITER" className="hud-btn">I'm a Recruiter</Link>
        </div>
      </section>

      <section className="max-w-5xl mx-auto px-8 pb-20">
        <HUDCard>
          <div className="hud-label mb-4">Live Opportunities</div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {latest?.slice(0, 6).map((job) => (
              <JobCard key={job.id} job={job} to={`/login`} />
            ))}
            {!latest?.length && <div className="text-cyan-400/50 text-sm">No live jobs yet.</div>}
          </div>
        </HUDCard>
      </section>

      <footer className="text-center text-[10px] uppercase tracking-[0.4em] text-cyan-400/40 pb-8">
        Nexus Hire - Built for the interview room
      </footer>
    </div>
  )
}
