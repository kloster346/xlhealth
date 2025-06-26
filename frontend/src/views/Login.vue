<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>登录</h2>
        <p>欢迎回到AI心理咨询系统</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="email">
          <el-input
            v-model="loginForm.email"
            type="email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <p>
          还没有账号？
          <el-link type="primary" @click="goToRegister">立即注册</el-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { validateEmail } from '../utils/validation'
import { loginUser } from '../api/auth'

export default {
  name: 'LoginPage',
  setup() {
    const store = useStore()
    const router = useRouter()
    const loginFormRef = ref()
    const loading = ref(false)
    
    // 表单数据
    const loginForm = reactive({
      email: '',
      password: ''
    })
    
    // 表单验证规则
    const loginRules = {
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { validator: validateEmail, trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }
    
    // 处理登录
    const handleLogin = async () => {
      if (!loginFormRef.value) return
      
      try {
        const valid = await loginFormRef.value.validate()
        if (!valid) return
        
        loading.value = true
        
        // 调用登录API
        const response = await loginUser(loginForm)
        
        // 保存用户信息和token到store
        await store.dispatch('login', {
          user: response.user,
          token: response.token
        })
        
        ElMessage.success('登录成功')
        
        // 跳转到首页或之前访问的页面
        const redirect = router.currentRoute.value.query.redirect || '/'
        router.push(redirect)
        
      } catch (error) {
        ElMessage.error(error.message || '登录失败，请检查邮箱和密码')
      } finally {
        loading.value = false
      }
    }
    
    // 跳转到注册页面
    const goToRegister = () => {
      router.push('/register')
    }
    
    return {
      loginFormRef,
      loginForm,
      loginRules,
      loading,
      handleLogin,
      goToRegister
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

.login-footer {
  text-align: center;
}

.login-footer p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .login-header h2 {
    font-size: 24px;
  }
}
</style>