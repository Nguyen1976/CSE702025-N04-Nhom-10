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
  subtasks: [
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
  createAt: {
    type: String,
    default: () => new Date().toISOString()
  },
  dueDate: {
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
