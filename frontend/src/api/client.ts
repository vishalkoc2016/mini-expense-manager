import axios from 'axios';
import type { Dashboard, Expense, ExpenseRequest, CsvUploadResponse } from '../types';

const api = axios.create({ baseURL: '/api' });

export const listExpenses = () => api.get<Expense[]>('/expenses').then(r => r.data);
export const createExpense = (payload: ExpenseRequest) =>
  api.post<Expense>('/expenses', payload).then(r => r.data);
export const deleteExpense = (id: number) => api.delete(`/expenses/${id}`);
export const getDashboard = () => api.get<Dashboard>('/expenses/dashboard').then(r => r.data);
export const uploadCsv = (file: File) => {
  const fd = new FormData();
  fd.append('file', file);
  return api.post<CsvUploadResponse>('/expenses/upload', fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then(r => r.data);
};
