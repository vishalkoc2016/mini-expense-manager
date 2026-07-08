export interface Expense {
  id: number;
  date: string;
  amount: number;
  vendor: string;
  description?: string;
  category: string;
  anomaly: boolean;
}

export interface VendorTotal {
  vendor: string;
  total: number;
}

export interface Dashboard {
  monthlyTotalsByCategory: Record<string, number>;
  topVendors: VendorTotal[];
  anomalyCount: number;
  anomalyIds: number[];
}

export interface CsvUploadResponse {
  inserted: number;
  failed: number;
  errors: string[];
}

export interface ExpenseRequest {
  date: string;
  amount: number;
  vendor: string;
  description?: string;
}
