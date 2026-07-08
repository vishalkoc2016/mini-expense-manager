import { useState, ChangeEvent } from 'react';
import { uploadCsv } from '../api/client';
import type { CsvUploadResponse } from '../types';

interface Props {
  onUploaded: () => void;
}

export default function CsvUpload({ onUploaded }: Props) {
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState<CsvUploadResponse | null>(null);
  const [err, setErr] = useState<string | null>(null);

  const onFile = async (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setBusy(true);
    setErr(null);
    setResult(null);
    try {
      const res = await uploadCsv(file);
      setResult(res);
      onUploaded();
    } catch (e: any) {
      setErr(e?.response?.data?.error || 'Upload failed');
    } finally {
      setBusy(false);
      e.target.value = '';
    }
  };

  return (
    <div className="card">
      <h3>Upload CSV</h3>
      <p className="hint">
        Columns: <code>date, amount, vendor, description</code>
      </p>
      <input type="file" accept=".csv" onChange={onFile} disabled={busy} />
      {busy && <div>Processing...</div>}
      {err && <div className="error">{err}</div>}
      {result && (
        <div className="upload-result">
          <div>Inserted: <strong>{result.inserted}</strong></div>
          <div>Failed: <strong>{result.failed}</strong></div>
          {result.errors.length > 0 && (
            <ul className="errors">
              {result.errors.slice(0, 5).map((msg, i) => (
                <li key={i}>{msg}</li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}
