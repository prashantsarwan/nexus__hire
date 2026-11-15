import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useParams } from 'react-router-dom'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'
import { formatDate } from '../../utils/format'

const COLUMNS = ['APPLIED', 'SCREENING', 'SHORTLISTED', 'INTERVIEW', 'OFFERED', 'HIRED']
const NEXT_STATUS = {
  APPLIED: 'SCREENING',
  SCREENING: 'SHORTLISTED',
  SHORTLISTED: 'INTERVIEW',
  INTERVIEW: 'OFFERED',
  OFFERED: 'HIRED'
}

export default function Applicants() {
  const { id } = useParams()
  const qc = useQueryClient()
  const { data, isLoading } = useQuery({
    queryKey: ['job-applicants', id],
    queryFn: () => api.get(`/api/recruiter/jobs/${id}/applicants?size=200`).then((r) => r.data)
  })

  const statusMut = useMutation({
    mutationFn: ({ appId, status }) => api.patch(`/api/recruiter/applications/${appId}/status`, { status, reason: null }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['job-applicants', id] })
  })

  const byStatus = (s) => data?.content?.filter((a) => a.status === s) || []

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Applicant Tracking</h1>
      {isLoading && <div className="text-cyan-400/50 text-sm">Loading pipeline...</div>}
      <div className="grid grid-cols-1 md:grid-cols-3 xl:grid-cols-6 gap-3">
        {COLUMNS.map((status) => (
          <div key={status} className="min-h-[200px]">
            <div className="hud-label mb-2">{status} ({byStatus(status).length})</div>
            <div className="space-y-2 max-h-[70vh] overflow-y-auto pr-1">
              {byStatus(status).map((app) => (
                <HUDCard key={app.id} className="p-3">
                  <div className="text-cyan-200 text-sm font-bold truncate">{app.seekerName}</div>
                  <div className="text-[10px] text-cyan-400/60 truncate">{app.seekerHeadline || app.seekerEmail}</div>
                  {app.matchScore != null && (
                    <div className="text-[10px] text-cyan-300 mt-1">Match: {Math.round(app.matchScore)}%</div>
                  )}
                  <div className="text-[9px] text-cyan-400/40 mt-1">{formatDate(app.appliedAt)}</div>
                  {app.seekerResumeUrl && (
                    <a href={app.seekerResumeUrl} target="_blank" rel="noreferrer" className="text-[10px] text-cyan-300 underline block mt-1">Resume</a>
                  )}
                  <div className="flex gap-1 mt-2">
                    {NEXT_STATUS[status] && (
                      <button
                        className="text-[9px] uppercase tracking-widest border border-cyan-400/40 rounded px-1 py-0.5 text-cyan-300 hover:bg-cyan-400/10"
                        onClick={() => statusMut.mutate({ appId: app.id, status: NEXT_STATUS[status] })}
                      >
                        Advance
                      </button>
                    )}
                    <button
                      className="text-[9px] uppercase tracking-widest border border-red-400/40 rounded px-1 py-0.5 text-red-400 hover:bg-red-400/10"
                      onClick={() => statusMut.mutate({ appId: app.id, status: 'REJECTED' })}
                    >
                      Reject
                    </button>
                  </div>
                </HUDCard>
              ))}
              {byStatus(status).length === 0 && <div className="text-[10px] text-cyan-400/30 text-center py-4">Empty</div>}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
