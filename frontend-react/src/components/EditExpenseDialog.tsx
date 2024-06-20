import React, {useEffect, useState} from 'react';
import IExpenseEditModalProps from "../types/IExpenseEditModalProps";
import {IExpenseSubCategoryData} from "../types/expenses/IExpenseSubCategoryData";
import {IExpenseSubCategoryCreateRequest} from "../types/expenses/IExpenseSubCategoryCreateRequest";
import LocalStorageService from "../services/LocalStorageService";
import ExpensesService from "../services/ExpensesService";
import {
    Alert,
    Button, Checkbox,
    Dialog,
    DialogContent,
    DialogTitle, FormControl,
    FormControlLabel,
    Grid, InputLabel,
    Paper, Select, SelectChangeEvent,
    TextField
} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import Typography from "@mui/material/Typography";

const EditExpenseDialog: React.FC<IExpenseEditModalProps> = ({open, onClose, rowData}) => {
    const [formData, setFormData] = useState({
        description: rowData?.description || '',
        amount: rowData?.amount || '',
        subCategoryId: rowData?.subCategoryId || 0,
        date: rowData?.date || new Date().toISOString(),
        recurring: rowData?.recurring || false,
        recurrencePeriod: rowData?.recurrencePeriod || '',
    });

    const [errors, setErrors] = useState<{ [key: string]: string }>({});
    const [subcategories, setSubcategories] = useState<IExpenseSubCategoryData[]>([]);
    const [newSubcategoryMessage, setNewSubcategoryMessage] = useState<string>("");
    const [newSubcategory, setNewSubcategory] = useState<IExpenseSubCategoryCreateRequest>({
        name: '',
        category: ''
    });
    const [isAddingSubcategory, setIsAddingSubcategory] = useState(false);
    const [currencyCode, setCurrencyCode] = useState('');

    const categories = [
        { value: 'SUBSCRIPTION', label: 'Subscription' },
        { value: 'FOOD', label: 'Food' },
        { value: 'UTILITIES', label: 'Utilities' },
        { value: 'ENTERTAINMENT', label: 'Entertainment' },
        { value: 'TRANSPORTATION', label: 'Transportation' },
        { value: 'HEALTHCARE', label: 'Healthcare' },
        { value: 'OTHER', label: 'Other' },
    ];

    useEffect(() => {
        if (open) {
            fetchSubcategories();

            const userPreferredCurrency = LocalStorageService.getCurrencyCodeFromLocalStorage();
            setCurrencyCode(userPreferredCurrency);
        }

        setFormData({
            description: rowData?.description || '',
            amount: rowData?.amount || '',
            subCategoryId: rowData?.subCategoryId || 0,
            date: rowData?.date || new Date().toISOString(),
            recurring: rowData?.recurring || false,
            recurrencePeriod: rowData?.recurrencePeriod || '',
        })
    }, [rowData, open]);

    const fetchSubcategories = async () => {
        ExpensesService.getExpenseSubCategories()
            .then((response: any) => {
                setSubcategories(response.data as IExpenseSubCategoryData[])
            })
            .catch((e: Error) => {
                console.log(e);
            });
    };

    const handleSelectChange = (event: SelectChangeEvent<number>) => {
        const { name, value } = event.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = event.target;
        const newValue = type === 'checkbox' ? checked : value;

        setFormData({
            ...formData,
            [name]: newValue,
        });
    };

    const handleAmountChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        const regex = /^\d+(\.\d{0,2})?$/;
        if (value === '' || regex.test(value)) {
            setFormData({
                ...formData,
                [name]: value,
            });
        }
    };

    const handleSubmit = (event: React.FormEvent) => {
        event.preventDefault();

        const expenseData = {
            ...formData,
            date: new Date(formData.date).toISOString(), // Convert to correct format
        };

        editExpense(expenseData);
        onClose();
    };

    const handleAddSubcategory = async () => {
        ExpensesService.addExpenseSubCategory(newSubcategory)
            .then((response: any) => {
                setNewSubcategory({ name: '', category: '' })
                setIsAddingSubcategory(false);
                setNewSubcategoryMessage("Subcategory added successfully.");
                fetchSubcategories();
            })
            .catch((e: Error) => {
                console.log(e);
            });
    };

    const editExpense = (data: any) => {
        if (rowData == null) {
            return;
        }

        ExpensesService.editExpense(rowData.id, data)
            .catch((e: any) => {
                console.error(e);
                if (e.response && e.response.data && e.response.data.errors) {
                    setErrors(e.response.data.errors);
                }
            });
    }

    return (
        <Dialog open={open} onClose={onClose} maxWidth={"md"} fullWidth>
            <DialogTitle>Edit Expense</DialogTitle>
            <DialogContent>
                <Paper elevation={3}
                       sx={{ padding: 3, marginTop: 3, width: '100%', marginLeft: 'auto', marginRight: 'auto' }}>
                    <form onSubmit={handleSubmit}>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <TextField
                                    label="Description"
                                    name="description"
                                    value={formData.description}
                                    onChange={handleChange}
                                    fullWidth
                                    required
                                    error={!!errors.description}
                                    helperText={errors.description}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <TextField
                                    label="Amount"
                                    name="amount"
                                    value={formData.amount}
                                    onChange={handleAmountChange}
                                    fullWidth
                                    required
                                    error={!!errors.amount}
                                    helperText={errors.amount}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                {newSubcategoryMessage && (<Alert severity="success" sx={{ marginBottom: '1rem' }}>{newSubcategoryMessage}</Alert>)}
                                <FormControl fullWidth required error={!!errors.subCategoryId}>
                                    <InputLabel id="subcategory-label">SubCategory</InputLabel>
                                    <Select
                                        labelId="subcategory-label"
                                        name="subCategoryId"
                                        value={formData.subCategoryId}
                                        onChange={handleSelectChange}
                                        label="SubCategory"
                                        disabled={subcategories.length === 0}
                                    >
                                        {subcategories.length === 0 ? (
                                            <MenuItem value="">
                                                <Typography variant="body2">
                                                    No subcategories available. Please add one.
                                                </Typography>
                                            </MenuItem>
                                        ) : (
                                            subcategories.map((subcategory) => (
                                                <MenuItem key={subcategory.id} value={subcategory.id}>
                                                    {subcategory.subCategoryName}
                                                </MenuItem>
                                            ))
                                        )}
                                    </Select>
                                    <Button onClick={() => setIsAddingSubcategory(true)}>Add New Subcategory</Button>
                                </FormControl>
                            </Grid>

                            {isAddingSubcategory && (
                                <Paper elevation={3}
                                       sx={{
                                           padding: 3,
                                           marginTop: 3,
                                           width: '100%',
                                           marginLeft: 'auto',
                                           marginRight: 'auto'
                                       }}>
                                    <Typography variant="h6" gutterBottom>Add New Subcategory</Typography>
                                    <Alert severity="info" sx={{ marginBottom: "1rem" }}>
                                        {"Example: Category -> Utilities; SubCategory -> Electricity"}</Alert>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <FormControl fullWidth required>
                                                <InputLabel id="category-label">Category</InputLabel>
                                                <Select
                                                    labelId="category-label"
                                                    name="category"
                                                    value={newSubcategory.category}
                                                    onChange={(e) => setNewSubcategory({
                                                        ...newSubcategory,
                                                        category: e.target.value
                                                    })}
                                                    label="Category"
                                                >
                                                    {categories.map((category) => (
                                                        <MenuItem key={category.value} value={category.value}>
                                                            {category.label}
                                                        </MenuItem>
                                                    ))}
                                                </Select>
                                            </FormControl>
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                label="Subcategory Name"
                                                name="name"
                                                value={newSubcategory.name}
                                                onChange={(e) => setNewSubcategory({
                                                    ...newSubcategory,
                                                    name: e.target.value
                                                })}
                                                fullWidth
                                                required
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Button variant="contained" color="primary" fullWidth
                                                    onClick={handleAddSubcategory}>
                                                Add Subcategory
                                            </Button>
                                            <Button variant="outlined" color="secondary" fullWidth
                                                    onClick={() => setIsAddingSubcategory(false)} sx={{ mt: 2 }}>
                                                Cancel
                                            </Button>
                                        </Grid>
                                    </Grid>
                                </Paper>
                            )}

                            <Grid item xs={12}>
                                <TextField
                                    label="Date"
                                    name="date"
                                    value={formData.date}
                                    onChange={handleChange}
                                    type="datetime-local"
                                    fullWidth
                                    required
                                    error={!!errors.date}
                                    helperText={errors.date}
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            name="recurring"
                                            checked={formData.recurring}
                                            onChange={handleChange}
                                        />
                                    }
                                    label="Recurring"
                                />
                            </Grid>
                            {formData.recurring && (
                                <Grid item xs={12}>
                                    <TextField
                                        label="Recurrence Period"
                                        name="recurrencePeriod"
                                        value={formData.recurrencePeriod}
                                        onChange={handleChange}
                                        select
                                        fullWidth
                                        required
                                        error={!!errors.recurrencePeriod}
                                        helperText={errors.recurrencePeriod}
                                    >
                                        <MenuItem value="WEEKLY">Weekly</MenuItem>
                                        <MenuItem value="MONTHLY">Monthly</MenuItem>
                                        <MenuItem value="YEARLY">Yearly</MenuItem>
                                    </TextField>
                                </Grid>
                            )}
                            <Grid item xs={12}>
                                <Alert severity="info" sx={{ marginBottom: '1rem' }}>Internally all finances are based on {currencyCode} until you update your preferred currency. You can do it 5 times a day only.</Alert>
                            </Grid>
                            <Grid item xs={12}>
                                <Button type="submit" variant="contained" color="primary" fullWidth>
                                    Update Expense
                                </Button>
                            </Grid>
                        </Grid>
                    </form>
                </Paper>
            </DialogContent>
        </Dialog>
    );
};

export default EditExpenseDialog;