import React, {useEffect, useState} from 'react';
import Typography from "@mui/material/Typography";
import {Button, Divider, FormControl, InputLabel, Paper, Select, Skeleton, TextField, Tooltip} from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import Box from "@mui/material/Box";
import DataTable, {TableColumn} from "react-data-table-component";
import IExpensesData from "../../types/expenses/IExpensesData";
import {useNavigate} from "react-router-dom";
import LocalStorageService from "../../services/LocalStorageService";
import ExpensesService from "../../services/ExpensesService";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import ExpandedExpenseDetails from "../../components/expenses/ExpandedExpenseDetails";
import AddExpenseDialog from "../../components/expenses/AddExpenseDialog";
import {format} from "date-fns";
import Swal from 'sweetalert2'
import EditExpenseDialog from "../../components/expenses/EditExpenseDialog";
import UpdateCurrencyDialog from "../../components/UpdateCurrencyDialog";
import MenuItem from "@mui/material/MenuItem";
import DownloadExpensesButtons from "../../components/expenses/DownloadExpensesButtons";

const ExpensesScreen = () => {
    const navigate = useNavigate();

    const [expenses, setExpenses] = useState<IExpensesData[]>([]);
    const [loading, setLoading] = useState(true);
    const isUserLogged = LocalStorageService.isUserLogged();
    const [addDialogOpen, setAddDialogOpen] = useState(false);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [updateDialogOpen, setUpdateDialogOpen] = useState(false);
    const [currencyCode, setCurrencyCode] = useState('');
    const [selectedExpense, setSelectedExpense] = useState<IExpensesData | null>(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [year, setYear] = useState(new Date().getFullYear());
    const [month, setMonth] = useState<number | null>(null);
    const [minYear, setMinYear] = useState<number | null>(null);
    const [maxYear, setMaxYear] = useState<number | null>(null);
    const [yearlyTotal, setYearlyTotal] = useState<number | null>(null);
    const [monthlyTotal, setMonthlyTotal] = useState<number | null>(null);

    useEffect(() => {
        if (!isUserLogged) {
            navigate("/login");
            return;
        }

        fetchExpenses();
        fetchYears();

        if (selectedExpense) {
            setEditDialogOpen(true);
        }

        const userPreferredCurrency = LocalStorageService.getCurrencyCodeFromLocalStorage();
        setCurrencyCode(userPreferredCurrency);
    }, [isUserLogged, navigate, selectedExpense]);

    const fetchExpenses = () => {
        ExpensesService.getAllExpensesNoPagination()
            .then((response: any) => {
                setExpenses(response.data as IExpensesData[])
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
            });
    };

    const fetchYears = () => {
        let localMinYear: number | null = null;
        let localMaxYear: number | null = null;

        ExpensesService.getMinYear()
            .then((response: any) => {
                localMinYear = response.data;
                setMinYear(localMinYear);
                return ExpensesService.getMaxYear();
            })
            .then((response: any) => {
                localMaxYear = response.data;
                setMaxYear(localMaxYear);
                if (localMinYear !== null && localMinYear === localMaxYear) {
                    setYear(localMinYear);
                    fetchExpensesByYear(localMinYear);
                }
            })
            .catch((e: Error) => {
                console.log(e);
            });
    };

    const fetchExpensesByYear = (year: number) => {
        setLoading(true);
        ExpensesService.getExpensesByYear(year)
            .then((response: any) => {
                setExpenses(response.data.records);
                setYearlyTotal(response.data.yearlyTotal);
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
                setLoading(false);
            });
    };

    const fetchExpensesByMonth = (year: number, month: number) => {
        setLoading(true);
        ExpensesService.getExpensesByMonth(year, month)
            .then((response: any) => {
                setExpenses(response.data.records);
                setMonthlyTotal(response.data.monthlyTotal);
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
                setLoading(false);
            });
    };

    const handleAdd = () => {
        setAddDialogOpen(true);
    }

    const handleAddDialogClose = () => {
        setAddDialogOpen(false);
        fetchExpenses();
    }

    const handleEdit = (row: IExpensesData) => {
        setSelectedExpense(row);
        setEditDialogOpen(true);
    }

    const handleDelete = (row: IExpensesData) => {
        Swal.fire({
            title: "Are you sure you want to delete " + row.category + " | " + row.subCategory + " expense?",
            text: "You won't be able to revert this!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!"
        }).then((result) => {
            if (result.isConfirmed) {
                deleteExpense(row.id)
            }
        });
    }

    const deleteExpense = (id: number) => {
        ExpensesService.deleteExpense(id)
            .then((response: any) => {
                Swal.fire({
                    title: "Deleted!",
                    text: response.data,
                    icon: "success"
                }).then(r => {
                });

                fetchExpenses();
            })
            .catch((e: Error) => {
                Swal.fire({
                    icon: "error",
                    title: "Oops...",
                    text: "Something went wrong!"
                }).then(r => {
                });

                console.log(e);
            });
    }

    const handleEditDialogClose = () => {
        setEditDialogOpen(false);
        setSelectedExpense(null);
        fetchExpenses();
    }

    const handleUpdateCurrencyDialogClose = () => {
        setUpdateDialogOpen(false);
        const userPreferredCurrency = LocalStorageService.getCurrencyCodeFromLocalStorage();
        setCurrencyCode(userPreferredCurrency);

        if (year !== null) {
            fetchExpensesByYear(year);
        }
        if (year !== null && month !== null) {
            fetchExpensesByMonth(year, month);
        }
    }

    const createTooltipColumn = (
        name: string,
        selector: (row: IExpensesData) => string
    ): TableColumn<IExpensesData> => {
        return {
            name: <Typography variant="body1" color="primary">{name}</Typography>,
            cell: (row: IExpensesData) => (
                <Tooltip title={selector(row)} arrow>
                    <Typography variant="body2" noWrap>
                        {selector(row)}
                    </Typography>
                </Tooltip>
            ),
            sortable: true,
        };
    };

    const columns: TableColumn<IExpensesData>[] = [
        {
            name: <Typography variant="body1" color="primary">ID</Typography>,
            selector: row => row.id,
            sortable: true,
        },
        createTooltipColumn('Category', (row) => row.category),
        createTooltipColumn('SubCategory', (row) => row.subCategory),
        {
            name: <Typography variant="body1" color="primary">Amount</Typography>,
            cell: (row: IExpensesData) => (
                <Typography variant="body2" noWrap>
                    {row.amount} {currencyCode}
                </Typography>
            ),
            sortable: true,
        },
        {
            name: <Typography variant="body1" color="primary">Recurring</Typography>,
            cell: (row: IExpensesData) => (
                <Typography variant="body2" noWrap>
                    {row.recurring ? 'Yes' : 'No'}
                </Typography>
            ),
            sortable: true,
        },
        {
            name: <Typography variant="body1" color="primary">Recurrence Period</Typography>,
            selector: row => row.recurrencePeriod,
            sortable: true,
        },
        {
            name: <Typography variant="body1" color="primary">Date</Typography>,
            selector: row => row.date,
            cell: (row: IExpensesData) => (
                <Typography variant="caption">
                    {format(new Date(row.date), 'PPP p')}
                </Typography>
            ),
            sortable: true,
        },
        {
            name: <Typography variant="body1" color="primary">Options</Typography>,
            cell: (row: IExpensesData) => (
                <Box sx={{mt: 1, mb: 1, display: 'flex', flexDirection: 'column', gap: '8px'}}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => handleEdit(row)}
                        size="small"
                        startIcon={<EditIcon/>}
                    >
                        Edit
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => handleDelete(row)}
                        size="small"
                        startIcon={<DeleteIcon/>}
                    >
                        Delete
                    </Button>
                </Box>
            ),
            style: {
                minWidth: '140px',
            },
        }
    ];

    const handleUpdateCurrency = () => {
        setUpdateDialogOpen(true);
        setCurrencyCode(LocalStorageService.getCurrencyCodeFromLocalStorage());
    }

    const filteredExpenses = expenses.filter(expense =>
        expense.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
        expense.category.toLowerCase().includes(searchTerm.toLowerCase()) ||
        expense.subCategory.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h4" gutterBottom>
                    Expenses
                </Typography>
                <Box display="flex" flexDirection="column" alignItems="flex-end">
                    <Typography variant="subtitle1" color="textSecondary" gutterBottom>
                        Current Currency: {currencyCode}
                    </Typography>
                    <Typography variant="caption" color="textSecondary">
                        This currency will be used for both expenses and incomes amounts.
                    </Typography>
                </Box>
            </Box>
            <Box display="flex" justifyContent="flex-start" alignItems="center" mb={2}>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleAdd}
                    size="small"
                    startIcon={<AddIcon/>}
                    sx={{mr: 2}}
                >
                    Add
                </Button>
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={handleUpdateCurrency}
                    size="small"
                    startIcon={<EditIcon/>}
                >
                    Update Currency
                </Button>
            </Box>

            <Box display="flex" justifyContent="flex-start" alignItems="center" mb={2}>
                <DownloadExpensesButtons
                    expenses={expenses}
                    year={year}
                    month={month}
                    currencyCode={currencyCode}
                    monthlyTotal={monthlyTotal}
                />
            </Box>

            <Box display="flex" flexDirection="column" justifyContent="flex-end" alignItems="flex-start" mb={2} gap={2}
                 flexWrap="wrap">
                <Box display="flex" flexDirection="row" flexWrap="wrap" alignItems="center" gap={2}>
                    <TextField
                        label="Search Expense"
                        variant="outlined"
                        size="small"
                        value={searchTerm}
                        onChange={e => setSearchTerm(e.target.value)}
                        sx={{width: '200px'}}
                    />
                    <FormControl variant="outlined" size="small" sx={{width: '200px'}}>
                        <InputLabel id="year-select-label">Year</InputLabel>
                        <Select
                            labelId="year-select-label"
                            value={year}
                            onChange={(e) => {
                                const selectedYear = e.target.value as number;
                                setYear(selectedYear);
                                fetchExpensesByYear(selectedYear);
                                setMonth(null); // Reset month selection
                            }}
                            label="Year"
                        >
                            {minYear && maxYear && [...Array(maxYear - minYear + 1)].map((_, i) => {
                                const currentYear = minYear + i;
                                return <MenuItem key={currentYear} value={currentYear}>{currentYear}</MenuItem>;
                            })}
                        </Select>
                    </FormControl>
                    {year && (
                        <FormControl variant="outlined" size="small" sx={{width: '200px'}}>
                            <InputLabel id="month-select-label">Month</InputLabel>
                            <Select
                                labelId="month-select-label"
                                value={month ?? ''}
                                onChange={(e) => {
                                    const selectedMonth = e.target.value as number;
                                    setMonth(selectedMonth);
                                    fetchExpensesByMonth(year, selectedMonth);
                                }}
                                label="Month"
                            >
                                {[...Array(12)].map((_, i) => (
                                    <MenuItem key={i + 1} value={i + 1}>
                                        {new Date(0, i).toLocaleString('default', {month: 'long'})}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    )}
                </Box>
            </Box>


            {loading ?
                (
                    <Box sx={{width: '100%'}}>
                        {/* Skeleton for table header */}
                        <Box display="flex" alignItems="center" p={1}>
                            {Array.from({length: columns.length}).map((_, index) => (
                                <Box key={index} flex={1} pr={2}>
                                    <Skeleton variant="text"/>
                                </Box>
                            ))}
                        </Box>

                        {/* Skeleton for table rows */}
                        {Array.from({length: 2}).map((_, rowIndex) => (
                            <Box key={rowIndex} display="flex" alignItems="center" p={1}>
                                {Array.from({length: columns.length}).map((_, colIndex) => (
                                    <Box key={colIndex} flex={1} pr={2}>
                                        <Skeleton variant="text"/>
                                    </Box>
                                ))}
                            </Box>
                        ))}
                    </Box>
                ) : (
                    <>
                        <Paper elevation={3}
                               sx={{padding: 3, marginTop: 3, marginLeft: 'auto', marginRight: 'auto'}}>

                            <Typography variant="button" display="block" gutterBottom>
                                Yearly Total for {year}:
                                <Typography component="span" color={yearlyTotal ? "error" : "error"}>
                                    {yearlyTotal ? ` - ${yearlyTotal.toString()} ${currencyCode}` : ' No expenses for this year'}
                                </Typography>
                            </Typography>

                            {month !== null && (
                                <>
                                    <Typography variant="button" display="block" gutterBottom>
                                        Monthly Total for {new Date(0, month - 1).toLocaleString('default', { month: 'long' })} {year}:
                                        <Typography component="span" color={monthlyTotal ? "error" : "error"}>
                                            {monthlyTotal ? ` - ${monthlyTotal.toString()} ${currencyCode}` : ' No expenses for this month'}
                                        </Typography>
                                    </Typography>
                                </>
                            )}

                            <Divider />

                            <DataTable
                                key={expenses.length}
                                columns={columns}
                                data={filteredExpenses}
                                pagination
                                expandableRows
                                expandableRowsComponent={ExpandedExpenseDetails}
                            />
                        </Paper>

                    </>
                )
            }

            <AddExpenseDialog
                open={addDialogOpen}
                onClose={handleAddDialogClose}
            />

            <EditExpenseDialog
                open={editDialogOpen}
                onClose={handleEditDialogClose}
                rowData={selectedExpense}
            />

            <UpdateCurrencyDialog
                open={updateDialogOpen}
                onClose={handleUpdateCurrencyDialogClose}
            />
        </>
    );
};

export default ExpensesScreen;