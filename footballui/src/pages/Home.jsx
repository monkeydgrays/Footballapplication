import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axios';

function Home() {
    const [leagues, setLeagues] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        API.get('/leagues')
            .then(res => {
                setLeagues(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setLoading(false);
            });
    }, []);

    if (loading) {
        return (
            <div className="flex justify-center items-center h-screen bg-gray-950">
                <p className="text-white text-xl animate-pulse">Loading leagues...</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-950 px-8 py-10">
            <h1 className="text-4xl font-bold text-white text-center mb-2">
                🏆 Football Leagues
            </h1>
            <p className="text-gray-400 text-center mb-10">
                Select a league to view standings and fixtures
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {leagues.map(league => (
                    <div
                        key={league.id}
                        onClick={() => navigate(`/league/${league.id}`)}
                        className="bg-gray-800 hover:bg-gray-700 rounded-2xl p-6 text-center cursor-pointer transition transform hover:scale-105 shadow-lg"
                    >
                        <img
                            src={league.logo}
                            alt={league.name}
                            className="w-20 h-20 object-contain mx-auto mb-4"
                            onError={(e) => { e.target.style.display = 'none' }}
                        />
                        <h2 className="text-white text-xl font-bold mb-2">
                            {league.name}
                        </h2>
                        <p className="text-green-400 text-sm mb-1">
                            🌍 {league.country}
                        </p>
                        <p className="text-gray-400 text-sm">
                            📅 {league.season}
                        </p>
                        <div className="mt-4 bg-green-400 text-gray-900 text-sm font-bold py-2 rounded-lg">
                            View League →
                        </div>
                    </div>
                ))}
            </div>

            {leagues.length === 0 && (
                <div className="text-center mt-20">
                    <p className="text-gray-400 text-xl">No leagues found.</p>
                    <p className="text-gray-500 text-sm mt-2">
                        Add leagues through the API first.
                    </p>
                </div>
            )}
        </div>
    );
}

export default Home;