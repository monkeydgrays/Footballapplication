import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import API from '../api/axios'

function TopScorersPage() {
  const [players, setPlayers] = useState([])
  const [leagues, setLeagues] = useState([])
  const [selectedLeague, setSelectedLeague] = useState('all')
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [playersRes, leaguesRes] = await Promise.all([
          API.get('/players/top-scorers'),
          API.get('/leagues')
        ])
        setPlayers(playersRes.data)
        setLeagues(leaguesRes.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])

  const filtered = selectedLeague === 'all'
    ? players
    : players.filter(p => p.leagueId === parseInt(selectedLeague))

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-950 flex justify-center items-center">
        <p className="text-white text-xl animate-pulse">Loading...</p>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-950 px-6 py-8">

      {/* Header */}
      <div className="text-center mb-8">
        <h1 className="text-white text-4xl font-bold mb-2">🥇 Top Scorers</h1>
        <p className="text-gray-400">Golden Boot Race</p>
      </div>

      {/* League Filter */}
      <div className="flex gap-3 flex-wrap justify-center mb-8">
        <button
          onClick={() => setSelectedLeague('all')}
          className={`px-4 py-2 rounded-full text-sm font-medium transition ${
            selectedLeague === 'all'
              ? 'bg-green-400 text-gray-900'
              : 'bg-gray-800 text-gray-400 hover:text-white'
          }`}
        >
          All Leagues
        </button>
        {leagues.map(league => (
          <button
            key={league.id}
            onClick={() => setSelectedLeague(String(league.id))}
            className={`px-4 py-2 rounded-full text-sm font-medium transition ${
              selectedLeague === String(league.id)
                ? 'bg-green-400 text-gray-900'
                : 'bg-gray-800 text-gray-400 hover:text-white'
            }`}
          >
            {league.name}
          </button>
        ))}
      </div>

      {/* Top 3 Podium */}
      {filtered.length >= 3 && (
        <div className="flex justify-center items-end gap-4 mb-8">
          {/* 2nd Place */}
          <div
            className="bg-gray-800 rounded-2xl p-4 text-center w-36 cursor-pointer hover:bg-gray-700 transition"
            onClick={() => navigate(`/player/${filtered[1].id}`)}
          >
            <div className="text-3xl mb-2">🥈</div>
            <img
              src={filtered[1].photo}
              alt={filtered[1].name}
              className="w-14 h-14 rounded-full object-cover bg-gray-700 mx-auto mb-2"
              onError={(e) => {
                e.target.src = `https://ui-avatars.com/api/?name=${filtered[1].name}&background=1f2937&color=4ecca3`
              }}
            />
            <p className="text-white font-bold text-sm">{filtered[1].name}</p>
            <p className="text-gray-400 text-xs">{filtered[1].teamName}</p>
            <p className="text-green-400 text-2xl font-bold mt-2">
              {filtered[1].goals}
            </p>
            <p className="text-gray-500 text-xs">goals</p>
          </div>

          {/* 1st Place */}
          <div
            className="bg-gray-800 rounded-2xl p-5 text-center w-40 cursor-pointer hover:bg-gray-700 transition border border-yellow-500"
            onClick={() => navigate(`/player/${filtered[0].id}`)}
          >
            <div className="text-4xl mb-2">🥇</div>
            <img
              src={filtered[0].photo}
              alt={filtered[0].name}
              className="w-16 h-16 rounded-full object-cover bg-gray-700 mx-auto mb-2 border-2 border-yellow-500"
              onError={(e) => {
                e.target.src = `https://ui-avatars.com/api/?name=${filtered[0].name}&background=1f2937&color=4ecca3`
              }}
            />
            <p className="text-white font-bold">{filtered[0].name}</p>
            <p className="text-gray-400 text-xs">{filtered[0].teamName}</p>
            <p className="text-yellow-400 text-3xl font-bold mt-2">
              {filtered[0].goals}
            </p>
            <p className="text-gray-500 text-xs">goals</p>
          </div>

          {/* 3rd Place */}
          <div
            className="bg-gray-800 rounded-2xl p-4 text-center w-36 cursor-pointer hover:bg-gray-700 transition"
            onClick={() => navigate(`/player/${filtered[2].id}`)}
          >
            <div className="text-3xl mb-2">🥉</div>
            <img
              src={filtered[2].photo}
              alt={filtered[2].name}
              className="w-14 h-14 rounded-full object-cover bg-gray-700 mx-auto mb-2"
              onError={(e) => {
                e.target.src = `https://ui-avatars.com/api/?name=${filtered[2].name}&background=1f2937&color=4ecca3`
              }}
            />
            <p className="text-white font-bold text-sm">{filtered[2].name}</p>
            <p className="text-gray-400 text-xs">{filtered[2].teamName}</p>
            <p className="text-green-400 text-2xl font-bold mt-2">
              {filtered[2].goals}
            </p>
            <p className="text-gray-500 text-xs">goals</p>
          </div>
        </div>
      )}

      {/* Full Table */}
      <div className="bg-gray-800 rounded-2xl overflow-hidden shadow-lg">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-900 text-gray-400 text-xs uppercase">
              <th className="px-4 py-3 text-left w-8">#</th>
              <th className="px-4 py-3 text-left">Player</th>
              <th className="px-4 py-3 text-center">Team</th>
              <th className="px-4 py-3 text-center">League</th>
              <th className="px-4 py-3 text-center">Apps</th>
              <th className="px-4 py-3 text-center">Assists</th>
              <th className="px-4 py-3 text-center text-green-400">Goals</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((player, index) => (
              <tr
                key={player.id}
                onClick={() => navigate(`/player/${player.id}`)}
                className="border-t border-gray-700 hover:bg-gray-700 cursor-pointer transition"
              >
                <td className="px-4 py-3">
                  {index === 0 ? '🥇' : index === 1 ? '🥈' : index === 2 ? '🥉' :
                    <span className="text-gray-400">{index + 1}</span>
                  }
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
                    <span className="text-white font-medium">{player.name}</span>
                  </div>
                </td>
                <td className="px-4 py-3 text-center text-gray-300">
                  {player.teamName}
                </td>
                <td className="px-4 py-3 text-center text-gray-300">
                  {player.leagueName}
                </td>
                <td className="px-4 py-3 text-center text-gray-300">
                  {player.appearances || 0}
                </td>
                <td className="px-4 py-3 text-center text-blue-400 font-bold">
                  {player.assists || 0}
                </td>
                <td className="px-4 py-3 text-center">
                  <span className="bg-green-400 text-gray-900 font-bold px-3 py-1 rounded-full text-xs">
                    {player.goals || 0}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {filtered.length === 0 && (
          <p className="text-gray-400 text-center py-10">
            No players found.
          </p>
        )}
      </div>
    </div>
  )
}

export default TopScorersPage