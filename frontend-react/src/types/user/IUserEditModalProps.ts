import IUserResponse from "./IUserResponse";

export default interface IUserEditModalProps {
    open: boolean;
    onClose: () => void;
    rowData: IUserResponse | null;
}