import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import LeaguePage from './pages/LeaguePage'
import TeamPage from './pages/TeamPage'
import PlayerPage from './pages/PlayerPage'
import Login from './pages/Login'
import Register from './pages/Register'
import SearchPage from './pages/SearchPage'
import TopScorersPage from './pages/TopScorersPage'
import HeadToHeadPage from './pages/HeadToHeadPage'
import ComparePage from './pages/ComparePage'

function App() {
    return (
        <BrowserRouter>
            <Navbar />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/league/:id" element={<LeaguePage />} />
                <Route path="/team/:id" element={<TeamPage />} />
                <Route path="/player/:id" element={<PlayerPage />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/search" element={<SearchPage />} />
                <Route path="/top-scorers" element={<TopScorersPage />} />
                <Route path="/head-to-head" element={<HeadToHeadPage />} />
                <Route path="/compare" element={<ComparePage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App