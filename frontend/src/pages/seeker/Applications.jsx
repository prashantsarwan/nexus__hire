import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'
import { formatDate, statusClass } from '../../utils/format'

export default function Applications() {
  const { data, isLoading } = useQuery({
    queryKey: ['seeker-applications-full'],
    queryFn: () => api.get('/api/seeker/applications?size=50&sort=appliedAt,desc').then((r) => r.data)
  })

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Application Log</h1>
      {isLoading && <div className="text-cyan-400/50 text-sm">Loading...</div>}
      {data && data.content.length === 0 && (
        <HUDCard>
          <div className="text-cyan-400/60 text-sm">
            No applications yet. <Link to="/seeker/jobs" className="text-cyan-300 underline">Find a mission</Link>.
          </div>
        </HUDCard>
      )}
      <div className="space-y-3">
        {data?.content?.map((app) => (
          <HUDCard key={app.id}>
            <div className="flex justify-between items-start gap-3 flex-wrap">
              <div>
                <Link to={`/seeker/jobs/${app.jobId}`} className="text-cyan-200 font-bold hover:underline">
                  {app.jobTitle}
                </Link>
                <div className="text-xs text-cyan-400/60">{app.companyName}</div>
                <div className="text-[10px] text-cyan-400/40 mt-1">Applied {formatDate(app.appliedAt)}</div>
              </div>
              <div className="flex items-center gap-3">
                {app.matchScore != null && (
                  <div className="text-xs text-cyan-300">
                    Match: <span className="font-bold">{Math.round(app.matchScore)}%</span>
                  </div>
                )}
                <span className={`px-2 py-1 text-[10px] uppercase tracking-widest border rounded ${statusClass(app.status)}`}>
                  {app.status}
                </span>
              </div>
            </div>
          </HUDCard>
        ))}
      </div>
    </div>
  )
}
