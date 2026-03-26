import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import API from '../api/axios'

function TeamPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [team, setTeam] = useState(null)
    const [players, setPlayers] = useState([])
    const [activeTab, setActiveTab] = useState('squad')
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [teamRes, playersRes] = await Promise.all([
                    API.get(`/teams/${id}`),
                    API.get(`/players/team/${id}`)
                ])
                setTeam(teamRes.data)
                setPlayers(playersRes.data)
            } catch (err) {
                console.error(err)
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [id])

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-950 flex justify-center items-center">
                <p className="text-white text-xl animate-pulse">Loading team...</p>
            </div>
        )
    }

    // Group players by position
    const positions = ['Goalkeeper', 'Defender', 'Midfielder', 'Forward']
    const groupedPlayers = positions.reduce((acc, pos) => {
        acc[pos] = players.filter(p =>
            p.position?.toLowerCase() === pos.toLowerCase()
        )
        return acc
    }, {})

    // Top stats
    const topScorer = [...players].sort((a, b) => b.goals - a.goals)[0]
    const topAssist = [...players].sort((a, b) => b.assists - a.assists)[0]
    const totalGoals = players.reduce((sum, p) => sum + (p.goals || 0), 0)
    const totalAssists = players.reduce((sum, p) => sum + (p.assists || 0), 0)

    return (
        <div className="min-h-screen bg-gray-950 px-6 py-8">

            {/* Team Header */}
            <div className="bg-gray-800 rounded-2xl p-6 mb-6 shadow-lg">
                <button
                    onClick={() => navigate(-1)}
                    className="text-gray-400 hover:text-white transition mb-4 flex items-center gap-2"
                >
                    ← Back
                </button>

                <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
                    <img
                        src={team?.logo}
                        alt={team?.name}
                        className="w-24 h-24 object-contain"
                        onError={(e) => { e.target.style.display = 'none' }}
                    />
                    <div className="text-center md:text-left">
                        <h1 className="text-white text-4xl font-bold mb-2">{team?.name}</h1>
                        <div className="flex flex-wrap gap-4 justify-center md:justify-start text-sm">
              <span className="text-gray-400">
                🌍 {team?.country}
              </span>
                            <span className="text-gray-400">
                🏟️ {team?.stadium}
              </span>
                            <span className="text-gray-400">
                👔 {team?.manager}
              </span>
                            <span className="text-gray-400">
                📅 Founded {team?.foundedYear}
              </span>
                            <span
                                onClick={() => navigate(`/league/${team?.leagueId}`)}
                                className="text-green-400 cursor-pointer hover:underline"
                            >
                🏆 {team?.leagueName}
              </span>
                        </div>
                    </div>
                </div>

                {/* Team Stats Bar */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-6">
                    <div className="bg-gray-900 rounded-xl p-4 text-center">
                        <p className="text-gray-400 text-xs mb-1">Squad Size</p>
                        <p className="text-white text-2xl font-bold">{players.length}</p>
                    </div>
                    <div className="bg-gray-900 rounded-xl p-4 text-center">
                        <p className="text-gray-400 text-xs mb-1">Total Goals</p>
                        <p className="text-green-400 text-2xl font-bold">{totalGoals}</p>
                    </div>
                    <div className="bg-gray-900 rounded-xl p-4 text-center">
                        <p className="text-gray-400 text-xs mb-1">Total Assists</p>
                        <p className="text-blue-400 text-2xl font-bold">{totalAssists}</p>
                    </div>
                    <div className="bg-gray-900 rounded-xl p-4 text-center">
                        <p className="text-gray-400 text-xs mb-1">Top Scorer</p>
                        <p className="text-white text-sm font-bold">
                            {topScorer ? `${topScorer.name} (${topScorer.goals})` : 'N/A'}
                        </p>
                    </div>
                </div>
            </div>

            {/* Tabs */}
            <div className="flex gap-2 mb-6 border-b border-gray-800">
                {['squad', 'stats'].map(tab => (
                    <button
                        key={tab}
                        onClick={() => setActiveTab(tab)}
                        className={`px-6 py-3 text-sm font-semibold capitalize transition ${
                            activeTab === tab
                                ? 'text-green-400 border-b-2 border-green-400'
                                : 'text-gray-400 hover:text-white'
                        }`}
                    >
                        {tab}
                    </button>
                ))}
            </div>

            {/* Squad Tab */}
            {activeTab === 'squad' && (
                <div className="space-y-6">
                    {positions.map(position => (
                        groupedPlayers[position]?.length > 0 && (
                            <div key={position}>
                                <h2 className="text-gray-400 text-xs uppercase font-bold mb-3 tracking-wider">
                                    {position}s
                                </h2>
                                <div className="bg-gray-800 rounded-2xl overflow-hidden">
                                    <table className="w-full text-sm">
                                        <thead>
                                        <tr className="bg-gray-900 text-gray-400 text-xs uppercase">
                                            <th className="px-4 py-3 text-left w-8">#</th>
                                            <th className="px-4 py-3 text-left">Player</th>
                                            <th className="px-4 py-3 text-center">Age</th>
                                            <th className="px-4 py-3 text-center">Nat</th>
                                            <th className="px-4 py-3 text-center">Apps</th>
                                            <th className="px-4 py-3 text-center">Goals</th>
                                            <th className="px-4 py-3 text-center">Assists</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {groupedPlayers[position].map(player => (
                                            <tr
                                                key={player.id}
                                                className="border-t border-gray-700 hover:bg-gray-700 cursor-pointer transition"
                                                onClick={() => navigate(`/player/${player.id}`)}
                                            >
                                                <td className="px-4 py-3 text-green-400 font-bold">
                                                    {player.jerseyNumber}
                                                </td>
                                                <td className="px-4 py-3">
                                                    <div className="flex items-center gap-3">
                                                        <img
                                                            src={player.photo}
                                                            alt={player.name}
                                                            className="w-8 h-8 rounded-full object-cover bg-gray-700"
                                                            onError={(e) => {
                                                                e.target.src = `https://ui-avatars.com/api/?name=${player.name}&background=1f2937&color=4ecca3`
                                                            }}
                                                        />
                                                        <span className="text-white font-medium">
                                {player.name}
                              </span>
                                                    </div>
                                                </td>
                                                <td className="px-4 py-3 text-center text-gray-300">
                                                    {player.age}
                                                </td>
                                                <td className="px-4 py-3 text-center text-gray-300">
                                                    {player.nationality}
                                                </td>
                                                <td className="px-4 py-3 text-center text-gray-300">
                                                    {player.appearances || 0}
                                                </td>
                                                <td className="px-4 py-3 text-center">
                            <span className={`font-bold ${player.goals > 0 ? 'text-green-400' : 'text-gray-500'}`}>
                              {player.goals || 0}
                            </span>
                                                </td>
                                                <td className="px-4 py-3 text-center">
                            <span className={`font-bold ${player.assists > 0 ? 'text-blue-400' : 'text-gray-500'}`}>
                              {player.assists || 0}
                            </span>
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        )
                    ))}

                    {players.length === 0 && (
                        <p className="text-gray-400 text-center py-10">
                            No players found for this team.
                        </p>
                    )}
                </div>
            )}

            {/* Stats Tab */}
            {activeTab === 'stats' && (
                <div className="space-y-4">
                    <h2 className="text-white text-xl font-bold mb-4">
                        Top Performers
                    </h2>

                    {/* Top Scorers */}
                    <div className="bg-gray-800 rounded-2xl overflow-hidden">
                        <div className="bg-gray-900 px-4 py-3">
                            <h3 className="text-green-400 font-bold">⚽ Top Scorers</h3>
                        </div>
                        <table className="w-full text-sm">
                            <thead>
                            <tr className="text-gray-400 text-xs uppercase">
                                <th className="px-4 py-2 text-left">Player</th>
                                <th className="px-4 py-2 text-center">Goals</th>
                                <th className="px-4 py-2 text-center">Assists</th>
                                <th className="px-4 py-2 text-center">Apps</th>
                            </tr>
                            </thead>
                            <tbody>
                            {[...players]
                                .sort((a, b) => b.goals - a.goals)
                                .slice(0, 5)
                                .map((player, index) => (
                                    <tr
                                        key={player.id}
                                        className="border-t border-gray-700 hover:bg-gray-700 cursor-pointer"
                                        onClick={() => navigate(`/player/${player.id}`)}
                                    >
                                        <td className="px-4 py-3">
                                            <div className="flex items-center gap-3">
                                                <span className="text-gray-500 w-4">{index + 1}</span>
                                                <img
                                                    src={player.photo}
                                                    alt={player.name}
                                                    className="w-7 h-7 rounded-full object-cover bg-gray-700"
                                                    onError={(e) => {
                                                        e.target.src = `https://ui-avatars.com/api/?name=${player.name}&background=1f2937&color=4ecca3`
                                                    }}
                                                />
                                                <span className="text-white">{player.name}</span>
                                            </div>
                                        </td>
                                        <td className="px-4 py-3 text-center text-green-400 font-bold">
                                            {player.goals || 0}
                                        </td>
                                        <td className="px-4 py-3 text-center text-blue-400 font-bold">
                                            {player.assists || 0}
                                        </td>
                                        <td className="px-4 py-3 text-center text-gray-300">
                                            {player.appearances || 0}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Cards */}
                    <div className="bg-gray-800 rounded-2xl overflow-hidden">
                        <div className="bg-gray-900 px-4 py-3">
                            <h3 className="text-yellow-400 font-bold">🟨 Disciplinary</h3>
                        </div>
                        <table className="w-full text-sm">
                            <thead>
                            <tr className="text-gray-400 text-xs uppercase">
                                <th className="px-4 py-2 text-left">Player</th>
                                <th className="px-4 py-2 text-center">Yellow</th>
                                <th className="px-4 py-2 text-center">Red</th>
                            </tr>
                            </thead>
                            <tbody>
                            {[...players]
                                .sort((a, b) =>
                                    (b.yellowCards + b.redCards * 3) -
                                    (a.yellowCards + a.redCards * 3)
                                )
                                .slice(0, 5)
                                .map(player => (
                                    <tr
                                        key={player.id}
                                        className="border-t border-gray-700 hover:bg-gray-700 cursor-pointer"
                                        onClick={() => navigate(`/player/${player.id}`)}
                                    >
                                        <td className="px-4 py-3 text-white">{player.name}</td>
                                        <td className="px-4 py-3 text-center text-yellow-400 font-bold">
                                            {player.yellowCards || 0}
                                        </td>
                                        <td className="px-4 py-3 text-center text-red-400 font-bold">
                                            {player.redCards || 0}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    )
}

export default TeamPage