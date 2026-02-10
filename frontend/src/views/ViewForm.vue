<template>
  <div class="view-form-container">
    <div class="page-header">
      <h2>View Form</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">Dashboard</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/my-forms', query: { status: 'DRAFT' } }">My Forms</el-breadcrumb-item>
        <el-breadcrumb-item>View Form</el-breadcrumb-item>
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
        <el-descriptions-item label="Last Action Date">{{ formatDate(form.lastActionDate) }}</el-descriptions-item>
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
          <el-table-column label="Actions" width="100">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="downloadAttachment(row)">Download</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      
      <el-card class="margin-top" title="Comments">
        <CommentsTable :comments="form.comments" />
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
        <el-button @click="handleBack">Back to My Forms</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useFormsStore } from '../store/forms';
import { ElMessage } from 'element-plus';
import CommentsTable from '../components/CommentsTable.vue';
import api from '../api';

const route = useRoute();
const router = useRouter();
const formsStore = useFormsStore();

const form = ref({});
const loading = ref(false);

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

const downloadAttachment = async (attachment) => {
  try {
    const response = await api.get(`/upload/attachment/${attachment.id}`, {
      responseType: 'blob'
    });
    
    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', attachment.filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('Download failed:', error);
    ElMessage.error('Failed to download file');
  }
};

const handleBack = () => {
  router.push('/my-forms');
};

onMounted(() => {
  fetchForm();
});
</script>

<style scoped>
.view-form-container {
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