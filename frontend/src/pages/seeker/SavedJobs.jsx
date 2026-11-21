import { useQuery } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'
import JobCard from '../../components/JobCard'

export default function SavedJobs() {
  const { data, isLoading } = useQuery({
    queryKey: ['saved-jobs-list'],
    queryFn: () => api.get('/api/seeker/saved-jobs').then((r) => r.data)
  })

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Saved Missions</h1>
      {isLoading && <div className="text-cyan-400/50 text-sm">Loading...</div>}
      <div className="space-y-3">
        {data?.map((job) => <JobCard key={job.id} job={job} />)}
        {data && data.length === 0 && (
          <HUDCard><div className="text-cyan-400/60 text-sm">No saved missions yet.</div></HUDCard>
        )}
      </div>
    </div>
  )
}
