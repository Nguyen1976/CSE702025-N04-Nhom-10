/**
 * Updated by trungquandev.com's author on Oct 8 2023
 * YouTube: https://youtube.com/@trungquandev
 * "A bit of fragrance clings to the hand that gives flowers!"
 */
//https://github.com/trungquandev/trungquandev-public-utilities-algorithms/blob/main/14-trello-mongodb-schemas/validators.js

const OBJECT_ID_RULE = /^[0-9a-fA-F]{24}$/
const OBJECT_ID_RULE_MESSAGE =
  'Your string fails to match the Object Id pattern!'

const USERNAME_RULE = /^[a-zA-Z0-9_]{3,30}$/
const USERNAME_RULE_MESSAGE =
  'Username must be 3-30 characters long and only include letters, numbers, or underscores.'

const FIELD_REQUIRED_MESSAGE = 'This field is required.'
const EMAIL_RULE = /^\S+@\S+\.\S+$/
const EMAIL_RULE_MESSAGE = 'Email is invalid. (example@gmail.com)'
const PASSWORD_RULE = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d\W]{8,256}$/
const PASSWORD_RULE_MESSAGE =
  'Password must include at least 1 letter, a number, and at least 8 characters.'
const PASSWORD_CONFIRMATION_MESSAGE = 'Password Confirmation does not match!'

module.exports = {
  OBJECT_ID_RULE,
  OBJECT_ID_RULE_MESSAGE,
  FIELD_REQUIRED_MESSAGE,
  EMAIL_RULE,
  EMAIL_RULE_MESSAGE,
  PASSWORD_RULE,
  PASSWORD_RULE_MESSAGE,
  PASSWORD_CONFIRMATION_MESSAGE,
  USERNAME_RULE,
  USERNAME_RULE_MESSAGE
}
