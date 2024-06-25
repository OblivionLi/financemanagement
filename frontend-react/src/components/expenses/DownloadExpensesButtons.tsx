import React from 'react';
import {Button, Tooltip} from '@mui/material';
import IExpensesData from "../../types/expenses/IExpensesData";
import {PDFDocument, rgb, StandardFonts} from 'pdf-lib';

interface DownloadButtonsProps {
    expenses: IExpensesData[];
    year: number;
    month: number | null;
    currencyCode: string;
    monthlyTotal: number | null;
}

const DownloadExpensesButtons: React.FC<DownloadButtonsProps> = ({
                                                                     expenses,
                                                                     year,
                                                                     month,
                                                                     currencyCode,
                                                                     monthlyTotal
                                                                 }) => {
    const downloadCSV = (year: number) => {
        const headers = ['ID', 'Username', 'Amount', 'Category', 'SubCategory', 'Date', 'Recurring', 'Recurrence Period', 'Currency Code'];
        const csvRows = [
            headers.join(','), // header row first
            ...expenses.map(expense => [
                expense.id,
                expense.username,
                expense.amount,
                expense.category,
                expense.subCategory,
                new Date(expense.date).toLocaleDateString(),
                expense.recurring ? 'Yes' : 'No',
                expense.recurrencePeriod,
                currencyCode
            ].join(','))
        ];

        // Calculate monthly totals
        const monthlyTotals = Array(12).fill(0);
        expenses.forEach(expense => {
            const month = new Date(expense.date).getMonth(); // 0-11
            monthlyTotals[month] += parseFloat(expense.amount.toString());
        });

        // Add monthly totals to CSV
        csvRows.push('\nMonthly Totals:');
        monthlyTotals.forEach((total, index) => {
            csvRows.push(`${new Date(0, index).toLocaleString('default', {month: 'long'})}: ${total.toFixed(2)} ${currencyCode}`);
        });

        // Add yearly total to CSV
        const yearlyTotal = monthlyTotals.reduce((acc, curr) => acc + curr, 0);
        csvRows.push(`\nYearly Total: ${yearlyTotal.toFixed(2)} ${currencyCode}`);

        const csvContent = "data:text/csv;charset=utf-8," + csvRows.join('\n');
        const encodedUri = encodeURI(csvContent);
        const link = document.createElement('a');
        link.setAttribute('href', encodedUri);
        link.setAttribute('download', `expenses_${year}.csv`);
        document.body.appendChild(link); // Required for FF
        link.click();
        document.body.removeChild(link);
    };

    const trimTextToWidth = (text: string, maxWidth: number, font: any, size: number) => {
        let trimmedText = text;
        while (font.widthOfTextAtSize(trimmedText, size) > maxWidth) {
            trimmedText = trimmedText.slice(0, -1);
        }
        return trimmedText + '...';
    };

    const downloadPDF = async () => {
        const pdfDoc = await PDFDocument.create();
        const timesRomanFont = await pdfDoc.embedFont(StandardFonts.TimesRoman);
        const boldFont = await pdfDoc.embedFont(StandardFonts.TimesRomanBold);

        let page = pdfDoc.addPage([600, 800]);
        const { width, height } = page.getSize();
        const fontSize = 12;
        const margin = 50;
        const padding = 10;
        const lineHeight = fontSize + 2;
        const maxWidth = width - 2 * margin;

        page.drawText(`Expenses for ${new Date(0, month! - 1).toLocaleString('default', { month: 'long' })} ${year}`, {
            x: margin,
            y: height - margin,
            size: fontSize * 2,
            font: boldFont,
            color: rgb(0, 0, 0),
        });

        let yPosition = height - margin - 4 * fontSize;

        expenses.forEach((expense, index) => {
            const boxHeight = 8 * lineHeight;
            if (yPosition < margin + boxHeight) {
                page = pdfDoc.addPage([600, 800]);
                yPosition = height - margin - 4 * fontSize;
            }

            page.drawRectangle({
                x: margin - padding,
                y: yPosition - boxHeight - padding,
                width: width - 2 * margin + 2 * padding,
                height: boxHeight + 2 * padding,
                borderColor: rgb(0, 0, 0),
                borderWidth: 1,
            });

            const expenseData = [
                `ID: ${expense.id}`,
                `Amount: ${expense.amount} ${currencyCode}`,
                `Category: ${expense.category}`,
                `SubCategory: ${expense.subCategory}`,
                `Date: ${new Date(expense.date).toLocaleDateString()}`,
                `Recurring: ${expense.recurring ? 'Yes' : 'No'}`,
                `Recurrence Period: ${expense.recurrencePeriod}`,
                `Description: ${trimTextToWidth(expense.description, maxWidth, timesRomanFont, fontSize)}`
            ];

            expenseData.forEach((text, idx) => {
                page.drawText(text, {
                    x: margin,
                    y: yPosition - lineHeight * (idx + 1),
                    size: fontSize,
                    font: timesRomanFont,
                    color: rgb(0, 0, 0),
                });
            });

            yPosition -= boxHeight + 2 * padding;
        });

        yPosition -= 2 * lineHeight;
        page.drawText(`Total Amount: ${monthlyTotal?.toFixed(2) || 0} ${currencyCode}`, {
            x: margin,
            y: yPosition,
            size: fontSize,
            font: boldFont,
            color: rgb(0, 0, 0),
        });

        const pdfBytes = await pdfDoc.save();
        const blob = new Blob([pdfBytes], { type: 'application/pdf' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = 'monthly_expenses.pdf';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    return (
        <>
            <Tooltip title="Download yearly expenses as CSV">
                <Button
                    variant="contained"
                    color="success"
                    onClick={() => downloadCSV(year)}
                    size="small"
                >
                    Download Yearly CSV
                </Button>
            </Tooltip>
            {month !== null && (
                <Tooltip title="Download monthly expenses as PDF">
                    <Button
                        variant="contained"
                        color="info"
                        onClick={downloadPDF}
                        size="small"
                        sx={{ml: 2}}
                    >
                        Download Monthly PDF
                    </Button>
                </Tooltip>
            )}
        </>
    );
};

export default DownloadExpensesButtons;