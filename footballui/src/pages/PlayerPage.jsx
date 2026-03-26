import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import API from '../api/axios'

function PlayerPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [player, setPlayer] = useState(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        API.get(`/players/${id}`)
            .then(res => {
                setPlayer(res.data)
                setLoading(false)
            })
            .catch(err => {
                console.error(err)
                setLoading(false)
            })
    }, [id])

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-950 flex justify-center items-center">
                <p className="text-white text-xl animate-pulse">Loading player...</p>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-gray-950 px-6 py-8">
            <button
                onClick={() => navigate(-1)}
                className="text-gray-400 hover:text-white transition mb-6 flex items-center gap-2"
            >
                ← Back
            </button>

            {/* Player Header */}
            <div className="bg-gray-800 rounded-2xl p-6 mb-6 shadow-lg">
                <div className="flex flex-col md:flex-row items-center gap-6">
                    <img
                        src={player?.photo}
                        alt={player?.name}
                        className="w-28 h-28 rounded-full object-cover bg-gray-700"
                        onError={(e) => {
                            e.target.src = `https://ui-avatars.com/api/?name=${player?.name}&size=112&background=1f2937&color=4ecca3`
                        }}
                    />
                    <div className="text-center md:text-left">
                        <div className="flex items-center gap-3 justify-center md:justify-start mb-2">
              <span className="bg-green-400 text-gray-900 font-bold text-lg w-10 h-10 rounded-full flex items-center justify-center">
                {player?.jerseyNumber}
              </span>
                            <h1 className="text-white text-4xl font-bold">{player?.name}</h1>
                        </div>
                        <div className="flex flex-wrap gap-4 justify-center md:justify-start text-sm mt-2">
                            <span className="text-gray-400">🌍 {player?.nationality}</span>
                            <span className="text-gray-400">📅 Age {player?.age}</span>
                            <span className="text-green-400 font-medium">⚽ {player?.position}</span>
                            <span
                                className="text-blue-400 cursor-pointer hover:underline"
                                onClick={() => navigate(`/team/${player?.teamId}`)}
                            >
                🏃 {player?.teamName}
              </span>
                            <span
                                className="text-yellow-400 cursor-pointer hover:underline"
                                onClick={() => navigate(`/league/${player?.leagueId}`)}
                            >
                🏆 {player?.leagueName}
              </span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Stats Grid */}
            <h2 className="text-white text-xl font-bold mb-4">Season Statistics</h2>
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
                {[
                    { label: 'Appearances', value: player?.appearances || 0, color: 'text-white' },
                    { label: 'Goals', value: player?.goals || 0, color: 'text-green-400' },
                    { label: 'Assists', value: player?.assists || 0, color: 'text-blue-400' },
                    { label: 'Yellow Cards', value: player?.yellowCards || 0, color: 'text-yellow-400' },
                    { label: 'Red Cards', value: player?.redCards || 0, color: 'text-red-400' },
                    { label: 'Goals/Game', value: player?.appearances > 0 ? (player.goals / player.appearances).toFixed(2) : '0.00', color: 'text-purple-400' },
                ].map(stat => (
                    <div key={stat.label} className="bg-gray-800 rounded-2xl p-4 text-center shadow">
                        <p className="text-gray-400 text-xs mb-2">{stat.label}</p>
                        <p className={`text-3xl font-bold ${stat.color}`}>{stat.value}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default PlayerPage