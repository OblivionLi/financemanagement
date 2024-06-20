import axios from "axios";
import {IExpenseCreateRequest} from "../types/expenses/IExpenseCreateRequest";
import LocalStorageService from "./LocalStorageService";
import {IExpenseSubCategoryCreateRequest} from "../types/expenses/IExpenseSubCategoryCreateRequest";

const getAllExpensesNoPagination = () => {
    return axios.get(`/api/expenses`, {
        headers: {
            'Content-Type': 'application/json'
        },
    });
}

const addExpense = (data: IExpenseCreateRequest) => {
    return axios.post(`/api/expenses/add`, data, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const getExpenseSubCategories = () => {
    return axios.get(`/api/expenses/subcategories`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const addExpenseSubCategory = (data: IExpenseSubCategoryCreateRequest) => {
    return axios.post(`/api/expenses/subcategories/add`, data, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const getCurrencies = () => {
    return axios.get(`/api/currencies`, {
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

const deleteExpense = (id: number) => {
    return axios.delete(`/api/expenses/delete/${id}`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const editExpense = (id: number, data: object) => {
    return axios.patch(`/api/expenses/edit/${id}`, data, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const updateCurrency = (data: object) => {
    return axios.patch(`/api/currencies/update`, data, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const getExpensesByYear = (year: number) => {
    return axios.get(`/api/stats/expenses/year/${year}`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const getExpensesByMonth = (year: number, month: number) => {
    return axios.get(`/api/stats/expenses/year/${year}/month/${month}`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const getMinYear = () => {
    return axios.get(`/api/stats/expenses/min-year`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const getMaxYear = () => {
    return axios.get(`/api/stats/expenses/max-year`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    });
}

const ExpensesService = {
    getAllExpensesNoPagination,
    addExpense,
    getExpenseSubCategories,
    addExpenseSubCategory,
    getCurrencies,
    deleteExpense,
    editExpense,
    updateCurrency,
    getExpensesByYear,
    getExpensesByMonth,
    getMinYear,
    getMaxYear
};

export default ExpensesService;