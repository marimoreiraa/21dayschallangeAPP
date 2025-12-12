const Authentication = require('./Authentication.js')
const Database = require('./Database')
const HttpResponse = require('./HttpResponse')
const User = require("./User")

class Challenges {

    #database
    #table_suggested = "suggested_challenges"
    #table_user = "user_challenges"
    #table_daily_check = "challenge_daily_check"
    #table_progress = "challenge_progress_history"
    #table_notifications = "notifications"
    constructor(database) {
        this.#database = database
    }

    async getSuggested(req, res) {
        let conditions = []
        for (let item in req.body)
            conditions.push(`${item} = '${req.body[item]}'`)

        let challenges = await this.#database.read(this.#table_suggested, "*", (conditions.length != 0) ? conditions : null)
        return HttpResponse.sendPayload(res, 200, JSON.stringify(challenges))
    }

    async getUserChallenges(req, res) {
        let conditions = []
        for (let item in req.body)
            conditions.push(`${item} = '${req.body[item]}'`)

        let challenges = await this.#database.read(this.#table_user, "*", (conditions.length != 0) ? conditions : null)
        return HttpResponse.sendPayload(res, 200, JSON.stringify(challenges))
    }

    async getDailyCheck(req, res) {
        let conditions = []
        for (let item in req.body)
            conditions.push(`${item} = '${req.body[item]}'`)

        let challenges = await this.#database.read(this.#table_daily_check, "*", (conditions.length != 0) ? conditions : null)
        return HttpResponse.sendPayload(res, 200, JSON.stringify(challenges))
    }

    async getProgressHistory(req, res) {
        let conditions = []
        for (let item in req.body)
            conditions.push(`${item} = '${req.body[item]}'`)

        let challenges = await this.#database.read(this.#table_progress, "*", (conditions.length != 0) ? conditions : null)
        return HttpResponse.sendPayload(res, 200, JSON.stringify(challenges))
    }

    async getNotifications(req, res) {
        let conditions = []
        for (let item in req.body)
            conditions.push(`${item} = '${req.body[item]}'`)

        let challenges = await this.#database.read(this.#table_notifications, "*", (conditions.length != 0) ? conditions : null)
        return HttpResponse.sendPayload(res, 200, JSON.stringify(challenges))
    }
}

module.exports = Challenges