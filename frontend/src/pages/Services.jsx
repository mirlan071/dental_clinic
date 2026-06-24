import { useState, useEffect, useCallback } from 'react';
import { Card, PageHeader, Button, Badge, Modal, Input, Loader } from '../components/UI';
import { services as servicesApi } from '../api';
import { useToast } from '../components/ToastContext';

const categoryIcons = { 'Диагностика': '🔍', 'Лечение': '💉', 'Гигиена': '🧹', 'Хирургия': '⚡', 'Эстетика': '✨', 'Ортодонтия': '🦷', 'Протезирование': '🔧' };
const categoryBadge = (cat) => {
  const colors = { 'Диагностика': 'blue', 'Лечение': 'green', 'Гигиена': 'yellow', 'Хирургия': 'red', 'Эстетика': 'yellow', 'Ортодонтия': 'blue', 'Протезирование': 'gray' };
  return <Badge variant={colors[cat] || 'gray'}>{cat}</Badge>;
};

export default function Services() {
  const { toast } = useToast();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [view, setView] = useState('grid');
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ name: '', description: '', price: '', durationMinutes: '', category: '' });
  const [editId, setEditId] = useState(null);
  const [saving, setSaving] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await servicesApi.list({ page: 0, size: 200 });
      setData(res.data.data?.content || []);
    } catch (err) {
      toast.error('Не удалось загрузить услуги');
    } finally {
      setLoading(false);
    }
  }, [toast]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleSave = async () => {
    if (!form.name || !form.price || !form.category) return toast.warning('Заполните обязательные поля');
    setSaving(true);
    try {
      const payload = {
        name: form.name,
        description: form.description,
        price: parseFloat(form.price),
        durationMinutes: parseInt(form.durationMinutes) || 30,
        category: form.category,
      };
      if (editId) {
        await servicesApi.update(editId, payload);
        toast.success('Услуга обновлена');
      } else {
        await servicesApi.create(payload);
        toast.success('Услуга создана');
      }
      setModalOpen(false);
      setForm({ name: '', description: '', price: '', durationMinutes: '', category: '' });
      setEditId(null);
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка сохранения');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Удалить услугу?')) return;
    try {
      await servicesApi.delete(id);
      toast.success('Услуга удалена');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка удаления');
    }
  };

  const handleEdit = (s) => {
    setForm({
      name: s.name || '',
      description: s.description || '',
      price: s.price || '',
      durationMinutes: s.durationMinutes || '',
      category: s.category || '',
    });
    setEditId(s.id);
    setModalOpen(true);
  };

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Каталог услуг" action={
        <div style={{ display: 'flex', gap: 8 }}>
          <Button variant={view === 'grid' ? 'primary' : 'outline'} onClick={() => setView('grid')}>▦</Button>
          <Button variant={view === 'table' ? 'primary' : 'outline'} onClick={() => setView('table')}>☰</Button>
          <Button onClick={() => { setForm({ name: '', description: '', price: '', durationMinutes: '', category: '' }); setEditId(null); setModalOpen(true); }}>+ Новая услуга</Button>
        </div>
      } />

      {loading ? <Loader /> : (
        view === 'grid' ? (
          <div className="grid grid-3">
            {data.map(s => (
              <Card key={s.id}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                  <span style={{ fontSize: 28 }}>{categoryIcons[s.category] || '🦷'}</span>
                  {categoryBadge(s.category)}
                </div>
                <h3 style={{ margin: '12px 0 4px' }}>{s.name}</h3>
                <p style={{ fontSize: 13, color: '#64748b' }}>{s.description || '—'}</p>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 16 }}>
                  <b style={{ color: '#2563eb', fontSize: 18 }}>{Number(s.price).toLocaleString()} сом</b>
                  <span style={{ fontSize: 13, color: '#64748b' }}>{s.durationMinutes} мин</span>
                </div>
                <div style={{ display: 'flex', gap: 4, marginTop: 12 }}>
                  <Button size="sm" variant="outline" onClick={() => handleEdit(s)}>✏️</Button>
                  <Button size="sm" variant="danger" onClick={() => handleDelete(s.id)}>✕</Button>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <Card>
            <table>
              <thead><tr><th>Название</th><th>Категория</th><th>Цена</th><th>Длительность</th><th></th></tr></thead>
              <tbody>
                {data.map(s => (
                  <tr key={s.id}>
                    <td><b>{s.name}</b></td>
                    <td>{categoryBadge(s.category)}</td>
                    <td>{Number(s.price).toLocaleString()} сом</td>
                    <td>{s.durationMinutes} мин</td>
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        <Button size="sm" variant="outline" onClick={() => handleEdit(s)}>✏️</Button>
                        <Button size="sm" variant="danger" onClick={() => handleDelete(s.id)}>✕</Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Card>
        )
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editId ? 'Редактировать услугу' : 'Новая услуга'} actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave} disabled={saving}>{saving ? 'Сохранение...' : 'Сохранить'}</Button>
        </>
      }>
        <Input label="Название *" value={form.name} onChange={e => setForm({...form, name: e.target.value})} />
        <Input label="Описание" value={form.description} onChange={e => setForm({...form, description: e.target.value})} />
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
          <Input label="Цена (сом) *" type="number" value={form.price} onChange={e => setForm({...form, price: e.target.value})} />
          <Input label="Длительность (мин)" type="number" value={form.durationMinutes} onChange={e => setForm({...form, durationMinutes: e.target.value})} />
        </div>
        <Input label="Категория *" value={form.category} onChange={e => setForm({...form, category: e.target.value})} />
      </Modal>
    </div>
  );
}
