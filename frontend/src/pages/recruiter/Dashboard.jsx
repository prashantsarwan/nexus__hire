import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'

export default function RecruiterDashboard() {
  const { data: analytics } = useQuery({
    queryKey: ['recruiter-analytics'],
    queryFn: () => api.get('/api/recruiter/analytics').then((r) => r.data)
  })
  const { data: profile } = useQuery({
    queryKey: ['recruiter-profile'],
    queryFn: () => api.get('/api/recruiter/profile').then((r) => r.data)
  })
  const { data: interviews } = useQuery({
    queryKey: ['recruiter-interviews'],
    queryFn: () => api.get('/api/recruiter/interviews').then((r) => r.data)
  })

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Recruiter Command</h1>

      {profile && !profile.company && (
        <HUDCard className="border-amber-400/40">
          <div className="text-amber-300 text-sm mb-2">Company profile not created.</div>
          <Link to="/recruiter/company" className="hud-btn">Create Company</Link>
        </HUDCard>
      )}

      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <HUDCard><div className="hud-label">Total Jobs</div><div className="text-3xl text-cyan-300 mt-2">{analytics?.totalJobs ?? 0}</div></HUDCard>
        <HUDCard><div className="hud-label">Active</div><div className="text-3xl text-emerald-300 mt-2">{analytics?.activeJobs ?? 0}</div></HUDCard>
        <HUDCard><div className="hud-label">Applications</div><div className="text-3xl text-cyan-300 mt-2">{analytics?.totalApplications ?? 0}</div></HUDCard>
        <HUDCard><div className="hud-label">Hired</div><div className="text-3xl text-green-300 mt-2">{analytics?.hiredCount ?? 0}</div></HUDCard>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <HUDCard>
          <div className="hud-label mb-3">Pipeline Snapshot</div>
          <div className="space-y-1 text-xs">
            {Object.entries(analytics?.applicationsByStatus || {}).map(([k, v]) => (
              <div key={k} className="flex justify-between border-b border-cyan-400/10 py-1">
                <span className="text-cyan-400/70 uppercase tracking-widest">{k}</span>
                <span className="text-cyan-200">{v}</span>
              </div>
            ))}
            {!analytics?.applicationsByStatus && <div className="text-cyan-400/50">No data yet.</div>}
          </div>
        </HUDCard>
        <HUDCard>
          <div className="hud-label mb-3">Upcoming Interviews</div>
          <div className="space-y-2 max-h-64 overflow-y-auto">
            {interviews?.slice(0, 5).map((i) => (
              <div key={i.id} className="text-xs border-l-2 border-amber-400/40 pl-2 py-1">
                <div className="text-cyan-200">{i.seekerName} - {i.jobTitle}</div>
                <div className="text-[10px] text-cyan-400/50">{new Date(i.scheduledAt).toLocaleString()}</div>
              </div>
            ))}
            {!interviews?.length && <div className="text-cyan-400/50 text-xs">No interviews scheduled.</div>}
          </div>
        </HUDCard>
      </div>
    </div>
  )
}
