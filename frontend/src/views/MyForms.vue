<template>
  <div class="my-forms-container">
    <div class="page-header">
      <h2>My Forms</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">Dashboard</el-breadcrumb-item>
        <el-breadcrumb-item>My Forms</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="filter-bar">
      <el-radio-group v-model="status" @change="handleStatusChange">
        <el-radio-button label="DRAFT">Draft</el-radio-button>
        <el-radio-button label="PROCESSING">Processing</el-radio-button>
        <el-radio-button label="COMPLETED">Completed</el-radio-button>
        <el-radio-button label="RETURNED">Returned</el-radio-button>
      </el-radio-group>
    </div>
    
    <FormTable
      :table-data="forms"
      :loading="loading"
      :show-actions="true"
      :pagination="pagination"
      @view="handleView"
      @edit="handleEdit"
      @submit="handleSubmit"
      @resubmit="handleResubmit"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { useFormsStore } from '../store/forms';
import FormTable from '../components/FormTable.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const formsStore = useFormsStore();

const status = ref(route.query.status || 'DRAFT');
const forms = ref([]);
const loading = ref(false);
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const fetchForms = async () => {
  loading.value = true;
  try {
    const userId = authStore.user?.id;
    if (userId) {
      const result = await formsStore.fetchMyForms(status.value, userId);
      forms.value = result || [];
    }
  } finally {
    loading.value = false;
  }
};

const handleStatusChange = () => {
  // Update route query parameter when status changes
  router.push({ query: { status: status.value } });
  fetchForms();
};

// Watch for route query parameter changes
watch(() => route.query.status, (newStatus) => {
  if (newStatus && newStatus !== status.value) {
    status.value = newStatus;
    fetchForms();
  }
});

const handleView = (row) => {
  // Navigate to view page with form ID
  router.push(`/view-form/${row.id}`);
};

const handleEdit = (row) => {
  // Navigate to edit page with form ID
  router.push(`/edit-form/${row.id}`);
};

const handleSubmit = (row) => {
  // Navigate to submit page with form ID
  router.push(`/submit-form/${row.id}`);
};

const handleResubmit = (row) => {
  // Navigate to resubmit page with form ID
  router.push(`/resubmit-form/${row.id}`);
};

onMounted(() => {
  fetchForms();
});
</script>

<style scoped>
.my-forms-container {
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