const express = require('express')
const authMiddleware = require('~/middlewares/authMiddleware')

const Router = express.Router()

//get list task of user
Router.route('/').get(
    //validation
    authMiddleware.isAuthorized
    //controller
)

Router.route('/').post(
     //validation
    authMiddleware.isAuthorized
    //controller
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