import axios from "axios";
import LocalStorageService from "./LocalStorageService";

const getStatsByYear = (year: number) => {
    return axios.get(`/api/stats/year/${year}`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const getStatsByYearAndMonth = (year: number, month: number) => {
    return axios.get(`/api/stats/year/${year}/month/${month}`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const getCategoryStatsByYear = (year: number) => {
    return axios.get(`/api/stats/year/${year}/category-breakdown`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const getComparisonData = (year: number, month: number) => {
    return axios.get(`/api/stats/year/${year}/month/${month}/comparison`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const getSavingsRateDate = (year: number) => {
    return axios.get(`/api/stats/year/${year}/savings-rate`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const getGrandTotals = () => {
    return axios.get(`/api/stats/grand-totals`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${LocalStorageService.getUserToken()}`
        },
    })
}

const StatsService = {
    getStatsByYear,
    getStatsByYearAndMonth,
    getCategoryStatsByYear,
    getComparisonData,
    getSavingsRateDate,
    getGrandTotals
};

export default StatsService;