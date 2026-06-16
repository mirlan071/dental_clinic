import { useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import './Layout.css';

const links = [
  { to: '/', label: 'Главная', icon: '📊' },
  { to: '/patients', label: 'Пациенты', icon: '👥' },
  { to: '/doctors', label: 'Врачи', icon: '👨‍⚕️' },
  { to: '/appointments', label: 'Записи', icon: '📅' },
  { to: '/services', label: 'Услуги', icon: '🦷' },
  { to: '/records', label: 'Медкарты', icon: '📋' },
  { to: '/finance', label: 'Финансы', icon: '💰' },
];

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const closeSidebar = () => setSidebarOpen(false);

  return (
    <div className="layout">
      <div className="mobile-header">
        <button className="mobile-menu-btn" onClick={() => setSidebarOpen(true)}>☰</button>
        <span className="logo-text" style={{ fontSize: 16 }}>Dental Clinic</span>
      </div>
      {sidebarOpen && <div className="sidebar-overlay" onClick={closeSidebar} />}
      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-logo">
          <span className="logo-icon">🦷</span>
          <span className="logo-text">Dental Clinic</span>
        </div>
        <nav className="sidebar-nav">
          {links.map(l => (
            <NavLink key={l.to} to={l.to} end={l.to === '/'} onClick={closeSidebar} className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
              <span className="sidebar-icon">{l.icon}</span>
              <span>{l.label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <div className="sidebar-user">
            <div className="user-avatar">{user?.firstName?.[0] || 'A'}</div>
            <div className="user-info">
              <div className="user-name">{user?.firstName} {user?.lastName}</div>
              <div className="user-role">{user?.role || 'ADMIN'}</div>
            </div>
          </div>
          <button className="logout-btn" onClick={handleLogout}>Выйти</button>
        </div>
      </aside>
      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
