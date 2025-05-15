import express from 'express'
import mongoose from 'mongoose'
import 'dotenv/config'

const app = express()

app.use(express.json())
app.use(express.urlencoded({ extended: false }))

app.use('/', (req, res) => {
  return res.json('Hello world')
})

mongoose.connect(process.env.MONGODB_URI).then(() => {
  console.log('Connect to db')
  app.listen(process.env.APP_PORT, () => {
    console.log(`Server is running port ${process.env.APP_PORT}`)
  })
})
