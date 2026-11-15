export const formatSalary = (min, max) => {
  if (!min && !max) return 'Not disclosed'
  const toLakhs = (v) => (v / 100000).toFixed(1)
  if (min && max) return `INR ${toLakhs(min)}L - ${toLakhs(max)}L`
  return `INR ${toLakhs(min || max)}L`
}

export const formatDate = (iso) => {
  if (!iso) return ''
  return new Date(iso).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
}

export const statusClass = (status) => {
  const map = {
    APPLIED: 'text-cyan-300 border-cyan-400/50',
    SCREENING: 'text-sky-300 border-sky-400/50',
    SHORTLISTED: 'text-violet-300 border-violet-400/50',
    INTERVIEW: 'text-amber-300 border-amber-400/50',
    OFFERED: 'text-emerald-300 border-emerald-400/50',
    HIRED: 'text-green-300 border-green-400/50',
    REJECTED: 'text-red-300 border-red-400/50'
  }
  return map[status] || 'text-cyan-300 border-cyan-400/50'
}
