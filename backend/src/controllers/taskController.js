const { StatusCodes } = require('http-status-codes')
const taskService = require('~/services/taskService')

const createNew = async (req, res, next) => {
  try {
    const createdTask = await taskService.createNew(req.body, req.jwtDecoded)
    
    res.status(StatusCodes.CREATED).json(createdTask)
  } catch (error) {
    next(error)
  }
}

const getTasks = async (req, res, next) => {
  try {
    const tasks = await taskService.getTasks(req.jwtDecoded)
    
    res.status(StatusCodes.OK).json(tasks)
  } catch (error) {
    next(error)
  }
}

module.exports = {
    createNew,
    getTasks
}