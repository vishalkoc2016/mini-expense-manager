import { useState, FormEvent } from 'react';
import { createExpense } from '../api/client';

interface Props {
  onCreated: () => void;
}

export default function AddExpenseForm({ onCreated }: Props) {
  const today = new Date().toISOString().slice(0, 10);
  const [date, setDate] = useState(today);
  const [amount, setAmount] = useState('');
  const [vendor, setVendor] = useState('');
  const [description, setDescription] = useState('');
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setErr(null);
    try {
      await createExpense({
        date,
        amount: Number(amount),
        vendor,
        description: description || undefined
      });
      setAmount('');
      setVendor('');
      setDescription('');
      onCreated();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'Failed to add expense');
    } finally {
      setBusy(false);
    }
  };

  return (
    <form className="card" onSubmit={submit}>
      <h3>Add Expense</h3>
      <div className="row">
        <label>
          Date
          <input type="date" value={date} onChange={e => setDate(e.target.value)} required />
        </label>
        <label>
          Amount
          <input
            type="number"
            step="0.01"
            min="0.01"
            value={amount}
            onChange={e => setAmount(e.target.value)}
            required
          />
        </label>
      </div>
      <label>
        Vendor
        <input
          type="text"
          value={vendor}
          onChange={e => setVendor(e.target.value)}
          placeholder="e.g. Swiggy"
          required
        />
      </label>
      <label>
        Description
        <input
          type="text"
          value={description}
          onChange={e => setDescription(e.target.value)}
          placeholder="optional"
        />
      </label>
      {err && <div className="error">{err}</div>}
      <button type="submit" disabled={busy}>
        {busy ? 'Saving...' : 'Add Expense'}
      </button>
    </form>
  );
}
