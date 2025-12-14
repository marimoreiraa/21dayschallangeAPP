const Authentication = require('./Authentication.js')
const Challenges = require('./Challenges.js')
const Database = require('./Database.js')
const HttpResponse = require('./HttpResponse.js')

const express = require('express')
const app = express()

let database = new Database()
let auth = new Authentication(database)
let challenges = new Challenges(database,auth)

app.use(express.json())

/* Account */
app.post(`/api/auth/register`, auth.register.bind(auth))
app.post(`/api/auth/login`, auth.login.bind(auth))
app.post(`/api/auth/refresh`, auth.refreshLogin.bind(auth))

/* Challenges */
app.get('/api/challenges/suggested', challenges.getSuggested.bind(challenges))
app.post('/api/challenges/add', challenges.addChallenge.bind(challenges))
app.post('/api/challenges/create-custom', challenges.createCustomChallenge.bind(challenges)) 
app.get('/api/challenges/user', challenges.getUserChallenges.bind(challenges))
app.get('/api/challenges/check', challenges.getDailyCheck.bind(challenges))
app.post('/api/challenges/checkin', challenges.registerDailyCheck.bind(challenges)) 
app.get('/api/challenges/progress', challenges.getProgressHistory.bind(challenges))
app.get('/api/challenges/notifications', challenges.getNotifications.bind(challenges))
app.get('/api/challenges/stats/:challengeId', (req, res) => challenges.getChallengeStats(req, res));
app.get('/api/challenges/overall-progress', (req, res) => challenges.getOverallProgress(req, res));

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