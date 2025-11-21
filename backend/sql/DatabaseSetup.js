const mysql = require('mysql2/promise')
const fs = require('fs')
const path = require('path')

async function main() {
    try {
        console.log("Running 21DaysApp database setup...")
        let config = {
            host: process.env.DB_HOST,
            user: process.env.DB_USER,
            password: process.env.DB_PASS,
            multipleStatements: true
        }
        connection = await mysql.createConnection(config)

        /* Create Database */
        console.log("creating database...")
        await connection.query(fs.readFileSync(path.join(__dirname, 'create_database.sql'), 'utf8'))
        await connection.query('USE 21daysapp')

        // console.log("creating challenges table (CHALLENGES)...")
        // await connection.query(fs.readFileSync(path.join(__dirname, 'create_table_challenges.sql'), 'utf8'))
        // console.log("creating password reset table (PASSWORD_RESET)...")
        // await connection.query(fs.readFileSync(path.join(__dirname, 'create_table_password_reset.sql'), 'utf8'))
        // console.log("creating users table (USERS)...")
        // await connection.query(fs.readFileSync(path.join(__dirname, 'create_table_users.sql'), 'utf8'))

        console.log("creating tables...")
        await connection.query(fs.readFileSync(path.join(__dirname, 'tables.sql'), 'utf8'))

        await connection.end()
    }
    catch (err) {
        console.log(err)
    }
}

main().then(() => {
    console.log("Finished!")
    process.exit(0)
}).catch((err) => {
    console.error(err)
    process.exit(1)
})
