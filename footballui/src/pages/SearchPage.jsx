import { useEffect, useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import API from '../api/axios'

function SearchPage() {
    const [searchParams] = useSearchParams()
    const query = searchParams.get('q')
    const navigate = useNavigate()
    const [results, setResults] = useState({ leagues: [], teams: [], players: [] })
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (!query) return
        setLoading(true)
        API.get(`/search?keyword=${query}`)
            .then(res => {
                setResults(res.data)
                setLoading(false)
            })
            .catch(err => {
                console.error(err)
                setLoading(false)
            })
    }, [query])

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-950 flex justify-center items-center">
                <p className="text-white text-xl animate-pulse">Searching...</p>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-gray-950 px-6 py-8">
            <h1 className="text-white text-3xl font-bold mb-2">
                Search Results
            </h1>
            <p className="text-gray-400 mb-8">
                {results.total} results for "<span className="text-green-400">{query}</span>"
            </p>

            {/* Leagues */}
            {results.leagues?.length > 0 && (
                <div className="mb-8">
                    <h2 className="text-gray-400 text-xs uppercase font-bold mb-3 tracking-wider">
                        Leagues
                    </h2>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        {results.leagues.map(league => (
                            <div
                                key={league.id}
                                onClick={() => navigate(`/league/${league.id}`)}
                                className="bg-gray-800 hover:bg-gray-700 rounded-xl p-4 flex items-center gap-4 cursor-pointer transition"
                            >
                                <img
                                    src={league.logo}
                                    alt={league.name}
                                    className="w-10 h-10 object-contain"
                                    onError={(e) => { e.target.style.display = 'none' }}
                                />
                                <div>
                                    <p className="text-white font-semibold">{league.name}</p>
                                    <p className="text-gray-400 text-sm">{league.country}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Teams */}
            {results.teams?.length > 0 && (
                <div className="mb-8">
                    <h2 className="text-gray-400 text-xs uppercase font-bold mb-3 tracking-wider">
                        Teams
                    </h2>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        {results.teams.map(team => (
                            <div
                                key={team.id}
                                onClick={() => navigate(`/team/${team.id}`)}
                                className="bg-gray-800 hover:bg-gray-700 rounded-xl p-4 flex items-center gap-4 cursor-pointer transition"
                            >
                                <img
                                    src={team.logo}
                                    alt={team.name}
                                    className="w-10 h-10 object-contain"
                                    onError={(e) => { e.target.style.display = 'none' }}
                                />
                                <div>
                                    <p className="text-white font-semibold">{team.name}</p>
                                    <p className="text-gray-400 text-sm">{team.country}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Players */}
            {results.players?.length > 0 && (
                <div className="mb-8">
                    <h2 className="text-gray-400 text-xs uppercase font-bold mb-3 tracking-wider">
                        Players
                    </h2>
                    <div className="bg-gray-800 rounded-2xl overflow-hidden">
                        <table className="w-full text-sm">
                            <thead>
                            <tr className="bg-gray-900 text-gray-400 text-xs uppercase">
                                <th className="px-4 py-3 text-left">Player</th>
                                <th className="px-4 py-3 text-center">Position</th>
                                <th className="px-4 py-3 text-center">Team</th>
                                <th className="px-4 py-3 text-center">Goals</th>
                                <th className="px-4 py-3 text-center">Assists</th>
                            </tr>
                            </thead>
                            <tbody>
                            {results.players.map(player => (
                                <tr
                                    key={player.id}
                                    onClick={() => navigate(`/player/${player.id}`)}
                                    className="border-t border-gray-700 hover:bg-gray-700 cursor-pointer transition"
                                >
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
                                            <span className="text-white font-medium">{player.name}</span>
                                        </div>
                                    </td>
                                    <td className="px-4 py-3 text-center text-green-400">
                                        {player.position}
                                    </td>
                                    <td className="px-4 py-3 text-center text-gray-300">
                                        {player.teamName}
                                    </td>
                                    <td className="px-4 py-3 text-center text-green-400 font-bold">
                                        {player.goals || 0}
                                    </td>
                                    <td className="px-4 py-3 text-center text-blue-400 font-bold">
                                        {player.assists || 0}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* No Results */}
            {results.total === 0 && (
                <div className="text-center py-20">
                    <p className="text-5xl mb-4">🔍</p>
                    <p className="text-white text-xl font-bold">No results found</p>
                    <p className="text-gray-400 mt-2">
                        Try searching for a different team, player or league
                    </p>
                </div>
            )}
        </div>
    )
}

export default SearchPage