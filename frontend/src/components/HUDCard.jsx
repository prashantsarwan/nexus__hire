export default function HUDCard({ children, className = '' }) {
  return <div className={`hud-card ${className}`}>{children}</div>
}
