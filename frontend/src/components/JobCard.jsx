import { Link } from 'react-router-dom'
import { formatSalary } from '../utils/format'

export default function JobCard({ job, to }) {
  return (
    <Link
      to={to || `/seeker/jobs/${job.id}`}
      className="block border border-cyan-400/20 rounded p-3 hover:border-cyan-400/60 hover:bg-cyan-400/5 transition"
    >
      <div className="flex justify-between gap-3 items-start">
        <div className="min-w-0">
          <div className="text-cyan-200 font-bold truncate">{job.title}</div>
          <div className="text-xs text-cyan-400/70 truncate">
            {job.companyName} - {job.location || 'Remote'}
          </div>
        </div>
        <div className="text-right text-xs text-cyan-400/70 whitespace-nowrap">
          <div>{formatSalary(job.salaryMin, job.salaryMax)}</div>
          <div className="text-[9px] mt-1 tracking-widest">{job.remote ? 'REMOTE' : 'ON-SITE'}</div>
        </div>
      </div>
    </Link>
  )
}
