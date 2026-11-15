import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'

export default function AdminUsers() {
  const qc = useQueryClient()
  const { data } = useQuery({
    queryKey: ['admin-users'],
    queryFn: () => api.get('/api/admin/users?size=50').then((r) => r.data)
  })
  const toggleMut = useMutation({
    mutationFn: ({ id, active }) => api.patch(`/api/admin/users/${id}/active?active=${active}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admin-users'] })
  })

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">User Registry</h1>
      <HUDCard>
        <table className="w-full text-xs">
          <thead>
            <tr className="text-left border-b border-cyan-400/30">
              <th className="py-2 text-cyan-400/70 uppercase tracking-widest">Name</th>
              <th className="py-2 text-cyan-400/70 uppercase tracking-widest">Email</th>
              <th className="py-2 text-cyan-400/70 uppercase tracking-widest">Role</th>
              <th className="py-2 text-cyan-400/70 uppercase tracking-widest">Status</th>
              <th className="py-2"></th>
            </tr>
          </thead>
          <tbody>
            {data?.content?.map((u) => (
              <tr key={u.id} className="border-b border-cyan-400/10">
                <td className="py-2 text-cyan-200">{u.fullName}</td>
                <td className="py-2 text-cyan-400/70">{u.email}</td>
                <td className="py-2 text-cyan-400/70">{u.role}</td>
                <td className="py-2 text-cyan-400/70">{u.active ? 'Active' : 'Banned'}</td>
                <td className="py-2">
                  <button
                    onClick={() => toggleMut.mutate({ id: u.id, active: !u.active })}
                    className={`text-[10px] uppercase tracking-widest border rounded px-2 py-1 ${u.active ? 'border-red-400/40 text-red-400' : 'border-emerald-400/40 text-emerald-300'}`}
                  >
                    {u.active ? 'Ban' : 'Unban'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </HUDCard>
    </div>
  )
}
