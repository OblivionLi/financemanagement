import React, {useState} from 'react';
import {IUpdateCurrencyModalProps} from "../types/currency/IUpdateCurrencyModalProps";
import {
    Button,
    Checkbox,
    Dialog,
    DialogContent,
    DialogTitle,
    FormControl,
    FormControlLabel,
    Grid, InputLabel,
    Paper, Select, SelectChangeEvent
} from "@mui/material";
import {IUpdateCurrencyRequest} from "../types/currency/IUpdateCurrencyRequest";
import {currencies} from "../screens/finances/currencies";
import MenuItem from "@mui/material/MenuItem";
import ExpensesService from "../services/ExpensesService";
import LocalStorageService from "../services/LocalStorageService";
import Swal from "sweetalert2";

const UpdateCurrencyDialog: React.FC<IUpdateCurrencyModalProps> = ({open, onClose}) => {
    const [formData, setFormData] = useState({
        convertAmounts: false,
        currencyCode: LocalStorageService.getCurrencyCodeFromLocalStorage()
    });

    const handleCheckboxChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const {name, checked} = event.target;
        setFormData({
            ...formData,
            [name]: checked,
        });
    };

    const handleSelectChange = (event: SelectChangeEvent<string>) => {
        const {name, value} = event.target;
        setFormData({
            ...formData,
            [name as string]: value,
        });
    };

    const handleSubmit = (event: React.FormEvent) => {
        event.preventDefault();

        const data: IUpdateCurrencyRequest = {
            convertAmounts: formData.convertAmounts,
            currencyCode: formData.currencyCode
        };

        updateCurrency(data);
    };

    const updateCurrency = (data: IUpdateCurrencyRequest) => {
        ExpensesService.updateCurrency(data)
            .then((response: any) => {
                LocalStorageService.addUserPreferredCurrencyToLocalStorage(response.data.currency);
                onClose();
            })
            .catch((e: any) => {
                console.error(e);
                onClose();
                Swal.fire({
                    icon: "error",
                    title: "Oops...",
                    text: e.response.data.message + ". You can only change your currency 5 times a day.",
                });
            });
    }

    return (
        <Dialog open={open} onClose={onClose} maxWidth={"md"} fullWidth>
            <DialogTitle>Update Currency</DialogTitle>
            <DialogContent>
                <Paper elevation={3}
                       sx={{padding: 3, marginTop: 3, width: '100%', marginLeft: 'auto', marginRight: 'auto'}}>
                    <form onSubmit={handleSubmit}>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <FormControl fullWidth margin="normal">
                                    <InputLabel id="currency-select-label">Currency</InputLabel>
                                    <Select
                                        labelId="currency-select-label"
                                        name="currencyCode"
                                        value={formData.currencyCode}
                                        onChange={handleSelectChange}
                                        label="Currency"
                                    >
                                        {currencies.map((currency) => (
                                            <MenuItem key={currency.code} value={currency.code}>
                                                {currency.name}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={12}>
                                <FormControlLabel
                                    control={
                                        <Checkbox
                                            name="convertAmounts"
                                            checked={formData.convertAmounts}
                                            onChange={handleCheckboxChange}
                                        />
                                    }
                                    label="Update Amounts?"
                                />
                            </Grid>
                            <Grid item xs={12}>
                                <Button type="submit" variant="contained" color="primary" fullWidth>
                                    Update Currency
                                </Button>
                            </Grid>
                        </Grid>
                    </form>
                </Paper>
            </DialogContent>
        </Dialog>
    );
};

export default UpdateCurrencyDialog;