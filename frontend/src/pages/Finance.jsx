import { useState, useEffect, useCallback } from 'react';
import { Card, PageHeader, Button, Badge, StatCard, Table, Modal, Input, Select, Loader } from '../components/UI';
import { finance as financeApi, patients as patientsApi } from '../api';
import { useToast } from '../components/ToastContext';

const statusBadge = (s) => {
  const map = { UNPAID: { v: 'red', t: 'Не оплачен' }, PARTIALLY_PAID: { v: 'yellow', t: 'Частично' }, PAID: { v: 'green', t: 'Оплачен' }, CANCELLED: { v: 'gray', t: 'Отменён' } };
  const { v, t } = map[s] || { v: 'gray', t: s };
  return <Badge variant={v}>{t}</Badge>;
};

export default function Finance() {
  const { toast } = useToast();
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [payModal, setPayModal] = useState(null);
  const [form, setForm] = useState({ patientId: '', totalAmount: '', notes: '' });
  const [payForm, setPayForm] = useState({ amount: '', paymentMethod: 'CASH' });
  const [saving, setSaving] = useState(false);

  const [patientList, setPatientList] = useState([]);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await financeApi.invoices.list({ page: 0, size: 200 });
      setInvoices(res.data.data?.content || []);
    } catch (err) {
      toast.error('Не удалось загрузить счета');
    } finally {
      setLoading(false);
    }
  }, [toast]);

  const fetchPatients = useCallback(async () => {
    try {
      const res = await patientsApi.list({ page: 0, size: 500 });
      setPatientList(res.data.data?.content || []);
    } catch (err) {
      console.error('Failed to load patients', err);
    }
  }, []);

  useEffect(() => { fetchData(); fetchPatients(); }, [fetchData, fetchPatients]);

  const totalPaid = invoices.filter(i => i.status === 'PAID').reduce((s, i) => s + Number(i.paidAmount), 0);
  const totalUnpaid = invoices.filter(i => i.status !== 'PAID' && i.status !== 'CANCELLED').reduce((s, i) => s + Number(i.remainingAmount || (i.totalAmount - i.paidAmount)), 0);

  const handleCreateInvoice = async () => {
    if (!form.patientId || !form.totalAmount) return toast.warning('Заполните обязательные поля');
    const totalAmount = parseFloat(form.totalAmount);
    if (isNaN(totalAmount) || totalAmount <= 0) return toast.warning('Введите корректную сумму');
    setSaving(true);
    try {
      await financeApi.invoices.create({
        patientId: parseInt(form.patientId),
        totalAmount,
        notes: form.notes,
      });
      toast.success('Счёт создан');
      setModalOpen(false);
      setForm({ patientId: '', totalAmount: '', notes: '' });
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка создания счёта');
    } finally {
      setSaving(false);
    }
  };

  const handlePayment = async () => {
    if (!payForm.amount) return;
    const amount = parseFloat(payForm.amount);
    if (isNaN(amount) || amount <= 0) return toast.warning('Введите корректную сумму');
    const remaining = Number(payModal.remainingAmount || (payModal.totalAmount - payModal.paidAmount));
    if (amount > remaining) return toast.warning(`Сумма не может превышать остаток: ${remaining.toLocaleString()} сом`);
    setSaving(true);
    try {
      await financeApi.payments.create({
        invoiceId: payModal.id,
        amount,
        paymentMethod: payForm.paymentMethod,
      });
      toast.success('Оплата проведена');
      setPayModal(null);
      setPayForm({ amount: '', paymentMethod: 'CASH' });
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка оплаты');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = async (id) => {
    if (!confirm('Отменить счёт?')) return;
    try {
      await financeApi.invoices.cancel(id);
      toast.success('Счёт отменён');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка отмены счёта');
    }
  };

  const patientOpts = patientList.map(p => ({ value: p.id, label: `${p.firstName} ${p.lastName}` }));

  const columns = [
    { header: '№', accessor: 'invoiceNumber' },
    { header: 'Пациент', accessor: 'patientName' },
    { header: 'Сумма', render: r => <b>{Number(r.totalAmount).toLocaleString()} сом</b> },
    { header: 'Оплачено', render: r => `${Number(r.paidAmount).toLocaleString()} сом` },
    { header: 'Статус', render: r => statusBadge(r.status) },
    { header: '', render: r => (
      <div style={{ display: 'flex', gap: 4 }}>
        {r.status !== 'PAID' && r.status !== 'CANCELLED' && (
          <Button size="sm" onClick={e => { e.stopPropagation(); setPayModal(r); setPayForm({ ...payForm, amount: String(Number(r.remainingAmount || (r.totalAmount - r.paidAmount))) }); }}>Оплата</Button>
        )}
        {r.status !== 'CANCELLED' && (
          <Button size="sm" variant="danger" onClick={e => { e.stopPropagation(); handleCancel(r.id); }}>✕</Button>
        )}
      </div>
    )},
  ];

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Финансы" action={<Button onClick={() => setModalOpen(true)}>+ Новый счёт</Button>} />

      <div className="grid grid-3" style={{ marginBottom: 24 }}>
        <StatCard icon="✅" value={`${(totalPaid / 1000).toFixed(0)}K`} label="Оплачено (сом)" color="green" />
        <StatCard icon="⏳" value={`${(totalUnpaid / 1000).toFixed(0)}K`} label="Ожидает оплаты" color="yellow" />
        <StatCard icon="📋" value={invoices.length} label="Всего счетов" color="blue" />
      </div>

      <Card>
        <h3 className="card-title" style={{ marginBottom: 16 }}>Счета</h3>
        {loading ? <Loader /> : <Table columns={columns} data={invoices} />}
      </Card>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Новый счёт" actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleCreateInvoice} disabled={saving}>{saving ? 'Сохранение...' : 'Сохранить'}</Button>
        </>
      }>
        <Select label="Пациент *" options={patientOpts} value={form.patientId} onChange={e => setForm({...form, patientId: e.target.value})} />
        <Input label="Сумма (сом) *" type="number" value={form.totalAmount} onChange={e => setForm({...form, totalAmount: e.target.value})} />
        <Input label="Заметки" value={form.notes} onChange={e => setForm({...form, notes: e.target.value})} />
      </Modal>

      <Modal open={!!payModal} onClose={() => setPayModal(null)} title="Оплата счёта" actions={
        <>
          <Button variant="outline" onClick={() => setPayModal(null)}>Отмена</Button>
          <Button variant="success" onClick={handlePayment} disabled={saving}>{saving ? 'Обработка...' : 'Оплатить'}</Button>
        </>
      }>
        {payModal && (
          <>
            <p style={{ marginBottom: 16, color: '#64748b' }}>
              Счёт {payModal.invoiceNumber} — к оплате: <b>{Number(payModal.remainingAmount || (payModal.totalAmount - payModal.paidAmount)).toLocaleString()} сом</b>
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
