const { StatusCodes } = require('http-status-codes')
const userService = require('~/services/userService')

const createNew = async (req, res, next) => {
  try {
    const createdUser = await userService.createNew(req.body)
    res.status(StatusCodes.CREATED).json(createdUser)
  } catch (error) {
    next(error)
  }
}

module.exports = {
  createNew
}
