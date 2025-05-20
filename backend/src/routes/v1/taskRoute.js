const express = require('express')
const authMiddleware = require('~/middlewares/authMiddleware')
const taskValidation = require('~/validations/taskValidation')
const taskController = require('~/controllers/taskController')

const Router = express.Router()

//get list task of user
Router.route('/').get(
    //validation
    authMiddleware.isAuthorized,
    //controller
    taskController.getTasks
)

//create
Router.route('/').post(
    taskValidation.createNew,
    authMiddleware.isAuthorized,
    taskController.createNew
)

Router.route('/:taskId').put(
     //validation
    authMiddleware.isAuthorized
    //controller
)

Router.route('/:taskId').delete(
    //validation
    authMiddleware.isAuthorized
    //controller
)

module.exports = Router