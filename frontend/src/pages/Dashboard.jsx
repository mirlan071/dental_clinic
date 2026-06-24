import { useState, useEffect } from 'react';
import { Card, StatCard, Badge, Loader } from '../components/UI';
import { appointments as appointmentsApi, patients as patientsApi, doctors as doctorsApi, finance as financeApi } from '../api';
import './Dashboard.css';

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
    CANCELLED: { v: 'red', t: 'Отменена' },
    NO_SHOW: { v: 'gray', t: 'Неявка' },
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

  const [stats, setStats] = useState({ patients: 0, doctors: 0, todayAppointments: 0, revenue: 0 });
  const [todayAppts, setTodayAppts] = useState([]);
  const [doctorLoad, setDoctorLoad] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const todayStr = today.toISOString().split('T')[0];
        const [pRes, dRes, aRes, invRes] = await Promise.all([
          patientsApi.list({ page: 0, size: 1 }),
          doctorsApi.list({ page: 0, size: 1 }),
          appointmentsApi.list({ page: 0, size: 100 }),
          financeApi.invoices.list({ page: 0, size: 200 }),
        ]);

        const totalPatients = pRes.data.data?.totalElements || 0;
        const totalDoctors = dRes.data.data?.totalElements || 0;
        const allAppts = aRes.data.data?.content || [];
        const invoices = invRes.data.data?.content || [];

        const todayApptsList = allAppts.filter(a => {
          const apptDate = new Date(a.startTime).toISOString().split('T')[0];
          return apptDate === todayStr;
        }).sort((a, b) => new Date(a.startTime) - new Date(b.startTime));

        const revenue = invoices
          .filter(i => i.status === 'PAID')
          .reduce((s, i) => s + Number(i.paidAmount), 0);

        const doctorMap = {};
        allAppts.forEach(a => {
          if (a.doctor) {
            const key = a.doctor.id;
            if (!doctorMap[key]) doctorMap[key] = { name: `${a.doctor.firstName} ${a.doctor.lastName} — ${a.doctor.specialization}`, total: 0, completed: 0 };
            doctorMap[key].total++;
            if (a.status === 'COMPLETED') doctorMap[key].completed++;
          }
        });
        const loadList = Object.values(doctorMap)
          .map(d => ({ ...d, load: d.total > 0 ? Math.round((d.completed / d.total) * 100) : 0 }))
          .sort((a, b) => b.total - a.total)
          .slice(0, 5);

        setStats({ patients: totalPatients, doctors: totalDoctors, todayAppointments: todayApptsList.length, revenue });
        setTodayAppts(todayApptsList);
        setDoctorLoad(loadList);
      } catch (err) {
        console.error('Failed to load dashboard', err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <div style={{ padding: 24 }}><Loader /></div>;

  return (
    <div className="dashboard">
      <h1 className="page-title" style={{ marginBottom: 24 }}>Добро пожаловать! 👋</h1>

      <div className="grid grid-4" style={{ marginBottom: 24 }}>
        <StatCard icon="👥" value={stats.patients} label="Пациентов" color="blue" />
        <StatCard icon="👨‍⚕️" value={stats.doctors} label="Врачей" color="green" />
        <StatCard icon="📅" value={stats.todayAppointments} label="Записей сегодня" color="yellow" />
        <StatCard icon="💰" value={`${(stats.revenue / 1000).toFixed(0)}K`} label="Выручка (сом)" color="red" />
      </div>

      <div className="grid grid-2">
        <Card>
          <h3 className="card-title" style={{ marginBottom: 16 }}>Сегодняшние записи</h3>
          {todayAppts.length === 0 ? (
            <p style={{ textAlign: 'center', color: '#64748b', padding: 20 }}>Нет записей на сегодня</p>
          ) : (
            <table>
              <thead><tr><th>Время</th><th>Пациент</th><th>Врач</th><th>Статус</th></tr></thead>
              <tbody>
                {todayAppts.map(a => (
                  <tr key={a.id}>
                    <td><b>{new Date(a.startTime).toLocaleTimeString('ru', { hour: '2-digit', minute: '2-digit' })}</b></td>
                    <td>{a.patient?.firstName} {a.patient?.lastName}</td>
                    <td>{a.doctor?.firstName} {a.doctor?.lastName}</td>
                    <td>{statusBadge(a.status)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
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

      {doctorLoad.length > 0 && (
        <Card style={{ marginTop: 20 }}>
          <h3 className="card-title" style={{ marginBottom: 16 }}>Загрузка врачей</h3>
          <div className="doctor-load">
            {doctorLoad.map((d, i) => {
              const colors = ['#2563eb', '#16a34a', '#f59e0b', '#ef4444', '#8b5cf6'];
              return (
                <div key={i} className="doctor-load-item">
                  <div className="doctor-load-header">
                    <span>{d.name}</span>
                    <span style={{ color: '#64748b' }}>{d.load}%</span>
                  </div>
                  <div className="progress-bar">
                    <div className="progress-fill" style={{ width: `${d.load}%`, background: colors[i % colors.length] }} />
                  </div>
                </div>
              );
            })}
          </div>
        </Card>
      )}
    </div>
  );
}
