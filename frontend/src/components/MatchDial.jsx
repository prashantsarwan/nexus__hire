export default function MatchDial({ value = 0, size = 130 }) {
  const clamped = Math.max(0, Math.min(100, Math.round(value)))
  const radius = size / 2 - 10
  const circumference = 2 * Math.PI * radius
  const offset = circumference - (clamped / 100) * circumference
  return (
    <div className="relative" style={{ width: size, height: size }}>
      <svg width={size} height={size} className="-rotate-90">
        <circle cx={size / 2} cy={size / 2} r={radius} stroke="rgba(68,221,255,0.15)" strokeWidth="8" fill="none" />
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          stroke="#4df"
          strokeWidth="8"
          fill="none"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          style={{ filter: 'drop-shadow(0 0 8px rgba(68,221,255,0.8))', transition: 'stroke-dashoffset 0.8s ease' }}
        />
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <div className="text-2xl font-bold text-cyan-200">{clamped}%</div>
        <div className="text-[9px] uppercase tracking-[0.3em] text-cyan-400/60">Match</div>
      </div>
    </div>
  )
}
