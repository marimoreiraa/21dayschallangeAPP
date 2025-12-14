const Database = require('./Database')
const User = require("./User")
const HttpResponse = require('./HttpResponse')

const jwt = require('jsonwebtoken')

class Account {

    #database
    #AUTH_SECRET = 'FFE900'

    constructor(database) {
        this.#database = database
    }

    async #validateRegistrationPayload(data) {
        return (data && data.email && data.username && data.password)
    }

    async #validateLoginPayload(data) {
        return (data && data.email && data.password)
    }

    async validate(authorizationHeader) {
        if (!authorizationHeader || !authorizationHeader.startsWith('Bearer ')) {
            return false 
        }

        const token = authorizationHeader.substring(7) 

        try {
            const decoded = jwt.verify(token, this.#AUTH_SECRET) 
            return decoded.userId 
        } catch (error) {
            return false
        }
    }

    async register(req, res) {

        let data = req.body
        
        if (!await this.#validateRegistrationPayload(data))
            return HttpResponse.sendMessage(res, 400, "Invalid request body: username, email and password are mandatory.")
        
        let user = new User(this.#database)

        if (await user.checkIfExists(data.email))
            return HttpResponse.sendMessage(res, 409, "User already registered")

        if (await user.create(data))
            return HttpResponse.sendMessage(res, 200, "User registered sucessfully")

        HttpResponse.sendMessage(res, 500)
    }

    async login(req, res) {

        let data = req.body
        let user = new User(this.#database)

        if (!this.#validateLoginPayload(data))
            return HttpResponse.sendMessage(res, 400, "Incorrect login data")

        let result = await user.getByEmail(data.email)

        if (!result || result.length > 1)
            return HttpResponse.sendMessage(res, 500)

        if (result.length == 0)
            return HttpResponse.sendMessage(res, 401, "User not registered")

        result = result[0]

        if (result.password != data.password)
            return HttpResponse.sendMessage(res, 401, "Invalid credentials")

        user.updateLastLogin(result.id)

        const jwtToken = jwt.sign({ userId: result.id }, this.#AUTH_SECRET, { expiresIn: '1h' })

        return HttpResponse.sendPayload(res, 200, { success: true, token: jwtToken })
    }

    async refreshLogin(req, res) {

    }

    async resetPassword(req, res){

    }

    
}

module.exports = Account