import { useEffect, useState } from 'react'
import API from '../api/axios'

function HeadToHeadPage() {
    const [teams, setTeams] = useState([])
    const [team1Id, setTeam1Id] = useState('')
    const [team2Id, setTeam2Id] = useState('')
    const [result, setResult] = useState(null)
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        API.get('/teams').then(res => setTeams(res.data))
    }, [])

    const handleCompare = async () => {
        if (!team1Id || !team2Id || team1Id === team2Id) return
        setLoading(true)
        try {
            const res = await API.get(`/fixtures/h2h?team1Id=${team1Id}&team2Id=${team2Id}`)
            setResult(res.data)
        } catch (err) {
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    const getBarWidth = (val, total) =>
        total === 0 ? 50 : Math.round((val / total) * 100)

    return (
        <div className="min-h-screen bg-gray-950 px-6 py-8">
            <div className="text-center mb-8">
                <h1 className="text-white text-4xl font-bold mb-2">⚔️ Head to Head</h1>
                <p className="text-gray-400">Compare two teams historical record</p>
            </div>

            {/* Team Selector */}
            <div className="flex flex-col md:flex-row gap-4 justify-center items-center mb-8">
                <select
                    value={team1Id}
                    onChange={(e) => setTeam1Id(e.target.value)}
                    className="bg-gray-800 text-white px-4 py-3 rounded-xl border border-gray-700 focus:outline-none focus:border-green-400 w-full md:w-64"
                >
                    <option value="">Select Team 1</option>
                    {teams.map(t => (
                        <option key={t.id} value={t.id}>{t.name}</option>
                    ))}
                </select>

                <span className="text-gray-400 font-bold text-xl">VS</span>

                <select
                    value={team2Id}
                    onChange={(e) => setTeam2Id(e.target.value)}
                    className="bg-gray-800 text-white px-4 py-3 rounded-xl border border-gray-700 focus:outline-none focus:border-green-400 w-full md:w-64"
                >
                    <option value="">Select Team 2</option>
                    {teams.map(t => (
                        <option key={t.id} value={t.id}>{t.name}</option>
                    ))}
                </select>

                <button
                    onClick={handleCompare}
                    disabled={!team1Id || !team2Id || team1Id === team2Id}
                    className="bg-green-400 hover:bg-green-500 text-gray-900 font-bold px-8 py-3 rounded-xl transition disabled:opacity-40 w-full md:w-auto"
                >
                    Compare
                </button>
            </div>

            {loading && (
                <p className="text-white text-center animate-pulse">Loading...</p>
            )}

            {result && !loading && (
                <div className="max-w-3xl mx-auto space-y-6">

                    {/* Teams Header */}
                    <div className="bg-gray-800 rounded-2xl p-6 flex justify-between items-center">
                        <div className="text-center">
                            <img
                                src={result.team1Logo}
                                alt={result.team1Name}
                                className="w-16 h-16 object-contain mx-auto mb-2"
                                onError={(e) => { e.target.style.display = 'none' }}
                            />
                            <p className="text-white font-bold text-lg">{result.team1Name}</p>
                        </div>
                        <div className="text-center">
                            <p className="text-gray-400 text-sm mb-1">Total Matches</p>
                            <p className="text-white text-3xl font-bold">{result.totalMatches}</p>
                        </div>
                        <div className="text-center">
                            <img
                                src={result.team2Logo}
                                alt={result.team2Name}
                                className="w-16 h-16 object-contain mx-auto mb-2"
                                onError={(e) => { e.target.style.display = 'none' }}
                            />
                            <p className="text-white font-bold text-lg">{result.team2Name}</p>
                        </div>
                    </div>

                    {/* Stats */}
                    <div className="bg-gray-800 rounded-2xl p-6 space-y-5">

                        {/* Wins Bar */}
                        <div>
                            <div className="flex justify-between text-sm text-gray-400 mb-2">
                                <span className="text-green-400 font-bold">{result.team1Wins} W</span>
                                <span>Wins</span>
                                <span className="text-green-400 font-bold">{result.team2Wins} W</span>
                            </div>
                            <div className="flex h-3 rounded-full overflow-hidden">
                                <div
                                    className="bg-green-400 transition-all"
                                    style={{ width: `${getBarWidth(result.team1Wins, result.totalMatches)}%` }}
                                />
                                <div
                                    className="bg-gray-600"
                                    style={{ width: `${getBarWidth(result.draws, result.totalMatches)}%` }}
                                />
                                <div
                                    className="bg-blue-400 transition-all"
                                    style={{ width: `${getBarWidth(result.team2Wins, result.totalMatches)}%` }}
                                />
                            </div>
                            <div className="flex justify-center mt-1">
                                <span className="text-gray-400 text-xs">{result.draws} Draws</span>
                            </div>
                        </div>

                        {/* Goals Bar */}
                        <div>
                            <div className="flex justify-between text-sm text-gray-400 mb-2">
                                <span className="text-white font-bold">{result.team1Goals}</span>
                                <span>Goals Scored</span>
                                <span className="text-white font-bold">{result.team2Goals}</span>
                            </div>
                            <div className="flex h-3 rounded-full overflow-hidden">
                                <div
                                    className="bg-green-400"
                                    style={{ width: `${getBarWidth(result.team1Goals, result.team1Goals + result.team2Goals)}%` }}
                                />
                                <div
                                    className="bg-blue-400"
                                    style={{ width: `${getBarWidth(result.team2Goals, result.team1Goals + result.team2Goals)}%` }}
                                />
                            </div>
                        </div>
                    </div>

                    {/* Recent Matches */}
                    {result.recentMatches?.length > 0 && (
                        <div>
                            <h2 className="text-white font-bold text-lg mb-3">Recent Meetings</h2>
                            <div className="space-y-3">
                                {result.recentMatches.map(fixture => (
                                    <div key={fixture.id} className="bg-gray-800 rounded-xl p-4">
                                        <div className="flex items-center justify-between gap-4">
                      <span className="text-white font-semibold flex-1 text-right">
                        {fixture.homeTeamName}
                      </span>
                                            <div className="bg-gray-900 px-4 py-2 rounded-lg min-w-[80px] text-center">
                                                {fixture.status === 'FINISHED' ? (
                                                    <span className="text-white font-bold">
                            {fixture.homeScore} - {fixture.awayScore}
                          </span>
                                                ) : (
                                                    <span className="text-gray-400 text-xs">Scheduled</span>
                                                )}
                                            </div>
                                            <span className="text-white font-semibold flex-1">
                        {fixture.awayTeamName}
                      </span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {result.totalMatches === 0 && (
                        <p className="text-gray-400 text-center py-6">
                            No matches found between these teams.
                        </p>
                    )}
                </div>
            )}
        </div>
    )
}

export default HeadToHeadPage