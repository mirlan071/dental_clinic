import { useState, useEffect, useCallback, useRef } from 'react';
import { Card, PageHeader, Button, Modal, Input, Select, Loader } from '../components/UI';
import { medicalRecords as recordsApi, patients as patientsApi, doctors as doctorsApi } from '../api';
import { useToast } from '../components/ToastContext';
import './Records.css';

export default function Records() {
  const { toast } = useToast();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [detail, setDetail] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ patientId: '', doctorId: '', diagnosis: '', treatment: '', recommendations: '', notes: '' });

  const [patientList, setPatientList] = useState([]);
  const [doctorList, setDoctorList] = useState([]);
  const [saving, setSaving] = useState(false);

  const [uploadModal, setUploadModal] = useState(null);
  const [uploadFile, setUploadFile] = useState(null);
  const [uploadDesc, setUploadDesc] = useState('');
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await recordsApi.list({ page: 0, size: 200 });
      setData(res.data.data?.content || []);
    } catch (err) {
      toast.error('Не удалось загрузить медкарты');
    } finally {
      setLoading(false);
    }
  }, [toast]);

  const fetchDropdowns = useCallback(async () => {
    try {
      const [pRes, dRes] = await Promise.all([
        patientsApi.list({ page: 0, size: 500 }),
        doctorsApi.list({ page: 0, size: 500 }),
      ]);
      setPatientList(pRes.data.data?.content || []);
      setDoctorList(dRes.data.data?.content || []);
    } catch (err) {
      console.error('Failed to load dropdowns', err);
    }
  }, []);

  useEffect(() => { fetchData(); fetchDropdowns(); }, [fetchData, fetchDropdowns]);

  const handleSave = async () => {
    if (!form.patientId || !form.doctorId || !form.diagnosis) return toast.warning('Заполните обязательные поля');
    setSaving(true);
    try {
      await recordsApi.create({
        patientId: parseInt(form.patientId),
        doctorId: parseInt(form.doctorId),
        diagnosis: form.diagnosis,
        treatment: form.treatment,
        recommendations: form.recommendations,
        notes: form.notes,
      });
      toast.success('Медкарта создана');
      setModalOpen(false);
      setForm({ patientId: '', doctorId: '', diagnosis: '', treatment: '', recommendations: '', notes: '' });
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка сохранения');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Удалить запись?')) return;
    try {
      await recordsApi.delete(id);
      toast.success('Запись удалена');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка удаления');
    }
  };

  const handleUpload = async () => {
    if (!uploadFile) return toast.warning('Выберите файл');
    setUploading(true);
    try {
      await recordsApi.uploadDocument(uploadModal.id, uploadFile, uploadDesc);
      toast.success('Файл загружен');
      setUploadModal(null);
      setUploadFile(null);
      setUploadDesc('');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка загрузки файла');
    } finally {
      setUploading(false);
    }
  };

  const handleDownload = async (attachmentId, fileName) => {
    try {
      const res = await recordsApi.downloadDocument(attachmentId);
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      toast.error('Ошибка скачивания файла');
    }
  };

  const handleDeleteDoc = async (attachmentId) => {
    if (!confirm('Удалить файл?')) return;
    try {
      await recordsApi.deleteDocument(attachmentId);
      toast.success('Файл удалён');
      fetchData();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Ошибка удаления файла');
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes) return '—';
    if (bytes < 1024) return bytes + ' Б';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' КБ';
    return (bytes / (1024 * 1024)).toFixed(1) + ' МБ';
  };

  const patientOpts = patientList.map(p => ({ value: p.id, label: `${p.firstName} ${p.lastName}` }));
  const doctorOpts = doctorList.map(d => ({ value: d.id, label: `${d.firstName} ${d.lastName}` }));

  return (
    <div style={{ padding: 24 }}>
      <PageHeader title="Медицинские карты" action={<Button onClick={() => setModalOpen(true)}>+ Новая запись</Button>} />
      {loading ? <Loader /> : (
        <div className="grid grid-2">
          {data.map(r => (
            <Card key={r.id} style={{ cursor: 'pointer' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 12 }}>
                <div style={{ width: 48, height: 48, borderRadius: '50%', background: '#dbeafe', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20 }}>👤</div>
                <div style={{ flex: 1 }} onClick={() => setDetail(r)}>
                  <h3 style={{ fontSize: 16 }}>{r.patientName}</h3>
                  <p style={{ fontSize: 13, color: '#64748b' }}>Врач: {r.doctorName}</p>
                </div>
                <Button size="sm" variant="danger" onClick={e => { e.stopPropagation(); handleDelete(r.id); }}>✕</Button>
              </div>
              <div className="timeline" onClick={() => setDetail(r)}>
                <div className="timeline-item done">
                  <div style={{ fontSize: 13, color: '#64748b' }}>{r.createdAt ? new Date(r.createdAt).toLocaleDateString('ru') : '—'}</div>
                  <div style={{ fontSize: 14, marginTop: 2 }}><b>Диагноз:</b> {r.diagnosis}</div>
                  <div style={{ fontSize: 13, color: '#64748b', marginTop: 2 }}><b>Лечение:</b> {r.treatment || '—'}</div>
                  {r.recommendations && <div style={{ fontSize: 13, color: '#64748b', marginTop: 2 }}><b>Рекомендации:</b> {r.recommendations}</div>}
                </div>
              </div>
              {r.documents && r.documents.length > 0 && (
                <div style={{ marginTop: 12, borderTop: '1px solid #e2e8f0', paddingTop: 12 }}>
                  <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 6 }}>Файлы ({r.documents.length})</div>
                  {r.documents.map(doc => (
                    <div key={doc.id} style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '4px 0', fontSize: 13 }}>
                      <span style={{ flex: 1, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>📄 {doc.fileName}</span>
                      <span style={{ color: '#64748b', fontSize: 11 }}>{formatFileSize(doc.fileSize)}</span>
                      <Button size="sm" variant="outline" onClick={e => { e.stopPropagation(); handleDownload(doc.id, doc.fileName); }}>⬇</Button>
                      <Button size="sm" variant="danger" onClick={e => { e.stopPropagation(); handleDeleteDoc(doc.id); }}>✕</Button>
                    </div>
                  ))}
                </div>
              )}
              <div style={{ marginTop: 12 }}>
                <Button size="sm" variant="outline" onClick={e => { e.stopPropagation(); setUploadModal(r); setUploadFile(null); setUploadDesc(''); }}>📎 Прикрепить файл</Button>
              </div>
            </Card>
          ))}
          {data.length === 0 && <p style={{ gridColumn: '1 / -1', textAlign: 'center', color: '#64748b', padding: 40 }}>Нет медицинских записей</p>}
        </div>
      )}

      <Modal open={!!detail} onClose={() => setDetail(null)} title="Медицинская карта" actions={<Button variant="outline" onClick={() => setDetail(null)}>Закрыть</Button>}>
        {detail && (
          <div>
            <p><b>Пациент:</b> {detail.patientName}</p>
            <p><b>Врач:</b> {detail.doctorName}</p>
            <p><b>Дата:</b> {detail.createdAt ? new Date(detail.createdAt).toLocaleDateString('ru') : '—'}</p>
            <hr style={{ margin: '12px 0', border: 'none', borderTop: '1px solid #e2e8f0' }} />
            <p><b>Диагноз:</b></p><p style={{ color: '#64748b' }}>{detail.diagnosis}</p>
            <p style={{ marginTop: 8 }}><b>Лечение:</b></p><p style={{ color: '#64748b' }}>{detail.treatment || '—'}</p>
            {detail.recommendations && <><p style={{ marginTop: 8 }}><b>Рекомендации:</b></p><p style={{ color: '#64748b' }}>{detail.recommendations}</p></>}
            {detail.documents && detail.documents.length > 0 && (
              <>
                <hr style={{ margin: '12px 0', border: 'none', borderTop: '1px solid #e2e8f0' }} />
                <p><b>Прикреплённые файлы:</b></p>
                {detail.documents.map(doc => (
                  <div key={doc.id} style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '6px 0', fontSize: 13 }}>
                    <span style={{ flex: 1 }}>📄 {doc.fileName}</span>
                    <span style={{ color: '#64748b', fontSize: 11 }}>{formatFileSize(doc.fileSize)}</span>
                    <Button size="sm" variant="outline" onClick={() => handleDownload(doc.id, doc.fileName)}>⬇ Скачать</Button>
                  </div>
                ))}
              </>
            )}
          </div>
        )}
      </Modal>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Новая медицинская запись" actions={
        <>
          <Button variant="outline" onClick={() => setModalOpen(false)}>Отмена</Button>
          <Button onClick={handleSave} disabled={saving}>{saving ? 'Сохранение...' : 'Сохранить'}</Button>
        </>
      }>
        <Select label="Пациент *" options={patientOpts} value={form.patientId} onChange={e => setForm({...form, patientId: e.target.value})} />
        <Select label="Врач *" options={doctorOpts} value={form.doctorId} onChange={e => setForm({...form, doctorId: e.target.value})} />
        <Input label="Диагноз *" value={form.diagnosis} onChange={e => setForm({...form, diagnosis: e.target.value})} />
        <Input label="Лечение" value={form.treatment} onChange={e => setForm({...form, treatment: e.target.value})} />
        <Input label="Рекомендации" value={form.recommendations} onChange={e => setForm({...form, recommendations: e.target.value})} />
      </Modal>

      <Modal open={!!uploadModal} onClose={() => setUploadModal(null)} title="Прикрепить файл" actions={
        <>
          <Button variant="outline" onClick={() => setUploadModal(null)}>Отмена</Button>
          <Button onClick={handleUpload} disabled={uploading}>{uploading ? 'Загрузка...' : 'Загрузить'}</Button>
        </>
      }>
        {uploadModal && (
          <div>
            <p style={{ marginBottom: 12, color: '#64748b', fontSize: 13 }}>Запись: <b>{uploadModal.patientName}</b></p>
            <div className="form-group">
              <label className="form-label">Файл *</label>
              <input
                ref={fileInputRef}
                type="file"
                className="form-input"
                onChange={e => setUploadFile(e.target.files[0])}
                accept=".pdf,.jpg,.jpeg,.png,.doc,.docx,.xls,.xlsx"
              />
              {uploadFile && <p style={{ fontSize: 12, color: '#64748b', marginTop: 4 }}>{uploadFile.name} ({formatFileSize(uploadFile.size)})</p>}
            </div>
            <Input label="Описание" value={uploadDesc} onChange={e => setUploadDesc(e.target.value)} placeholder="Описание файла (необязательно)" />
          </div>
        )}
      </Modal>
    </div>
  );
}
