require('module-alias/register')
const express = require('express')
const mongoose = require('mongoose')
const env = require('~/config/environtment')
const APIs_v1 = require('~/routes/v1/index')
const errorHandlingMiddleware = require('~/middlewares/errorHandlingMiddleware.js')

const app = express()

app.use(express.json())

app.use(express.urlencoded({ extended: false }))

app.use('/api/v1', APIs_v1)

app.use(errorHandlingMiddleware)

mongoose.connect(env.MONGODB_URI).then(() => {
  console.log('Connect to db')
  app.listen(env.APP_PORT, () => {
    console.log(`Server is running port ${env.APP_PORT}`)
  })
})
