// 邮箱验证
export const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入有效的邮箱地址'))
  } else {
    callback()
  }
}

// 密码强度验证
export const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (value.length > 20) {
    callback(new Error('密码长度不能超过20位'))
  } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含大小写字母和数字'))
  } else {
    callback()
  }
}

// 昵称验证
export const validateNickname = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入昵称'))
  } else if (value.length < 2) {
    callback(new Error('昵称长度不能少于2位'))
  } else if (value.length > 20) {
    callback(new Error('昵称长度不能超过20位'))
  } else if (!/^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('昵称只能包含中文、英文、数字和下划线'))
  } else {
    callback()
  }
}

// 手机号验证
export const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入有效的手机号'))
  } else {
    callback()
  }
}

// 验证码验证
export const validateCode = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入验证码'))
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error('验证码必须是6位数字'))
  } else {
    callback()
  }
}

// 通用非空验证
export const validateRequired = (message = '此项为必填项') => {
  return (rule, value, callback) => {
    if (!value || (typeof value === 'string' && !value.trim())) {
      callback(new Error(message))
    } else {
      callback()
    }
  }
}

// 长度验证
export const validateLength = (min, max, message) => {
  return (rule, value, callback) => {
    if (!value) {
      callback()
      return
    }
    const length = value.length
    if (length < min || length > max) {
      callback(new Error(message || `长度应在${min}-${max}个字符之间`))
    } else {
      callback()
    }
  }
}

// 数字验证
export const validateNumber = (rule, value, callback) => {
  if (value !== '' && value !== null && value !== undefined) {
    if (isNaN(value)) {
      callback(new Error('请输入数字'))
    } else {
      callback()
    }
  } else {
    callback()
  }
}

// 正整数验证
export const validatePositiveInteger = (rule, value, callback) => {
  if (value !== '' && value !== null && value !== undefined) {
    if (!Number.isInteger(Number(value)) || Number(value) <= 0) {
      callback(new Error('请输入正整数'))
    } else {
      callback()
    }
  } else {
    callback()
  }
}

// URL验证
export const validateUrl = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  const urlRegex = /^(https?:\/\/)[\da-z.-]+\.([a-z.]{2,6})([/\w .-]*)*\/?$/
  if (!urlRegex.test(value)) {
    callback(new Error('请输入有效的URL地址'))
  } else {
    callback()
  }
}

// 身份证验证
export const validateIdCard = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if (!idCardRegex.test(value)) {
    callback(new Error('请输入有效的身份证号'))
  } else {
    callback()
  }
}