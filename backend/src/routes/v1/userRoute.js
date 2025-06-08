const express = require('express')
const authMiddleware = require('~/middlewares/authMiddleware')
const userValidation = require('~/validations/userValidation')
const userController = require('~/controllers/userController')

const Router = express.Router()

Router.route('/register').post(
  userValidation.createNew,
  userController.createNew
)

Router.route('/login').post(userValidation.login, userController.login)
Router.route('/logout').post(userController.logout)
Router.route('/').get(authMiddleware.isAuthorized, userController.getDetail)
Router.route('/').post(userValidation.update, authMiddleware.isAuthorized, userController.update)
Router.route('/update-password').post(userValidation.updatePassword, authMiddleware.isAuthorized, userController.updatePassword)
Router.route('/refresh_token').get(userController.refreshToken)
module.exports = Router
