<template>
  <div class="tree-menu">
    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      @select="handleSelect"
      class="tree-menu-container"
    >
      <el-menu-item index="index">
        <el-icon><HomeFilled /></el-icon>
        <span>Dashboard</span>
      </el-menu-item>
      
      <el-sub-menu index="myForms">
        <template #title>
          <el-icon><Document /></el-icon>
          <span>My Forms</span>
        </template>
        <el-menu-item index="myForms-draft">Draft</el-menu-item>
        <el-menu-item index="myForms-processing">Processing</el-menu-item>
        <el-menu-item index="myForms-completed">Completed</el-menu-item>
        <el-menu-item index="myForms-returned">Returned</el-menu-item>
      </el-sub-menu>
      
      <el-sub-menu index="actions">
        <template #title>
          <el-icon><Tickets /></el-icon>
          <span>Pending Actions</span>
        </template>
        <el-menu-item index="actions-processing">Processing Forms</el-menu-item>
      </el-sub-menu>
      
      <el-menu-item index="createForm">
        <el-icon><Plus /></el-icon>
        <span>Create Form</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { 
  HomeFilled, 
  Document, 
  Tickets, 
  Plus,
  Fold,
  Expand
} from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:collapsed']);

const isCollapse = computed({
  get: () => props.collapsed,
  set: (value) => emit('update:collapsed', value)
});

const activeMenu = computed(() => {
  const path = route.path;
  if (path === '/') return 'index';
  if (path.startsWith('/my-forms')) return 'myForms';
  if (path.startsWith('/for-my-action')) return 'actions';
  if (path === '/create-form') return 'createForm';
  return 'index';
});

const handleSelect = (index) => {
  if (index === 'index') {
    router.push('/');
  } else if (index === 'myForms-draft') {
    router.push('/my-forms?status=DRAFT');
  } else if (index === 'myForms-processing') {
    router.push('/my-forms?status=PROCESSING');
  } else if (index === 'myForms-completed') {
    router.push('/my-forms?status=COMPLETED');
  } else if (index === 'myForms-returned') {
    router.push('/my-forms?status=RETURNED');
  } else if (index === 'actions-processing') {
    router.push('/for-my-action?sortBy=DEPARTMENT');
  } else if (index === 'createForm') {
    router.push('/create-form');
  }
};

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};
</script>

<style scoped>
.tree-menu {
  height: 100vh;
  background-color: #545c64;
}

.tree-menu-container {
  height: 100%;
  border-right: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: #fff;
}

:deep(.el-menu-item.is-active),
:deep(.el-sub-menu .el-menu-item.is-active) {
  background-color: #001f3d !important;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #00152b !important;
}
</style>