import { useEffect, useState } from 'react'
import API from '../api/axios'

function ComparePage() {
    const [players, setPlayers] = useState([])
    const [player1Id, setPlayer1Id] = useState('')
    const [player2Id, setPlayer2Id] = useState('')
    const [result, setResult] = useState(null)
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        API.get('/players').then(res => setPlayers(res.data))
    }, [])

    const handleCompare = async () => {
        if (!player1Id || !player2Id || player1Id === player2Id) return
        setLoading(true)
        try {
            const res = await API.get(
                `/players/compare?player1Id=${player1Id}&player2Id=${player2Id}`
            )
            setResult(res.data)
        } catch (err) {
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    const statRows = [
        { label: 'Appearances', key: 'appearances', color: 'text-white' },
        { label: 'Goals', key: 'goals', color: 'text-green-400' },
        { label: 'Assists', key: 'assists', color: 'text-blue-400' },
        { label: 'Yellow Cards', key: 'yellowCards', color: 'text-yellow-400' },
        { label: 'Red Cards', key: 'redCards', color: 'text-red-400' },
    ]

    const getBetter = (key) => {
        if (!result) return null
        const v1 = result.player1[key] || 0
        const v2 = result.player2[key] || 0
        if (key === 'yellowCards' || key === 'redCards') {
            return v1 < v2 ? 'p1' : v2 < v1 ? 'p2' : 'draw'
        }
        return v1 > v2 ? 'p1' : v2 > v1 ? 'p2' : 'draw'
    }

    return (
        <div className="min-h-screen bg-gray-950 px-6 py-8">
            <div className="text-center mb-8">
                <h1 className="text-white text-4xl font-bold mb-2">📊 Player Comparison</h1>
                <p className="text-gray-400">Compare two players stats side by side</p>
            </div>

            {/* Player Selector */}
            <div className="flex flex-col md:flex-row gap-4 justify-center items-center mb-8">
                <select
                    value={player1Id}
                    onChange={(e) => setPlayer1Id(e.target.value)}
                    className="bg-gray-800 text-white px-4 py-3 rounded-xl border border-gray-700 focus:outline-none focus:border-green-400 w-full md:w-64"
                >
                    <option value="">Select Player 1</option>
                    {players.map(p => (
                        <option key={p.id} value={p.id}>{p.name} ({p.teamName})</option>
                    ))}
                </select>

                <span className="text-gray-400 font-bold text-xl">VS</span>

                <select
                    value={player2Id}
                    onChange={(e) => setPlayer2Id(e.target.value)}
                    className="bg-gray-800 text-white px-4 py-3 rounded-xl border border-gray-700 focus:outline-none focus:border-green-400 w-full md:w-64"
                >
                    <option value="">Select Player 2</option>
                    {players.map(p => (
                        <option key={p.id} value={p.id}>{p.name} ({p.teamName})</option>
                    ))}
                </select>

                <button
                    onClick={handleCompare}
                    disabled={!player1Id || !player2Id || player1Id === player2Id}
                    className="bg-green-400 hover:bg-green-500 text-gray-900 font-bold px-8 py-3 rounded-xl transition disabled:opacity-40 w-full md:w-auto"
                >
                    Compare
                </button>
            </div>

            {loading && (
                <p className="text-white text-center animate-pulse">Loading...</p>
            )}

            {result && !loading && (
                <div className="max-w-3xl mx-auto">

                    {/* Player Headers */}
                    <div className="grid grid-cols-3 gap-4 mb-4">
                        <div className="bg-gray-800 rounded-2xl p-4 text-center">
                            <img
                                src={result.player1.photo}
                                alt={result.player1.name}
                                className="w-16 h-16 rounded-full object-cover bg-gray-700 mx-auto mb-2"
                                onError={(e) => {
                                    e.target.src = `https://ui-avatars.com/api/?name=${result.player1.name}&background=1f2937&color=4ecca3`
                                }}
                            />
                            <p className="text-white font-bold">{result.player1.name}</p>
                            <p className="text-gray-400 text-xs">{result.player1.teamName}</p>
                            <p className="text-green-400 text-xs mt-1">{result.player1.position}</p>
                        </div>

                        <div className="flex items-center justify-center">
                            <span className="text-gray-400 font-bold text-2xl">VS</span>
                        </div>

                        <div className="bg-gray-800 rounded-2xl p-4 text-center">
                            <img
                                src={result.player2.photo}
                                alt={result.player2.name}
                                className="w-16 h-16 rounded-full object-cover bg-gray-700 mx-auto mb-2"
                                onError={(e) => {
                                    e.target.src = `https://ui-avatars.com/api/?name=${result.player2.name}&background=1f2937&color=4ecca3`
                                }}
                            />
                            <p className="text-white font-bold">{result.player2.name}</p>
                            <p className="text-gray-400 text-xs">{result.player2.teamName}</p>
                            <p className="text-green-400 text-xs mt-1">{result.player2.position}</p>
                        </div>
                    </div>

                    {/* Stats Comparison */}
                    <div className="bg-gray-800 rounded-2xl overflow-hidden">
                        {statRows.map(stat => {
                            const better = getBetter(stat.key)
                            return (
                                <div
                                    key={stat.key}
                                    className="grid grid-cols-3 border-t border-gray-700 first:border-0"
                                >
                                    <div className={`px-6 py-4 text-center font-bold text-xl ${
                                        better === 'p1' ? 'bg-green-400 bg-opacity-10' : ''
                                    }`}>
                    <span className={better === 'p1' ? 'text-green-400' : 'text-white'}>
                      {result.player1[stat.key] || 0}
                    </span>
                                        {better === 'p1' && (
                                            <span className="text-green-400 text-xs ml-1">✓</span>
                                        )}
                                    </div>
                                    <div className="px-6 py-4 text-center flex items-center justify-center">
                    <span className="text-gray-400 text-sm font-medium">
                      {stat.label}
                    </span>
                                    </div>
                                    <div className={`px-6 py-4 text-center font-bold text-xl ${
                                        better === 'p2' ? 'bg-green-400 bg-opacity-10' : ''
                                    }`}>
                    <span className={better === 'p2' ? 'text-green-400' : 'text-white'}>
                      {result.player2[stat.key] || 0}
                    </span>
                                        {better === 'p2' && (
                                            <span className="text-green-400 text-xs ml-1">✓</span>
                                        )}
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                </div>
            )}
        </div>
    )
}

export default ComparePage