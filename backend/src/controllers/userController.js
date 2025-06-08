const { StatusCodes } = require('http-status-codes')
const userService = require('~/services/userService')
const ApiError = require('~/utils/ApiError')

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

const logout = async (req, res, next) => {
  try {
    const status = await userService.logout(req.body)
    if (!status) {
      throw new ApiError(StatusCodes.FORBIDDEN, 'Logout failed!')
    }
    res.status(StatusCodes.OK).json({ message: 'Logout successfully!' })
  } catch (error) {
    next(
      new ApiError(StatusCodes.FORBIDDEN, 'Please Sign In! (Error from logout)')
    )
  }
}

const getDetail = async (req, res, next) => {
  try {
    const user = await userService.getDetail(req.jwtDecoded._id)
    if (!user) {
      throw new ApiError(StatusCodes.NOT_FOUND, 'User not found')
    }
    res.status(StatusCodes.OK).json(user)
  } catch (error) {
    next(error)
  }
}

const update = async (req, res, next) => {
  try {
    const updatedUser = await userService.update(req.jwtDecoded._id, req.body)
    res.status(StatusCodes.OK).json(updatedUser)
  } catch (error) {
    next(error)
  }
}

const updatePassword = async (req, res, next) => {
  try {
    const updatedUser = await userService.updatePassword(
      req.jwtDecoded._id,
      req.body
    )
    res.status(StatusCodes.OK).json(updatedUser)
  } catch (error) {
    next(error)
  }
}

module.exports = {
  createNew,
  login,
  refreshToken,
  logout,
  getDetail,
  update,
  updatePassword
}
