import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'
import { formatSalary } from '../../utils/format'

export default function JobDetail() {
  const { id } = useParams()
  const nav = useNavigate()
  const qc = useQueryClient()
  const [coverNote, setCoverNote] = useState('')

  const { data: job, isLoading } = useQuery({
    queryKey: ['job', id],
    queryFn: () => api.get(`/api/jobs/${id}`).then((r) => r.data)
  })
  const { data: savedJobs } = useQuery({
    queryKey: ['saved-jobs'],
    queryFn: () => api.get('/api/seeker/saved-jobs').then((r) => r.data)
  })

  const applyMut = useMutation({
    mutationFn: () => api.post('/api/seeker/applications', { jobId: Number(id), coverNote }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['seeker-applications'] })
      qc.invalidateQueries({ queryKey: ['seeker-notifications'] })
      nav('/seeker/applications')
    }
  })

  const saveMut = useMutation({
    mutationFn: () => api.post(`/api/seeker/saved-jobs/${id}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['saved-jobs'] })
  })

  const isSaved = savedJobs?.some((j) => j.id === Number(id))

  if (isLoading) return <div className="text-cyan-400/60 text-sm">Loading mission brief...</div>
  if (!job) return <div className="text-red-400 text-sm">Job not found.</div>

  return (
    <div className="space-y-6">
      <HUDCard>
        <div className="flex justify-between items-start gap-4 flex-wrap">
          <div>
            <h1 className="text-2xl text-cyan-200 tracking-widest">{job.title}</h1>
            <div className="text-sm text-cyan-400/70 mt-1">
              {job.companyName} - {job.location || 'Remote'} - {job.remote ? 'Remote' : 'On-site'}
            </div>
            <div className="text-xs text-cyan-400/60 mt-1">
              {job.employmentType?.replace('_', ' ')} - {job.minExperience}-{job.maxExperience} yrs - {formatSalary(job.salaryMin, job.salaryMax)}
            </div>
          </div>
          <button onClick={() => saveMut.mutate()} disabled={isSaved || saveMut.isPending} className="hud-btn">
            {isSaved ? 'Saved' : saveMut.isPending ? 'Saving...' : 'Save'}
          </button>
        </div>
      </HUDCard>

      <HUDCard>
        <div className="hud-label mb-2">Mission Brief</div>
        <p className="text-cyan-100 whitespace-pre-line leading-relaxed">{job.description}</p>
      </HUDCard>

      <HUDCard>
        <div className="hud-label mb-3">Required Stack</div>
        <div className="flex flex-wrap gap-2">
          {job.skills?.map((s) => (
            <span key={s.skillId} className="px-2 py-1 text-xs border border-cyan-400/40 rounded tracking-wider">
              {s.name}{s.mandatory ? ' *' : ''}
            </span>
          ))}
          {!job.skills?.length && <div className="text-cyan-400/50 text-xs">No specific skills listed.</div>}
        </div>
      </HUDCard>

      <HUDCard>
        <div className="hud-label mb-3">Transmit Application</div>
        <textarea
          value={coverNote}
          onChange={(e) => setCoverNote(e.target.value)}
          rows={4}
          placeholder="Optional cover note..."
          className="hud-input"
        />
        <div className="flex items-center gap-3 mt-3">
          <button onClick={() => applyMut.mutate()} disabled={applyMut.isPending} className="hud-btn-primary">
            {applyMut.isPending ? 'Transmitting...' : 'Submit Application'}
          </button>
          {job.applicantCount > 0 && (
            <span className="text-[10px] uppercase tracking-widest text-cyan-400/50">
              {job.applicantCount} applicant(s) so far
            </span>
          )}
        </div>
        {applyMut.isError && (
          <div className="text-red-400 text-xs mt-2">{applyMut.error?.response?.data?.message || 'Failed'}</div>
        )}
      </HUDCard>
    </div>
  )
}
