import type { Expense } from '../types';
import { deleteExpense } from '../api/client';

interface Props {
  expenses: Expense[];
  onChange: () => void;
}

const fmt = (n: number) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(n);

export default function ExpenseTable({ expenses, onChange }: Props) {
  const remove = async (id: number) => {
    if (!confirm('Delete this expense?')) return;
    await deleteExpense(id);
    onChange();
  };

  return (
    <div className="card">
      <h3>Expenses ({expenses.length})</h3>
      {expenses.length === 0 ? (
        <div className="hint">No expenses yet. Add one or upload a CSV.</div>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Vendor</th>
              <th>Category</th>
              <th>Amount</th>
              <th>Description</th>
              <th>Flag</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {expenses.map(e => (
              <tr key={e.id} className={e.anomaly ? 'anomaly-row' : ''}>
                <td>{e.date}</td>
                <td>{e.vendor}</td>
                <td>{e.category}</td>
                <td>{fmt(e.amount)}</td>
                <td>{e.description || '-'}</td>
                <td>{e.anomaly ? <span className="anomaly-badge">ANOMALY</span> : ''}</td>
                <td>
                  <button className="link" onClick={() => remove(e.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
