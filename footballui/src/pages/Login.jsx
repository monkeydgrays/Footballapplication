import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import API from '../api/axios';

function Login() {
    const [form, setForm] = useState({ email: '', password: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const res = await API.post('/auth/login', form);
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('username', res.data.username);
            navigate('/');
        } catch (err) {
            setError('Invalid email or password. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-950 flex justify-center items-center px-4">
            <div className="bg-gray-800 rounded-2xl p-8 w-full max-w-md shadow-2xl">

                <div className="text-center mb-8">
                    <span className="text-5xl">⚽</span>
                    <h2 className="text-white text-3xl font-bold mt-3">Welcome Back</h2>
                    <p className="text-gray-400 mt-1">Login to your account</p>
                </div>

                {error && (
                    <div className="bg-red-500 bg-opacity-20 border border-red-500 text-red-400 px-4 py-3 rounded-lg mb-6 text-sm">
                        {error}
                    </div>
                )}

                <div className="space-y-4">
                    <input
                        name="email"
                        type="email"
                        placeholder="Email address"
                        value={form.email}
                        onChange={handleChange}
                        className="w-full bg-gray-900 text-white px-4 py-3 rounded-lg border border-gray-700 focus:outline-none focus:border-green-400 transition"
                    />
                    <input
                        name="password"
                        type="password"
                        placeholder="Password"
                        value={form.password}
                        onChange={handleChange}
                        className="w-full bg-gray-900 text-white px-4 py-3 rounded-lg border border-gray-700 focus:outline-none focus:border-green-400 transition"
                    />
                    <button
                        onClick={handleSubmit}
                        disabled={loading}
                        className="w-full bg-green-400 hover:bg-green-500 text-gray-900 font-bold py-3 rounded-lg transition disabled:opacity-50"
                    >
                        {loading ? 'Logging in...' : 'Login'}
                    </button>
                </div>

                <p className="text-gray-400 text-center mt-6">
                    No account?{' '}
                    <Link to="/register" className="text-green-400 hover:underline font-medium">
                        Register here
                    </Link>
                </p>
            </div>
        </div>
    );
}

export default Login;