import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import API from '../api/axios'

function LeaguePage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const [league, setLeague] = useState(null)
    const [standings, setStandings] = useState([])
    const [fixtures, setFixtures] = useState([])
    const [activeTab, setActiveTab] = useState('standings')
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [leagueRes, standingsRes, fixturesRes] = await Promise.all([
                    API.get(`/leagues/${id}`),
                    API.get(`/standings/league/${id}`),
                    API.get(`/fixtures/league/${id}`)
                ])
                setLeague(leagueRes.data)
                setStandings(standingsRes.data)
                setFixtures(fixturesRes.data)
            } catch (err) {
                console.error('Failed request:', err.config?.url, err.message)
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [id])

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-950 flex justify-center items-center">
                <p className="text-white text-xl animate-pulse">Loading...</p>
            </div>
        )
    }

    const scheduled = fixtures.filter(f => f.status === 'SCHEDULED')
    const finished = fixtures.filter(f => f.status === 'FINISHED')
    const live = fixtures.filter(f => f.status === 'LIVE')

    return (
        <div className="min-h-screen bg-gray-950 px-6 py-8">

            {/* League Header */}
            <div className="flex items-center gap-6 mb-8">
                <button
                    onClick={() => navigate('/')}
                    className="text-gray-400 hover:text-white transition text-2xl"
                >
                    ←
                </button>
                <img
                    src={league?.logo}
                    alt={league?.name}
                    className="w-16 h-16 object-contain"
                    onError={(e) => { e.target.style.display = 'none' }}
                />
                <div>
                    <h1 className="text-white text-3xl font-bold">{league?.name}</h1>
                    <p className="text-gray-400">
                        🌍 {league?.country} &nbsp;•&nbsp; 📅 {league?.season}
                    </p>
                </div>
            </div>

            {/* Tabs */}
            <div className="flex gap-2 mb-6 border-b border-gray-800">
                {['standings', 'fixtures', 'results'].map(tab => (
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
                        {tab === 'fixtures' && scheduled.length > 0 &&
                            <span className="ml-2 bg-gray-700 text-xs px-2 py-0.5 rounded-full">
                {scheduled.length}
              </span>
                        }
                        {tab === 'results' && finished.length > 0 &&
                            <span className="ml-2 bg-gray-700 text-xs px-2 py-0.5 rounded-full">
                {finished.length}
              </span>
                        }
                    </button>
                ))}
                {live.length > 0 && (
                    <button
                        onClick={() => setActiveTab('live')}
                        className={`px-6 py-3 text-sm font-semibold transition ${
                            activeTab === 'live'
                                ? 'text-green-400 border-b-2 border-green-400'
                                : 'text-red-400 hover:text-red-300'
                        }`}
                    >
                        🔴 Live ({live.length})
                    </button>
                )}
            </div>

            {/* Standings Tab */}
            {activeTab === 'standings' && (
                <div className="bg-gray-800 rounded-2xl overflow-hidden shadow-lg">
                    <table className="w-full text-sm">
                        <thead>
                        <tr className="bg-gray-900 text-gray-400 text-xs uppercase">
                            <th className="px-4 py-3 text-left w-8">#</th>
                            <th className="px-4 py-3 text-left">Team</th>
                            <th className="px-4 py-3 text-center">P</th>
                            <th className="px-4 py-3 text-center">W</th>
                            <th className="px-4 py-3 text-center">D</th>
                            <th className="px-4 py-3 text-center">L</th>
                            <th className="px-4 py-3 text-center">GF</th>
                            <th className="px-4 py-3 text-center">GA</th>
                            <th className="px-4 py-3 text-center">GD</th>
                            <th className="px-4 py-3 text-center font-bold text-white">PTS</th>
                            <th className="px-4 py-3 text-center hidden md:table-cell">Form</th>
                        </tr>
                        </thead>
                        <tbody>
                        {standings.map((s, index) => (
                            <tr
                                key={s.teamId}
                                onClick={() => navigate(`/team/${s.teamId}`)}
                                className="border-t border-gray-700 hover:bg-gray-700 cursor-pointer transition"
                            >
                                <td className="px-4 py-3 text-gray-400 font-medium">
                                    {s.position <= 4 ? (
                                        <span className="text-green-400 font-bold">{s.position}</span>
                                    ) : s.position >= standings.length - 2 ? (
                                        <span className="text-red-400 font-bold">{s.position}</span>
                                    ) : (
                                        <span className="text-white">{s.position}</span>
                                    )}
                                </td>
                                <td className="px-4 py-3">
                                    <div className="flex items-center gap-3">
                                        <img
                                            src={s.teamLogo}
                                            alt={s.teamName}
                                            className="w-6 h-6 object-contain"
                                            onError={(e) => { e.target.style.display = 'none' }}
                                        />
                                        <span className="text-white font-medium">{s.teamName}</span>
                                    </div>
                                </td>
                                <td className="px-4 py-3 text-center text-gray-300">{s.played}</td>
                                <td className="px-4 py-3 text-center text-gray-300">{s.won}</td>
                                <td className="px-4 py-3 text-center text-gray-300">{s.drawn}</td>
                                <td className="px-4 py-3 text-center text-gray-300">{s.lost}</td>
                                <td className="px-4 py-3 text-center text-gray-300">{s.goalsFor}</td>
                                <td className="px-4 py-3 text-center text-gray-300">{s.goalsAgainst}</td>
                                <td className="px-4 py-3 text-center text-gray-300">
                                    {s.goalDifference > 0 ? `+${s.goalDifference}` : s.goalDifference}
                                </td>
                                <td className="px-4 py-3 text-center font-bold text-white">
                                    {s.points}
                                </td>
                                <td className="px-4 py-3 text-center hidden md:table-cell">
                                    <div className="flex gap-1 justify-center">
                                        {s.form && s.form.split('').map((result, i) => (
                                            <span
                                                key={i}
                                                className={`w-5 h-5 rounded-full text-xs flex items-center justify-center font-bold ${
                                                    result === 'W' ? 'bg-green-500 text-white' :
                                                        result === 'D' ? 'bg-yellow-500 text-gray-900' :
                                                            'bg-red-500 text-white'
                                                }`}
                                            >
                          {result}
                        </span>
                                        ))}
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    {standings.length === 0 && (
                        <p className="text-gray-400 text-center py-10">
                            No standings yet. Add fixtures and results first.
                        </p>
                    )}

                    {/* Legend */}
                    <div className="flex gap-6 px-4 py-3 border-t border-gray-700 text-xs text-gray-400">
            <span className="flex items-center gap-1">
              <span className="w-3 h-3 bg-green-400 rounded-sm inline-block"></span>
              Champions League
            </span>
                        <span className="flex items-center gap-1">
              <span className="w-3 h-3 bg-red-400 rounded-sm inline-block"></span>
              Relegation
            </span>
                    </div>
                </div>
            )}

            {/* Fixtures Tab */}
            {activeTab === 'fixtures' && (
                <div className="space-y-3">
                    {scheduled.length === 0 && (
                        <p className="text-gray-400 text-center py-10">
                            No upcoming fixtures.
                        </p>
                    )}
                    {scheduled.map(fixture => (
                        <FixtureCard key={fixture.id} fixture={fixture} />
                    ))}
                </div>
            )}

            {/* Results Tab */}
            {activeTab === 'results' && (
                <div className="space-y-3">
                    {finished.length === 0 && (
                        <p className="text-gray-400 text-center py-10">
                            No results yet.
                        </p>
                    )}
                    {finished.map(fixture => (
                        <FixtureCard key={fixture.id} fixture={fixture} />
                    ))}
                </div>
            )}

            {/* Live Tab */}
            {activeTab === 'live' && (
                <div className="space-y-3">
                    {live.map(fixture => (
                        <FixtureCard key={fixture.id} fixture={fixture} live />
                    ))}
                </div>
            )}
        </div>
    )
}

// Fixture Card Component
function FixtureCard({ fixture, live }) {
    const formatDate = (dateStr) => {
        const date = new Date(dateStr)
        return date.toLocaleDateString('en-GB', {
            weekday: 'short',
            day: 'numeric',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit'
        })
    }

    return (
        <div className={`bg-gray-800 rounded-xl p-4 shadow ${live ? 'border border-red-500' : ''}`}>
            {live && (
                <div className="flex justify-center mb-2">
          <span className="bg-red-500 text-white text-xs px-3 py-1 rounded-full font-bold animate-pulse">
            🔴 LIVE
          </span>
                </div>
            )}
            <div className="flex items-center justify-between gap-4">

                {/* Home Team */}
                <div className="flex items-center gap-3 flex-1 justify-end">
          <span className="text-white font-semibold text-right">
            {fixture.homeTeamName}
          </span>
                    <img
                        src={fixture.homeTeamLogo}
                        alt={fixture.homeTeamName}
                        className="w-8 h-8 object-contain"
                        onError={(e) => { e.target.style.display = 'none' }}
                    />
                </div>

                {/* Score or Time */}
                <div className="text-center min-w-[80px]">
                    {fixture.status === 'FINISHED' || fixture.status === 'LIVE' ? (
                        <div className="bg-gray-900 px-4 py-2 rounded-lg">
              <span className="text-white font-bold text-xl">
                {fixture.homeScore} - {fixture.awayScore}
              </span>
                        </div>
                    ) : (
                        <div className="text-center">
              <span className="text-green-400 font-bold text-sm">
                {formatDate(fixture.matchDate)}
              </span>
                        </div>
                    )}
                </div>

                {/* Away Team */}
                <div className="flex items-center gap-3 flex-1">
                    <img
                        src={fixture.awayTeamLogo}
                        alt={fixture.awayTeamName}
                        className="w-8 h-8 object-contain"
                        onError={(e) => { e.target.style.display = 'none' }}
                    />
                    <span className="text-white font-semibold">
            {fixture.awayTeamName}
          </span>
                </div>
            </div>

            {/* Venue */}
            {fixture.venue && (
                <p className="text-gray-500 text-xs text-center mt-2">
                    📍 {fixture.venue}
                </p>
            )}
        </div>
    )
}

export default LeaguePage