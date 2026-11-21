import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'
import { formatSalary } from '../../utils/format'

const emptyForm = {
  title: '',
  description: '',
  employmentType: 'FULL_TIME',
  minExperience: '',
  maxExperience: '',
  salaryMin: '',
  salaryMax: '',
  salaryCurrency: 'INR',
  location: '',
  remote: false,
  status: 'ACTIVE'
}

export default function MyJobs() {
  const qc = useQueryClient()
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(emptyForm)
  const [skills, setSkills] = useState([])
  const [skillToAdd, setSkillToAdd] = useState('')
  const [mandatory, setMandatory] = useState(false)
  const [error, setError] = useState('')

  const { data: jobs } = useQuery({
    queryKey: ['recruiter-jobs'],
    queryFn: () => api.get('/api/recruiter/jobs?size=50').then((r) => r.data)
  })
  const { data: allSkills } = useQuery({
    queryKey: ['skills-list'],
    queryFn: () => api.get('/api/skills').then((r) => r.data)
  })

  useEffect(() => {
    if (!showForm) {
      setForm(emptyForm)
      setSkills([])
      setError('')
    }
  }, [showForm])

  const createMut = useMutation({
    mutationFn: (payload) => api.post('/api/recruiter/jobs', payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['recruiter-jobs'] })
      qc.invalidateQueries({ queryKey: ['recruiter-analytics'] })
      setShowForm(false)
    },
    onError: (err) => setError(err.response?.data?.message || 'Failed to create job')
  })

  const deleteMut = useMutation({
    mutationFn: (id) => api.delete(`/api/recruiter/jobs/${id}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['recruiter-jobs'] })
  })

  const addSkill = () => {
    if (!skillToAdd) return
    const id = Number(skillToAdd)
    if (skills.some((s) => s.skillId === id)) return
    setSkills([...skills, { skillId: id, mandatory }])
    setSkillToAdd('')
    setMandatory(false)
  }

  const removeSkill = (id) => setSkills(skills.filter((s) => s.skillId !== id))

  const submit = (e) => {
    e.preventDefault()
    setError('')
    const payload = {
      ...form,
      minExperience: form.minExperience === '' ? null : Number(form.minExperience),
      maxExperience: form.maxExperience === '' ? null : Number(form.maxExperience),
      salaryMin: form.salaryMin === '' ? null : Number(form.salaryMin),
      salaryMax: form.salaryMax === '' ? null : Number(form.salaryMax),
      skills: skills.map((s) => ({ skillId: s.skillId, mandatory: s.mandatory }))
    }
    createMut.mutate(payload)
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">My Jobs</h1>
        <button className="hud-btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancel' : '+ New Job'}
        </button>
      </div>

      {showForm && (
        <HUDCard>
          <form onSubmit={submit} className="space-y-3">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div><label className="hud-label">Title</label><input className="hud-input mt-1" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required /></div>
              <div><label className="hud-label">Location</label><input className="hud-input mt-1" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} required /></div>
              <div><label className="hud-label">Employment Type</label>
                <select className="hud-input mt-1" value={form.employmentType} onChange={(e) => setForm({ ...form, employmentType: e.target.value })}>
                  <option value="FULL_TIME">Full Time</option>
                  <option value="PART_TIME">Part Time</option>
                  <option value="CONTRACT">Contract</option>
                  <option value="INTERNSHIP">Internship</option>
                  <option value="FREELANCE">Freelance</option>
                </select>
              </div>
              <div><label className="hud-label">Status</label>
                <select className="hud-input mt-1" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                  <option value="DRAFT">Draft</option>
                  <option value="ACTIVE">Active</option>
                  <option value="CLOSED">Closed</option>
                </select>
              </div>
              <div><label className="hud-label">Min Experience</label><input className="hud-input mt-1" type="number" value={form.minExperience} onChange={(e) => setForm({ ...form, minExperience: e.target.value })} /></div>
              <div><label className="hud-label">Max Experience</label><input className="hud-input mt-1" type="number" value={form.maxExperience} onChange={(e) => setForm({ ...form, maxExperience: e.target.value })} /></div>
              <div><label className="hud-label">Salary Min</label><input className="hud-input mt-1" type="number" value={form.salaryMin} onChange={(e) => setForm({ ...form, salaryMin: e.target.value })} /></div>
              <div><label className="hud-label">Salary Max</label><input className="hud-input mt-1" type="number" value={form.salaryMax} onChange={(e) => setForm({ ...form, salaryMax: e.target.value })} /></div>
            </div>
            <label className="flex items-center gap-2 text-[10px] uppercase tracking-widest text-cyan-400/70">
              <input type="checkbox" checked={form.remote} onChange={(e) => setForm({ ...form, remote: e.target.checked })} /> Remote
            </label>
            <div><label className="hud-label">Description</label><textarea className="hud-input mt-1" rows={5} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} required /></div>

            <div>
              <label className="hud-label">Required Skills</label>
              <div className="flex gap-2 mt-1 flex-wrap">
                <select className="hud-input max-w-xs" value={skillToAdd} onChange={(e) => setSkillToAdd(e.target.value)}>
                  <option value="">Select skill</option>
                  {allSkills?.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
                </select>
                <label className="flex items-center gap-2 text-[10px] uppercase tracking-widest text-cyan-400/70">
                  <input type="checkbox" checked={mandatory} onChange={(e) => setMandatory(e.target.checked)} /> Mandatory
                </label>
                <button type="button" className="hud-btn" onClick={addSkill}>Add Skill</button>
              </div>
              <div className="flex flex-wrap gap-2 mt-2">
                {skills.map((s) => {
                  const sk = allSkills?.find((x) => x.id === s.skillId)
                  return (
                    <span key={s.skillId} className="px-2 py-1 text-xs border border-cyan-400/40 rounded flex items-center gap-2">
                      {sk?.name} {s.mandatory && <span className="text-amber-300">*</span>}
                      <button type="button" onClick={() => removeSkill(s.skillId)} className="text-red-400">x</button>
                    </span>
                  )
                })}
                {!skills.length && <span className="text-cyan-400/50 text-xs">No skills added yet.</span>}
              </div>
            </div>

            {error && <div className="text-red-400 text-xs">{error}</div>}
            <button type="submit" disabled={createMut.isPending} className="hud-btn-primary">
              {createMut.isPending ? 'Creating...' : 'Publish Job'}
            </button>
          </form>
        </HUDCard>
      )}

      <div className="space-y-3">
        {jobs?.content?.map((job) => (
          <HUDCard key={job.id}>
            <div className="flex justify-between items-start gap-3 flex-wrap">
              <div>
                <div className="text-cyan-200 font-bold">{job.title}</div>
                <div className="text-xs text-cyan-400/60">{job.location} - {formatSalary(job.salaryMin, job.salaryMax)}</div>
                <div className="text-[10px] text-cyan-400/40 mt-1 tracking-widest">{job.status}</div>
              </div>
              <div className="flex gap-2 flex-wrap">
                <Link to={`/recruiter/jobs/${job.id}/applicants`} className="hud-btn">Applicants</Link>
                <button className="hud-btn text-red-400 border-red-400/40" onClick={() => deleteMut.mutate(job.id)}>Delete</button>
              </div>
            </div>
          </HUDCard>
        ))}
        {jobs && jobs.content.length === 0 && <HUDCard><div className="text-cyan-400/60 text-sm">No jobs yet. Create one above.</div></HUDCard>}
      </div>
    </div>
  )
}
