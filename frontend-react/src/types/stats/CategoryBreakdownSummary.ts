export interface CategoryBreakdownSummary {
    expensesByCategory: { [key: string]: number };
    incomesBySource: { [key: string]: number };
}