# System Architecture

## Core Components
1. **Account**: Represents a financial entity with a balance and unique identifier.
2. **TransactionManager**: Handles the logic for transferring funds and ensuring ACID properties.
3. **Database Layer**: Persists transaction history and account states via SQL.

## Data Flow
- User interacts via `Main.java`.
- `TransactionManager` validates the request.
- Changes are pushed to the SQL database.

