import { useState } from 'react';
import { Card, PageHeader, Button, Badge, Table, Modal, Input } from '../components/UI';
import { DEMO_DOCTORS } from '../data/demo';

const emptyForm = { firstName: '', lastName: '', email: '', specialization: '', licenseNumber: '' };

export default function Doctors() {
  const [data, setData] = useState(DEMO_DOCTORS);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);

  const handleSave = () => {
    if (!form.firstName || !form.lastName || !form.specialization) return alert('Заполните обязательные поля');
    setData([{ ...form, id: Date.now(), active: true }, ...data]);
    setModalOpen(false);
    setForm(emptyForm);
  };

  const columns = [
    { header: 'ID', accessor: 'id' },
    { header: 'Имя', render: r => <b>{r.firstName} {r.lastName}</b> },
    { header: 'Специализация', render: r => <Badge variant="blue">{r.specialization}</Badge> },
    { header: 'Лицензия', accessor: 'licenseNumber' },
    { header: 'Статус', render: r => <Badge variant={r.active ? 'green' : 'gray'}>{r.active ? 'Активный' : 'Неактивный'}</Badge> },
  ];

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Врачи" action={<Button onClick={() => setModalOpen(true)}>+ Новый врач</Button>} />
      <Card>
        <Table columns={columns} data={data} />
      </Card>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Новый врач" actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave}>Сохранить</Button>
        </>
      }>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
          <Input label="Фамилия *" value={form.lastName} onChange={e => setForm({...form, lastName: e.target.value})} />
          <Input label="Имя *" value={form.firstName} onChange={e => setForm({...form, firstName: e.target.value})} />
          <Input label="Email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} />
          <Input label="Специализация *" value={form.specialization} onChange={e => setForm({...form, specialization: e.target.value})} />
          <Input label="Лицензия" value={form.licenseNumber} onChange={e => setForm({...form, licenseNumber: e.target.value})} />
        </div>
      </Modal>
    </div>
  );
}
