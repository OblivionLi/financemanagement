import IExpensesData from "./expenses/IExpensesData";

export default interface IExpenseEditModalProps {
    open: boolean;
    onClose: () => void;
    rowData: IExpensesData | null;
}