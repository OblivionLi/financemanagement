export default interface IExpensesData {
    id: number,
    username: string,
    description: string,
    amount: string,
    category: string,
    subcategory: string,
    date: string,
    recurring: boolean,
    recurrencePeriod: string
}