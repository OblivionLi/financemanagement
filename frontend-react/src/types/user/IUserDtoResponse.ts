export default interface IUserDtoResponse {
    id: number;
    username: string;
    email: string;
    token: string;
    userGroupCodes: string[]
}