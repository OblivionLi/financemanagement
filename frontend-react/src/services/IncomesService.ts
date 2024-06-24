import axios from "axios";

const getAllIncomesNoPagination = () => {
    return axios.get(`/api/incomes`, {
        headers: {
            'Content-Type': 'application/json'
        },
    });
}


const IncomesService = {
    getAllIncomesNoPagination
};

export default IncomesService;