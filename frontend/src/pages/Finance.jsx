import { useState } from 'react';
import { Card, PageHeader, Button, Badge, StatCard, Table, Modal, Input } from '../components/UI';
import { DEMO_INVOICES } from '../data/demo';

const statusBadge = (s) => {
  const map = { UNPAID: { v: 'red', t: 'Не оплачен' }, PARTIALLY_PAID: { v: 'yellow', t: 'Частично' }, PAID: { v: 'green', t: 'Оплачен' }, CANCELLED: { v: 'gray', t: 'Отменён' } };
  const { v, t } = map[s] || { v: 'gray', t: s };
  return <Badge variant={v}>{t}</Badge>;
};

export default function Finance() {
  const [data, setData] = useState(DEMO_INVOICES);
  const [modalOpen, setModalOpen] = useState(false);
  const [payModal, setPayModal] = useState(null);
  const [form, setForm] = useState({ patientName: '', totalAmount: '', notes: '' });
  const [payForm, setPayForm] = useState({ amount: '', paymentMethod: 'CASH' });

  const totalPaid = data.filter(i => i.status === 'PAID').reduce((s, i) => s + i.paidAmount, 0);
  const totalUnpaid = data.filter(i => i.status !== 'PAID' && i.status !== 'CANCELLED').reduce((s, i) => s + (i.totalAmount - i.paidAmount), 0);

  const handleCreateInvoice = () => {
    if (!form.patientName || !form.totalAmount) return alert('Заполните обязательные поля');
    const totalAmount = parseFloat(form.totalAmount);
    if (isNaN(totalAmount) || totalAmount <= 0) return alert('Введите корректную сумму');
    const invNum = `INV-${String(data.length + 1).padStart(3, '0')}`;
    const [first, last] = form.patientName.split(' ');
    setData([{ id: Date.now(), invoiceNumber: invNum, patientFirstName: first || '', patientLastName: last || '', totalAmount, paidAmount: 0, status: 'UNPAID' }, ...data]);
    setModalOpen(false);
    setForm({ patientName: '', totalAmount: '', notes: '' });
  };

  const handlePayment = () => {
    if (!payForm.amount) return;
    const amount = parseFloat(payForm.amount);
    if (isNaN(amount) || amount <= 0) return alert('Введите корректную сумму');
    const remaining = payModal.totalAmount - payModal.paidAmount;
    if (amount > remaining) return alert(`Сумма не может превышать остаток: ${remaining.toLocaleString()} сом`);
    setData(data.map(i => {
      if (i.id !== payModal.id) return i;
      const newPaid = i.paidAmount + amount;
      return { ...i, paidAmount: newPaid, status: newPaid >= i.totalAmount ? 'PAID' : 'PARTIALLY_PAID' };
    }));
    setPayModal(null);
    setPayForm({ amount: '', paymentMethod: 'CASH' });
  };

  const columns = [
    { header: '№', accessor: 'invoiceNumber' },
    { header: 'Пациент', render: r => `${r.patientFirstName} ${r.patientLastName}` },
    { header: 'Сумма', render: r => <b>{r.totalAmount?.toLocaleString()} сом</b> },
    { header: 'Оплачено', render: r => `${r.paidAmount?.toLocaleString()} сом` },
    { header: 'Статус', render: r => statusBadge(r.status) },
    { header: '', render: r => r.status !== 'PAID' && r.status !== 'CANCELLED' ?
      <Button size="sm" onClick={e => { e.stopPropagation(); setPayModal(r); setPayForm({ ...payForm, amount: (r.totalAmount - r.paidAmount).toString() }); }}>Оплата</Button> : null
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Финансы" action={<Button onClick={() => setModalOpen(true)}>+ Новый счёт</Button>} />

      <div className="grid grid-3" style={{ marginBottom: 24 }}>
        <StatCard icon="✅" value={`${(totalPaid / 1000).toFixed(0)}K`} label="Оплачено (сом)" color="green" />
        <StatCard icon="⏳" value={`${(totalUnpaid / 1000).toFixed(0)}K`} label="Ожидает оплаты" color="yellow" />
        <StatCard icon="📋" value={data.length} label="Всего счетов" color="blue" />
      </div>

      <Card>
        <h3 className="card-title" style={{ marginBottom: 16 }}>Счета</h3>
        <Table columns={columns} data={data} />
      </Card>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Новый счёт" actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleCreateInvoice}>Сохранить</Button>
        </>
      }>
        <Input label="Пациент *" value={form.patientName} onChange={e => setForm({...form, patientName: e.target.value})} placeholder="Имя Фамилия" />
        <Input label="Сумма (сом) *" type="number" value={form.totalAmount} onChange={e => setForm({...form, totalAmount: e.target.value})} />
        <Input label="Заметки" value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} />
      </Modal>

      <Modal open={!!payModal} onClose={() => setPayModal(null)} title="Оплата счёта" actions={
        <>
          <Button variant="outline" onClick={() => setPayModal(null)}>Отмена</Button>
          <Button variant="success" onClick={handlePayment}>Оплатить</Button>
        </>
      }>
        {payModal && (
          <>
            <p style={{ marginBottom: 16, color: '#64748b' }}>
              Счёт {payModal.invoiceNumber} — к оплате: <b>{(payModal.totalAmount - payModal.paidAmount).toLocaleString()} сом</b>
            </p>
            <Input label="Сумма *" type="number" value={payForm.amount} onChange={e => setPayForm({...payForm, amount: e.target.value})} />
            <div className="form-group">
              <label className="form-label">Способ оплаты</label>
              <select className="form-input" value={payForm.paymentMethod} onChange={e => setPayForm({...payForm, paymentMethod: e.target.value})}>
                <option value="CASH">Наличные</option>
                <option value="CARD">Карта</option>
                <option value="BANK_TRANSFER">Перевод</option>
                <option value="INSURANCE">Страховка</option>
              </select>
            </div>
          </>
        )}
      </Modal>
    </div>
  );
}
