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

const ExpensesService = {
    getAllExpensesNoPagination,
    addExpense,
    getExpenseSubCategories,
    addExpenseSubCategory,
    getCurrencies
};

export default ExpensesService;