import React, {ChangeEvent, FormEvent, useEffect, useState} from 'react';
import {Link, useNavigate} from "react-router-dom";
import {Button, FormControl, InputLabel, Paper, Select, TextField} from "@mui/material";
import BreadcrumbMulti from "../../components/BreadcrumbMulti";
import UsersService from "../../services/UsersService";
import LocalStorageService from "../../services/LocalStorageService";
import MenuItem from "@mui/material/MenuItem";
import {ICurrencyData} from "../../types/currencies/ICurrencyData";
import { currencies } from "../finances/currencies";

const RegisterScreen = () => {
    const navigate = useNavigate();
    const [selectedCurrency, setSelectedCurrency] = useState<string>("EUR");

    const checkTokenAndRedirect = () => {
        const isUserLoggedIn = LocalStorageService.isUserLogged();

        if (isUserLoggedIn) {
            navigate("/");
        }
    }

    useEffect(() => {
        checkTokenAndRedirect();
    }, []);

    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
    });

    const [validationMessages, setValidationMessages] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
    });

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.id]: e.target.value,
        })
    }

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();

        setValidationMessages({
            username: '',
            email: '',
            password: '',
            confirmPassword: '',
        });

        if (formData.password !== formData.confirmPassword) {
            setValidationMessages({
                ...validationMessages,
                confirmPassword: 'Passwords do not match.',
            });
            return;
        }

        const dataToSubmit = {
            ...formData,
            currency: selectedCurrency,
        };

        registerUser(dataToSubmit);
        navigate("/");
    }

    const registerUser = (formData: object) => {
        UsersService.registerUser(formData)
            .then((response: any) => {
                LocalStorageService.addUserTokenToLocalStorage(response.data.token)
            })
            .catch((e: any) => {
                if (e.response) {
                    const errorData = e.response.data;

                    if (errorData.errors && Array.isArray(errorData.errors)) {
                        errorData.errors.forEach((errorMessage: string) => {
                            const [fieldName, errorDescription] = errorMessage.split(':');

                            setValidationMessages((prevValidationMessages) => ({
                                ...prevValidationMessages,
                                [fieldName.trim()]: errorDescription.trim(),
                            }));
                        });
                    }
                }

            });
    }

    return (
        <>
            {/*<MainNavbar/>*/}
            <BreadcrumbMulti items={["Register"]}/>
            <Paper elevation={3}
                   sx={{padding: 3, marginTop: 3, width: '85%', marginLeft: 'auto', marginRight: 'auto'}}>
                <form onSubmit={handleSubmit}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="username"
                        label="Username"
                        value={formData.username}
                        onChange={handleChange}
                        error={Boolean(validationMessages.username)}
                        helperText={validationMessages.username}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="Email"
                        value={formData.email}
                        onChange={handleChange}
                        error={Boolean(validationMessages.email)}
                        helperText={validationMessages.email}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="password"
                        label="Password"
                        type="password"
                        value={formData.password}
                        onChange={handleChange}
                        helperText={validationMessages.password}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="confirmPassword"
                        label="Confirm Password"
                        type="password"
                        value={formData.confirmPassword}
                        error={Boolean(validationMessages.confirmPassword)}
                        onChange={handleChange}
                        helperText={validationMessages.confirmPassword}
                    />
                    <FormControl fullWidth margin="normal">
                        <InputLabel id="currency-label">Currency</InputLabel>
                        <Select
                            labelId="currency-label"
                            id="currency"
                            value={selectedCurrency}
                            onChange={(e) => setSelectedCurrency(e.target.value)}
                            label="Currency"
                        >
                            {currencies.map((currency) => (
                                <MenuItem key={currency.code} value={currency.code}>
                                    {currency.name} ({currency.code})
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <div className="auth-actions">
                        <Link to={"/login"} style={{textDecoration: "none"}}>Already have an account? Login
                            here..</Link>
                        <Button type="submit" fullWidth variant="contained" color="primary" style={{marginTop: '1rem'}}>
                            Register
                        </Button>
                    </div>
                </form>
            </Paper>
        </>
    );
};

export default RegisterScreen;