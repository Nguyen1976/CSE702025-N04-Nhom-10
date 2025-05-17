const ApiError = require('~/utils/ApiError')
const { pickUser } = require('~/utils/formatters')
const User = require('~/models/UserModel')
const bcrypt = require('bcryptjs')

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

module.exports = {
  createNew
}
