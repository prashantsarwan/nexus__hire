import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'

export default function AdminJobs() {
  const qc = useQueryClient()
  const { data } = useQuery({
    queryKey: ['admin-jobs'],
    queryFn: () => api.get('/api/admin/jobs?size=50').then((r) => r.data)
  })
  const modMut = useMutation({
    mutationFn: ({ id, status }) => api.patch(`/api/admin/jobs/${id}/moderate?status=${status}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin-jobs'] })
  })

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Job Moderation</h1>
      <HUDCard>
        <table className="w-full text-xs">
          <thead>
            <tr className="text-left border-b border-cyan-400/30">
              <th className="py-2 text-cyan-400/70 uppercase tracking-widest">Title</th>
              <th className="py-2 text-cyan-400/70 uppercase tracking-widest">Company</th>
              <th className="py-2 text-cyan-400/70 uppercase tracking-widest">Status</th>
              <th className="py-2"></th>
            </tr>
          </thead>
          <tbody>
            {data?.content?.map((j) => (
              <tr key={j.id} className="border-b border-cyan-400/10">
                <td className="py-2 text-cyan-200">{j.title}</td>
                <td className="py-2 text-cyan-400/70">{j.companyName}</td>
                <td className="py-2 text-cyan-400/70">{j.status}</td>
                <td className="py-2 flex gap-1 flex-wrap">
                  {['ACTIVE', 'FLAGGED', 'CLOSED'].map((s) => (
                    <button
                      key={s}
                      onClick={() => modMut.mutate({ id: j.id, status: s })}
                      className="text-[10px] uppercase tracking-widest border border-cyan-400/40 rounded px-2 py-1 text-cyan-300"
                    >
                      {s}
                    </button>
                  ))}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </HUDCard>
    </div>
  )
}
