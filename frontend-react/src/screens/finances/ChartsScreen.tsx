import React, {ReactNode, useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import LocalStorageService from "../../services/LocalStorageService";
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
    BarChart,
    Bar, PieChart, Pie, Cell
} from 'recharts';
import {YearlyFinancialSummary} from "../../types/stats/YearlyFinancialSummary";
import StatsService from "../../services/StatsService";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import {Card, CardContent, Divider, FormControl, Grid, InputLabel, Select, SelectChangeEvent} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import {MonthlyFinancialSummary} from "../../types/stats/MonthlyFinancialSummary";
import {CategoryBreakdownSummary} from "../../types/stats/CategoryBreakdownSummary";
import {ComparisonSummary} from "../../types/stats/ComparisonSummary";
import {SavingsRateSummary} from "../../types/stats/SavingsRateSummary";
import {GrandTotalsSummary} from "../../types/stats/GrandTotalsSummary";

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#FF4567', '#0C0FDF', '#D3D3D3'];

const ChartsScreen = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);

    const [yearlySummary, setYearlySummary] = useState<YearlyFinancialSummary | null>(null);
    const [monthlySummary, setMonthlySummary] = useState<MonthlyFinancialSummary | null>(null);
    const [categoryBreakdown, setCategoryBreakdown] = useState<CategoryBreakdownSummary | null>(null);
    const [comparisonData, setComparisonData] = useState<ComparisonSummary | null>(null);
    const [savingsRateData, setSavingsRateData] = useState<SavingsRateSummary | null>(null);
    const [grandTotals, setGrandTotals] = useState<GrandTotalsSummary | null>(null);

    const [year, setYear] = useState(new Date().getFullYear());
    const [month, setMonth] = useState(new Date().getMonth() + 1);

    const [availableYears, setAvailableYears] = useState<number[]>([]);
    const [currencyCode, setCurrencyCode] = useState('');

    const isUserLogged = LocalStorageService.isUserLogged();

    useEffect(() => {
        if (!isUserLogged) {
            navigate("/login");
            return;
        }

        setLoading(false);

        fetchYearlyFinancialSummary();
        fetchMonthlyFinancialSummary();
        fetchCategoryBreakdownSummary();
        fetchComparisonSummary();
        fetchSavingsRateSummary();
        fetchGrandTotals();

        const userPreferredCurrency = LocalStorageService.getCurrencyCodeFromLocalStorage();
        setCurrencyCode(userPreferredCurrency);
    }, [navigate, isUserLogged, year, month, currencyCode]);

    const fetchYearlyFinancialSummary = () => {
        StatsService.getStatsByYear(year)
            .then((response: any) => {
                setYearlySummary(response.data)
                const years = [];
                for (let y = response.data.minYear; y <= response.data.maxYear; y++) {
                    years.push(y);
                }
                setAvailableYears(years);
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
                setLoading(false);
            });
    };

    const fetchMonthlyFinancialSummary = () => {
        StatsService.getStatsByYearAndMonth(year, month)
            .then((response: any) => {
                setMonthlySummary(response.data);
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
                setLoading(false);
            });
    };

    const fetchCategoryBreakdownSummary = () => {
        StatsService.getCategoryStatsByYear(year)
            .then((response: any) => {
                setCategoryBreakdown(response.data);
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
                setLoading(false);
            });
    };

    const fetchComparisonSummary = () => {
        StatsService.getComparisonData(year, month)
            .then((response: any) => {
                setComparisonData(response.data);
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
                setLoading(false);
            });
    };

    const fetchSavingsRateSummary = () => {
        StatsService.getSavingsRateDate(year)
            .then((response: any) => {
                setSavingsRateData(response.data);
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
                setLoading(false);
            });
    };

    const fetchGrandTotals = () => {
        StatsService.getGrandTotals()
            .then((response: any) => {
                setGrandTotals(response.data);
                setLoading(false);
            })
            .catch((e: Error) => {
                console.log(e);
                setLoading(false);
            });
    };

    const handleYearChange = (event: SelectChangeEvent<number>, child: ReactNode) => {
        setYear(event.target.value as number);
    };

    const handleMonthChange = (event: SelectChangeEvent<number>, child: ReactNode) => {
        setMonth(event.target.value as number);
    };

    if (loading) {
        return <Typography>Loading...</Typography>;
    }

    if (!yearlySummary || !monthlySummary || !categoryBreakdown || !comparisonData || !savingsRateData || !grandTotals) {
        return <Typography>No data available</Typography>;
    }

    const combinedYearlyData = Object.keys(yearlySummary.monthlyExpenses).map((key) => {
        const month = parseInt(key, 10);
        return {
            month: `Month ${month}`,
            expenses: yearlySummary.monthlyExpenses[month] || 0,
            incomes: yearlySummary.monthlyIncomes[month] || 0,
            expenseTransactions: yearlySummary.monthlyExpenseTransactions[month] || 0,
            incomeTransactions: yearlySummary.monthlyIncomeTransactions[month] || 0,
        };
    });

    const combinedMonthlyData = Object.keys(monthlySummary.dailyExpenses).map((key) => {
        const day = parseInt(key, 10);
        return {
            day: `Day ${day}`,
            expenses: monthlySummary.dailyExpenses[day] || 0,
            incomes: monthlySummary.dailyIncomes[day] || 0,
            expenseTransactions: monthlySummary.dailyExpenseTransactions[day] || 0,
            incomeTransactions: monthlySummary.dailyIncomeTransactions[day] || 0,
        };
    });

    const expenseData = Object.keys(categoryBreakdown.expensesByCategory).map((key) => ({
        name: key,
        value: categoryBreakdown.expensesByCategory[key],
    }));

    const incomeData = Object.keys(categoryBreakdown.incomesBySource).map((key) => ({
        name: key,
        value: categoryBreakdown.incomesBySource[key],
    }));

    const monthComparisonData = [
        {
            name: 'Current Month',
            expenses: comparisonData.currentMonthExpenses,
            incomes: comparisonData.currentMonthIncomes
        },
        {
            name: 'Previous Month',
            expenses: comparisonData.previousMonthExpenses,
            incomes: comparisonData.previousMonthIncomes
        }
    ];

    const yearComparisonData = [
        {
            name: 'Current Year',
            expenses: comparisonData.currentYearExpenses,
            incomes: comparisonData.currentYearIncomes
        },
        {
            name: 'Previous Year',
            expenses: comparisonData.previousYearExpenses,
            incomes: comparisonData.previousYearIncomes
        }
    ];

    const savingsRateDataForChart = Object.keys(savingsRateData.monthlySavingsRate).map((key) => {
        const month = parseInt(key, 10);
        return {
            month: `Month ${month}`,
            savingsRate: savingsRateData.monthlySavingsRate[month] || 0,
        };
    });

    const pieData = [
        {name: 'Total Incomes', value: grandTotals.totalIncomes},
        {name: 'Total Expenses', value: grandTotals.totalExpenses}
    ];

    if (loading) {
        return null;
    }

    return (
        <Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h4" gutterBottom>
                    Financial Dashboard
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
            <Box display="flex" flexDirection="column" justifyContent="flex-end" alignItems="flex-start" mb={2} gap={2}
                 flexWrap="wrap">

                <Box display="flex" flexDirection="row" flexWrap="wrap" alignItems="center" gap={2}>
                    <FormControl variant="outlined" style={{minWidth: 120}}>
                        <InputLabel id="year-select-label">Year</InputLabel>
                        <Select
                            labelId="year-select-label"
                            value={year}
                            onChange={handleYearChange}
                            label="Year"
                        >
                            {availableYears.map((yearOption) => (
                                <MenuItem key={yearOption} value={yearOption}>
                                    {yearOption}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl variant="outlined" style={{minWidth: 120}}>
                        <InputLabel id="month-select-label">Month</InputLabel>
                        <Select
                            labelId="month-select-label"
                            value={month}
                            onChange={handleMonthChange}
                            label="Month"
                        >
                            {Array.from({length: 12}, (_, i) => i + 1).map((monthOption) => (
                                <MenuItem key={monthOption} value={monthOption}>
                                    {monthOption}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Box>
            </Box>

            <Divider sx={{my: 3}}/>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Box>
                        <Card>
                            <CardContent>
                                <Typography variant="h4" component="div">
                                    {grandTotals.netBalance}
                                </Typography>
                                <Typography color="textSecondary">
                                    Net Balance
                                </Typography>
                            </CardContent>
                        </Card>
                    </Box>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Total Expenses and Incomes</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <PieChart>
                                <Pie
                                    data={pieData}
                                    dataKey="value"
                                    nameKey="name"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={150}
                                    fill="#8884d8"
                                    label
                                >
                                    {pieData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]}/>
                                    ))}
                                </Pie>
                                <Tooltip/>
                            </PieChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>
            </Grid>

            <Divider sx={{my: 3}}/>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Total Expenses and Incomes by Month</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <LineChart
                                data={combinedYearlyData}
                                margin={{
                                    top: 5,
                                    right: 30,
                                    left: 20,
                                    bottom: 5,
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3"/>
                                <XAxis dataKey="month"/>
                                <YAxis/>
                                <Tooltip/>
                                <Legend/>
                                <Line type="monotone" dataKey="expenses" stroke="#8884d8" name="Expenses"/>
                                <Line type="monotone" dataKey="incomes" stroke="#82ca9d" name="Incomes"/>
                            </LineChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Number of Transactions by Month</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <LineChart
                                data={combinedYearlyData}
                                margin={{
                                    top: 5,
                                    right: 30,
                                    left: 20,
                                    bottom: 5,
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3"/>
                                <XAxis dataKey="month"/>
                                <YAxis/>
                                <Tooltip/>
                                <Legend/>
                                <Line type="monotone" dataKey="expenseTransactions" stroke="#8884d8"
                                      name="Expense Transactions"/>
                                <Line type="monotone" dataKey="incomeTransactions" stroke="#82ca9d"
                                      name="Income Transactions"/>
                            </LineChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>
            </Grid>

            <Divider sx={{my: 3}}/>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Daily Expenses and Incomes for Selected Month</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <LineChart
                                data={combinedMonthlyData}
                                margin={{
                                    top: 5,
                                    right: 30,
                                    left: 20,
                                    bottom: 5,
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3"/>
                                <XAxis dataKey="day"/>
                                <YAxis/>
                                <Tooltip/>
                                <Legend/>
                                <Line type="monotone" dataKey="expenses" stroke="#8884d8" name="Expenses"/>
                                <Line type="monotone" dataKey="incomes" stroke="#82ca9d" name="Incomes"/>
                            </LineChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Number of Daily Transactions for Selected Month</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <BarChart
                                data={combinedMonthlyData}
                                margin={{
                                    top: 5,
                                    right: 30,
                                    left: 20,
                                    bottom: 5,
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3"/>
                                <XAxis dataKey="day"/>
                                <YAxis/>
                                <Tooltip/>
                                <Legend/>
                                <Bar dataKey="expenseTransactions" fill="#8884d8" name="Expense Transactions"/>
                                <Bar dataKey="incomeTransactions" fill="#82ca9d" name="Income Transactions"/>
                            </BarChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>
            </Grid>

            <Divider sx={{my: 3}}/>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Expenses by Category</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <PieChart>
                                <Pie
                                    data={expenseData}
                                    dataKey="value"
                                    nameKey="name"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={150}
                                    fill="#8884d8"
                                    label
                                >
                                    {expenseData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]}/>
                                    ))}
                                </Pie>
                                <Tooltip/>
                            </PieChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Incomes by Source</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <PieChart>
                                <Pie
                                    data={incomeData}
                                    dataKey="value"
                                    nameKey="name"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={150}
                                    fill="#82ca9d"
                                    label
                                >
                                    {incomeData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]}/>
                                    ))}
                                </Pie>
                                <Tooltip/>
                            </PieChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>
            </Grid>
            <Divider sx={{my: 3}}/>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Month-over-Month Comparison</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <BarChart
                                data={monthComparisonData}
                                margin={{
                                    top: 5,
                                    right: 30,
                                    left: 20,
                                    bottom: 5,
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3"/>
                                <XAxis dataKey="name"/>
                                <YAxis/>
                                <Tooltip/>
                                <Legend/>
                                <Bar dataKey="expenses" fill="#8884d8" name="Expenses"/>
                                <Bar dataKey="incomes" fill="#82ca9d" name="Incomes"/>
                            </BarChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Box>
                        <Typography variant="h6">Year-over-Year Comparison</Typography>
                        <ResponsiveContainer width="100%" height={400}>
                            <BarChart
                                data={yearComparisonData}
                                margin={{
                                    top: 5,
                                    right: 30,
                                    left: 20,
                                    bottom: 5,
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3"/>
                                <XAxis dataKey="name"/>
                                <YAxis/>
                                <Tooltip/>
                                <Legend/>
                                <Bar dataKey="expenses" fill="#8884d8" name="Expenses"/>
                                <Bar dataKey="incomes" fill="#82ca9d" name="Incomes"/>
                            </BarChart>
                        </ResponsiveContainer>
                    </Box>
                </Grid>
            </Grid>
            <Divider sx={{my: 3}}/>
            <Grid item xs={12} md={6}>
                <Box>
                    <Typography variant="h6">Monthly Savings Rate</Typography>
                    <ResponsiveContainer width="100%" height={400}>
                        <BarChart
                            data={savingsRateDataForChart}
                            margin={{
                                top: 5,
                                right: 30,
                                left: 20,
                                bottom: 5,
                            }}
                        >
                            <CartesianGrid strokeDasharray="3 3"/>
                            <XAxis dataKey="month"/>
                            <YAxis/>
                            <Tooltip/>
                            <Legend/>
                            <Bar dataKey="savingsRate" fill="#82ca9d" name="Savings Rate (%)"/>
                        </BarChart>
                    </ResponsiveContainer>
                </Box>
            </Grid>
        </Box>
    );
};

export default ChartsScreen;