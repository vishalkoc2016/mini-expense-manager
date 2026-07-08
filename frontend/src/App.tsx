import { useCallback, useEffect, useState } from 'react';
import AddExpenseForm from './components/AddExpenseForm';
import CsvUpload from './components/CsvUpload';
import DashboardPanel from './components/DashboardPanel';
import ExpenseTable from './components/ExpenseTable';
import { getDashboard, listExpenses } from './api/client';
import type { Dashboard, Expense } from './types';

export default function App() {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [dashboard, setDashboard] = useState<Dashboard | null>(null);

  const refresh = useCallback(async () => {
    const [ex, dash] = await Promise.all([listExpenses(), getDashboard()]);
    setExpenses(ex);
    setDashboard(dash);
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  return (
    <div className="app">
      <header>
        <h1>Mini Expense Manager</h1>
      </header>
      <main>
        <DashboardPanel data={dashboard} />
        <div className="two-col">
          <AddExpenseForm onCreated={refresh} />
          <CsvUpload onUploaded={refresh} />
        </div>
        <ExpenseTable expenses={expenses} onChange={refresh} />
      </main>
      <footer>
        <small>Built with React + TypeScript · Spring Boot · PostgreSQL</small>
      </footer>
    </div>
  );
}
