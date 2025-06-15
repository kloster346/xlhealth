module.exports = {
  root: true,
  env: {
    node: true
  },
  'extends': [
    'plugin:vue/vue3-essential',
    'eslint:recommended'
  ],
  parserOptions: {
    parser: '@babel/eslint-parser',
    requireConfigFile: false
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    // 允许未使用的变量，支持开发过程中的临时代码
    'no-unused-vars': ['warn', {
      'vars': 'local',           // 只检查局部变量
      'args': 'after-used',      // 只检查使用后的参数
      'argsIgnorePattern': '^_', // 忽略以_开头的参数
      'varsIgnorePattern': '^_'  // 忽略以_开头的变量
    }]
  }
}
