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

const login = async (req, res, next) => {
  try {
    const result = await userService.login(req.body)

    res.status(StatusCodes.OK).json(result)
  } catch (error) {
    next(error)
  }
}

const refreshToken = async (req, res, next) => {
  try {
    const result = await userService.refreshToken(req.body?.refreshToken)
   
    res.status(StatusCodes.OK).json(result)
  } catch (error) {
    next(
      new ApiError(
        StatusCodes.FORBIDDEN,
        'Please Sign In! (Error from refresh Token)'
      )
    )
  }
}

module.exports = {
  createNew,
  login,
  refreshToken
}
