import { useState, useEffect, useCallback } from 'react';
import { Card, PageHeader, Button, Badge, Table, Modal, Input, Loader } from '../components/UI';
import { doctors as doctorsApi } from '../api';
import { useToast } from '../components/ToastContext';

const emptyForm = { firstName: '', lastName: '', email: '', specialization: '', licenseNumber: '', biography: '' };

export default function Doctors() {
  const { toast } = useToast();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [editId, setEditId] = useState(null);
  const [saving, setSaving] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await doctorsApi.list({ page: 0, size: 100, sort: 'id,desc' });
      setData(res.data.data?.content || []);
    } catch (err) {
      toast.error('Не удалось загрузить врачей');
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleSave = async () => {
    if (!form.firstName || !form.lastName || !form.specialization) return toast.warning('Заполните обязательные поля');
    setSaving(true);
    try {
      if (editId) {
        await doctorsApi.update(editId, {
          specialization: form.specialization,
          licenseNumber: form.licenseNumber,
          biography: form.biography,
        });
        toast.success('Врач обновлён');
      } else {
        await doctorsApi.create(form);
        toast.success('Врач создан');
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
    if (!confirm('Удалить врача?')) return;
    try {
      await doctorsApi.delete(id);
      toast.success('Врач удалён');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка удаления');
    }
  };

  const handleEdit = (row) => {
    setForm({
      firstName: row.firstName || '',
      lastName: row.lastName || '',
      email: row.email || '',
      specialization: row.specialization || '',
      licenseNumber: row.licenseNumber || '',
      biography: row.biography || '',
    });
    setEditId(row.id);
    setModalOpen(true);
  };

  const columns = [
    { header: 'ID', accessor: 'id' },
    { header: 'Имя', render: r => <b>{r.firstName} {r.lastName}</b> },
    { header: 'Специализация', render: r => <Badge variant="blue">{r.specialization}</Badge> },
    { header: 'Лицензия', accessor: 'licenseNumber' },
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
      <PageHeader title="Врачи" action={<Button onClick={() => { setForm(emptyForm); setEditId(null); setModalOpen(true); }}>+ Новый врач</Button>} />
      <Card>
        {loading ? <Loader /> : <Table columns={columns} data={data} />}
      </Card>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editId ? 'Редактировать врача' : 'Новый врач'} actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave} disabled={saving}>{saving ? 'Сохранение...' : 'Сохранить'}</Button>
        </>
      }>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
          <Input label="Фамилия *" value={form.lastName} onChange={e => setForm({...form, lastName: e.target.value})} />
          <Input label="Имя *" value={form.firstName} onChange={e => setForm({...form, firstName: e.target.value})} />
          <Input label="Email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} />
          <Input label="Специализация *" value={form.specialization} onChange={e => setForm({...form, specialization: e.target.value})} />
          <Input label="Лицензия" value={form.licenseNumber} onChange={e => setForm({...form, licenseNumber: e.target.value})} />
        </div>
        <Input label="Биография" value={form.biography} onChange={e => setForm({...form, biography: e.target.value})} />
      </Modal>
    </div>
  );
}
