
export default interface IUserResponse {
    id: number;
    username: string;
    email: string;
    locked: boolean;
    userGroupCodes: string[];
    createdAt: Date;
    updatedAt: Date;
}