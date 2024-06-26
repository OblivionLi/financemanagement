import React, {useEffect, useState} from 'react';
import {
    Alert,
    Button, Checkbox,
    Dialog,
    DialogContent,
    DialogTitle,
    FormControlLabel,
    Grid,
    Paper,
    TextField
} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import LocalStorageService from "../../services/LocalStorageService";
import {IIncomeCreateRequest} from "../../types/incomes/IIncomeCreateRequest";
import IncomesService from "../../services/IncomesService";
import {IIncomeEditModalProps} from "../../types/IIncomeEditModalProps";

const EditIncomeDialog: React.FC<IIncomeEditModalProps> = ({open, onClose, rowData}) => {

    const [formData, setFormData] = useState({
        description: rowData?.description || '',
        amount: rowData?.amount || '',
        source: rowData?.source || '',
        date: rowData?.date || new Date().toISOString(),
        recurring: rowData?.recurring || false,
        recurrencePeriod: rowData?.recurrencePeriod || '',
    });

    const [errors, setErrors] = useState<{ [key: string]: string }>({});
    const [currencyCode, setCurrencyCode] = useState('');

    useEffect(() => {
        if (open) {
            const userPreferredCurrency = LocalStorageService.getCurrencyCodeFromLocalStorage();
            setCurrencyCode(userPreferredCurrency);
        }

        setFormData({
            description: rowData?.description || '',
            amount: rowData?.amount || '',
            source: rowData?.source || '',
            date: rowData?.date || new Date().toISOString(),
            recurring: rowData?.recurring || false,
            recurrencePeriod: rowData?.recurrencePeriod || '',
        })
    }, [rowData, open]);

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value, type, checked} = event.target;
        const newValue = type === 'checkbox' ? checked : value;

        setFormData({
            ...formData,
            [name]: newValue,
        });
    };

    const handleAmountChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = event.target;
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

        const incomeData = {
            ...formData,
            date: new Date(formData.date).toISOString(),
        };

        editIncome(incomeData);
        onClose();
    };

    const editIncome = (data: any) => {
        if (rowData == null) {
            return;
        }

        IncomesService.editIncome(rowData.id, data)
            .catch((e: any) => {
                console.error(e);
                if (e.response && e.response.data && e.response.data.errors) {
                    setErrors(e.response.data.errors);
                }
            });
    }

    return (
        <Dialog open={open} onClose={onClose} maxWidth={"md"} fullWidth>
            <DialogTitle>Edit Income</DialogTitle>
            <DialogContent>
                <Paper elevation={3}
                       sx={{padding: 3, marginTop: 3, width: '100%', marginLeft: 'auto', marginRight: 'auto'}}>
                    <form onSubmit={handleSubmit}>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <TextField
                                    label="Source"
                                    name="source"
                                    value={formData.source}
                                    onChange={handleChange}
                                    fullWidth
                                    required
                                    error={!!errors.source}
                                    helperText={errors.source}
                                />
                            </Grid>
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
                                        value={formData.recurrencePeriod || ''}
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
                                <Alert severity="info" sx={{marginBottom: '1rem'}}>Internally all finances are based on {currencyCode} until you update your preferred currency. You can do it 5 times a day only.</Alert>
                            </Grid>
                            <Grid item xs={12}>
                                <Button type="submit" variant="contained" color="primary" fullWidth>
                                    Edit Income
                                </Button>
                            </Grid>
                        </Grid>
                    </form>
                </Paper>

            </DialogContent>
        </Dialog>
    );
};

export default EditIncomeDialog;