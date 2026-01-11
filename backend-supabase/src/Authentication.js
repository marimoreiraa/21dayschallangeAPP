const Database = require('./Database')
const User = require("./User")
const HttpResponse = require('./HttpResponse')

const jwt = require('jsonwebtoken')
const crypto = require('crypto');

class Account {

    #database
    #AUTH_SECRET = 'FFE900'

    constructor(database) {
        this.#database = database
    }

    async #validateRegistrationPayload(data) {
        return (data && data.email && data.username && data.password && data.recoveryAnswer)
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

    async recoverPassword(req, res) {
        const { email, recoveryAnswer, newPassword } = req.body;

        let user = await this.#database.read('users', 'id, recovery_answer', `email = '${email}'`);

        if (user && user.length > 0) {
            const hashNoBanco = user[0].recovery_answer.trim();
            const hashRecebido = recoveryAnswer.trim();

            if (hashNoBanco === hashRecebido) {
                await this.#database.update('users', 'password', newPassword, `id = ${user[0].id}`);
                return HttpResponse.sendMessage(res, 200, "Sucesso");
            }
        }
        console.log("Banco:", user[0].recovery_answer);
        console.log("Recebido:", recoveryAnswer);
        return HttpResponse.sendMessage(res, 400, "Dados inválidos.");
    }

    async getUserProfile(req, res) {
        let userId = await this.validate(req.header('Authorization'));

        if (!userId) {
            return HttpResponse.sendMessage(res, 401, "Unauthorized");
        }

        try {
            
            let userResult = await this.#database.read('users', 'username, email', `id = ${userId}`);

            if (!userResult || userResult.length === 0) {
                return HttpResponse.sendMessage(res, 404, "User not found");
            }

            const payload = {
                user: {
                    name: userResult[0].username,
                    email: userResult[0].email
                }
            };
            
            return HttpResponse.sendPayload(res, 200, payload);

        } catch (error) {
            console.error("Error fetching profile:", error);
            return HttpResponse.sendMessage(res, 500, "Internal server error");
        }
    }

    async changePassword(req, res) {
        let userId = await this.validate(req.header('Authorization'));
        if (!userId) return HttpResponse.sendMessage(res, 401, "Unauthorized");

        const { currentPassword, newPassword } = req.body;

        let userResult = await this.#database.read('users', 'password', `id = ${userId}`);
        const senhaHashNoBanco = userResult[0].password.trim();

        const senhaDigitadaHash = crypto.createHash('sha256').update(currentPassword).digest('hex');

        if (senhaHashNoBanco !== senhaDigitadaHash) {
            return HttpResponse.sendMessage(res, 400, "Senha atual incorreta");
        }

        const novaSenhaHash = crypto.createHash('sha256').update(newPassword).digest('hex');
        await this.#database.update('users', 'password', novaSenhaHash, `id = ${userId}`);
        
        return HttpResponse.sendMessage(res, 200, "Senha alterada com sucesso");
    }

    async deleteAccount(req, res) {
        let userId = await this.validate(req.header('Authorization'));
        if (!userId) return HttpResponse.sendMessage(res, 401, "Unauthorized");

        const success = await this.#database.delete('users', `id = ${userId}`);
        
        if (success) {
            return HttpResponse.sendMessage(res, 200, "Conta excluída com sucesso");
        } else {
            return HttpResponse.sendMessage(res, 500, "Erro ao excluir conta");
        }
    }

    
}

module.exports = Account