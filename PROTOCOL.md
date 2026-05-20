# Auth Protocol Specification

All messages are terminated by a newline (`\n`).

## Login
**Format:** `LOGIN <USERNAME> <PASSWORD>`
- `TYPE`: Must be `BASIC` or `TOKEN`.
- Example: `LOGIN BASIC user123 secretPass`

## Register
**Format:** `REGISTER <USERNAME> <PASSWORD>`
- Example: `REGISTER newuser mypassword`