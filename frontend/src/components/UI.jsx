import './UI.css';

export function Card({ children, className = '', ...props }) {
  return <div className={`card ${className}`} {...props}>{children}</div>;
}

export function CardHeader({ title, subtitle, action, children }) {
  return (
    <div className="card-header">
      <div>
        {title && <h3 className="card-title">{title}</h3>}
        {subtitle && <p className="card-subtitle">{subtitle}</p>}
      </div>
      <div className="card-header-right">{action}{children}</div>
    </div>
  );
}

export function Badge({ variant = 'gray', children }) {
  return <span className={`badge badge-${variant}`}>{children}</span>;
}

export function Button({ variant = 'primary', size = 'md', children, ...props }) {
  return <button className={`btn btn-${variant} btn-${size}`} {...props}>{children}</button>;
}

export function Input({ label, error, ...props }) {
  return (
    <div className="form-group">
      {label && <label className="form-label">{label}</label>}
      <input className={`form-input ${error ? 'error' : ''}`} {...props} />
      {error && <span className="form-error">{error}</span>}
    </div>
  );
}

export function Select({ label, options, ...props }) {
  return (
    <div className="form-group">
      {label && <label className="form-label">{label}</label>}
      <select className="form-input" {...props}>
        {options.map(o => (
          <option key={o.value} value={o.value}>{o.label}</option>
        ))}
      </select>
    </div>
  );
}

export function Modal({ open, onClose, title, children, actions }) {
  if (!open) return null;
  return (
    <div className="modal-overlay" onClick={onClose} style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,.4)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 200, backdropFilter: 'blur(2px)' }}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <h3 className="modal-title">{title}</h3>
        <div className="modal-body">{children}</div>
        <div className="modal-actions">{actions}</div>
      </div>
    </div>
  );
}

export function Table({ columns, data, onRowClick }) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>{columns.map((c, i) => <th key={i}>{c.header}</th>)}</tr>
        </thead>
        <tbody>
          {data.map((row, idx) => (
            <tr key={row.id ?? `row-${idx}`} onClick={() => onRowClick?.(row)} style={{ cursor: onRowClick ? 'pointer' : 'default' }}>
              {columns.map((c, j) => <td key={j}>{c.render ? c.render(row) : row[c.accessor]}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function Pagination({ page, total, pageSize, onChange }) {
  const pages = Math.ceil(total / pageSize);
  if (pages <= 1) return null;

  const getPageNumbers = () => {
    const maxVisible = 5;
    if (pages <= maxVisible) {
      return Array.from({ length: pages }, (_, i) => i + 1);
    }
    const half = Math.floor(maxVisible / 2);
    let start = Math.max(1, page - half);
    let end = Math.min(pages, start + maxVisible - 1);
    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1);
    }
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  };

  return (
    <div className="pagination">
      <span className="pagination-info">Показано {((page-1)*pageSize)+1}-{Math.min(page*pageSize, total)} из {total}</span>
      <div className="pagination-buttons">
        <Button size="sm" variant="outline" disabled={page <= 1} onClick={() => onChange(page - 1)}>←</Button>
        {getPageNumbers().map(p => (
          <Button key={p} size="sm" variant={p === page ? 'primary' : 'outline'} onClick={() => onChange(p)}>{p}</Button>
        ))}
        <Button size="sm" variant="outline" disabled={page >= pages} onClick={() => onChange(page + 1)}>→</Button>
      </div>
    </div>
  );
}

export function StatCard({ icon, value, label, color = 'blue' }) {
  const colors = { blue: '#dbeafe', green: '#dcfce7', yellow: '#fef3c7', red: '#fee2e2' };
  const textColors = { blue: '#2563eb', green: '#16a34a', yellow: '#f59e0b', red: '#dc2626' };
  return (
    <div className="card stat-card">
      <div className="stat-icon" style={{ background: colors[color], color: textColors[color] }}>{icon}</div>
      <div className="stat-value" style={{ color: textColors[color] }}>{value}</div>
      <div className="stat-label">{label}</div>
    </div>
  );
}

export function Tabs({ tabs, active, onChange }) {
  return (
    <div className="tabs">
      {tabs.map(t => (
        <div key={t.value} className={`tab ${active === t.value ? 'active' : ''}`} onClick={() => onChange(t.value)}>
          {t.label}
        </div>
      ))}
    </div>
  );
}

export function Empty({ message = 'Нет данных' }) {
  return <div className="empty-state"><p>{message}</p></div>;
}

export function Loader() {
  return <div className="loader"><div className="spinner" /></div>;
}

export function PageHeader({ title, action }) {
  return (
    <div className="page-header">
      <h1 className="page-title">{title}</h1>
      {action}
    </div>
  );
}
