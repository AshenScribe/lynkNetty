# Auth Protocol Specification

All messages are terminated by a newline (`\n`).

## Login
**Format:** `LOGIN <USERNAME> <PASSWORD>`
- `TYPE`: Must be `BASIC` or `TOKEN`.
- Example: `LOGIN BASIC user123 secretPass`

username and password must be Base64 encoded

## Register
**Format:** `REGISTER <USERNAME> <PASSWORD>`
- Example: `REGISTER newuser mypassword`

username and password must be Base64 encoded