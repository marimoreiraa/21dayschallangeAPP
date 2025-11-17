const mysql = require('mysql2/promise')

class Database {

    #connection
    #config

    constructor() {
        this.#config = {
            host: process.env.DB_HOST,
            user: process.env.DB_USER,
            password: process.env.DB_PASS,
            database: process.env.DB_NAME
        }
    }

    async connect() {
        if (!this.#connection)
            this.#connection = await mysql.createConnection(this.#config)
    }

    async disconnect() {
        await this.#connection.end()
    }

    async create(table, columns, values) {
        await this.connect()

        let cols = Object.values(columns).join(", ")
        let vals = Object.values(values).map((value) => `'${value}'`).join(", ")

        let query = `INSERT INTO ${table} (${cols}) VALUES (${vals})`
        let results = await this.#connection.query(query)
        console.log(results)
        return true
    }

    async read(table, columns, conditions) {
        await this.connect()
        let query = `SELECT ${columns} FROM ${table} ${(conditions) ? "WHERE " + conditions : ""}`
        let [results] = await this.#connection.query(query)
        return results
    }

    async update(table, columns, values, where) {

    }

    async delete(from, where) {

    }

    async count(table, conditions){

        console.log(`conditions = ${conditions}`)

        await this.connect()
        let [result] = await this.#connection.query(`SELECT count(1) AS count from ${table} ${(conditions) ? "WHERE " + conditions : ""}`)
        return result[0].count
    }
}

module.exports = Database