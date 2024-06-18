import React, {useEffect, useState} from 'react';
import Typography from "@mui/material/Typography";
import {Button, Paper, Skeleton, Tooltip} from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import Box from "@mui/material/Box";
import DataTable, {TableColumn} from "react-data-table-component";
import IExpensesData from "../../types/expenses/IExpensesData";
import {useNavigate} from "react-router-dom";
import LocalStorageService from "../../services/LocalStorageService";
import ExpensesService from "../../services/ExpensesService";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import ExpandedExpenseDetails from "../../components/ExpandedExpenseDetails";
import AddExpenseDialog from "../../components/AddExpenseDialog";

const ExpensesScreen = () => {
    const navigate = useNavigate();

    const [expenses, setExpenses] = useState<IExpensesData[]>([]);
    const [loading, setLoading] = useState(true);
    const isUserLogged = LocalStorageService.isUserLogged();
    const [addDialogOpen, setAddDialogOpen] = useState(false);
    const [editDialogOpen, setEditDialogOpen] = useState(false);

    useEffect(() => {
        if (!isUserLogged) {
            navigate("/login");
            return;
        }

        fetchExpenses();

        // if (selectedBook) {
        //     setEditDialogOpen(true);
        // }
    }, []);

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

    const handleAdd = () => {
        setAddDialogOpen(true);
    }

    const handleAddDialogClose = () => {
        setAddDialogOpen(false);
        fetchExpenses();
    }

    const handleEdit = (row: IExpensesData) => {
        console.log("edit clicked")
    }

    const handleDelete = (row: IExpensesData) => {
        console.log("delete clicked")
    }

    const createTooltipColumn = (
        name: string,
        selector: (row: IExpensesData) => string
    ): TableColumn<IExpensesData> => {
        return {
            name,
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
            name: 'ID',
            selector: row => row.id,
            sortable: true,
        },
        createTooltipColumn('Category', (row) => row.category),
        createTooltipColumn('SubCategory', (row) => row.subcategory),
        {
            name: 'Amount',
            selector: row => row.amount,
            sortable: true,
        },
        {
            name: 'Recurring',
            selector: row => row.recurring,
            sortable: true,
        },
        {
            name: 'Recurrence Period',
            selector: row => row.recurrencePeriod,
            sortable: true,
        },
        {
            name: 'Date',
            selector: row => row.date,
            sortable: true,
        },
        {
            name: 'Options',
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

    return (
        <>
            <Typography variant={"h4"} gutterBottom>
                Expenses
            </Typography>
            <Button
                variant="contained"
                color="primary"
                onClick={() => handleAdd()}
                size="small"
                startIcon={<AddIcon/>}
            >
                Add
            </Button>

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
                    <Paper elevation={3}
                           sx={{padding: 3, marginTop: 3, marginLeft: 'auto', marginRight: 'auto'}}>

                        <DataTable
                            key={expenses.length}
                            columns={columns}
                            data={expenses}
                            pagination
                            expandableRows
                            expandableRowsComponent={ExpandedExpenseDetails}
                        />
                    </Paper>
                )
            }

            <AddExpenseDialog
                open={addDialogOpen}
                onClose={handleAddDialogClose}
            />
        </>
    );
};

export default ExpensesScreen;