import { useEffect, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'

export default function CompanyPage() {
  const qc = useQueryClient()
  const [status, setStatus] = useState('')
  const [error, setError] = useState('')
  const { data: profile } = useQuery({
    queryKey: ['recruiter-profile'],
    queryFn: () => api.get('/api/recruiter/profile').then((r) => r.data)
  })

  const [form, setForm] = useState({
    name: '', logoUrl: '', website: '', industry: '', size: '', description: '', cultureText: ''
  })

  useEffect(() => {
    if (profile?.company) {
      setForm({
        name: profile.company.name || '',
        logoUrl: profile.company.logoUrl || '',
        website: profile.company.website || '',
        industry: profile.company.industry || '',
        size: profile.company.size || '',
        description: profile.company.description || '',
        cultureText: profile.company.cultureText || ''
      })
    }
  }, [profile])

  const saveMut = useMutation({
    mutationFn: (payload) => profile?.company
      ? api.put('/api/recruiter/company', payload)
      : api.post('/api/recruiter/company', payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['recruiter-profile'] })
      setStatus('Saved')
      setError('')
    },
    onError: (err) => setError(err.response?.data?.message || 'Failed')
  })

  const onSubmit = (e) => {
    e.preventDefault()
    setStatus('')
    setError('')
    saveMut.mutate(form)
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Company Profile</h1>
      <HUDCard>
        <form onSubmit={onSubmit} className="space-y-3">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div><label className="hud-label">Name</label><input className="hud-input mt-1" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required /></div>
            <div><label className="hud-label">Logo URL</label><input className="hud-input mt-1" value={form.logoUrl} onChange={(e) => setForm({ ...form, logoUrl: e.target.value })} /></div>
            <div><label className="hud-label">Website</label><input className="hud-input mt-1" value={form.website} onChange={(e) => setForm({ ...form, website: e.target.value })} /></div>
            <div><label className="hud-label">Industry</label><input className="hud-input mt-1" value={form.industry} onChange={(e) => setForm({ ...form, industry: e.target.value })} /></div>
            <div><label className="hud-label">Size</label><input className="hud-input mt-1" value={form.size} onChange={(e) => setForm({ ...form, size: e.target.value })} placeholder="e.g. 50-200" /></div>
          </div>
          <div><label className="hud-label">Description</label><textarea className="hud-input mt-1" rows={4} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></div>
          <div><label className="hud-label">Culture</label><textarea className="hud-input mt-1" rows={3} value={form.cultureText} onChange={(e) => setForm({ ...form, cultureText: e.target.value })} /></div>
          <button type="submit" disabled={saveMut.isPending} className="hud-btn-primary">
            {saveMut.isPending ? 'Saving...' : profile?.company ? 'Update Company' : 'Create Company'}
          </button>
          {status && <span className="ml-3 text-xs text-emerald-300">{status}</span>}
          {error && <span className="ml-3 text-xs text-red-400">{error}</span>}
        </form>
      </HUDCard>
    </div>
  )
}
