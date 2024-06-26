import React from 'react';
import './App.css';
import './styles.css'
import {Route, Routes} from "react-router-dom";
import ResetPasswordScreen from "./screens/auth/ResetPasswordScreen";
import RegisterScreen from "./screens/auth/RegisterScreen";
import LoginScreen from "./screens/auth/LoginScreen";
import ForgotPasswordScreen from "./screens/auth/ForgotPasswordScreen";
import FinancesScreen from "./screens/finances/FinancesScreen";

function App() {
    return (
        <Routes>
            <Route path="/*" element={<FinancesScreen/>} />

            {/*  Auth routes  */}
            <Route path={"/register"} element={<RegisterScreen/>}/>
            <Route path={"/login"} element={<LoginScreen/>}/>
            <Route path={"/forgot-password"} element={<ForgotPasswordScreen/>}/>
            <Route path={"/reset-password"} element={<ResetPasswordScreen/>}/>
        </Routes>
    );
}

export default App;
