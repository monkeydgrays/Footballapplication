import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'

function Navbar() {
    const navigate = useNavigate()
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')
    const [searchOpen, setSearchOpen] = useState(false)
    const [query, setQuery] = useState('')

    const handleLogout = () => {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        navigate('/login')
    }

    const handleSearch = (e) => {
        if (e.key === 'Enter' && query.trim()) {
            navigate(`/search?q=${query.trim()}`)
            setSearchOpen(false)
            setQuery('')
        }
    }

    return (
        <nav className="bg-gray-900 px-8 py-4 flex justify-between items-center shadow-lg">
            <Link to="/" className="text-white text-2xl font-bold hover:text-green-400 transition">
                ⚽ Football App
            </Link>

            <div className="flex items-center gap-4">
                {/* Search Bar */}
                {searchOpen ? (
                    <input
                        autoFocus
                        type="text"
                        placeholder="Search teams, players, leagues..."
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        onKeyDown={handleSearch}
                        onBlur={() => { setSearchOpen(false); setQuery('') }}
                        className="bg-gray-800 text-white px-4 py-2 rounded-lg border border-gray-700 focus:outline-none focus:border-green-400 w-64 transition"
                    />
                ) : (
                    <button
                        onClick={() => setSearchOpen(true)}
                        className="text-gray-400 hover:text-white transition text-xl"
                    >
                        🔍
                    </button>
                )}

                {/* Nav Links */}
                <Link
                    to="/top-scorers"
                    className="text-gray-400 hover:text-green-400 transition text-sm font-medium hidden md:block"
                >
                    🥇 Top Scorers
                </Link>
                <Link
                    to="/head-to-head"
                    className="text-gray-400 hover:text-green-400 transition text-sm font-medium hidden md:block"
                >
                    ⚔️ H2H
                </Link>
                <Link
                    to="/compare"
                    className="text-gray-400 hover:text-green-400 transition text-sm font-medium hidden md:block"
                >
                    📊 Compare
                </Link>

                {token ? (
                    <>
                        <span className="text-green-400 font-medium text-sm">👤 {username}</span>
                        <button
                            onClick={handleLogout}
                            className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg transition font-medium text-sm"
                        >
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="text-white hover:text-green-400 transition font-medium text-sm">
                            Login
                        </Link>
                        <Link to="/register" className="bg-green-400 hover:bg-green-500 text-gray-900 px-4 py-2 rounded-lg transition font-bold text-sm">
                            Register
                        </Link>
                    </>
                )}
            </div>
        </nav>
    )
}

export default Navbar