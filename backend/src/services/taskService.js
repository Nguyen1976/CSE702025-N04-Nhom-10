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

const updateTask = async (taskId, reqBody, user) => {
  try {
    const task = await Task.findOneAndUpdate(
      { _id: taskId, userId: user._id },
      { ...reqBody },
      { new: true }
    )

    if (!task) {
      throw new ApiError(StatusCodes.NOT_FOUND)
    }

    return task
  } catch (error) {
    throw error
  }
}

const deleteTask = async (taskId, user) => {
  try {
    const deleted = await Task.findOneAndDelete({
      _id: taskId,
      userId: user._id
    })

    if (!deleted) {
      throw new ApiError(StatusCodes.NOT_FOUND)
    }
  } catch (error) {
    throw error
  }
}

module.exports = {
  createNew,
  getTasks,
  updateTask,
  deleteTask
}