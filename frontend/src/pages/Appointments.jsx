import { useState, useEffect, useCallback } from 'react';
import { Card, PageHeader, Button, Badge, Table, Tabs, Modal, Input, Select, Loader } from '../components/UI';
import { appointments as appointmentsApi, patients as patientsApi, doctors as doctorsApi, services as servicesApi } from '../api';
import { useToast } from '../components/ToastContext';

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
  const { toast } = useToast();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tab, setTab] = useState('all');
  const [detail, setDetail] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ patientId: '', doctorId: '', serviceIds: [], startTime: '', notes: '' });

  const [patientList, setPatientList] = useState([]);
  const [doctorList, setDoctorList] = useState([]);
  const [serviceList, setServiceList] = useState([]);
  const [saving, setSaving] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await appointmentsApi.list({ page: 0, size: 200 });
      setData(res.data.data?.content || []);
    } catch (err) {
      toast.error('Не удалось загрузить записи');
    } finally {
      setLoading(false);
    }
  }, [toast]);

  const fetchDropdowns = useCallback(async () => {
    try {
      const [pRes, dRes, sRes] = await Promise.all([
        patientsApi.list({ page: 0, size: 500 }),
        doctorsApi.list({ page: 0, size: 500 }),
        servicesApi.list({ page: 0, size: 500 }),
      ]);
      setPatientList(pRes.data.data?.content || []);
      setDoctorList(dRes.data.data?.content || []);
      setServiceList(sRes.data.data?.content || []);
    } catch (err) {
      console.error('Failed to load dropdowns', err);
    }
  }, []);

  useEffect(() => { fetchData(); fetchDropdowns(); }, [fetchData, fetchDropdowns]);

  const filtered = tab === 'all' ? data : data.filter(a => a.status === tab);

  const handleStatus = async (id, status) => {
    try {
      await appointmentsApi.updateStatus(id, status);
      toast.success('Статус обновлён');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка обновления статуса');
    }
  };

  const handleSave = async () => {
    if (!form.patientId || !form.doctorId || !form.serviceIds.length || !form.startTime) return toast.warning('Заполните обязательные поля');
    setSaving(true);
    try {
      await appointmentsApi.create({
        patientId: parseInt(form.patientId),
        doctorId: parseInt(form.doctorId),
        serviceIds: form.serviceIds.map(Number),
        startTime: form.startTime,
        notes: form.notes,
      });
      toast.success('Запись создана');
      setModalOpen(false);
      setForm({ patientId: '', doctorId: '', serviceIds: [], startTime: '', notes: '' });
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка создания записи');
    } finally {
      setSaving(false);
    }
  };

  const toggleService = (id) => {
    setForm(f => ({
      ...f,
      serviceIds: f.serviceIds.includes(id)
        ? f.serviceIds.filter(s => s !== id)
        : [...f.serviceIds, id],
    }));
  };

  const patientOpts = patientList.map(p => ({ value: p.id, label: `${p.firstName} ${p.lastName}` }));
  const doctorOpts = doctorList.map(d => ({ value: d.id, label: `${d.firstName} ${d.lastName} — ${d.specialization}` }));

  const columns = [
    { header: 'Время', render: r => <div><b>{new Date(r.startTime).toLocaleTimeString('ru', { hour: '2-digit', minute: '2-digit' })}</b><br/><span style={{ fontSize: 12, color: '#64748b' }}>{new Date(r.startTime).toLocaleDateString('ru')}</span></div> },
    { header: 'Пациент', render: r => r.patient ? `${r.patient.firstName} ${r.patient.lastName}` : '—' },
    { header: 'Врач', render: r => r.doctor ? `${r.doctor.firstName} ${r.doctor.lastName}` : '—' },
    { header: 'Услуги', render: r => r.services?.map(s => s.name).join(', ') || '—' },
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
        {loading ? <Loader /> : <Table columns={columns} data={filtered} onRowClick={setDetail} />}
      </Card>

      <Modal open={!!detail} onClose={() => setDetail(null)} title="Детали записи" actions={<Button variant="outline" onClick={() => setDetail(null)}>Закрыть</Button>}>
        {detail && (
          <div>
            <p><b>Пациент:</b> {detail.patient?.firstName} {detail.patient?.lastName}</p>
            <p><b>Врач:</b> {detail.doctor?.firstName} {detail.doctor?.lastName}</p>
            <p><b>Время:</b> {new Date(detail.startTime).toLocaleString('ru')}</p>
            <p><b>Услуги:</b> {detail.services?.map(s => s.name).join(', ')}</p>
            <p><b>Статус:</b> {statusBadge(detail.status)}</p>
            {detail.notes && <p><b>Заметки:</b> {detail.notes}</p>}
          </div>
        )}
      </Modal>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Новая запись" actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave} disabled={saving}>{saving ? 'Сохранение...' : 'Сохранить'}</Button>
        </>
      }>
        <Select label="Пациент *" options={patientOpts} value={form.patientId} onChange={e => setForm({...form, patientId: e.target.value})} />
        <Select label="Врач *" options={doctorOpts} value={form.doctorId} onChange={e => setForm({...form, doctorId: e.target.value})} />
        <Input label="Дата и время *" type="datetime-local" value={form.startTime} onChange={e => setForm({...form, startTime: e.target.value})} />
        <div className="form-group">
          <label className="form-label">Услуги *</label>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
            {serviceList.map(s => (
              <Button
                key={s.id}
                size="sm"
                variant={form.serviceIds.includes(s.id) ? 'primary' : 'outline'}
                onClick={() => toggleService(s.id)}
              >
                {s.name}
              </Button>
            ))}
          </div>
        </div>
        <Input label="Заметки" value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} />
      </Modal>
    </div>
  );
}
