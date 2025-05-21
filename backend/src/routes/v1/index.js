const express = require('express')
const { StatusCodes } = require('http-status-codes')
const userRoute = require('./userRoute')
const taskRoute = require('./taskRoute')

const Router = express.Router()

Router.get('/status', (req, res) => {
  res.status(StatusCodes.OK).json({ message: 'APIs v1 are ready to use.' })
})

Router.use('/users', userRoute)
Router.use('/tasks', taskRoute)
module.exports = Router
