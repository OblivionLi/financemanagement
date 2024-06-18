import React, {useEffect, useState} from 'react';
import LocalStorageService from "../../services/LocalStorageService";
import {Link, Route, Routes, useNavigate} from "react-router-dom";
import {
    CssBaseline,
    Divider,
    Drawer,
    List,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText,
    Paper
} from "@mui/material";
import ExpensesScreen from "./ExpensesScreen";
import Toolbar from "@mui/material/Toolbar";
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import BarChartIcon from '@mui/icons-material/BarChart';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import RepeatIcon from '@mui/icons-material/Repeat';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import ChartsScreen from "./ChartsScreen";
import MenuIcon from '@mui/icons-material/Menu';

const drawerWidth = 240;

const FinancesScreen = () => {
    const navigate = useNavigate();
    const [mobileOpen, setMobileOpen] = useState(false);
    const isUserLogged = LocalStorageService.isUserLogged();

    useEffect(() => {
        if (!isUserLogged) {
            navigate("/login");
            return;
        }
    }, []);

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };

    const handleLogout = () => {
        LocalStorageService.logoutUser();
        navigate("/");
    }

    const drawer = (
        <div>
            <Toolbar />
            <Divider />
            <List>
                <ListItem disablePadding>
                    <ListItemButton>
                        <ListItemIcon>
                            <BarChartIcon />
                        </ListItemIcon>
                        <Link
                            to={"/"}
                        >
                            Charts
                        </Link>
                    </ListItemButton>
                </ListItem>
            </List>
            <Divider />
            <List>
                <ListItem disablePadding>
                    <ListItemButton>
                        <ListItemIcon>
                            <ReceiptLongIcon />
                        </ListItemIcon>
                        <Link
                            to={"/expenses"}
                        >
                            Expenses
                        </Link>
                    </ListItemButton>
                </ListItem>
            </List>
            <List>
                <ListItem disablePadding>
                    <ListItemButton>
                        <ListItemIcon>
                            <RepeatIcon />
                        </ListItemIcon>
                        <Link
                            to={"/expenses/recurring"}
                        >
                            Recurring Expenses
                        </Link>
                    </ListItemButton>
                </ListItem>
            </List>
            <Divider />
            <List>
                <ListItem disablePadding>
                    <ListItemButton>
                        <ListItemIcon>
                            <AttachMoneyIcon />
                        </ListItemIcon>
                        <Link
                            to={"/incomes"}
                        >
                            Incomes
                        </Link>
                    </ListItemButton>
                </ListItem>
            </List>
            <List>
                <ListItem disablePadding>
                    <ListItemButton>
                        <ListItemIcon>
                            <RepeatIcon />
                        </ListItemIcon>
                        <Link
                            to={"/incomes/recurring"}
                        >
                            Recurring Incomes
                        </Link>
                    </ListItemButton>
                </ListItem>
            </List>
            <Divider />
            <List>
                <ListItem disablePadding>
                    <ListItemButton onClick={handleLogout}>
                        <ListItemIcon>
                            <ExitToAppIcon />
                        </ListItemIcon>
                        <ListItemText primary={"Logout"} />
                    </ListItemButton>
                </ListItem>
            </List>
        </div>
    );

    return (
        <>
            <Box sx={{ display: 'flex' }}>
                <CssBaseline />
                <AppBar
                    position="fixed"
                    sx={{
                        width: { sm: `calc(100% - ${drawerWidth}px)` },
                        ml: { sm: `${drawerWidth}px` },
                    }}
                >
                    <Toolbar>
                        <IconButton
                            color="inherit"
                            aria-label="open drawer"
                            edge="start"
                            onClick={handleDrawerToggle}
                            sx={{ mr: 2, display: { sm: 'none' } }}
                        >
                            <MenuIcon />
                        </IconButton>
                        <Typography variant="h6" noWrap component="div">
                            Administration Area
                        </Typography>
                    </Toolbar>
                </AppBar>
                <Box
                    component="nav"
                    sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
                    aria-label="mailbox folders"
                >
                    <Drawer
                        container={document.body}
                        variant="temporary"
                        open={mobileOpen}
                        onClose={handleDrawerToggle}
                        ModalProps={{
                            keepMounted: true,
                        }}
                        sx={{
                            display: { xs: 'block', sm: 'none' },
                            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
                        }}
                    >
                        {drawer}
                    </Drawer>
                    <Drawer
                        variant="permanent"
                        sx={{
                            display: { xs: 'none', sm: 'block' },
                            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
                        }}
                        open
                    >
                        {drawer}
                    </Drawer>
                </Box>

                <Box
                    component="main"
                    sx={{ flexGrow: 1, p: 3, width: { sm: `calc(100% - ${drawerWidth}px)` } }}
                >
                    <Toolbar />
                    <Routes>
                        <Route path="/" element={<ChartsScreen />} />
                        <Route path="/expenses" element={<ExpensesScreen />} />
                    </Routes>
                </Box>
            </Box>

            {/*<Paper elevation={3} className="paper-content">*/}
            {/*    sdfg*/}
            {/*    <Divider sx={{width: '50%', margin: '2rem auto'}}/>*/}
            {/*    /!*<ExpensesScreen />*!/*/}
            {/*</Paper>*/}
            {/*  Charts */}
            {/*  Expenses  */}
            {/*  Incomes  */}
        </>
    );
};

export default FinancesScreen;