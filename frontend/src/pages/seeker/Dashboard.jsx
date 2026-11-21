import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'
import MatchDial from '../../components/MatchDial'
import JobCard from '../../components/JobCard'

function computeCompletion(p) {
  if (!p) return 0
  let s = 0
  if (p.headline) s += 15
  if (p.summary) s += 15
  if (p.resumeUrl) s += 20
  if (p.currentLocation) s += 10
  if (p.totalExperienceYears != null) s += 10
  if (p.skills?.length) s += Math.min(30, p.skills.length * 6)
  return Math.min(100, s)
}

export default function Dashboard() {
  const { data: profile } = useQuery({
    queryKey: ['seeker-profile'],
    queryFn: () => api.get('/api/seeker/profile').then((r) => r.data)
  })
  const { data: apps } = useQuery({
    queryKey: ['seeker-applications'],
    queryFn: () => api.get('/api/seeker/applications?size=5').then((r) => r.data)
  })
  const { data: latest } = useQuery({
    queryKey: ['latest-jobs'],
    queryFn: () => api.get('/api/jobs/latest').then((r) => r.data)
  })
  const { data: notifications } = useQuery({
    queryKey: ['seeker-notifications'],
    queryFn: () => api.get('/api/seeker/notifications').then((r) => r.data)
  })

  const completion = computeCompletion(profile)

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Command Center</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <HUDCard>
          <div className="hud-label">Profile Strength</div>
          <div className="mt-4 flex justify-center"><MatchDial value={completion} /></div>
        </HUDCard>
        <HUDCard>
          <div className="hud-label">Applications</div>
          <div className="mt-2 text-4xl text-cyan-300">{apps?.totalElements ?? 0}</div>
          <div className="text-xs text-cyan-400/60 mt-1">Total submissions</div>
          <Link to="/seeker/applications" className="hud-btn mt-4 inline-block">View</Link>
        </HUDCard>
        <HUDCard>
          <div className="hud-label">Live Ops</div>
          <div className="mt-2 text-4xl text-cyan-300">{latest?.length ?? 0}</div>
          <div className="text-xs text-cyan-400/60 mt-1">Jobs on the board</div>
          <Link to="/seeker/jobs" className="hud-btn mt-4 inline-block">Explore</Link>
        </HUDCard>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <HUDCard className="md:col-span-2">
          <div className="hud-label mb-4">Recommended Missions</div>
          <div className="space-y-3">
            {latest?.slice(0, 4).map((job) => <JobCard key={job.id} job={job} />)}
            {!latest?.length && <div className="text-cyan-400/50 text-sm">No live jobs yet.</div>}
          </div>
        </HUDCard>
        <HUDCard>
          <div className="hud-label mb-4">Recent Signals</div>
          <div className="space-y-2 max-h-72 overflow-y-auto">
            {notifications?.slice(0, 6).map((n) => (
              <div key={n.id} className="text-xs border-l-2 border-cyan-400/40 pl-2 py-1">
                <div className="text-cyan-200">{n.message}</div>
                <div className="text-[10px] text-cyan-400/50 mt-0.5">{new Date(n.createdAt).toLocaleString()}</div>
              </div>
            ))}
            {!notifications?.length && <div className="text-cyan-400/50 text-xs">No signals yet.</div>}
          </div>
        </HUDCard>
      </div>
    </div>
  )
}
