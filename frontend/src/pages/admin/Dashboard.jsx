import { useQuery } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'

export default function AdminDashboard() {
  const { data } = useQuery({
    queryKey: ['admin-stats'],
    queryFn: () => api.get('/api/admin/stats').then((r) => r.data)
  })

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Admin Overview</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <HUDCard><div className="hud-label">Users</div><div className="text-4xl text-cyan-300 mt-2">{data?.totalUsers ?? 0}</div></HUDCard>
        <HUDCard><div className="hud-label">Jobs</div><div className="text-4xl text-cyan-300 mt-2">{data?.totalJobs ?? 0}</div></HUDCard>
        <HUDCard><div className="hud-label">Applications</div><div className="text-4xl text-cyan-300 mt-2">{data?.totalApplications ?? 0}</div></HUDCard>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {['usersByRole', 'jobsByStatus', 'applicationsByStatus'].map((key) => (
          <HUDCard key={key}>
            <div className="hud-label mb-3">{key}</div>
            <div className="space-y-1 text-xs">
              {Object.entries(data?.[key] || {}).map(([k, v]) => (
                <div key={k} className="flex justify-between border-b border-cyan-400/10 py-1">
                  <span className="text-cyan-400/70 uppercase tracking-widest">{k}</span>
                  <span className="text-cyan-200">{v}</span>
                </div>
              ))}
            </div>
          </HUDCard>
        ))}
      </div>
    </div>
  )
}
