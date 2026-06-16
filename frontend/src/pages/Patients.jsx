import { useState } from 'react';
import { Card, PageHeader, Button, Badge, Table, Modal, Input, Select } from '../components/UI';
import { DEMO_PATIENTS } from '../data/demo';

const genderOptions = [
  { value: 'MALE', label: 'Мужской' },
  { value: 'FEMALE', label: 'Женский' },
];

const emptyForm = { firstName: '', lastName: '', phone: '', dateOfBirth: '', gender: 'MALE', email: '', address: '', insurancePolicy: '', notes: '' };

export default function Patients() {
  const [data, setData] = useState(DEMO_PATIENTS);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [editId, setEditId] = useState(null);

  const filtered = search
    ? data.filter(p => `${p.firstName} ${p.lastName} ${p.phone}`.toLowerCase().includes(search.toLowerCase()))
    : data;

  const pageSize = 8;
  const totalPages = Math.ceil(filtered.length / pageSize);
  const paged = filtered.slice((page - 1) * pageSize, page * pageSize);

  const handleSave = () => {
    if (!form.firstName || !form.lastName || !form.phone) return alert('Заполните обязательные поля');
    if (editId) {
      setData(data.map(p => p.id === editId ? { ...p, ...form } : p));
    } else {
      setData([{ ...form, id: Date.now(), active: true }, ...data]);
    }
    setModalOpen(false);
    setForm(emptyForm);
    setEditId(null);
  };

  const handleDelete = (id) => {
    if (!confirm('Удалить пациента?')) return;
    setData(data.filter(p => p.id !== id));
  };

  const handleEdit = (row) => {
    setForm(row);
    setEditId(row.id);
    setModalOpen(true);
  };

  const columns = [
    { header: 'ID', accessor: 'id' },
    { header: 'Имя', render: r => <b>{r.firstName} {r.lastName}</b> },
    { header: 'Телефон', accessor: 'phone' },
    { header: 'Дата рождения', render: r => r.dateOfBirth ? new Date(r.dateOfBirth).toLocaleDateString('ru') : '—' },
    { header: 'Пол', render: r => r.gender === 'MALE' ? 'М' : 'Ж' },
    { header: 'Страховка', render: r => r.insurancePolicy || '—' },
    { header: 'Статус', render: r => <Badge variant={r.active ? 'green' : 'gray'}>{r.active ? 'Активный' : 'Неактивный'}</Badge> },
    { header: '', render: r => (
      <div style={{ display: 'flex', gap: 4 }}>
        <Button size="sm" variant="outline" onClick={e => { e.stopPropagation(); handleEdit(r); }}>✏️</Button>
        <Button size="sm" variant="danger" onClick={e => { e.stopPropagation(); handleDelete(r.id); }}>✕</Button>
      </div>
    )},
  ];

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Пациенты" action={<Button onClick={() => { setForm(emptyForm); setEditId(null); setModalOpen(true); }}>+ Новый пациент</Button>} />
      <Card>
        <div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
          <div className="search-bar" style={{ flex: 1 }}>
            <input className="form-input" placeholder="Поиск по имени, телефону..." value={search} onChange={e => { setSearch(e.target.value); setPage(1); }} />
          </div>
        </div>
        <Table columns={columns} data={paged} />
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 16, fontSize: 13, color: '#64748b' }}>
          <span>Показано {((page-1)*pageSize)+1}-{Math.min(page*pageSize, filtered.length)} из {filtered.length}</span>
          <div style={{ display: 'flex', gap: 4 }}>
            <Button size="sm" variant="outline" disabled={page<=1} onClick={() => setPage(page-1)}>←</Button>
            {Array.from({length: totalPages}, (_, i) => i+1).map(p => (
              <Button key={p} size="sm" variant={p===page?'primary':'outline'} onClick={() => setPage(p)}>{p}</Button>
            ))}
            <Button size="sm" variant="outline" disabled={page>=totalPages} onClick={() => setPage(page+1)}>→</Button>
          </div>
        </div>
      </Card>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editId ? 'Редактировать пациента' : 'Новый пациент'} actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave}>Сохранить</Button>
        </>
      }>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
          <Input label="Фамилия *" value={form.lastName} onChange={e => setForm({...form, lastName: e.target.value})} />
          <Input label="Имя *" value={form.firstName} onChange={e => setForm({...form, firstName: e.target.value})} />
          <Input label="Телефон *" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} />
          <Input label="Дата рождения" type="date" value={form.dateOfBirth} onChange={e => setForm({...form, dateOfBirth: e.target.value})} />
          <Select label="Пол" options={genderOptions} value={form.gender} onChange={e => setForm({...form, gender: e.target.value})} />
          <Input label="Email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} />
          <div style={{ gridColumn: '1 / -1' }}><Input label="Адрес" value={form.address} onChange={e => setForm({...form, address: e.target.value})} /></div>
          <Input label="Страховка" value={form.insurancePolicy} onChange={e => setForm({...form, insurancePolicy: e.target.value})} />
          <Input label="Заметки" value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} />
        </div>
      </Modal>
    </div>
  );
}
