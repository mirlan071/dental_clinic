import { Card, StatCard, Badge } from '../components/UI';
import './Dashboard.css';

const DEMO_APPTS = [
  { id: 1, startTime: new Date().setHours(9, 0), patientFirstName: 'Азамат', patientLastName: 'Токтосунов', doctorFirstName: 'Джон', doctorLastName: 'Смит', status: 'SCHEDULED' },
  { id: 2, startTime: new Date().setHours(9, 30), patientFirstName: 'Айбек', patientLastName: 'Мамбетов', doctorFirstName: 'Эмили', doctorLastName: 'Джонс', status: 'IN_PROGRESS' },
  { id: 3, startTime: new Date().setHours(10, 0), patientFirstName: 'Нурзат', patientLastName: 'Кадырова', doctorFirstName: 'Джон', doctorLastName: 'Смит', status: 'SCHEDULED' },
  { id: 4, startTime: new Date().setHours(10, 30), patientFirstName: 'Султан', patientLastName: 'Жумабаев', doctorFirstName: 'Азамат', doctorLastName: 'Козлов', status: 'SCHEDULED' },
  { id: 5, startTime: new Date().setHours(11, 0), patientFirstName: 'Бегимай', patientLastName: 'Асанова', doctorFirstName: 'Эмили', doctorLastName: 'Джонс', status: 'SCHEDULED' },
];

function getMonthDays(year, month) {
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const daysInMonth = lastDay.getDate();
  const startDayOfWeek = (firstDay.getDay() + 6) % 7;
  const totalCells = Math.ceil((startDayOfWeek + daysInMonth) / 7) * 7;
  const days = [];
  for (let i = 0; i < totalCells; i++) {
    const dayNum = i - startDayOfWeek + 1;
    days.push(dayNum >= 1 && dayNum <= daysInMonth ? dayNum : 0);
  }
  return days;
}

const statusBadge = (s) => {
  const map = {
    SCHEDULED: { v: 'blue', t: 'Запланирована' },
    IN_PROGRESS: { v: 'yellow', t: 'В процессе' },
    COMPLETED: { v: 'green', t: 'Завершена' },
  };
  const { v, t } = map[s] || { v: 'gray', t: s };
  return <Badge variant={v}>{t}</Badge>;
};

export default function Dashboard() {
  const today = new Date();
  const todayDate = today.getDate();
  const monthNames = ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'];
  const dayNames = ['Пн','Вт','Ср','Чт','Пт','Сб','Вс'];
  const monthDays = getMonthDays(today.getFullYear(), today.getMonth());

  return (
    <div className="dashboard">
      <h1 className="page-title" style={{ marginBottom: 24 }}>Добро пожаловать! 👋</h1>

      <div className="grid grid-4" style={{ marginBottom: 24 }}>
        <StatCard icon="👥" value="248" label="Пациентов" color="blue" />
        <StatCard icon="👨‍⚕️" value="12" label="Врачей" color="green" />
        <StatCard icon="📅" value="18" label="Записей сегодня" color="yellow" />
        <StatCard icon="💰" value="156K" label="Выручка (сом)" color="red" />
      </div>

      <div className="grid grid-2">
        <Card>
          <h3 className="card-title" style={{ marginBottom: 16 }}>Сегодняшние записи</h3>
          <table>
            <thead><tr><th>Время</th><th>Пациент</th><th>Врач</th><th>Статус</th></tr></thead>
            <tbody>
              {DEMO_APPTS.map(a => (
                <tr key={a.id}>
                  <td><b>{new Date(a.startTime).toLocaleTimeString('ru', { hour: '2-digit', minute: '2-digit' })}</b></td>
                  <td>{a.patientFirstName} {a.patientLastName}</td>
                  <td>{a.doctorFirstName} {a.doctorLastName}</td>
                  <td>{statusBadge(a.status)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>

        <Card>
          <h3 className="card-title" style={{ marginBottom: 16 }}>{monthNames[today.getMonth()]} {today.getFullYear()}</h3>
          <div className="calendar-grid">
            {dayNames.map(d => <div key={d} className="cal-header">{d}</div>)}
            {monthDays.map((day, i) => (
              <div key={i} className={`cal-day ${day === todayDate ? 'today' : ''} ${day === 0 ? 'muted' : ''}`}>
                {day || ''}
              </div>
            ))}
          </div>
        </Card>
      </div>

      <Card style={{ marginTop: 20 }}>
        <h3 className="card-title" style={{ marginBottom: 16 }}>Загрузка врачей</h3>
        <div className="doctor-load">
          {[
            { name: 'Джон Смит — Терапевт', load: 75, color: '#2563eb' },
            { name: 'Эмили Джонс — Ортодонт', load: 67, color: '#16a34a' },
            { name: 'Азамат Козлов — Хирург', load: 50, color: '#f59e0b' },
          ].map((d, i) => (
            <div key={i} className="doctor-load-item">
              <div className="doctor-load-header">
                <span>{d.name}</span>
                <span style={{ color: '#64748b' }}>{d.load}%</span>
              </div>
              <div className="progress-bar">
                <div className="progress-fill" style={{ width: `${d.load}%`, background: d.color }} />
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}
