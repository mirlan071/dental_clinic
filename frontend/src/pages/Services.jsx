import { useState } from 'react';
import { Card, PageHeader, Button, Badge, Modal, Input } from '../components/UI';
import { DEMO_SERVICES } from '../data/demo';

const categoryIcons = { 'Диагностика': '🔍', 'Лечение': '💉', 'Гигиена': '🧹', 'Хирургия': '⚡', 'Эстетика': '✨', 'Ортодонтия': '🦷', 'Протезирование': '🔧' };
const categoryBadge = (cat) => {
  const colors = { 'Диагностика': 'blue', 'Лечение': 'green', 'Гигиена': 'yellow', 'Хирургия': 'red', 'Эстетика': 'yellow', 'Ортодонтия': 'blue', 'Протезирование': 'gray' };
  return <Badge variant={colors[cat] || 'gray'}>{cat}</Badge>;
};

export default function Services() {
  const [data, setData] = useState(DEMO_SERVICES);
  const [view, setView] = useState('grid');
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ name: '', description: '', price: '', durationMinutes: '', category: '' });

  const handleSave = () => {
    if (!form.name || !form.price || !form.category) return alert('Заполните обязательные поля');
    setData([{ ...form, id: Date.now(), price: parseFloat(form.price), durationMinutes: parseInt(form.durationMinutes) }, ...data]);
    setModalOpen(false);
    setForm({ name: '', description: '', price: '', durationMinutes: '', category: '' });
  };

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Каталог услуг" action={
        <div style={{ display: 'flex', gap: 8 }}>
          <Button variant={view === 'grid' ? 'primary' : 'outline'} onClick={() => setView('grid')}>▦</Button>
          <Button variant={view === 'table' ? 'primary' : 'outline'} onClick={() => setView('table')}>☰</Button>
          <Button onClick={() => setModalOpen(true)}>+ Новая услуга</Button>
        </div>
      } />

      {view === 'grid' ? (
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
                <b style={{ color: '#2563eb', fontSize: 18 }}>{s.price?.toLocaleString()} сом</b>
                <span style={{ fontSize: 13, color: '#64748b' }}>{s.durationMinutes} мин</span>
              </div>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <table>
            <thead><tr><th>Название</th><th>Категория</th><th>Цена</th><th>Длительность</th></tr></thead>
            <tbody>
              {data.map(s => (
                <tr key={s.id}>
                  <td><b>{s.name}</b></td>
                  <td>{categoryBadge(s.category)}</td>
                  <td>{s.price?.toLocaleString()} сом</td>
                  <td>{s.durationMinutes} мин</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Новая услуга" actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave}>Сохранить</Button>
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
