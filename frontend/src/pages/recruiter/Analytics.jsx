import { useQuery } from '@tanstack/react-query'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, PieChart, Pie, Cell } from 'recharts'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'

const COLORS = ['#4df', '#0ff', '#0f0', '#fa0', '#f4f', '#f33', '#8ff']

export default function Analytics() {
  const { data } = useQuery({
    queryKey: ['recruiter-analytics-page'],
    queryFn: () => api.get('/api/recruiter/analytics').then((r) => r.data)
  })

  const chartData = Object.entries(data?.applicationsByStatus || {}).map(([name, value]) => ({ name, value }))

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Hiring Analytics</h1>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <HUDCard><div className="hud-label">Total Jobs</div><div className="text-3xl text-cyan-300 mt-2">{data?.totalJobs ?? 0}</div></HUDCard>
        <HUDCard><div className="hud-label">Applications</div><div className="text-3xl text-cyan-300 mt-2">{data?.totalApplications ?? 0}</div></HUDCard>
        <HUDCard><div className="hud-label">Interviews</div><div className="text-3xl text-amber-300 mt-2">{data?.interviewCount ?? 0}</div></HUDCard>
        <HUDCard><div className="hud-label">Hired</div><div className="text-3xl text-emerald-300 mt-2">{data?.hiredCount ?? 0}</div></HUDCard>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <HUDCard>
          <div className="hud-label mb-3">Funnel</div>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={chartData}>
              <CartesianGrid stroke="#4df" strokeOpacity={0.1} />
              <XAxis dataKey="name" tick={{ fill: '#c8e6ff', fontSize: 10 }} />
              <YAxis tick={{ fill: '#c8e6ff', fontSize: 10 }} />
              <Tooltip contentStyle={{ background: '#001122', border: '1px solid #4df', color: '#c8e6ff' }} />
              <Bar dataKey="value" fill="#4df" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </HUDCard>
        <HUDCard>
          <div className="hud-label mb-3">Status Distribution</div>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie data={chartData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={90} label={{ fill: '#c8e6ff', fontSize: 10 }}>
                {chartData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip contentStyle={{ background: '#001122', border: '1px solid #4df', color: '#c8e6ff' }} />
            </PieChart>
          </ResponsiveContainer>
        </HUDCard>
      </div>
    </div>
  )
}
