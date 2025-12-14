const Authentication = require('./Authentication.js')
const Database = require('./Database')
const HttpResponse = require('./HttpResponse')
const User = require("./User")

class Challenges {

    #database
    #authentication
    #table_suggested = "suggested_challenges"
    #table_user = "user_challenges"
    #table_daily_check = "challenge_daily_check"
    #table_progress = "challenge_progress_history"
    #table_notifications = "notifications"

    constructor(database,authentication) {
        this.#database = database
        this.#authentication = authentication
    }

    async getSuggested(req, res) {
        let database = new Database()
        let suggestedChallenges = await database.read('suggested_challenges', '*')

        if (!suggestedChallenges)
            return HttpResponse.sendMessage(res, 400, "Could not fetch suggested challenges")

        const payload = {
            suggestedChallenges: suggestedChallenges,
        };
        
        return HttpResponse.sendPayload(res, 200, payload)
    }

    async getUserChallenges(req, res) {
        let userId = await this.#authentication.validate(req.header('Authorization'))

        if (!userId)
            return HttpResponse.sendMessage(res, 401, "Unauthorized")

        // 1. Buscar todos os desafios ativos do usuário
        let challenges = await this.#database.read(this.#table_user, 
                                                  "*", 
                                                  `user_id = ${userId} AND status = 'active'`)

        if (!challenges)
            return HttpResponse.sendMessage(res, 400, "Could not fetch user challenges")
            
        const date = new Date();
        const today = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
            
        // 2. Para cada desafio, buscar os dias concluídos E o status de HOJE
        const challengesWithProgress = await Promise.all(challenges.map(async (challenge) => {
            
            // Contar o progresso (Dias Concluídos)
            let progress = await this.#database.query(
                `SELECT COUNT(id) FROM ${this.#table_daily_check} 
                 WHERE user_challenge_id = ${challenge.id} AND completed = TRUE;`
            );
            
            const daysCompleted = progress && progress.length > 0 ? parseInt(progress[0].count, 10) : 0;
            
            let todayCheck = await this.#database.read(
                this.#table_daily_check, 
                'id', 
                `user_challenge_id = ${challenge.id} AND date = '${today}' AND completed = TRUE;`
            );
            
            const isCheckedToday = (todayCheck != null && todayCheck.length > 0);
            
            // Adicionar a contagem e o status ao objeto do desafio
            return {
                ...challenge,
                days_completed: daysCompleted,
                is_checked_today: isCheckedToday 
            };
        }));
        
        const payload = {
            userChallenges: challengesWithProgress,
        };

        return HttpResponse.sendPayload(res, 200, payload)
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

    async addChallenge(req, res) {
        
        let userId = await this.#authentication.validate(req.header('Authorization'))

        if (!userId)
            return HttpResponse.sendMessage(res, 401, "Unauthorized")

        let data = req.body
        
        if (!data.suggested_id)
            return HttpResponse.sendMessage(res, 400, "Missing suggested_id")

        let suggestedResult = await this.#database.read('suggested_challenges', '*', `id = ${data.suggested_id}`)

        if (!suggestedResult || suggestedResult.length === 0)
            return HttpResponse.sendMessage(res, 404, "Suggested challenge not found")

        let suggestedChallenge = suggestedResult[0]

        
        try {
            const date = new Date();
            const today = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`; // Formato YYYY-MM-DD

            let challengeData = {
                user_id: userId,
                suggested_id: suggestedChallenge.id,
                title: suggestedChallenge.title,
                description: suggestedChallenge.description,
                category: suggestedChallenge.category,
                duration_days: suggestedChallenge.duration_days,
                frequency: suggestedChallenge.frequency,
                start_date: today, 
                end_date: 'NULL', 
                notifications_enabled: false,
                status: 'active'
            }
            
            await this.#database.create('user_challenges', Object.keys(challengeData), Object.values(challengeData))

            return HttpResponse.sendMessage(res, 200, "Challenge added to user successfully")

        } catch (e) {
            console.error("Error adding challenge:", e)
            return HttpResponse.sendMessage(res, 500, "Internal server error while adding challenge")
        }
    }

    async createCustomChallenge(req, res) {
        let userId = await this.#authentication.validate(req.header('Authorization'))

        if (!userId)
            return HttpResponse.sendMessage(res, 401, "Unauthorized")

        let data = req.body
        
        if (!data.title || !data.description || !data.category || !data.duration_days || !data.frequency || !data.icon_name)
            return HttpResponse.sendMessage(res, 400, "Missing mandatory fields (title, description, category, duration_days, frequency, icon).")

        try {
            const date = new Date();
            const today = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
            const durationDays = parseInt(data.duration_days, 10);
            const endDate = new Date();
            endDate.setDate(endDate.getDate() + durationDays); 
            
            const endDateString = `${endDate.getFullYear()}-${(endDate.getMonth() + 1).toString().padStart(2, '0')}-${endDate.getDate().toString().padStart(2, '0')}`;

            let challengeData = {
                user_id: userId,
                title: data.title,
                description: data.description,
                category: data.category,
                duration_days: data.duration_days,
                frequency: data.frequency,
                start_date: today, 
                end_date: endDateString, 
                notifications_enabled: data.notifications_enabled || false,
                status: 'active',
                icon_name: data.icon_name
            }
            
            await this.#database.create(this.#table_user, Object.keys(challengeData), Object.values(challengeData))

            return HttpResponse.sendMessage(res, 200, "Custom challenge created successfully")

        } catch (e) {
            console.error("Error creating custom challenge:", e)
            return HttpResponse.sendMessage(res, 500, "Internal server error while creating challenge")
        }
    }

    async registerDailyCheck(req, res) {
        let userId = await this.#authentication.validate(req.header('Authorization'))

        if (!userId)
            return HttpResponse.sendMessage(res, 401, "Unauthorized")

        let data = req.body
        
        if (!data.user_challenge_id)
            return HttpResponse.sendMessage(res, 400, "Missing user_challenge_id")
        
        const challengeId = data.user_challenge_id;
        const completeStatus = data.completed === true || data.completed === 'true'; 

        let userChallenge = await this.#database.read(this.#table_user, 'id', `id = ${challengeId} AND user_id = ${userId}`);
        
        if (!userChallenge || userChallenge.length === 0)
            return HttpResponse.sendMessage(res, 404, "Challenge not found or does not belong to user.")
            
        try {
            const date = new Date();
            const today = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
            
            let existingCheck = await this.#database.read(
                this.#table_daily_check, 
                'id', 
                `user_challenge_id = ${challengeId} AND date = '${today}'`
            );

            if (existingCheck && existingCheck.length > 0) {
                const recordId = existingCheck[0].id;
                
                if (completeStatus === false) {
                    await this.#database.update(
                        this.#table_daily_check, 
                        'completed', 
                        false,       
                        `id = ${recordId}` 
                    );         
                    return HttpResponse.sendMessage(res, 200, "Daily check-in removed successfully.")
                } else {
                    let currentRecord = await this.#database.read(this.#table_daily_check, 'completed', `id = ${recordId}`);
                    const isCurrentlyCompleted = currentRecord[0].completed;

                    if (!isCurrentlyCompleted) {
                        await this.#database.update(this.#table_daily_check, 'completed', true, `id = ${recordId}`);
                        return HttpResponse.sendMessage(res, 200, "Daily check-in registered successfully (Updated).")
                    } else {
                        return HttpResponse.sendMessage(res, 200, "Challenge already checked in for today.")
                    }
                }
                
            } else if (completeStatus === true) {
                let checkData = {
                    user_challenge_id: challengeId,
                    date: today,
                    completed: true
                }
                await this.#database.create(this.#table_daily_check, Object.keys(checkData), Object.values(checkData))
                return HttpResponse.sendMessage(res, 200, "Daily check-in registered successfully.")

            } else {
                return HttpResponse.sendMessage(res, 200, "No check-in to remove.")
            }


        } catch (e) {
            console.error("Error registering daily check:", e)
            return HttpResponse.sendMessage(res, 500, "Internal server error while registering check-in")
        }
    }

    async getChallengeStats(req, res) {
        let userId = await this.#authentication.validate(req.header('Authorization'));

        if (!userId)
            return HttpResponse.sendMessage(res, 401, "Unauthorized");

        const challengeId = req.params.challengeId;

        if (!challengeId)
            return HttpResponse.sendMessage(res, 400, "Missing challenge ID.");

        let challengeDetails = await this.#database.read(
            this.#table_user, 
            'id, duration_days, start_date', 
            `id = ${challengeId} AND user_id = ${userId} AND status = 'active'`
        );

        if (!challengeDetails || challengeDetails.length === 0)
            return HttpResponse.sendMessage(res, 404, "Challenge not found or inactive.");

        const challenge = challengeDetails[0];
        const durationDays = parseInt(challenge.duration_days, 10);
        

        
        let completedDaysResult = await this.#database.query(
            `SELECT COUNT(id) AS count FROM ${this.#table_daily_check} 
            WHERE user_challenge_id = ${challengeId} AND completed = TRUE;`
        );
        
        const daysCompleted = (completedDaysResult && completedDaysResult.rows && completedDaysResult.rows.length > 0) 
            ? parseInt(completedDaysResult.rows[0].count, 10) 
            : 0;
        
        const currentStreak = daysCompleted; 

        let skippedDaysResult = await this.#database.query(
            `SELECT COUNT(id) AS count FROM ${this.#table_daily_check} 
            WHERE user_challenge_id = ${challengeId} AND completed = FALSE;`
        );
        let skippedDays = (skippedDaysResult && skippedDaysResult.rows && skippedDaysResult.rows.length > 0)
            ? parseInt(skippedDaysResult.rows[0].count, 10) 
            : 0;
        
        const completionRate = durationDays > 0 
            ? Math.round((daysCompleted / durationDays) * 100) 
            : 0;
            
        let daysRemaining = durationDays - daysCompleted;
        if (daysRemaining < 0) daysRemaining = 0; 
            
        
        const payload = {
            stats: {
                currentStreak: currentStreak, 
                habitCompletionRate: completionRate,
                skippedDays: skippedDays,
                daysRemaining: daysRemaining,
            }
            
        };

        return HttpResponse.sendPayload(res, 200, payload);
    }

    async getOverallProgress(req, res) {
        let userId = await this.#authentication.validate(req.header('Authorization'));

        if (!userId)
            return HttpResponse.sendMessage(res, 401, "Unauthorized");

        let overallProgressQuery = `
            SELECT
                TO_CHAR(cdc.date, 'YYYY-MM-DD') AS check_date,
                COUNT(cdc.id) AS challenges_completed_count
            FROM
                ${this.#table_daily_check} cdc
            JOIN
                ${this.#table_user} uc ON cdc.user_challenge_id = uc.id
            WHERE
                uc.user_id = ${userId}
                AND cdc.completed = TRUE
                AND cdc.date >= CURRENT_DATE - INTERVAL '30 days'
            GROUP BY
                cdc.date
            ORDER BY
                cdc.date ASC;
        `;

        try {
            let progressResult = await this.#database.query(overallProgressQuery);

            const rows = progressResult && progressResult.rows ? progressResult.rows : [];

            const progressData = rows.map(row => ({
                date: row.check_date,
                count: parseInt(row.challenges_completed_count, 10)
            }));

            const payload = {
                progressData: progressData,
            };

            return HttpResponse.sendPayload(res, 200, payload);

        } catch (e) {
            console.error("Error fetching overall progress:", e);
            return HttpResponse.sendMessage(res, 500, "Internal server error while fetching overall progress.");
        }
    }
}

module.exports = Challenges