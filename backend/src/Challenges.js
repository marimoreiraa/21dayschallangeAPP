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

    async updateUserChallenge(req, res) {
        if (!req.user || !req.user.id) {
            return HttpResponse.sendError(res, 401, "Unauthorized")
        }

        const challengeId = req.params.id
        const userId = req.user.id

        const { title, description, category, duration_days, frequency, start_date, end_date, notifications_enabled, status } = req.body

        const updateFields = {}
        if (title !== undefined) updateFields.title = title
        if (description !== undefined) updateFields.description = description
        if (category !== undefined) updateFields.category = category
        if (duration_days !== undefined) updateFields.duration_days = duration_days
        if (frequency !== undefined) updateFields.frequency = frequency
        if (start_date !== undefined) updateFields.start_date = start_date
        if (end_date !== undefined) updateFields.end_date = end_date
        if (notifications_enabled !== undefined) updateFields.notifications_enabled = notifications_enabled
        if (status !== undefined) updateFields.status = status

        if (Object.keys(updateFields).length === 0) {
            return HttpResponse.sendError(res, 400, "No fields provided for update.")
        }

        try {
            const existingChallenge = await this.#database.read(this.#table_user, "id, user_id", [`id = '${challengeId}'`, `user_id = '${userId}'`])

            if (existingChallenge.length === 0) {
                return HttpResponse.sendError(res, 404, "Challenge not found or not owned by user.")
            }

            const result = await this.#database.update(
                this.#table_user,
                updateFields,
                `id = '${challengeId}' AND user_id = '${userId}'`
            )

            if (result && result.affectedRows > 0) {
                return HttpResponse.sendPayload(res, 200, "Challenge updated successfully.")
            } else {
                return HttpResponse.sendError(res, 500, "Failed to update challenge or no changes made.")
            }
        } catch (error) {
            console.error("Error updating user challenge:", error)
            return HttpResponse.sendError(res, 500, "Internal server error.")
        }
    }

    async deleteUserChallenge(req, res) {
        if (!req.user || !req.user.id) {
            return HttpResponse.sendError(res, 401, "Unauthorized")
        }

        const challengeId = req.params.id
        const userId = req.user.id

        try {
            // First, check if the user owns the challenge they are trying to delete
            const existingChallenge = await this.#database.read(this.#table_user, "id, user_id", [`id = '${challengeId}'`, `user_id = '${userId}'`])

            if (existingChallenge.length === 0) {
                return HttpResponse.sendError(res, 404, "Challenge not found or not owned by user.")
            }

            const result = await this.#database.delete(
                this.#table_user,
                `id = '${challengeId}' AND user_id = '${userId}'`
            )

            if (result && result.affectedRows > 0) {
                return HttpResponse.sendPayload(res, 200, "Challenge deleted successfully.")
            } else {
                return HttpResponse.sendError(res, 500, "Failed to delete challenge or no changes made.")
            }
        } catch (error) {
            console.error("Error deleting user challenge:", error)
            return HttpResponse.sendError(res, 500, "Internal server error.")
        }
    }

    async markDailyChallengeCompleted(req, res) {
        if (!req.user || !req.user.id) {
            return HttpResponse.sendError(res, 401, "Unauthorized")
        }

        const challengeId = req.params.id
        const userId = req.user.id
        const currentDate = req.body.date || new Date().toISOString().slice(0, 10) // YYYY-MM-DD

        try {
            // Verify ownership of the user_challenge
            const userChallenge = await this.#database.read(this.#table_user, "id", [`id = '${challengeId}'`, `user_id = '${userId}'`])
            if (userChallenge.length === 0) {
                return HttpResponse.sendError(res, 404, "Challenge not found or not owned by user.")
            }

            // Check if an entry for today already exists
            const existingCheck = await this.#database.read(this.#table_daily_check, "id", [`user_challenge_id = '${challengeId}'`, `date = '${currentDate}'`])

            let result
            if (existingCheck.length > 0) {
                // Update existing entry
                result = await this.#database.update(
                    this.#table_daily_check,
                    { completed: true },
                    `user_challenge_id = '${challengeId}' AND date = '${currentDate}'`
                )
            } else {
                // Create new entry
                result = await this.#database.create(
                    this.#table_daily_check,
                    ['user_challenge_id', 'date', 'completed'],
                    [challengeId, currentDate, true]
                )
            }

            if (result) { // result for create is true, for update it's affectedRows
                return HttpResponse.sendPayload(res, 200, "Daily challenge status updated successfully.")
            } else {
                return HttpResponse.sendError(res, 500, "Failed to update daily challenge status.")
            }
        } catch (error) {
            console.error("Error marking daily challenge completed:", error)
            return HttpResponse.sendError(res, 500, "Internal server error.")
        }
    }

    async getUserDailyStatus(req, res) {
        if (!req.user || !req.user.id) {
            return HttpResponse.sendError(res, 401, "Unauthorized")
        }

        const userId = req.user.id
        const currentDate = new Date().toISOString().slice(0, 10) // YYYY-MM-DD

        try {
            // Get all active user challenges for the authenticated user
            let activeChallenges = await this.#database.read(this.#table_user, "id, title, description, category, frequency", [`user_id = '${userId}'`, `status = 'active'`])

            if (activeChallenges.length === 0) {
                return HttpResponse.sendPayload(res, 200, JSON.stringify([])) // No active challenges
            }

            // For each active challenge, get its daily check status for today
            for (let i = 0; i < activeChallenges.length; i++) {
                const challenge = activeChallenges[i]
                const dailyCheck = await this.#database.read(this.#table_daily_check, "completed", [`user_challenge_id = '${challenge.id}'`, `date = '${currentDate}'`])

                challenge.completed_today = dailyCheck.length > 0 ? dailyCheck[0].completed : false
            }

            return HttpResponse.sendPayload(res, 200, JSON.stringify(activeChallenges))
        } catch (error) {
            return HttpResponse.sendError(res, 500, "Internal server error.")
        }
    }

    async recordChallengeProgress(req, res) {
        if (!req.user || !req.user.id) {
            return HttpResponse.sendError(res, 401, "Unauthorized")
        }

        const challengeId = req.params.id
        const userId = req.user.id
        const date = req.body.date || new Date().toISOString().slice(0, 10) // YYYY-MM-DD
        const { value, note } = req.body

        if (value === undefined || value === null) {
            return HttpResponse.sendError(res, 400, "Progress value is required.")
        }

        try {
            // Verify ownership of the user_challenge
            const userChallenge = await this.#database.read(this.#table_user, "id", [`id = '${challengeId}'`, `user_id = '${userId}'`])
            if (userChallenge.length === 0) {
                return HttpResponse.sendError(res, 404, "Challenge not found or not owned by user.")
            }

            const columns = ['user_challenge_id', 'date', 'value']
            const values = [challengeId, date, value]

            if (note !== undefined) {
                columns.push('note')
                values.push(note)
            }

            const result = await this.#database.create(
                this.#table_progress,
                columns,
                values
            )

            if (result) {
                return HttpResponse.sendPayload(res, 201, "Challenge progress recorded successfully.")
            } else {
                return HttpResponse.sendError(res, 500, "Failed to record challenge progress.")
            }
        } catch (error) {
            console.error("Error recording challenge progress:", error)
            return HttpResponse.sendError(res, 500, "Internal server error.")
        }
    }

    async getUserStatistics(req, res) {
        if (!req.user || !req.user.id) {
            return HttpResponse.sendError(res, 401, "Unauthorized")
        }

        const userId = req.user.id

        try {
            // Total active challenges
            const activeChallengesCount = await this.#database.count(
                this.#table_user,
                [`user_id = '${userId}'`, `status = 'active'`]
            )

            // Total completed challenges (based on user_challenges status)
            const completedChallengesCount = await this.#database.count(
                this.#table_user,
                [`user_id = '${userId}'`, `status = 'completed'`]
            )

            // Total daily checks completed (all time)
            // This requires a join or a subquery. For simplicity with current Database.js,
            // we'll first get user's challenge IDs, then count daily checks.
            const userChallengeIds = await this.#database.read(this.#table_user, "id", [`user_id = '${userId}'`])
            let totalDailyChecksCompleted = 0
            if (userChallengeIds.length > 0) {
                const ids = userChallengeIds.map(ch => ch.id)
                // Note: The current count method takes a condition array directly.
                // A more robust SQL query would be:
                // SELECT COUNT(*) FROM challenge_daily_check cdc JOIN user_challenges uc ON cdc.user_challenge_id = uc.id WHERE uc.user_id = ? AND cdc.completed = TRUE
                // For now, we'll iterate or construct a longer WHERE clause
                const dailyCheckConditions = ids.map(id => `user_challenge_id = '${id}'`).join(' OR ')
                if (dailyCheckConditions) {
                    totalDailyChecksCompleted = await this.#database.count(
                        this.#table_daily_check,
                        [`(${dailyCheckConditions})`, `completed = TRUE`]
                    )
                }
            }

            // Streak calculation is complex and will be omitted for initial implementation
            // Progress graph data will be a separate endpoint as it requires historical data

            const statistics = {
                activeChallenges: activeChallengesCount,
                completedChallenges: completedChallengesCount,
                totalDailyChecksCompleted: totalDailyChecksCompleted
                // currentStreak: 0, // Placeholder
                // progressGraphData: {} // Placeholder for future endpoint
            }

            return HttpResponse.sendPayload(res, 200, JSON.stringify(statistics))

        } catch (error) {
            console.error("Error getting user statistics:", error)
            return HttpResponse.sendError(res, 500, "Internal server error.")
        }
    }

    async getChallengeProgressHistory(req, res) {
        if (!req.user || !req.user.id) {
            return HttpResponse.sendError(res, 401, "Unauthorized")
        }

        const challengeId = req.params.id
        const userId = req.user.id

        try {
            // Verify ownership of the user_challenge
            const userChallenge = await this.#database.read(this.#table_user, "id", [`id = '${challengeId}'`, `user_id = '${userId}'`])
            if (userChallenge.length === 0) {
                return HttpResponse.sendError(res, 404, "Challenge not found or not owned by user.")
            }

            const progressHistory = await this.#database.read(
                this.#table_progress,
                "*",
                [`user_challenge_id = '${challengeId}'`],
                "date ASC" // Order by date for graph
            )

            return HttpResponse.sendPayload(res, 200, JSON.stringify(progressHistory))
        } catch (error) {
            console.error("Error getting challenge progress history:", error)
            return HttpResponse.sendError(res, 500, "Internal server error.")
        }
    }











}

module.exports = Challenges