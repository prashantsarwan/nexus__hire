import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'

export default function Candidates() {
  const [filters, setFilters] = useState({ keyword: '', location: '', minExperience: '', minSalary: '', skillIds: [] })
  const [applied, setApplied] = useState(filters)

  const { data: skills } = useQuery({
    queryKey: ['skills-list'],
    queryFn: () => api.get('/api/skills').then((r) => r.data)
  })
  const { data, isLoading } = useQuery({
    queryKey: ['candidates', applied],
    queryFn: () => api.get('/api/recruiter/candidates', {
      params: {
        keyword: applied.keyword || undefined,
        location: applied.location || undefined,
        minExperience: applied.minExperience || undefined,
        minSalary: applied.minSalary || undefined,
        skillIds: applied.skillIds.length ? applied.skillIds : undefined,
        size: 30
      }
    }).then((r) => r.data)
  })

  const toggleSkill = (id) => {
    setFilters((f) => ({
      ...f,
      skillIds: f.skillIds.includes(id) ? f.skillIds.filter((x) => x !== id) : [...f.skillIds, id]
    }))
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Candidate Search</h1>
      <HUDCard>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <input className="hud-input" placeholder="Keyword..." value={filters.keyword} onChange={(e) => setFilters({ ...filters, keyword: e.target.value })} />
          <input className="hud-input" placeholder="Location..." value={filters.location} onChange={(e) => setFilters({ ...filters, location: e.target.value })} />
          <input className="hud-input" type="number" placeholder="Min exp..." value={filters.minExperience} onChange={(e) => setFilters({ ...filters, minExperience: e.target.value })} />
          <button className="hud-btn-primary" onClick={() => setApplied(filters)}>Scan</button>
        </div>
        <div className="hud-label mt-3 mb-2">Filter by skills</div>
        <div className="flex flex-wrap gap-2">
          {skills?.map((s) => (
            <button
              key={s.id}
              onClick={() => toggleSkill(s.id)}
              className={`text-[10px] uppercase tracking-widest px-2 py-1 rounded border ${filters.skillIds.includes(s.id) ? 'border-cyan-400 bg-cyan-400/20 text-cyan-200' : 'border-cyan-400/30 text-cyan-400/60'}`}
            >
              {s.name}
            </button>
          ))}
        </div>
      </HUDCard>

      {isLoading && <div className="text-cyan-400/50 text-sm">Scanning candidates...</div>}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {data?.content?.map((c) => (
          <HUDCard key={c.id}>
            <div className="flex justify-between items-start">
              <div>
                <div className="text-cyan-200 font-bold">{c.fullName}</div>
                <div className="text-xs text-cyan-400/60">{c.headline || 'No headline'} - {c.currentLocation || 'Unknown'}</div>
                <div className="text-[10px] text-cyan-400/40 mt-1">{c.totalExperienceYears ?? 0} yrs exp</div>
              </div>
              {c.resumeUrl && <a href={c.resumeUrl} target="_blank" rel="noreferrer" className="hud-btn">Resume</a>}
            </div>
            <div className="flex flex-wrap gap-1 mt-2">
              {c.skills?.slice(0, 6).map((s) => (
                <span key={s.skillId} className="px-1.5 py-0.5 text-[10px] border border-cyan-400/30 rounded">
                  {s.name}{s.verified ? ' ✓' : ''}
                </span>
              ))}
            </div>
          </HUDCard>
        ))}
        {data && data.content.length === 0 && <div className="text-cyan-400/50 text-sm">No candidates match.</div>}
      </div>
    </div>
  )
}
