export interface MonthlyFinancialSummary {
    dailyExpenses: { [key: number]: number };
    dailyIncomes: { [key: number]: number };
    dailyExpenseTransactions: { [key: number]: number };
    dailyIncomeTransactions: { [key: number]: number };
}