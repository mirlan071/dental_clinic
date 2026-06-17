import axios from 'axios';

const API = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api/v1',
  headers: { 'Content-Type': 'application/json' }
});

API.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

API.interceptors.response.use(r => r, error => {
  if (error.response?.status === 401) {
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
  return Promise.reject(error);
});

export const auth = {
  login: (data) => API.post('/auth/login', data),
  register: (data) => API.post('/auth/register', data),
};

export const patients = {
  list: (params) => API.get('/patients', { params }),
  get: (id) => API.get(`/patients/${id}`),
  search: (q) => API.get('/patients/search', { params: { query: q } }),
  create: (data) => API.post('/patients', data),
  update: (id, data) => API.put(`/patients/${id}`, data),
  delete: (id) => API.delete(`/patients/${id}`),
};

export const doctors = {
  list: (params) => API.get('/doctors', { params }),
  get: (id) => API.get(`/doctors/${id}`),
  search: (q) => API.get('/doctors/search', { params: { query: q } }),
  create: (data) => API.post('/doctors', data),
  update: (id, data) => API.put(`/doctors/${id}`, data),
  delete: (id) => API.delete(`/doctors/${id}`),
  getSchedule: (id) => API.get(`/doctors/${id}/schedule`),
  addSchedule: (id, data) => API.post(`/doctors/${id}/schedule`, data),
};

export const appointments = {
  list: (params) => API.get('/appointments', { params }),
  get: (id) => API.get(`/appointments/${id}`),
  byPatient: (id) => API.get(`/appointments/patient/${id}`),
  byDoctor: (id) => API.get(`/appointments/doctor/${id}`),
  create: (data) => API.post('/appointments', data),
  reschedule: (id, newStartTime) => API.put(`/appointments/${id}/reschedule`, null, { params: { newStartTime } }),
  cancel: (id) => API.put(`/appointments/${id}/cancel`),
  updateStatus: (id, status) => API.patch(`/appointments/${id}/status`, { status }),
};

export const services = {
  list: (params) => API.get('/services', { params }),
  get: (id) => API.get(`/services/${id}`),
  search: (q) => API.get('/services/search', { params: { query: q } }),
  byCategory: (cat) => API.get(`/services/category/${cat}`),
  create: (data) => API.post('/services', data),
  update: (id, data) => API.put(`/services/${id}`, data),
  delete: (id) => API.delete(`/services/${id}`),
};

export const medicalRecords = {
  list: (params) => API.get('/medical-records', { params }),
  get: (id) => API.get(`/medical-records/${id}`),
  byPatient: (id) => API.get(`/medical-records/patient/${id}`),
  create: (data) => API.post('/medical-records', data),
  update: (id, data) => API.put(`/medical-records/${id}`, data),
  delete: (id) => API.delete(`/medical-records/${id}`),
};

export const finance = {
  invoices: {
    list: (params) => API.get('/finance/invoices', { params }),
    get: (id) => API.get(`/finance/invoices/${id}`),
    byPatient: (id) => API.get(`/finance/invoices/patient/${id}`),
    byStatus: (s) => API.get(`/finance/invoices/status/${s}`),
    create: (data) => API.post('/finance/invoices', data),
  },
  payments: {
    create: (data) => API.post('/finance/payments', data),
    byInvoice: (id) => API.get(`/finance/payments/invoice/${id}`),
    byPatient: (id) => API.get(`/finance/payments/patient/${id}`),
  },
};

export default API;
