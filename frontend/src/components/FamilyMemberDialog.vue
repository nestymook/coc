<template>
  <el-dialog
    v-model="dialog"
    :title="isEditing ? 'Edit Family Member' : 'Add Family Member'"
    width="600px"
  >
    <el-form
      ref="formRef"
      :model="familyMember"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="Name" prop="name">
        <el-input v-model="familyMember.name" placeholder="Please enter name" />
      </el-form-item>
      <el-form-item label="Gender" prop="gender">
        <el-select v-model="familyMember.gender" placeholder="Please select gender" style="width: 100%">
          <el-option label="Male" value="Male" />
          <el-option label="Female" value="Female" />
        </el-select>
      </el-form-item>
      <el-form-item label="ID Number" prop="idNumber">
        <el-input v-model="familyMember.idNumber" placeholder="Please enter ID number" />
      </el-form-item>
      <el-form-item label="Relationship" prop="relationship">
        <el-input v-model="familyMember.relationship" placeholder="Please enter relationship" />
      </el-form-item>
      <el-form-item label="Birth Date" prop="birthDate">
        <el-date-picker
          v-model="familyMember.birthDate"
          type="date"
          placeholder="Please select birth date"
          style="width: 100%"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="close">Cancel</el-button>
      <el-button type="primary" @click="handleSave">Save</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';

const dialog = ref(false);
const isEditing = ref(false);
const formRef = ref(null);

const familyMember = ref({
  name: '',
  gender: 'Male',
  idNumber: '',
  relationship: '',
  birthDate: ''
});

const rules = reactive({
  name: [
    { required: true, message: 'Please enter name', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: 'Please select gender', trigger: 'change' }
  ],
  idNumber: [
    { required: true, message: 'Please enter ID number', trigger: 'blur' }
  ],
  relationship: [
    { required: true, message: 'Please enter relationship', trigger: 'blur' }
  ],
  birthDate: [
    { required: true, message: 'Please select birth date', trigger: 'change' }
  ]
});

const emit = defineEmits(['save', 'close']);

const open = (member = null) => {
  if (member) {
    isEditing.value = true;
    familyMember.value = { ...member };
    if (member.birthDate) {
      familyMember.value.birthDate = new Date(member.birthDate).toISOString().split('T')[0];
    }
  } else {
    isEditing.value = false;
    familyMember.value = {
      name: '',
      gender: 'Male',
      idNumber: '',
      relationship: '',
      birthDate: new Date().toISOString().split('T')[0]
    };
  }
  dialog.value = true;
};

const close = () => {
  dialog.value = false;
  emit('close');
};

const handleSave = () => {
  if (!formRef.value) return;
  
  formRef.value.validate((valid) => {
    if (valid) {
      // Emit save event first, then close dialog
      const memberData = { ...familyMember.value };
      emit('save', memberData);
      close();
    } else {
      ElMessage.error('Please fill in all required fields');
    }
  });
};

defineExpose({
  open
});
</script>

<style scoped>
</style>