# Finance Management App

This finance management app provides robust user authentication and allows users to track their incomes and expenses efficiently. Users can create and manage incomes and expenses, with the option to set them as recurring (weekly, monthly, or yearly). The app automatically creates these recurring entries at midnight on the specified date using cron jobs. Additionally, users can specify the currency for each income and expense, and convert amounts to any other currency. Currency exchange rates are updated daily at midnight.

## Features

- **User Authentication**: Secure login and registration for users.
- **Income and Expense Tracking**: Easily add and manage financial records.
- **Recurring Entries**: Set incomes and expenses to recur weekly, monthly, or yearly.
- **Currency Support**: Choose the currency for each entry and convert between currencies with up-to-date exchange rates.
- **Categories and Subcategories**: Organize expenses under main categories and custom subcategories.
- **Data Export**: Export data to a database, CSV file, or download as a chart.

## Examples

### Income

- **Attributes**: Source, description, amount, date, recurrence (optional), and recurrence period (optional).
- **Example**: Salary from a job, rental income, money received from a family member, etc.

### Expense

- **Attributes**: Description, amount, date, recurrence (optional), recurrence period (optional), subcategory, and main category.
- **Example**: A subscription to Netflix categorized under 'Subscription' with a user-created subcategory 'Netflix', set as recurring if desired.

## Categories

There are seven main categories available:

1. Subscription
2. Food
3. Utilities
4. Entertainment
5. Transportation
6. Healthcare
7. Other

Users can create custom subcategories within these main categories for better organization and tracking.

## Data Management

The app offers three ways to save and manage your data:

1. **Database Storage**: All data is stored securely in a database.
2. **CSV Export**: Generate and download CSV reports anytime.
3. **Charts**: Visualize your data with downloadable chart files.

This application ensures that users can efficiently manage their finances, keep track of recurring payments, and have a clear overview of their financial situation through detailed categorization and data export options.

[Video Presentation of the Project](https://youtu.be/Aeciz3ZQLu8)

## TODOs

1. Add more tests
