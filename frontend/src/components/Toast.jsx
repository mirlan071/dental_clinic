import { useToast } from './ToastContext';
import './Toast.css';

const icons = { success: '✓', error: '✕', warning: '⚠', info: 'ℹ' };

export default function ToastContainer() {
  const { toasts, removeToast } = useToast();

  return (
    <div className="toast-container">
      {toasts.map(t => (
        <div key={t.id} className={`toast toast-${t.type}`} onClick={() => removeToast(t.id)}>
          <span className="toast-icon">{icons[t.type]}</span>
          <span className="toast-message">{t.message}</span>
          <span className="toast-close" onClick={e => { e.stopPropagation(); removeToast(t.id); }}>✕</span>
        </div>
      ))}
    </div>
  );
}
