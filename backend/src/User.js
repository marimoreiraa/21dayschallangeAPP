class User {

    #database
    #table = "USERS"

    constructor(database) {
        this.#database = database
    }

    async checkIfExists(email) {
        await this.#database.connect()
        let data = await this.#database.count(this.#table, `email = '${email}'`)
        return (data != 0)
    }

    async getById(id) {
        await this.#database.connect()
        return await this.#database.read(this.#table, '*', `id = '${id}'`)
    }

    async getByEmail(email) {
        await this.#database.connect()
        return await this.#database.read(this.#table, '*', `email = '${email}'`)
    }

    async getByEmailAndPassword(email, password) {
        await this.#database.connect()
        return await this.#database.read(this.#table, '*', `email = '${email}' AND password = ${password}`)
    }

    async create(data) {
        let payload = {
            email: data.email,
            password: data.password,
            username: data.username,
        }

        if (await this.#database.create(this.#table, Object.keys(payload), Object.values(payload)))
            return true

        return false
            
    }

}

module.exports = User