export interface YearlyFinancialSummary {
    monthlyExpenses: { [key: number]: number };
    monthlyIncomes: { [key: number]: number };
    monthlyExpenseTransactions: { [key: number]: number };
    monthlyIncomeTransactions: { [key: number]: number };
    minYear: number;
    maxYear: number;
}