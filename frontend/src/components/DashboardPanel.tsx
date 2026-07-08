import type { Dashboard } from '../types';

interface Props {
  data: Dashboard | null;
}

const fmt = (n: number) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(n);

export default function DashboardPanel({ data }: Props) {
  if (!data) return <div className="card">Loading dashboard...</div>;

  const categories = Object.entries(data.monthlyTotalsByCategory).sort((a, b) => b[1] - a[1]);

  return (
    <div className="card">
      <h3>Dashboard</h3>
      <div className="dashboard-grid">
        <div>
          <h4>Monthly Totals (Current Month)</h4>
          {categories.length === 0 ? (
            <div className="hint">No expenses yet.</div>
          ) : (
            <table>
              <thead>
                <tr><th>Category</th><th>Total</th></tr>
              </thead>
              <tbody>
                {categories.map(([cat, total]) => (
                  <tr key={cat}><td>{cat}</td><td>{fmt(total)}</td></tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div>
          <h4>Top 5 Vendors</h4>
          {data.topVendors.length === 0 ? (
            <div className="hint">No vendors yet.</div>
          ) : (
            <table>
              <thead>
                <tr><th>Vendor</th><th>Total</th></tr>
              </thead>
              <tbody>
                {data.topVendors.map(v => (
                  <tr key={v.vendor}><td>{v.vendor}</td><td>{fmt(v.total)}</td></tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div>
          <h4>Anomalies</h4>
          <div className="anomaly-count">{data.anomalyCount}</div>
          <div className="hint">flagged expense(s) &gt; 3× category average</div>
        </div>
      </div>
    </div>
  );
}
