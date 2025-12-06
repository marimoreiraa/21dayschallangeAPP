const Authentication = require('./Authentication.js')
const Database = require('./Database.js')

const express = require('express')
const HttpResponse = require('./HttpResponse.js')
const app = express()

let database = new Database()
let auth = new Authentication(database)

app.use(express.json())

/* Account */
app.post(`/api/auth/register`, auth.register.bind(auth))
app.post(`/api/auth/login`, auth.login.bind(auth))
app.post(`/api/auth/refresh`, auth.refreshLogin.bind(auth))

/* Cacth All */
app.all(/.*/, placeholder)

function placeholder(req, res){
    console.log(`${req.url}\n${JSON.stringify(req.body, null, 4)}`)
    HttpResponse.sendMessage(res, 404, `Resource ${req.url} not found`)
}

function setup(){
    app.listen(process.env.PORT, process.env.HOST)
    console.log(`> 21DaysApp Backend running on ${process.env.HOST}:${process.env.PORT}`)
}

setup()