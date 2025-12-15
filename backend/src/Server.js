const Authentication = require('./Authentication.js')
const Challenges = require('./Challenges.js')
const Database = require('./Database.js')
const HttpResponse = require('./HttpResponse.js')

const express = require('express')
const app = express()

let database = new Database()
let auth = new Authentication(database)
let challenges = new Challenges(database)

app.use(express.json())

/* Account */
app.post(`/api/auth/register`, auth.register.bind(auth))
app.post(`/api/auth/login`, auth.login.bind(auth))
app.post(`/api/auth/refresh`, auth.refreshLogin.bind(auth))

/* Challenges */
app.get('/api/challenges/suggested', challenges.getSuggested.bind(challenges))
app.get('/api/challenges/user', challenges.getUserChallenges.bind(challenges))
app.get('/api/challenges/check', challenges.getDailyCheck.bind(challenges))
app.get('/api/challenges/progress', challenges.getProgressHistory.bind(challenges))
app.get('/api/challenges/notifications', challenges.getNotifications.bind(challenges))
// New routes for updating and deleting user challenges
app.put('/api/challenges/user/:id', challenges.updateUserChallenge.bind(challenges))
app.delete('/api/challenges/user/:id', challenges.deleteUserChallenge.bind(challenges))
// New route for marking daily challenge as completed
app.post('/api/challenges/user/:id/check', challenges.markDailyChallengeCompleted.bind(challenges))
// New route for getting user's daily challenge status
app.get('/api/challenges/user/daily-status', challenges.getUserDailyStatus.bind(challenges))
// New route for recording challenge progress
app.post('/api/challenges/user/:id/progress', challenges.recordChallengeProgress.bind(challenges))
// New route for getting user statistics
app.get('/api/challenges/user/statistics', challenges.getUserStatistics.bind(challenges))
// New route for getting challenge progress history
app.get('/api/challenges/user/:id/progress-history', challenges.getChallengeProgressHistory.bind(challenges))


/* Cacth All */
app.all(/.*/, placeholder)

function placeholder(req, res){
    console.log(`${req.url}\n${JSON.stringify(req.body, null, 4)}`)
    HttpResponse.sendMessage(res, 404, `Resource ${req.url} not found`)
}

function setup(){
    let port = process.env.PORT || 3100
    let host = process.env.HOST || "0.0.0.0"

    app.listen(port, host)
    console.clear()
    console.log(`> 21DaysApp Backend running on ${host}:${port}`)
}

setup()