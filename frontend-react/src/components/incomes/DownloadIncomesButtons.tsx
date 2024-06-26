import React from 'react';
import {Button, Tooltip} from '@mui/material';
import {PDFDocument, rgb, StandardFonts} from 'pdf-lib';
import {IIncomesData} from "../../types/incomes/IIncomesData";

interface DownloadButtonsProps {
    incomes: IIncomesData[];
    year: number;
    month: number | null;
    currencyCode: string;
    monthlyTotal: number | null;
}

const DownloadIncomesButtons: React.FC<DownloadButtonsProps> = ({
                                                                     incomes,
                                                                     year,
                                                                     month,
                                                                     currencyCode,
                                                                     monthlyTotal
                                                                 }) => {
    const downloadCSV = (year: number) => {
        const headers = ['ID', 'Username', 'Amount', 'Source', 'Date', 'Recurring', 'Recurrence Period', 'Currency Code'];
        const csvRows = [
            headers.join(','), // header row first
            ...incomes.map(income => [
                income.id,
                income.username,
                income.amount,
                income.source,
                new Date(income.date).toLocaleDateString(),
                income.recurring ? 'Yes' : 'No',
                income.recurrencePeriod,
                currencyCode
            ].join(','))
        ];

        // Calculate monthly totals
        const monthlyTotals = Array(12).fill(0);
        incomes.forEach(expense => {
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
        link.setAttribute('download', `incomes_${year}.csv`);
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

        page.drawText(`Incomes for ${new Date(0, month! - 1).toLocaleString('default', { month: 'long' })} ${year}`, {
            x: margin,
            y: height - margin,
            size: fontSize * 2,
            font: boldFont,
            color: rgb(0, 0, 0),
        });

        let yPosition = height - margin - 4 * fontSize;

        incomes.forEach((income, index) => {
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

            const incomeData = [
                `Amount: ${income.amount} ${currencyCode}`,
                `Source: ${income.source}`,
                `Date: ${new Date(income.date).toLocaleDateString()}`,
                `Recurring: ${income.recurring ? 'Yes' : 'No'}`,
                `Recurrence Period: ${income.recurrencePeriod}`,
                `Description: ${trimTextToWidth(income.description, maxWidth, timesRomanFont, fontSize)}`
            ];

            incomeData.forEach((text, idx) => {
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
        link.download = 'monthly_incomes.pdf';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    return (
        <>
            <Tooltip title="Download yearly incomes as CSV">
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
                <Tooltip title="Download monthly incomes as PDF">
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

export default DownloadIncomesButtons;