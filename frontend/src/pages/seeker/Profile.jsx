import { useEffect, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'
import RadarSkills from '../../components/RadarSkills'

export default function Profile() {
  const qc = useQueryClient()
  const { data: profile } = useQuery({
    queryKey: ['seeker-profile'],
    queryFn: () => api.get('/api/seeker/profile').then((r) => r.data)
  })
  const { data: skills } = useQuery({
    queryKey: ['skills-list'],
    queryFn: () => api.get('/api/skills').then((r) => r.data)
  })

  const [form, setForm] = useState({
    headline: '',
    summary: '',
    totalExperienceYears: '',
    currentLocation: '',
    preferredLocations: '',
    expectedSalaryMin: '',
    expectedSalaryMax: '',
    noticePeriodDays: ''
  })
  const [selectedSkill, setSelectedSkill] = useState('')
  const [status, setStatus] = useState('')

  useEffect(() => {
    if (profile) {
      setForm({
        headline: profile.headline || '',
        summary: profile.summary || '',
        totalExperienceYears: profile.totalExperienceYears ?? '',
        currentLocation: profile.currentLocation || '',
        preferredLocations: profile.preferredLocations || '',
        expectedSalaryMin: profile.expectedSalaryMin ?? '',
        expectedSalaryMax: profile.expectedSalaryMax ?? '',
        noticePeriodDays: profile.noticePeriodDays ?? ''
      })
    }
  }, [profile])

  const saveMut = useMutation({
    mutationFn: (payload) => api.put('/api/seeker/profile', payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['seeker-profile'] })
      setStatus('Profile saved')
    }
  })

  const uploadMut = useMutation({
    mutationFn: (file) => {
      const fd = new FormData()
      fd.append('file', file)
      return api.post('/api/seeker/profile/resume', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['seeker-profile'] })
      setStatus('Resume uploaded')
    }
  })

  const addSkillMut = useMutation({
    mutationFn: (skillId) => api.post('/api/seeker/profile/skills', { skillId: Number(skillId), proficiency: 'INTERMEDIATE' }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['seeker-profile'] })
      setSelectedSkill('')
    }
  })

  const removeSkillMut = useMutation({
    mutationFn: (skillId) => api.delete(`/api/seeker/profile/skills/${skillId}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['seeker-profile'] })
  })

  const onSubmit = (e) => {
    e.preventDefault()
    setStatus('')
    const payload = {
      ...form,
      totalExperienceYears: form.totalExperienceYears === '' ? null : Number(form.totalExperienceYears),
      expectedSalaryMin: form.expectedSalaryMin === '' ? null : Number(form.expectedSalaryMin),
      expectedSalaryMax: form.expectedSalaryMax === '' ? null : Number(form.expectedSalaryMax),
      noticePeriodDays: form.noticePeriodDays === '' ? null : Number(form.noticePeriodDays),
      skills: profile?.skills?.map((s) => ({ skillId: s.skillId, proficiency: s.proficiency })) || []
    }
    saveMut.mutate(payload)
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Operator Profile</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <HUDCard className="md:col-span-2">
          <form onSubmit={onSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div>
                <label className="hud-label">Headline</label>
                <input className="hud-input mt-1" value={form.headline} onChange={(e) => setForm({ ...form, headline: e.target.value })} />
              </div>
              <div>
                <label className="hud-label">Experience (years)</label>
                <input className="hud-input mt-1" type="number" value={form.totalExperienceYears} onChange={(e) => setForm({ ...form, totalExperienceYears: e.target.value })} />
              </div>
              <div>
                <label className="hud-label">Current Location</label>
                <input className="hud-input mt-1" value={form.currentLocation} onChange={(e) => setForm({ ...form, currentLocation: e.target.value })} />
              </div>
              <div>
                <label className="hud-label">Preferred Locations</label>
                <input className="hud-input mt-1" value={form.preferredLocations} onChange={(e) => setForm({ ...form, preferredLocations: e.target.value })} placeholder="Comma separated" />
              </div>
              <div>
                <label className="hud-label">Expected Salary Min (INR)</label>
                <input className="hud-input mt-1" type="number" value={form.expectedSalaryMin} onChange={(e) => setForm({ ...form, expectedSalaryMin: e.target.value })} />
              </div>
              <div>
                <label className="hud-label">Expected Salary Max (INR)</label>
                <input className="hud-input mt-1" type="number" value={form.expectedSalaryMax} onChange={(e) => setForm({ ...form, expectedSalaryMax: e.target.value })} />
              </div>
              <div>
                <label className="hud-label">Notice Period (days)</label>
                <input className="hud-input mt-1" type="number" value={form.noticePeriodDays} onChange={(e) => setForm({ ...form, noticePeriodDays: e.target.value })} />
              </div>
            </div>
            <div>
              <label className="hud-label">Summary</label>
              <textarea className="hud-input mt-1" rows={4} value={form.summary} onChange={(e) => setForm({ ...form, summary: e.target.value })} />
            </div>
            <button type="submit" disabled={saveMut.isPending} className="hud-btn-primary">
              {saveMut.isPending ? 'Saving...' : 'Save Profile'}
            </button>
            {status && <span className="ml-3 text-xs text-emerald-300">{status}</span>}
          </form>
        </HUDCard>

        <div className="space-y-4">
          <HUDCard>
            <div className="hud-label mb-2">Resume</div>
            <div className="text-xs text-cyan-400/70 break-all">{profile?.resumeUrl || 'Not uploaded'}</div>
            {profile?.resumeUrl && (
              <a href={profile.resumeUrl} target="_blank" rel="noreferrer" className="text-cyan-300 text-xs underline block mt-1">
                View resume
              </a>
            )}
            <input
              type="file"
              accept="application/pdf"
              onChange={(e) => e.target.files?.[0] && uploadMut.mutate(e.target.files[0])}
              className="text-xs text-cyan-300 mt-3 block w-full"
            />
            {uploadMut.isPending && <div className="text-xs text-cyan-400/50 mt-1">Uploading...</div>}
          </HUDCard>

          <HUDCard>
            <div className="hud-label mb-2">Skill Matrix</div>
            <RadarSkills skills={profile?.skills || []} />
          </HUDCard>
        </div>
      </div>

      <HUDCard>
        <div className="hud-label mb-3">Skills</div>
        <div className="flex flex-wrap gap-2 mb-4">
          {profile?.skills?.map((s) => (
            <span key={s.skillId} className="px-2 py-1 text-xs border border-cyan-400/40 rounded flex items-center gap-2">
              {s.name}
              {s.verified && <span className="text-emerald-300">verified</span>}
              <button onClick={() => removeSkillMut.mutate(s.skillId)} className="text-red-400 hover:text-red-300">x</button>
            </span>
          ))}
          {!profile?.skills?.length && <div className="text-cyan-400/50 text-xs">No skills added.</div>}
        </div>
        <div className="flex gap-2 flex-wrap">
          <select className="hud-input max-w-xs" value={selectedSkill} onChange={(e) => setSelectedSkill(e.target.value)}>
            <option value="">Choose a skill...</option>
            {skills?.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
          <button onClick={() => selectedSkill && addSkillMut.mutate(selectedSkill)} className="hud-btn-primary">Add</button>
        </div>
      </HUDCard>
    </div>
  )
}
