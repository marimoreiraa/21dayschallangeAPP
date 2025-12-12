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
