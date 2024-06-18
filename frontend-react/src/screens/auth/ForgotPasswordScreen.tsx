import React, {ChangeEvent, FormEvent, useState} from 'react';
import {Link, useNavigate} from "react-router-dom";
import {Alert, Button, Paper, TextField} from "@mui/material";
import UsersService from "../../services/UsersService";
import BreadcrumbMulti from "../../components/BreadcrumbMulti";
import MainNavbar from "../../components/MainNavbar";
import CheckIcon from '@mui/icons-material/Check';

const ForgotPasswordScreen = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();

        forgotPassword(email);
    }

    const forgotPassword = (email: string) => {
        UsersService.forgotPassword(email)
            .then((response: any) => {
                navigate("/")
                setIsEmailSentMessage("")
            })
            .catch((e: any) => {
                console.log(e);
                setIsEmailSentMessage("If email is valid, you will receive an url in your email inbox that will redirect you to a reset password form.")
            });
    }

    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        setEmail(e.target.value)
    }

    const [isEmailSentMessage, setIsEmailSentMessage] = useState("");

    return (
        <>
            <MainNavbar/>
            <BreadcrumbMulti items={["Forgot Password"]}/>
            <Paper elevation={3}
                   sx={{padding: 3, marginTop: 3, width: '85%', marginLeft: 'auto', marginRight: 'auto'}}>

                {isEmailSentMessage &&
                    <Alert icon={<CheckIcon fontSize="inherit"/>} severity="success">
                        {isEmailSentMessage}
                    </Alert>
                }


                <form onSubmit={handleSubmit}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="Email"
                        onChange={handleChange}
                    />
                    <div className="auth-actions">
                        <Link to={"/login"} style={{textDecoration: "none"}}>Never mind, I remembered my
                            password..</Link>
                        <Link to={"/register"} style={{textDecoration: "none"}}>Don't have an account yet? Click here to
                            register.
                        </Link>
                        <Button type="submit" fullWidth variant="contained" color="primary" style={{marginTop: '1rem'}}>
                            Send Password Request
                        </Button>
                    </div>
                </form>
            </Paper>
        </>
    );
};

export default ForgotPasswordScreen;