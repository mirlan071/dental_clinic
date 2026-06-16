import { useState } from 'react';
import { Card, PageHeader, Button, Modal, Input } from '../components/UI';
import { DEMO_RECORDS } from '../data/demo';
import './Records.css';

export default function Records() {
  const [data, setData] = useState(DEMO_RECORDS);
  const [detail, setDetail] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ patientFirstName: '', patientLastName: '', doctorFirstName: '', doctorLastName: '', diagnosis: '', treatment: '', recommendations: '' });

  const handleSave = () => {
    if (!form.patientFirstName || !form.diagnosis) return alert('Заполните обязательные поля');
    setData([{ ...form, id: Date.now(), createdAt: new Date().toISOString() }, ...data]);
    setModalOpen(false);
    setForm({ patientFirstName: '', patientLastName: '', doctorFirstName: '', doctorLastName: '', diagnosis: '', treatment: '', recommendations: '' });
  };

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Медицинские карты" action={<Button onClick={() => setModalOpen(true)}>+ Новая запись</Button>} />
      <div className="grid grid-2">
        {data.map(r => (
          <Card key={r.id} onClick={() => setDetail(r)} style={{ cursor: 'pointer' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 12 }}>
              <div style={{ width: 48, height: 48, borderRadius: '50%', background: '#dbeafe', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20 }}>👤</div>
              <div>
                <h3 style={{ fontSize: 16 }}>{r.patientFirstName} {r.patientLastName}</h3>
                <p style={{ fontSize: 13, color: '#64748b' }}>Врач: {r.doctorFirstName} {r.doctorLastName}</p>
              </div>
            </div>
            <div className="timeline">
              <div className="timeline-item done">
                <div style={{ fontSize: 13, color: '#64748b' }}>{r.createdAt ? new Date(r.createdAt).toLocaleDateString('ru') : '—'}</div>
                <div style={{ fontSize: 14, marginTop: 2 }}><b>Диагноз:</b> {r.diagnosis}</div>
                <div style={{ fontSize: 13, color: '#64748b', marginTop: 2 }}><b>Лечение:</b> {r.treatment || '—'}</div>
                {r.recommendations && <div style={{ fontSize: 13, color: '#64748b', marginTop: 2 }}><b>Рекомендации:</b> {r.recommendations}</div>}
              </div>
            </div>
          </Card>
        ))}
      </div>

      <Modal open={!!detail} onClose={() => setDetail(null)} title="Медицинская карта" actions={<Button variant="outline" onClick={() => setDetail(null)}>Закрыть</Button>}>
        {detail && (
          <div>
            <p><b>Пациент:</b> {detail.patientFirstName} {detail.patientLastName}</p>
            <p><b>Врач:</b> {detail.doctorFirstName} {detail.doctorLastName}</p>
            <p><b>Дата:</b> {detail.createdAt ? new Date(detail.createdAt).toLocaleDateString('ru') : '—'}</p>
            <hr style={{ margin: '12px 0', border: 'none', borderTop: '1px solid #e2e8f0' }} />
            <p><b>Диагноз:</b></p><p style={{ color: '#64748b' }}>{detail.diagnosis}</p>
            <p style={{ marginTop: 8 }}><b>Лечение:</b></p><p style={{ color: '#64748b' }}>{detail.treatment || '—'}</p>
            {detail.recommendations && <><p style={{ marginTop: 8 }}><b>Рекомендации:</b></p><p style={{ color: '#64748b' }}>{detail.recommendations}</p></>}
          </div>
        )}
      </Modal>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Новая медицинская запись" actions={
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
        <Input label="Диагноз *" value={form.diagnosis} onChange={e => setForm({...form, diagnosis: e.target.value})} />
        <Input label="Лечение" value={form.treatment} onChange={e => setForm({...form, treatment: e.target.value})} />
        <Input label="Рекомендации" value={form.recommendations} onChange={e => setForm({...form, recommendations: e.target.value})} />
      </Modal>
    </div>
  );
}
