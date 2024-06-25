import React from 'react';
import { ExpanderComponentProps } from "react-data-table-component";
import { Box, Paper, Typography } from "@mui/material";
import {IIncomesData} from "../types/incomes/IIncomesData";

const ExpandedIncomesDetails: React.FC<ExpanderComponentProps<IIncomesData>> = ({ data }) => {
    return (
        <Paper elevation={3}
               sx={{ padding: 3, marginTop: 3, marginBottom: 3, width: '85%', marginLeft: 'auto', marginRight: 'auto' }}>
            <Box sx={{ margin: 2 }}>
                <Typography variant="h6" gutterBottom>Description: </Typography>
                <Typography variant="body2" gutterBottom>{data?.description}</Typography>
            </Box>
        </Paper>
    );
};

export default ExpandedIncomesDetails;
