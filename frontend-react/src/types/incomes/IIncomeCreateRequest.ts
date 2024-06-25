export interface IIncomeCreateRequest {
    description: string;
    amount: string;
    source: string;
    date: string;
    recurring: boolean;
    recurrencePeriod?: "WEEKLY" | "MONTHLY" | "YEARLY";
}