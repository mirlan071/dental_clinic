import { useState, useEffect, useCallback } from 'react';
import { Card, PageHeader, Button, Badge, Table, Modal, Input, Select, Loader } from '../components/UI';
import { patients as patientsApi } from '../api';
import { useToast } from '../components/ToastContext';

const genderOptions = [
  { value: 'MALE', label: 'Мужской' },
  { value: 'FEMALE', label: 'Женский' },
];

const emptyForm = { firstName: '', lastName: '', phone: '', dateOfBirth: '', gender: 'MALE', email: '', address: '', insurancePolicy: '', notes: '' };

export default function Patients() {
  const { toast } = useToast();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [editId, setEditId] = useState(null);
  const [saving, setSaving] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      if (search) {
        const res = await patientsApi.search(search);
        const items = res.data.data || [];
        setData(items);
        setTotalElements(items.length);
        setTotalPages(1);
      } else {
        const res = await patientsApi.list({ page, size: 8, sort: 'id,desc' });
        const paged = res.data.data;
        setData(paged.content || []);
        setTotalPages(paged.totalPages || 0);
        setTotalElements(paged.totalElements || 0);
      }
    } catch (err) {
      toast.error('Не удалось загрузить пациентов');
    } finally {
      setLoading(false);
    }
  }, [page, search, toast]);

  useEffect(() => { fetchData(); }, [fetchData]);

  useEffect(() => { setPage(0); }, [search]);

  const handleSave = async () => {
    if (!form.firstName || !form.lastName || !form.phone) return toast.warning('Заполните обязательные поля');
    setSaving(true);
    try {
      if (editId) {
        await patientsApi.update(editId, form);
        toast.success('Пациент обновлён');
      } else {
        await patientsApi.create(form);
        toast.success('Пациент создан');
      }
      setModalOpen(false);
      setForm(emptyForm);
      setEditId(null);
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка сохранения');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Удалить пациента?')) return;
    try {
      await patientsApi.delete(id);
      toast.success('Пациент удалён');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка удаления');
    }
  };

  const handleEdit = (row) => {
    setForm({
      firstName: row.firstName || '',
      lastName: row.lastName || '',
      phone: row.phone || '',
      dateOfBirth: row.dateOfBirth || '',
      gender: row.gender || 'MALE',
      email: row.email || '',
      address: row.address || '',
      insurancePolicy: row.insurancePolicy || '',
      notes: row.notes || '',
    });
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

  const pageSize = 8;
  const startItem = page * pageSize + 1;
  const endItem = Math.min((page + 1) * pageSize, totalElements);

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Пациенты" action={<Button onClick={() => { setForm(emptyForm); setEditId(null); setModalOpen(true); }}>+ Новый пациент</Button>} />
      <Card>
        <div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
          <div className="search-bar" style={{ flex: 1 }}>
            <input className="form-input" placeholder="Поиск по имени, телефону..." value={search} onChange={e => setSearch(e.target.value)} />
          </div>
        </div>
        {loading ? <Loader /> : (
          <>
            <Table columns={columns} data={data} />
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 16, fontSize: 13, color: '#64748b' }}>
              <span>{totalElements > 0 ? `Показано ${startItem}-${endItem} из ${totalElements}` : 'Нет данных'}</span>
              <div style={{ display: 'flex', gap: 4 }}>
                <Button size="sm" variant="outline" disabled={page <= 0} onClick={() => setPage(page - 1)}>←</Button>
                {Array.from({ length: totalPages }, (_, i) => i).map(p => (
                  <Button key={p} size="sm" variant={p === page ? 'primary' : 'outline'} onClick={() => setPage(p)}>{p + 1}</Button>
                ))}
                <Button size="sm" variant="outline" disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>→</Button>
              </div>
            </div>
          </>
        )}
      </Card>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editId ? 'Редактировать пациента' : 'Новый пациент'} actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave} disabled={saving}>{saving ? 'Сохранение...' : 'Сохранить'}</Button>
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
