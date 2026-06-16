import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import './Login.css';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(username, password);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Ошибка авторизации');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-box">
        <div className="login-header">
          <div className="login-logo">🦷</div>
          <h2>Dental Clinic</h2>
          <p>Система управления стоматологической клиникой</p>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Логин</label>
            <input className="form-input" value={username} onChange={e => setUsername(e.target.value)} placeholder="Введите логин" required />
          </div>
          <div className="form-group">
            <label className="form-label">Пароль</label>
            <input className="form-input" type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="••••••" required />
          </div>
          {error && <div className="login-error">{error}</div>}
          <button className="btn btn-primary login-btn" type="submit" disabled={loading}>
            {loading ? 'Вход...' : 'Войти'}
          </button>
        </form>
      </div>
    </div>
  );
}
