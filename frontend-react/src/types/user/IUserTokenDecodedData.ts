export default interface IUserTokenDecodedData {
    username: string,
    sub: string,
    roles: string[],
}