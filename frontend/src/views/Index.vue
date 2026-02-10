<template>
  <div class="index-container">
    <el-container>
      <el-aside width="200px">
        <TreeMenu :collapsed="isCollapsed" />
      </el-aside>
      
      <el-container>
        <el-header style="background: #fff; display: flex; align-items: center; justify-content: space-between;">
          <div class="header-left">
            <el-button
              type="text"
              @click="isCollapsed = !isCollapsed"
              style="font-size: 20px"
            >
              <el-icon v-if="!isCollapsed"><Fold /></el-icon>
              <el-icon v-else><Expand /></el-icon>
            </el-button>
            <span class="page-title">{{ pageTitle }}</span>
          </div>
          
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-icon><User /></el-icon>
                <span>{{ authStore.user?.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">Logout</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { User, Fold, Expand } from '@element-plus/icons-vue';
import TreeMenu from '../components/TreeMenu.vue';

const route = useRoute();
const authStore = useAuthStore();

const isCollapsed = ref(false);

const pageTitle = computed(() => {
  const routeName = route.name;
  const nameMap = {
    'Dashboard': 'Dashboard',
    'MyForms': 'My Forms',
    'ForMyAction': 'Pending Actions',
    'CreateForm': 'Create Form',
    'EditForm': 'Edit Form',
    'ResubmitForm': 'Resubmit Form',
    'CompletedForm': 'Completed Forms',
    'ProcessingForm': 'Processing Forms'
  };
  return nameMap[routeName] || 'Form Management';
});

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout();
    window.location.href = '/login';
  }
};

onMounted(() => {
  // Load user from localStorage if not already loaded
  if (!authStore.user) {
    authStore.loadUser();
  }
  
  if (!authStore.isAuthenticated) {
    window.location.href = '/login';
  }
});
</script>

<style scoped>
.index-container {
  height: 100vh;
  overflow: hidden;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.page-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}

.user-info:hover {
  color: #409eff;
}
</style>