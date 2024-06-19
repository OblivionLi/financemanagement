export default interface IExpensesData {
    id: number,
    username: string,
    description: string,
    amount: string,
    category: string,
    subCategory: string,
    subCategoryId: number,
    date: string,
    recurring: boolean,
    recurrencePeriod: string
}