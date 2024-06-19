import * as jose from 'jose';
import IUserTokenDecodedData from "../types/user/IUserTokenDecodedData";

const isUserAuthorized = () => {
    const isLoggedInAndTokenNotExpired = isUserLogged();
    const hasPermissions = userHasPermissions();

    return isLoggedInAndTokenNotExpired && hasPermissions;
}

const isUserLogged = () => {
    const token = localStorage.getItem("userInfo");
    return token && !isUserTokenExpired(token);
}

const isUserTokenExpired = (token: string) => {
    if (!token) {
        return true;
    }

    const decodedToken: { exp: number } = jose.decodeJwt(token) as { exp: number };
    const currentDate = Math.floor(Date.now() / 1000);

    if (decodedToken.exp < currentDate) {
        localStorage.removeItem("userInfo");
        return true;
    }

    return false;
}

const addUserTokenToLocalStorage = (token: string) => {
    localStorage.setItem("userInfo", token)
}

const addUserPreferredCurrencyToLocalStorage = (currencyCode: string) => {
    localStorage.setItem("userPreferredCurrency", currencyCode)
}

const getCurrencyCodeFromLocalStorage = () => {
    const currencyCode = localStorage.getItem("userPreferredCurrency");
    if (!currencyCode) {
        return "Unknown Currency";
    }

    return currencyCode;
}

const getUsernameFromLocalStorage = () => {
    const token = localStorage.getItem("userInfo");
    if (!token || isUserTokenExpired(token)) {
        localStorage.removeItem("userInfo");
        return null;
    }

    const decodedToken: { lastName: string } = jose.decodeJwt(token) as { lastName: string };
    return decodedToken.lastName;
}

const getEmailFromLocalStorage = () => {
    const token = localStorage.getItem("userInfo");
    if (!token || isUserTokenExpired(token)) {
        localStorage.removeItem("userInfo");
        return null;
    }

    const decodedToken: { sub: string } = jose.decodeJwt(token) as { sub: string };
    return decodedToken.sub;
}

const logoutUser = () => {
    localStorage.removeItem("userInfo");
}

const getUserData = (): IUserTokenDecodedData | null => {
    const token = localStorage.getItem("userInfo");
    if (!token) {
        return null;
    }

    return jose.decodeJwt(token) as IUserTokenDecodedData;
}

const getUserToken = () => {
    const token = localStorage.getItem("userInfo");
    if (!token) {
        return null;
    }

    return token;
}

const userHasPermissions = () => {
    const token = localStorage.getItem("userInfo");
    if (!token) {
        return false;
    }

    try {
        const claims: { roles: string[] } = jose.decodeJwt(token) as { roles: string[] };
        return claims.roles.includes("ROLE_ADMIN");
    } catch (error) {
        return false;
    }
}

const getUserPermissions = () => {
    const token = localStorage.getItem("userInfo");
    if (!token) {
        return ['ROLE_ANONYMOUS'];
    }

    try {
        const claims: { roles: string[] } = jose.decodeJwt(token) as { roles: string[] };
        return claims.roles;
    } catch (error) {
        return ['ROLE_ANONYMOUS'];
    }
}

const LocalStorageService = {
    addUserTokenToLocalStorage,
    getUserPermissions,
    getUserToken,
    isUserLogged,
    getUsernameFromLocalStorage,
    logoutUser,
    getUserData,
    getEmailFromLocalStorage,
    isUserAuthorized,
    addUserPreferredCurrencyToLocalStorage,
    getCurrencyCodeFromLocalStorage
}

export default LocalStorageService;