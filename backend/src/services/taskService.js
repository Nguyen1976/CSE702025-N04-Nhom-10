const ApiError = require('~/utils/ApiError')
const { StatusCodes } = require('http-status-codes')
const env = require('~/config/environtment')
const Task = require('~/models/TaskModel')

const createNew = async (reqBody, user) => {
  try {
    const newTasks = {
        userId: user._id,
        ...reqBody,
    }

    const createdTask = await Task.create(newTasks)
    return createdTask
  } catch (error) {
    throw error
  }
}

const getTasks = async (user) => {
  try {
    const tasks = await Task.find({
        userId: user._id
    })
    
    return tasks
  } catch (error) {
    throw error
  }
}


module.exports = {
    createNew,
    getTasks
}