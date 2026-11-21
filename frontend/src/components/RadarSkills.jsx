import { RadarChart, PolarGrid, PolarAngleAxis, Radar, ResponsiveContainer } from 'recharts'

export default function RadarSkills({ skills = [] }) {
  if (!skills.length) {
    return <div className="text-cyan-400/50 text-xs">No skills to chart yet.</div>
  }
  const levelMap = { BEGINNER: 40, INTERMEDIATE: 70, EXPERT: 100 }
  const data = skills.slice(0, 6).map((s) => ({
    name: s.name,
    level: levelMap[s.proficiency] || 50,
    verified: s.verified ? 100 : 0
  }))
  return (
    <ResponsiveContainer width="100%" height={220}>
      <RadarChart outerRadius={80} data={data}>
        <PolarGrid stroke="#4df" strokeOpacity={0.25} />
        <PolarAngleAxis dataKey="name" tick={{ fill: '#c8e6ff', fontSize: 10 }} />
        <Radar name="Skill" dataKey="level" stroke="#4df" fill="#4df" fillOpacity={0.35} />
        <Radar name="Verified" dataKey="verified" stroke="#0f0" fill="#0f0" fillOpacity={0.15} />
      </RadarChart>
    </ResponsiveContainer>
  )
}
