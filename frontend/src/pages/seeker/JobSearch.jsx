import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import api from '../../api/client'
import JobCard from '../../components/JobCard'
import HUDCard from '../../components/HUDCard'

export default function JobSearch() {
  const [filters, setFilters] = useState({ keyword: '', location: '', remote: false, minSalary: '' })
  const [applied, setApplied] = useState(filters)

  const { data, isLoading } = useQuery({
    queryKey: ['jobs', applied],
    queryFn: () =>
      api
        .get('/api/jobs', {
          params: {
            keyword: applied.keyword || undefined,
            location: applied.location || undefined,
            remote: applied.remote || undefined,
            minSalary: applied.minSalary || undefined,
            size: 20
          }
        })
        .then((r) => r.data)
  })

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Job Scanner</h1>
      <HUDCard>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <input className="hud-input" placeholder="Keyword..." value={filters.keyword} onChange={(e) => setFilters({ ...filters, keyword: e.target.value })} />
          <input className="hud-input" placeholder="Location..." value={filters.location} onChange={(e) => setFilters({ ...filters, location: e.target.value })} />
          <input className="hud-input" type="number" placeholder="Min salary (INR)..." value={filters.minSalary} onChange={(e) => setFilters({ ...filters, minSalary: e.target.value })} />
          <div className="flex gap-2 items-center">
            <label className="flex items-center gap-2 text-[10px] uppercase tracking-widest text-cyan-400/70">
              <input type="checkbox" checked={filters.remote} onChange={(e) => setFilters({ ...filters, remote: e.target.checked })} /> Remote
            </label>
            <button className="hud-btn-primary flex-1" onClick={() => setApplied(filters)}>Scan</button>
          </div>
        </div>
      </HUDCard>
      <div className="space-y-3">
        {isLoading && <div className="text-cyan-400/50 text-sm">Scanning frequencies...</div>}
        {data?.content?.map((job) => <JobCard key={job.id} job={job} />)}
        {data && data.content.length === 0 && <div className="text-cyan-400/50 text-sm">No missions found on this frequency.</div>}
      </div>
    </div>
  )
}
