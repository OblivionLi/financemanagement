import {IIncomesData} from "./incomes/IIncomesData";

export interface IIncomeEditModalProps {
    open: boolean;
    onClose: () => void;
    rowData: IIncomesData | null;
}