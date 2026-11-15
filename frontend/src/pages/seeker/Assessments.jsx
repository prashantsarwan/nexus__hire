import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import api from '../../api/client'
import HUDCard from '../../components/HUDCard'
import { formatDate } from '../../utils/format'

const DEMO_ANSWERS = [0, 1, 1, 1, 0]

export default function Assessments() {
  const qc = useQueryClient()
  const [result, setResult] = useState(null)

  const { data: assessments } = useQuery({
    queryKey: ['assessments'],
    queryFn: () => api.get('/api/seeker/assessments').then((r) => r.data)
  })
  const { data: attempts } = useQuery({
    queryKey: ['assessment-attempts'],
    queryFn: () => api.get('/api/seeker/assessments/attempts').then((r) => r.data)
  })

  const submitMut = useMutation({
    mutationFn: (assessmentId) => api.post('/api/seeker/assessments/submit', { assessmentId, answers: DEMO_ANSWERS }),
    onSuccess: (res) => {
      qc.invalidateQueries({ queryKey: ['assessment-attempts'] })
      qc.invalidateQueries({ queryKey: ['seeker-profile'] })
      setResult(res.data)
    }
  })

  return (
    <div className="space-y-6">
      <h1 className="text-2xl uppercase tracking-[0.3em] text-cyan-300">Skill Verifications</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <HUDCard>
          <div className="hud-label mb-3">Available Assessments</div>
          <div className="space-y-2">
            {assessments?.map((a) => (
              <div key={a.id} className="flex justify-between items-center border border-cyan-400/20 rounded p-2">
                <div>
                  <div className="text-cyan-200 text-sm">{a.title}</div>
                  <div className="text-[10px] text-cyan-400/60">{a.skillName} - {a.durationMinutes} min - pass {a.passingScore}%</div>
                </div>
                <button
                  onClick={() => submitMut.mutate(a.id)}
                  disabled={submitMut.isPending}
                  className="hud-btn"
                >
                  Attempt
                </button>
              </div>
            ))}
            {!assessments?.length && <div className="text-cyan-400/50 text-xs">No assessments available.</div>}
          </div>
          {result && (
            <div className="mt-3 text-xs">
              Result: <span className={result.passed ? 'text-emerald-300' : 'text-red-400'}>
                {result.score}% - {result.passed ? 'PASSED' : 'FAILED'}
              </span>
            </div>
          )}
        </HUDCard>
        <HUDCard>
          <div className="hud-label mb-3">Attempt History</div>
          <div className="space-y-2 max-h-80 overflow-y-auto">
            {attempts?.map((a) => (
              <div key={a.id} className="text-xs border-l-2 border-cyan-400/40 pl-2 py-1">
                <div className="text-cyan-200">{a.assessmentTitle}</div>
                <div className="text-cyan-400/60">{a.skillName} - {a.score}% - {a.passed ? 'PASS' : 'FAIL'}</div>
                <div className="text-[10px] text-cyan-400/40">{formatDate(a.attemptedAt)}</div>
              </div>
            ))}
            {!attempts?.length && <div className="text-cyan-400/50 text-xs">No attempts yet.</div>}
          </div>
        </HUDCard>
      </div>
    </div>
  )
}
