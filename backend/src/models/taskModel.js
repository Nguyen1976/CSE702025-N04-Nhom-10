const mongoose = require('mongoose')

const TaskSchema = new mongoose.Schema({
  userId: {
    type: String,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  subtasks: {
    type: [
      {
        title: {
          type: String,
          required: true
        },
        subtaskDes: {
          type: String,
          required: true
        }
      }
    ],
    default: [] // <-- mặc định là mảng rỗng
  },
  createAt: {
    type: String,
    default: () => new Date().toISOString()
  },
  taskDate: {
    type: String,
    required: true
  },
  taskTime: {
    type: String,
    required: true
  },
  type: {
    type: String,
    required: true
  },
  isSuccess: {
    type: Boolean,
    default: false
  }
})

module.exports = mongoose.model('Task', TaskSchema)
