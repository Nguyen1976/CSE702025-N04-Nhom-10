const express = require('express')
const { StatusCodes } = require('http-status-codes')

const Router = express.Router()

Router.get('/status', (req, res) => {
  res.status(StatusCodes.OK).json({ message: 'APIs v1 are ready to use.' })
})

module.exports = Router