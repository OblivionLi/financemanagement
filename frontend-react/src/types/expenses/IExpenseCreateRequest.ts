export interface IExpenseCreateRequest {
    description: string;
    amount: string;
    subCategoryId: number;
    date: string;
    recurring: boolean;
    recurrencePeriod?: "WEEKLY" | "MONTHLY" | "YEARLY";
}