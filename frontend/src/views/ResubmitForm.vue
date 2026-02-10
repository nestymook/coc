<template>
  <div class="resubmit-form-container">
    <div class="page-header">
      <h2>Resubmit Form</h2>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">Dashboard</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/my-forms', query: { status: 'RETURNED' } }">My Forms</el-breadcrumb-item>
        <el-breadcrumb-item>Resubmit Form</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    
    <el-alert
      type="warning"
      title="Notice"
      description="This form has been returned. Please modify it based on the return reason and resubmit."
      show-icon
      style="margin-bottom: 20px"
    />
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="form-content"
    >
      <el-form-item label="Form Title" prop="title">
        <el-input v-model="form.title" placeholder="Please enter form title" />
      </el-form-item>
      
      <el-form-item label="Family Members">
        <el-button type="primary" size="small" @click="openFamilyMemberDialog">
          Add Member
        </el-button>
        
        <el-table
          :data="form.familyMembers"
          style="width: 100%; margin-top: 10px"
          size="small"
        >
          <el-table-column prop="name" label="Name" width="150">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" placeholder="Name" />
            </template>
          </el-table-column>
          <el-table-column prop="gender" label="Gender" width="100">
            <template #default="{ row }">
              <span>{{ row.gender }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="idNumber" label="ID Number" width="150">
            <template #default="{ row }">
              <span>{{ row.idNumber }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="relationship" label="Relationship" width="150">
            <template #default="{ row }">
              <el-input v-model="row.relationship" size="small" placeholder="Relationship" />
            </template>
          </el-table-column>
          <el-table-column prop="birthDate" label="Birth Date" width="120">
            <template #default="{ row }">
              <span>{{ formatDate(row.birthDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="150">
            <template #default="{ row, $index }">
              <el-button
                type="primary"
                size="small"
                @click="editFamilyMember(row, $index)"
              >
                Edit
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="removeFamilyMember($index)"
              >
                Delete
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      
      <el-form-item label="Attachments">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :show-file-list="true"
          :file-list="attachmentList"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          multiple
          limit="5"
          :on-exceed="handleExceed"
          :http-request="handleUploadRequest"
        >
          <el-button type="primary">Click to Upload</el-button>
        </el-upload>
        <div class="upload-hint">Supports uploading up to 5 files</div>
        
        <div v-if="form.attachments && form.attachments.length > 0" style="margin-top: 10px">
          <h4>Uploaded Files:</h4>
          <ul>
            <li v-for="att in form.attachments" :key="att.id">
              {{ att.filename }} ({{ formatFileSize(att.size) }})
            </li>
          </ul>
        </div>
      </el-form-item>
      
      <el-form-item label="Return Reason">
        <el-input
          v-model="returnReason"
          type="textarea"
          :rows="3"
          readonly
          placeholder="Return reason for this form"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">Resubmit</el-button>
        <el-button @click="handleSaveDraft">Save Draft</el-button>
        <el-button @click="handleCancel">Cancel</el-button>
      </el-form-item>
    </el-form>
    
    <!-- Family Member Dialog -->
    <FamilyMemberDialog
      ref="familyMemberDialogRef"
      @save="handleFamilyMemberSave"
      @close="handleFamilyMemberClose"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '../store/auth';
import { useFormsStore } from '../store/forms';
import { ElMessage } from 'element-plus';
import FamilyMemberDialog from '../components/FamilyMemberDialog.vue';
import api from '../api';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const formsStore = useFormsStore();

const formRef = ref(null);
const familyMemberDialogRef = ref(null);
const editingFamilyMemberIndex = ref(-1);
const uploadRef = ref(null);
const attachmentList = ref([]);

const form = ref({
  title: '',
  familyMembers: [],
  attachments: []
});
const returnReason = ref('');

const rules = {
  title: [
    { required: true, message: 'Please enter form title', trigger: 'blur' },
    { min: 1, max: 100, message: 'Title length must be between 1 and 100 characters', trigger: 'blur' }
  ]
};

const fetchForm = async () => {
  const id = route.params.id;
  const result = await formsStore.fetchFormById(id);
  if (result) {
    form.value = { 
      ...result, 
      familyMembers: result.familyMembers || [],
      attachments: result.attachments || []
    };
    // Set attachment list for el-upload
    attachmentList.value = (result.attachments || []).map(att => ({
      name: att.filename,
      url: att.path
    }));
    returnReason.value = result.returnReason || 'No return reason';
  }
};

const openFamilyMemberDialog = () => {
  editingFamilyMemberIndex.value = -1;
  familyMemberDialogRef.value?.open();
};

const editFamilyMember = (member, index) => {
  editingFamilyMemberIndex.value = index;
  familyMemberDialogRef.value?.open(member);
};

const handleFamilyMemberSave = (member) => {
  if (editingFamilyMemberIndex.value === -1) {
    // Add new member
    form.value.familyMembers.push(member);
  } else {
    // Update existing member
    form.value.familyMembers[editingFamilyMemberIndex.value] = member;
  }
};

const handleFamilyMemberClose = () => {
  editingFamilyMemberIndex.value = -1;
};

const removeFamilyMember = (index) => {
  form.value.familyMembers.splice(index, 1);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
};

const formatFileSize = (size) => {
  if (!size) return '0 bytes';
  if (size < 1024) return size + ' bytes';
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB';
  return (size / (1024 * 1024)).toFixed(2) + ' MB';
};

const handleFileChange = (file, fileList) => {
  console.log('File changed:', file);
};

const handleFileRemove = (file, fileList) => {
  console.log('File removed:', file);
};

const handleExceed = () => {
  ElMessage.warning('Maximum 5 files can be uploaded');
};

// Custom upload handler
const handleUploadRequest = async (options) => {
  const { file, onError, onSuccess } = options;
  
  try {
    const formId = route.params.id;
    
    // Upload file
    const formData = new FormData();
    formData.append('file', file);
    formData.append('formId', formId);
    formData.append('filename', file.name);
    
    const response = await api.post('/upload/attachment', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    
    if (response.data.success) {
      // Add attachment to form
      form.value.attachments.push(response.data.attachment);
      // Update upload list
      attachmentList.value.push({
        name: file.name,
        url: response.data.attachment.path
      });
      onSuccess(response.data);
    } else {
      onError(response.data.message || 'Upload failed');
    }
  } catch (error) {
    onError(error.response?.data?.message || 'Upload failed');
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const id = route.params.id;
        const result = await formsStore.resubmitForm(id, form.value);
        if (result.success) {
          ElMessage.success('Form resubmitted successfully');
          router.push('/my-forms?status=PROCESSING');
        } else {
          ElMessage.error(result.message || 'Submission failed');
        }
      } catch (error) {
        ElMessage.error('Submission failed');
      }
    }
  });
};

const handleSaveDraft = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const id = route.params.id;
        const result = await formsStore.saveDraft(id, form.value);
        if (result.success) {
          ElMessage.success('Draft saved successfully');
          router.push('/my-forms?status=DRAFT');
        } else {
          ElMessage.error(result.message || 'Save failed');
        }
      } catch (error) {
        ElMessage.error('Save failed');
      }
    }
  });
};

const handleCancel = () => {
  router.push('/my-forms?status=RETURNED');
};

onMounted(() => {
  fetchForm();
});
</script>

<style scoped>
.resubmit-form-container {
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
  max-width: 800px;
}

.upload-hint {
  margin-top: 10px;
  color: #999;
  font-size: 12px;
}
</style>