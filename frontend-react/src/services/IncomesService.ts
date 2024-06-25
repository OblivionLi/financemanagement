import axios from "axios";
import LocalStorageService from "./LocalStorageService";
import {IIncomeCreateRequest} from "../types/incomes/IIncomeCreateRequest";

const getAllIncomesNoPagination = () => {
    return axios.get(`/api/incomes`, {
        headers: {
            'Content-Type': 'application/json'
        },
    });
}

const getIncomesByYear = (year: number) => {
    return axios.get(`/api/stats/incomes/year/${year}`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const getIncomesByMonth = (year: number, month: number) => {
    return axios.get(`/api/stats/incomes/year/${year}/month/${month}`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const getMinYear = () => {
    return axios.get(`/api/stats/incomes/min-year`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const getMaxYear = () => {
    return axios.get(`/api/stats/incomes/max-year`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const deleteIncome = (id: number) => {
    return axios.delete(`/api/incomes/delete/${id}`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const addIncome = (data: IIncomeCreateRequest) => {
    return axios.post(`/api/incomes/add`, data, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const IncomesService = {
    getAllIncomesNoPagination,
    getIncomesByYear,
    getIncomesByMonth,
    getMinYear,
    getMaxYear,
    deleteIncome,
    addIncome
};

export default IncomesService;