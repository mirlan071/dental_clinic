import { useState } from 'react';
import { Card, PageHeader, Button, Badge, Table, Tabs, Modal, Input } from '../components/UI';
import { DEMO_APPOINTMENTS } from '../data/demo';

const statusTabs = [
  { value: 'all', label: 'Все' },
  { value: 'SCHEDULED', label: 'Запланированы' },
  { value: 'IN_PROGRESS', label: 'В процессе' },
  { value: 'COMPLETED', label: 'Завершены' },
  { value: 'NO_SHOW', label: 'Неявка' },
];

const statusBadge = (s) => {
  const map = { SCHEDULED: { v: 'blue', t: 'Запланирована' }, IN_PROGRESS: { v: 'yellow', t: 'В процессе' }, COMPLETED: { v: 'green', t: 'Завершена' }, CANCELLED: { v: 'red', t: 'Отменена' }, NO_SHOW: { v: 'gray', t: 'Неявка' } };
  const { v, t } = map[s] || { v: 'gray', t: s };
  return <Badge variant={v}>{t}</Badge>;
};

export default function Appointments() {
  const [data, setData] = useState(DEMO_APPOINTMENTS);
  const [tab, setTab] = useState('all');
  const [detail, setDetail] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ patientFirstName: '', patientLastName: '', doctorFirstName: '', doctorLastName: '', startTime: '', serviceNames: '' });

  const filtered = tab === 'all' ? data : data.filter(a => a.status === tab);

  const handleStatus = (id, status) => {
    setData(data.map(a => a.id === id ? { ...a, status } : a));
  };

  const handleSave = () => {
    if (!form.patientFirstName || !form.startTime) return alert('Заполните обязательные поля');
    setData([{ ...form, id: Date.now(), serviceNames: form.serviceNames ? form.serviceNames.split(',').map(s => s.trim()) : [], status: 'SCHEDULED' }, ...data]);
    setModalOpen(false);
    setForm({ patientFirstName: '', patientLastName: '', doctorFirstName: '', doctorLastName: '', startTime: '', serviceNames: '' });
  };

  const columns = [
    { header: 'Время', render: r => <div><b>{new Date(r.startTime).toLocaleTimeString('ru', { hour: '2-digit', minute: '2-digit' })}</b><br/><span style={{ fontSize: 12, color: '#64748b' }}>{new Date(r.startTime).toLocaleDateString('ru')}</span></div> },
    { header: 'Пациент', render: r => `${r.patientFirstName} ${r.patientLastName}` },
    { header: 'Врач', render: r => `${r.doctorFirstName} ${r.doctorLastName}` },
    { header: 'Услуги', render: r => r.serviceNames?.join(', ') || '—' },
    { header: 'Статус', render: r => statusBadge(r.status) },
    { header: '', render: r => (
      <div style={{ display: 'flex', gap: 4 }}>
        {r.status === 'SCHEDULED' && <Button size="sm" onClick={() => handleStatus(r.id, 'IN_PROGRESS')}>Начать</Button>}
        {r.status === 'IN_PROGRESS' && <Button size="sm" variant="success" onClick={() => handleStatus(r.id, 'COMPLETED')}>Завершить</Button>}
        {(r.status === 'SCHEDULED' || r.status === 'IN_PROGRESS') && <Button size="sm" variant="danger" onClick={() => handleStatus(r.id, 'CANCELLED')}>✕</Button>}
      </div>
    )},
  ];

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Записи на приём" action={<Button onClick={() => setModalOpen(true)}>+ Новая запись</Button>} />
      <Card>
        <Tabs tabs={statusTabs} active={tab} onChange={setTab} />
        <Table columns={columns} data={filtered} onRowClick={setDetail} />
      </Card>

      <Modal open={!!detail} onClose={() => setDetail(null)} title="Детали записи" actions={<Button variant="outline" onClick={() => setDetail(null)}>Закрыть</Button>}>
        {detail && (
          <div>
            <p><b>Пациент:</b> {detail.patientFirstName} {detail.patientLastName}</p>
            <p><b>Врач:</b> {detail.doctorFirstName} {detail.doctorLastName}</p>
            <p><b>Время:</b> {new Date(detail.startTime).toLocaleString('ru')}</p>
            <p><b>Услуги:</b> {detail.serviceNames?.join(', ')}</p>
            <p><b>Статус:</b> {statusBadge(detail.status)}</p>
          </div>
        )}
      </Modal>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Новая запись" actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave}>Сохранить</Button>
        </>
      }>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
          <Input label="Имя пациента *" value={form.patientFirstName} onChange={e => setForm({...form, patientFirstName: e.target.value})} />
          <Input label="Фамилия пациента" value={form.patientLastName} onChange={e => setForm({...form, patientLastName: e.target.value})} />
          <Input label="Имя врача" value={form.doctorFirstName} onChange={e => setForm({...form, doctorFirstName: e.target.value})} />
          <Input label="Фамилия врача" value={form.doctorLastName} onChange={e => setForm({...form, doctorLastName: e.target.value})} />
        </div>
        <Input label="Дата и время *" type="datetime-local" value={form.startTime} onChange={e => setForm({...form, startTime: e.target.value})} />
        <Input label="Услуги (через запятую)" value={form.serviceNames} onChange={e => setForm({...form, serviceNames: e.target.value})} placeholder="Осмотр, Рентген" />
      </Modal>
    </div>
  );
}
