<template>
  <div class="processing-form-container">
    <div class="page-header">
      <h2>Processing Forms</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">Dashboard</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/my-forms', query: { status: 'PROCESSING' } }">My Forms</el-breadcrumb-item>
        <el-breadcrumb-item>Processing Forms</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span class="form-title">{{ form.title }}</span>
          <el-tag type="warning" size="small">Processing</el-tag>
        </div>
      </template>
      
      <div class="form-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Form ID">{{ form.id }}</el-descriptions-item>
          <el-descriptions-item label="Submitter">{{ form.creator?.username || 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="Submit Date">{{ form.submissionDate ? new Date(form.submissionDate).toLocaleString() : 'N/A' }}</el-descriptions-item>
          <el-descriptions-item label="Current Step">{{ form.status || 'N/A' }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="section">
          <h4>Family Members</h4>
          <el-table
            :data="form.familyMembers"
            style="width: 100%"
            size="small"
          >
            <el-table-column prop="name" label="Name" width="150" />
            <el-table-column prop="relationship" label="Relationship" width="150" />
          </el-table>
        </div>
        
        <div class="section">
          <h4>Attachments</h4>
          <el-table
            :data="form.attachments"
            style="width: 100%"
            size="small"
          >
            <el-table-column prop="fileName" label="File Name" width="200" />
            <el-table-column prop="fileSize" label="File Size" width="100" />
            <el-table-column label="Download" width="100">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleDownload(row)">Download</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="section">
          <h4>Comments</h4>
          <CommentsTable :comments="form.comments" />
        </div>
      </div>
    </el-card>
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

const form = ref({
  title: '',
  familyMembers: [],
  attachments: [],
  comments: []
});

const fetchForm = async () => {
  const id = route.params.id;
  console.log('Fetching form with ID:', id);
  if (!id || id === 'undefined') {
    console.error('Invalid form ID:', id);
    return;
  }
  const result = await formsStore.fetchFormById(id);
  if (result) {
    form.value = { 
      ...result, 
      familyMembers: result.familyMembers || [] 
    };
  }
};

const handleDownload = async (attachment) => {
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

onMounted(() => {
  fetchForm();
});
</script>

<style scoped>
.processing-form-container {
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

.form-card {
  max-width: 1000px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-title {
  font-size: 16px;
  font-weight: 500;
}

.section {
  margin-top: 20px;
}

.section h4 {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 10px;
  color: #666;
}
</style>