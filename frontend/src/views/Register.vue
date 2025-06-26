<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h2>注册</h2>
        <p>加入AI心理咨询系统</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            type="email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="agreement">
          <el-checkbox v-model="registerForm.agreement">
            我已阅读并同意
            <el-link type="primary" @click="showTerms">用户协议</el-link>
            和
            <el-link type="primary" @click="showPrivacy">隐私政策</el-link>
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-footer">
        <p>
          已有账号？
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { validateEmail } from '../utils/validation'
import { registerUser } from '../api/auth'

export default {
  name: 'RegisterPage',
  setup() {
    const router = useRouter()
    const registerFormRef = ref()
    const loading = ref(false)
    
    // 表单数据
    const registerForm = reactive({
      nickname: '',
      email: '',
      password: '',
      confirmPassword: '',
      agreement: false
    })
    
    // 自定义验证器
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请确认密码'))
      } else if (value !== registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    const validateAgreement = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请阅读并同意用户协议和隐私政策'))
      } else {
        callback()
      }
    }
    
    // 表单验证规则
    const registerRules = {
      nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' },
        { min: 2, max: 20, message: '昵称长度在2到20个字符', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { validator: validateEmail, trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' },
        { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
      ],
      confirmPassword: [
        { validator: validateConfirmPassword, trigger: 'blur' }
      ],
      agreement: [
        { validator: validateAgreement, trigger: 'change' }
      ]
    }
    
    // 处理注册
    const handleRegister = async () => {
      if (!registerFormRef.value) return
      
      try {
        const valid = await registerFormRef.value.validate()
        if (!valid) return
        
        loading.value = true
        
        // 调用注册API
        await registerUser({
          nickname: registerForm.nickname,
          email: registerForm.email,
          password: registerForm.password
        })
        
        ElMessage.success('注册成功，请登录')
        
        // 跳转到登录页面
        router.push('/login')
        
      } catch (error) {
        ElMessage.error(error.message || '注册失败，请重试')
      } finally {
        loading.value = false
      }
    }
    
    // 跳转到登录页面
    const goToLogin = () => {
      router.push('/login')
    }
    
    // 显示用户协议
    const showTerms = () => {
      ElMessageBox.alert(
        '这里是用户协议的内容...',
        '用户协议',
        {
          confirmButtonText: '确定',
          type: 'info'
        }
      )
    }
    
    // 显示隐私政策
    const showPrivacy = () => {
      ElMessageBox.alert(
        '这里是隐私政策的内容...',
        '隐私政策',
        {
          confirmButtonText: '确定',
          type: 'info'
        }
      )
    }
    
    return {
      registerFormRef,
      registerForm,
      registerRules,
      loading,
      handleRegister,
      goToLogin,
      showTerms,
      showPrivacy
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
}

.register-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.register-form {
  margin-bottom: 20px;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

.register-footer {
  text-align: center;
}

.register-footer p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
  }
  
  .register-header h2 {
    font-size: 24px;
  }
}
</style>