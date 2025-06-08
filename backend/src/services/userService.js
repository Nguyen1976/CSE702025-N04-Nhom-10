const ApiError = require('~/utils/ApiError')
const { pickUser } = require('~/utils/formatters')
const User = require('~/models/userModel')
const bcrypt = require('bcryptjs')
const env = require('~/config/environtment')
const JWTProvider = require('~/providers/JwtProvider')
const { StatusCodes } = require('http-status-codes')

const createNew = async (reqBody) => {
  try {
    const existUser = await User.findOne({ email: reqBody.email })

    if (existUser) {
      throw new ApiError(StatusCodes.CONFLICT, 'Email already exists!')
    }

    const newUser = {
      username: reqBody.username,
      email: reqBody.email,
      password: bcrypt.hashSync(reqBody.password, 10)
    }

    const createdUser = await User.create(newUser)

    return pickUser(createdUser)
  } catch (error) {
    throw error
  }
}

const login = async (reqBody) => {
  try {
    const existUser = await User.findOne({ email: reqBody.email })

    if (!existUser)
      throw new ApiError(StatusCodes.NOT_FOUND, 'Account not found!')

    if (!bcrypt.compareSync(reqBody.password, existUser.password)) {
      throw new ApiError(
        StatusCodes.NOT_ACCEPTABLE,
        'Your Email or Password is incorrect!'
      )
    }

    const userInfo = {
      _id: existUser._id,
      email: existUser.email
    }

    const accessToken = await JWTProvider.generateToken(
      userInfo,
      env.ACCESS_TOKEN_SECRET_SIGNATURE,
      env.ACCESS_TOKEN_LIFE
    )

    const refreshToken = await JWTProvider.generateToken(
      userInfo,
      env.REFRESH_TOKEN_SECRET_SIGNATURE,
      env.REFRESH_TOKEN_LIFE
    )

    return {
      accessToken,
      refreshToken,
      ...pickUser(existUser)
    }
  } catch (error) {
    throw error
  }
}

const refreshToken = async (clientRefreshToken) => {
  try {
    const refreshTokenDecoded = await JWTProvider.verifyToken(
      clientRefreshToken,
      env.REFRESH_TOKEN_SECRET_SIGNATURE
    )

    const userInfo = {
      _id: refreshTokenDecoded._id,
      email: refreshTokenDecoded.email
    }

    const accessToken = await JWTProvider.generateToken(
      userInfo,
      env.ACCESS_TOKEN_SECRET_SIGNATURE,
      env.ACCESS_TOKEN_LIFE
      // 5
    )

    return { accessToken }
  } catch (error) {
    throw error
  }
}

const logout = async (reqBody) => {
  try {
    //Vì không quản lý token ở server nên k cần làm gì
    return true
  } catch (error) {
    throw new ApiError(
      StatusCodes.FORBIDDEN,
      'Please Sign In! (Error from logout)'
    )
  }
}

const getDetail = async (userId) => {
  try {
    const user = await User.findById(userId)

    if (!user) {
      throw new ApiError(StatusCodes.NOT_FOUND, 'User not found')
    }

    return pickUser(user)
  } catch (error) {
    throw error
  }
}

const update = async (userId, reqBody) => {
  try {
    const updatedUser = await User.findByIdAndUpdate(
      userId,
      { ...reqBody },
      { new: true }
    )
    if (!updatedUser) {
      throw new ApiError(StatusCodes.NOT_FOUND, 'User not found')
    }
    return pickUser(updatedUser)
  } catch (error) {
    throw error
  }
}

const updatePassword = async (userId, reqBody) => {
  try {
    const { oldPassword, password } = reqBody

    const user = await User.findById(userId)

    if (!user) {
      throw new ApiError(StatusCodes.NOT_FOUND, 'User not found')
    }

    const isMatch = await bcrypt.compare(oldPassword, user.password)

    if (!isMatch) {
      throw new ApiError(StatusCodes.UNAUTHORIZED, 'Old password is incorrect')
    }

    const hashedPassword = await bcrypt.hash(password, 10)

    const updatedUser = await User.findByIdAndUpdate(userId, { password: hashedPassword })

    return pickUser(updatedUser)
  } catch (error) {
    throw error
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
