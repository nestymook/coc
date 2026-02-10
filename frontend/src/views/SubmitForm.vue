<template>
  <div class="submit-form-container">
    <div class="page-header">
      <h2>Submit Form</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">Dashboard</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/my-forms', query: { status: 'DRAFT' } }">My Forms</el-breadcrumb-item>
        <el-breadcrumb-item>Submit Form</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <div class="form-content" v-loading="loading">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="Form Code">{{ form.code }}</el-descriptions-item>
        <el-descriptions-item label="Form Title">{{ form.title }}</el-descriptions-item>
        <el-descriptions-item label="Form Type">{{ form.formType }}</el-descriptions-item>
        <el-descriptions-item label="Status">
          <el-tag :type="getStatusType(form.status)">{{ getStatusText(form.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Creator">{{ form.creator?.username || 'N/A' }}</el-descriptions-item>
        <el-descriptions-item label="Department">{{ form.creatorDepartment || 'N/A' }}</el-descriptions-item>
        <el-descriptions-item label="Submission Date">{{ formatDate(form.submissionDate) }}</el-descriptions-item>
      </el-descriptions>
      
      <el-card class="margin-top" title="Family Members">
        <el-table
          :data="form.familyMembers"
          style="width: 100%"
        >
          <el-table-column prop="name" label="Name" width="150" />
          <el-table-column prop="relationship" label="Relationship" width="150" />
          <el-table-column prop="gender" label="Gender" width="100" />
          <el-table-column prop="birthDate" label="Birth Date" width="150">
            <template #default="{ row }">
              {{ formatDate(row.birthDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="idNumber" label="ID Number" />
        </el-table>
      </el-card>
      
      <el-card class="margin-top" title="Attachments">
        <el-table
          :data="form.attachments"
          style="width: 100%"
        >
          <el-table-column prop="filename" label="Filename" />
          <el-table-column prop="contentType" label="Content Type" width="150" />
          <el-table-column prop="size" label="Size" width="100">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      
      <el-card class="margin-top" title="Declaration">
        <p>I declare that the information provided in this form is true and complete to the best of my knowledge.</p>
        <p>Declaration Checked: {{ form.declarationChecked ? 'Yes' : 'No' }}</p>
      </el-card>
      
      <el-card class="margin-top" title="Other Information">
        <p>{{ form.otherInfo || 'No additional information' }}</p>
      </el-card>
      
      <el-card class="margin-top" title="Recommendation">
        <p>Recommended: {{ form.recommended ? 'Yes' : 'No' }}</p>
      </el-card>
      
      <div class="actions">
        <el-button type="primary" @click="handleConfirmSubmit">Confirm Submit</el-button>
        <el-button @click="handleCancel">Cancel</el-button>
      </div>
    </div>
    
    <el-dialog
      v-model="showConfirmDialog"
      title="Confirm Submission"
      width="500px"
    >
      <p>Are you sure you want to submit this form? Once submitted, you will not be able to edit it.</p>
      <template #footer>
        <el-button @click="showConfirmDialog = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useFormsStore } from '../store/forms';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const formsStore = useFormsStore();

const form = ref({});
const loading = ref(false);
const showConfirmDialog = ref(false);

const fetchForm = async () => {
  loading.value = true;
  try {
    const id = route.params.id;
    const result = await formsStore.fetchFormById(id);
    if (result) {
      form.value = { 
        ...result, 
        familyMembers: result.familyMembers || [] 
      };
    }
  } finally {
    loading.value = false;
  }
};

const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PROCESSING': 'warning',
    'COMPLETED': 'success',
    'RETURNED': 'danger'
  };
  return typeMap[status] || 'info';
};

const getStatusText = (status) => {
  const textMap = {
    'DRAFT': 'Draft',
    'PROCESSING': 'Processing',
    'COMPLETED': 'Completed',
    'RETURNED': 'Returned'
  };
  return textMap[status] || status;
};

const formatDate = (date) => {
  if (!date) return 'N/A';
  return new Date(date).toLocaleString('en-US', { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric', 
    hour: '2-digit', 
    minute: '2-digit' 
  });
};

const formatFileSize = (size) => {
  if (!size) return '0 B';
  const units = ['B', 'KB', 'MB', 'GB'];
  let index = 0;
  let fileSize = size;
  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024;
    index++;
  }
  return `${fileSize.toFixed(1)} ${units[index]}`;
};

const handleConfirmSubmit = () => {
  showConfirmDialog.value = true;
};

const handleSubmit = async () => {
  showConfirmDialog.value = false;
  loading.value = true;
  try {
    const id = route.params.id;
    const result = await formsStore.submitForm(id, form.value);
    if (result.success) {
      ElMessage.success('Form submitted successfully');
      router.push('/my-forms?status=PROCESSING');
    } else {
      ElMessage.error(result.message || 'Submission failed');
    }
  } catch (error) {
    ElMessage.error('Submission failed');
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  router.push('/my-forms?status=DRAFT');
};

onMounted(() => {
  fetchForm();
});
</script>

<style scoped>
.submit-form-container {
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

.form-content {
  max-width: 1000px;
}

.margin-top {
  margin-top: 20px;
}

.actions {
  margin-top: 20px;
}
</style>