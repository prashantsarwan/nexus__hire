import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'
import Landing from './pages/Landing'
import Login from './pages/Login'
import Register from './pages/Register'
import SeekerDashboard from './pages/seeker/Dashboard'
import JobSearch from './pages/seeker/JobSearch'
import JobDetail from './pages/seeker/JobDetail'
import Applications from './pages/seeker/Applications'
import SavedJobs from './pages/seeker/SavedJobs'
import Profile from './pages/seeker/Profile'
import Assessments from './pages/seeker/Assessments'
import RecruiterDashboard from './pages/recruiter/Dashboard'
import MyJobs from './pages/recruiter/MyJobs'
import Applicants from './pages/recruiter/Applicants'
import Candidates from './pages/recruiter/Candidates'
import Analytics from './pages/recruiter/Analytics'
import CompanyPage from './pages/recruiter/Company'
import AdminDashboard from './pages/admin/Dashboard'
import AdminUsers from './pages/admin/Users'
import AdminJobs from './pages/admin/Jobs'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route element={<ProtectedRoute roles={['JOB_SEEKER']} />}>
        <Route path="/seeker" element={<Layout title="SEEKER CONSOLE" />}>
          <Route index element={<SeekerDashboard />} />
          <Route path="jobs" element={<JobSearch />} />
          <Route path="jobs/:id" element={<JobDetail />} />
          <Route path="applications" element={<Applications />} />
          <Route path="saved" element={<SavedJobs />} />
          <Route path="assessments" element={<Assessments />} />
          <Route path="profile" element={<Profile />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute roles={['RECRUITER']} />}>
        <Route path="/recruiter" element={<Layout title="RECRUITER CONSOLE" />}>
          <Route index element={<RecruiterDashboard />} />
          <Route path="jobs" element={<MyJobs />} />
          <Route path="jobs/:id/applicants" element={<Applicants />} />
          <Route path="candidates" element={<Candidates />} />
          <Route path="analytics" element={<Analytics />} />
          <Route path="company" element={<CompanyPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute roles={['ADMIN']} />}>
        <Route path="/admin" element={<Layout title="ADMIN CONSOLE" />}>
          <Route index element={<AdminDashboard />} />
          <Route path="users" element={<AdminUsers />} />
          <Route path="jobs" element={<AdminJobs />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
