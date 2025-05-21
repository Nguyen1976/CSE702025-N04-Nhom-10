const { StatusCodes } = require('http-status-codes')
const Joi = require('joi')
const ApiError = require('~/utils/ApiError')
const {
  TITLE_TASK_RULE,
  DESCRIPTION_TASK_RULE,
  TITLE_TASK_MESSAGE,
  DESCRIPTION_TASK_MESSAGE
} = require('~/utils/validators')

const createNew = async (req, res, next) => {
  const correctCondition = Joi.object({
    title: Joi.string()
      .required()
      .pattern(TITLE_TASK_RULE)
      .message(TITLE_TASK_MESSAGE),
    description: Joi.string()
      .required()
      .pattern(DESCRIPTION_TASK_RULE)
      .message(DESCRIPTION_TASK_MESSAGE)
  }).unknown(true)

  try {
    await correctCondition.validateAsync(req.body, {
      abortEarly: false
    })
    next()
  } catch (error) {
    next(
      new ApiError(StatusCodes.UNPROCESSABLE_ENTITY, new Error(error).message)
    )
  }
}

const updateTask = async (req, res, next) => {
  const correctCondition = Joi.object({
    title: Joi.string().pattern(TITLE_TASK_RULE).message(TITLE_TASK_MESSAGE),
    description: Joi.string()
      .pattern(DESCRIPTION_TASK_RULE)
      .message(DESCRIPTION_TASK_MESSAGE)
  }).unknown(true) // Cho phép các field khác

  try {
    await correctCondition.validateAsync(req.body, {
      abortEarly: false
    })
    next()
  } catch (error) {
    next(
      new ApiError(StatusCodes.UNPROCESSABLE_ENTITY, new Error(error).message)
    )
  }
}

module.exports = {
  createNew,
  updateTask
}
