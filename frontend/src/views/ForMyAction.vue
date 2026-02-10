<template>
  <div class="for-my-action-container">
    <div class="page-header">
      <h2>Pending Actions</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">Dashboard</el-breadcrumb-item>
        <el-breadcrumb-item>Pending Actions</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="filter-bar">
      <el-radio-group v-model="sortBy" @change="fetchActions">
        <el-radio-button label="DEPARTMENT">By Department</el-radio-button>
        <el-radio-button label="MONTH">By Month</el-radio-button>
        <el-radio-button label="FORM">By Form</el-radio-button>
      </el-radio-group>
    </div>
    
    <FormTable
      :table-data="forms"
      :loading="loading"
      :show-actions="false"
      :pagination="pagination"
      @view="handleView"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { useFormsStore } from '../store/forms';
import FormTable from '../components/FormTable.vue';

const router = useRouter();

const authStore = useAuthStore();
const formsStore = useFormsStore();

const sortBy = ref('DEPARTMENT');
const forms = ref([]);
const loading = ref(false);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const fetchActions = async () => {
  loading.value = true;
  try {
    const result = await formsStore.fetchActionsList(sortBy.value);
    forms.value = result || [];
  } finally {
    loading.value = false;
  }
};

const handleView = (row) => {
  // Navigate to view page with form ID
  router.push(`/view-form/${row.id}`);
};

onMounted(() => {
  fetchActions();
});
</script>

<style scoped>
.for-my-action-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 500;
  color: #333;
  margin-bottom: 10px;
}

.filter-bar {
  margin-bottom: 20px;
}
</style>