const { Pool } = require('pg')
class Database {

    #pool
    #config

    constructor() {
        this.#config = {
            host: process.env.DB_HOST,
            user: process.env.DB_USER,
            password: process.env.DB_PASS,
            database: process.env.DB_NAME,
            
            port: process.env.DB_PORT || 5432, 
            ssl: {
                rejectUnauthorized: false 
            }
        }
    }

    async connect() {
        if (!this.#pool)
            this.#pool = new Pool(this.#config)
    }

    async disconnect() {
        if (this.#pool) {
            await this.#pool.end()
            this.#pool = null
        }
    }

    async query(queryString, values) {
        console.log(queryString)
        await this.connect()
        return this.#pool.query(queryString,values)
    }

    async create(table, columns, values) {
        let cols = Object.values(columns).join(", ");
        
        let vals = Object.values(values).map((value) => {
            if (value === true) return 'TRUE';
            if (value === false) return 'FALSE';

            return `'${value}'`;
        }).join(", ");
        
        let query = `INSERT INTO ${table.toLowerCase()} (${cols}) VALUES (${vals}) RETURNING id`
        let results = await this.query(query) 

        console.log(results)
        return true
    }

    async read(table, columns, conditions) {
        let query = `SELECT ${columns} FROM ${table.toLowerCase()} ${(conditions) ? "WHERE " + conditions : ""}`        
        let result = await this.query(query) 

        return result.rows
    }

    async update(table, columns, values, where) {
        let val;
        
        if (values === true) {
            val = 'TRUE';
        } else if (values === false) {
            val = 'FALSE';
        } else if (!isNaN(values) && typeof values !== 'string') {
            val = values;
        } else {
            val = `'${values}'`; 
        }
        
        let query = `UPDATE ${table.toLowerCase()} SET "${columns}"=${val} WHERE ${where}`
        
        console.log(`UPDATE query: ${query}`); 
        
        let result = await this.query(query) 
        
        return result.rows
    }

    async delete(from, where) {
        let query = `DELETE FROM ${from.toLowerCase()} WHERE ${where}`
        
        console.log(`DELETE query: ${query}`); 
        
        let result = await this.query(query) 
        
        return result.rowCount > 0; 
    }

    async count(table, conditions) {
        console.log(`conditions = ${conditions}`)
        let result = await this.query(`SELECT count(1) AS count from ${table.toLowerCase()} ${(conditions) ? "WHERE " + conditions : ""}`)

        return result.rows[0].count
    }
}

module.exports = Database