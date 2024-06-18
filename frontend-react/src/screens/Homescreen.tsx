import React from 'react';
import MainNavbar from "../components/MainNavbar";
import FinancesScreen from "./finances/FinancesScreen";

function Homescreen() {
    return (
        <>
            <MainNavbar/>
            <FinancesScreen/>
        </>
    );
}

export default Homescreen;