export default interface IUserEditRequest {
    id: number | undefined;
    email: string | undefined;
    roles: string[];
}