const express = require('express')
const { StatusCodes } = require('http-status-codes')
const userRoute = require('./userRoute')

const Router = express.Router()

Router.get('/status', (req, res) => {
  res.status(StatusCodes.OK).json({ message: 'APIs v1 are ready to use.' })
})

Router.use('/users', userRoute)
module.exports = Router
