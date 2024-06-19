import axios from "axios";
import IUserDtoResponse from "../types/user/IUserDtoResponse";
import LocalStorageService from "./LocalStorageService";
import IUserResponse from "../types/user/IUserResponse";
import IUsersRolesResponse from "../types/user/IUsersRolesResponse";
import IUserEditRequest from "../types/user/IUserEditRequest";

const registerUser = (formData: object) => {
    return axios.post<Array<IUserDtoResponse>>(`/api/auth/register`, JSON.stringify(formData), {
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

const loginUser = (formData: object) => {
    return axios.post<Array<IUserDtoResponse>>(`/api/auth/login`, JSON.stringify(formData), {
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

const forgotPassword = (email: string) => {
    return axios.post(`/api/auth/forgot-password`, {email});
}

const isResetPasswordTokenValid = (token: string) => {
    return axios.get(`/api/auth/reset-password/` + token);
}

const resetUserPassword = (formData: object) => {
    return axios.patch(`/api/auth/reset-password`, formData);
}

// const updateUserDetails = (formData: object) => {
//     return axios.patch<Array<IUserDtoResponse>>(`/api/user/change-details`, JSON.stringify(formData), {
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
//         },
//     });
// }

// const getAllUsers = () => {
//     return axios.get<IUserResponse>(`/api/admin/users`, {
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
//         },
//     });
// }
//
// const deleteUser = (id: number) => {
//     return axios.delete(`/api/admin/users/${id}/delete`, {
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
//         },
//     });
// }
//
// const lockUser = (id: number, newLockValue: boolean) => {
//     return axios.patch(`/api/admin/users/${id}/lock`, newLockValue,{
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
//         },
//     });
// }
//
// const getUserRoles = () => {
//     return axios.get<IUsersRolesResponse>(`/api/admin/users/roles`, {
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
//         },
//     });
// }
//
// const editUser = (data: IUserEditRequest) => {
//     return axios.patch(`/api/admin/users/${data.id}/edit`, data,{
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
//         },
//     });
// }

const UsersService = {
    registerUser,
    loginUser,
    forgotPassword,
    isResetPasswordTokenValid,
    resetUserPassword,
    // updateUserDetails,
    // getAllUsers,
    // deleteUser,
    // lockUser,
    // getUserRoles,
    // editUser
};

export default UsersService;