const { Client } = require('pg')
const fs = require('fs')
const path = require('path')

async function main() {
    try {
        console.log("Running 21DaysApp database setup...")
        let config = { 
            host: process.env.DB_HOST,
            user: process.env.DB_USER,
            password: process.env.DB_PASS,
            port: process.env.DB_PORT || 5432, 
            ssl: { rejectUnauthorized: false }
        }
        
        connection = new Client(config)
        await connection.connect()

        /* Create Database */
        const tablesSql = fs.readFileSync(path.join(__dirname, 'tables.sql'), 'utf8')
        await connection.query(tablesSql)

        console.log("populating database...")
        const populateSql = fs.readFileSync(path.join(__dirname, 'populate.sql'), 'utf8')
        await connection.query(populateSql) // Executa os comandos INSERT

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
