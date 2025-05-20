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
    taskValidation.updateTask,
    authMiddleware.isAuthorized,
    taskController.updateTask
)

Router.route('/:taskId').delete(
    //validation
    authMiddleware.isAuthorized,
    taskController.deleteTask
)

module.exports = Router