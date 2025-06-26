<template>
  <div class="profile-edit">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>编辑个人资料</span>
        </div>
      </template>
      
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="80px"
        class="profile-form"
      >
        <el-form-item label="头像" prop="avatar">
          <div class="avatar-section">
            <el-avatar :size="80" class="avatar-display">
              <img v-if="profileForm.avatar" :src="profileForm.avatar" alt="头像" />
              <el-icon v-else><User /></el-icon>
            </el-avatar>
            <div class="avatar-actions">
              <el-button size="small" @click="handleAvatarUpload">更换头像</el-button>
              <p class="avatar-tip">支持 JPG、PNG 格式，文件大小不超过 2MB</p>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input
            v-model="profileForm.nickname"
            placeholder="请输入昵称"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="profileForm.email"
            type="email"
            placeholder="请输入邮箱"
            :disabled="emailDisabled"
          >
            <template #suffix>
              <el-button
                v-if="emailDisabled"
                link
                type="primary"
                size="small"
                @click="enableEmailEdit"
              >
                修改
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSave"
          >
            保存修改
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 修改密码卡片 -->
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="80px"
        class="password-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="passwordLoading"
            @click="handlePasswordChange"
          >
            修改密码
          </el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { validateEmail, validateNickname } from '../utils/validation'
import { updateUserInfo, changePassword } from '../api/auth'

export default {
  name: 'UserProfileEdit',
  components: {
    User
  },
  setup() {
    const store = useStore()
    const profileFormRef = ref()
    const passwordFormRef = ref()
    const loading = ref(false)
    const passwordLoading = ref(false)
    const emailDisabled = ref(true)
    
    // 当前用户信息
    const currentUser = computed(() => store.getters.currentUser)
    
    // 个人资料表单
    const profileForm = reactive({
      nickname: '',
      email: '',
      avatar: ''
    })
    
    // 密码修改表单
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    // 自定义验证器
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请确认新密码'))
      } else if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    // 个人资料验证规则
    const profileRules = {
      nickname: [
        { validator: validateNickname, trigger: 'blur' }
      ],
      email: [
        { validator: validateEmail, trigger: 'blur' }
      ]
    }
    
    // 密码验证规则
    const passwordRules = {
      oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' },
        { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
      ],
      confirmPassword: [
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]
    }
    
    // 初始化表单数据
    const initForm = () => {
      if (currentUser.value) {
        profileForm.nickname = currentUser.value.nickname || ''
        profileForm.email = currentUser.value.email || ''
        profileForm.avatar = currentUser.value.avatar || ''
      }
    }
    
    // 启用邮箱编辑
    const enableEmailEdit = () => {
      emailDisabled.value = false
    }
    
    // 处理头像上传
    const handleAvatarUpload = () => {
      ElMessage.info('头像上传功能将在后续版本中实现')
    }
    
    // 保存个人资料
    const handleSave = async () => {
      if (!profileFormRef.value) return
      
      try {
        const valid = await profileFormRef.value.validate()
        if (!valid) return
        
        loading.value = true
        
        // 调用更新API
        const response = await updateUserInfo(currentUser.value.id, {
          nickname: profileForm.nickname,
          email: profileForm.email,
          avatar: profileForm.avatar
        })
        
        // 更新store中的用户信息
        store.dispatch('updateUser', response.user)
        
        ElMessage.success('个人资料更新成功')
        emailDisabled.value = true
        
      } catch (error) {
        ElMessage.error(error.message || '更新失败，请重试')
      } finally {
        loading.value = false
      }
    }
    
    // 取消编辑
    const handleCancel = () => {
      initForm()
      emailDisabled.value = true
      profileFormRef.value?.clearValidate()
    }
    
    // 修改密码
    const handlePasswordChange = async () => {
      if (!passwordFormRef.value) return
      
      try {
        const valid = await passwordFormRef.value.validate()
        if (!valid) return
        
        passwordLoading.value = true
        
        // 调用修改密码API
        await changePassword(currentUser.value.id, {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        
        ElMessage.success('密码修改成功')
        resetPasswordForm()
        
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败，请重试')
      } finally {
        passwordLoading.value = false
      }
    }
    
    // 重置密码表单
    const resetPasswordForm = () => {
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      passwordFormRef.value?.clearValidate()
    }
    
    // 组件挂载时初始化
    onMounted(() => {
      initForm()
    })
    
    return {
      profileFormRef,
      passwordFormRef,
      profileForm,
      passwordForm,
      profileRules,
      passwordRules,
      loading,
      passwordLoading,
      emailDisabled,
      enableEmailEdit,
      handleAvatarUpload,
      handleSave,
      handleCancel,
      handlePasswordChange,
      resetPasswordForm
    }
  }
}
</script>

<style scoped>
.profile-edit {
  max-width: 600px;
  margin: 0 auto;
}

.profile-card,
.password-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-display {
  flex-shrink: 0;
}

.avatar-actions {
  flex: 1;
}

.avatar-tip {
  margin: 8px 0 0 0;
  font-size: 12px;
  color: #909399;
}

.profile-form .el-form-item,
.password-form .el-form-item {
  margin-bottom: 20px;
}

.profile-form .el-form-item:last-child,
.password-form .el-form-item:last-child {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .profile-edit {
    padding: 0 10px;
  }
  
  .avatar-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>