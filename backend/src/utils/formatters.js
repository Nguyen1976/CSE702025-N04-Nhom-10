const { pick } = require('lodash')

//Nhận vào thông tin user và chỉ lẩy những trường chúng ta cần
const pickUser = (user) => {
  if (!user) return {}
  return pick(user, ['_id', 'email', 'username', 'createdAt', 'updatedAt'])
}

module.exports = {
  pickUser
}
