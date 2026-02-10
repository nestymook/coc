<template>
  <div class="form-table-container">
    <el-table
      :data="tableData"
      :loading="loading"
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="id" label="Form ID" width="100" />
      <el-table-column prop="title" label="Form Title" min-width="200" />
      <el-table-column label="Submitter" width="120">
        <template #default="{ row }">
          {{ row.creator?.username || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="submissionDate" label="Submit Date" width="160">
        <template #default="{ row }">
          {{ row.submissionDate ? new Date(row.submissionDate).toLocaleString() : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="Status" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="200" v-if="showActions">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="$emit('view', row)"
          >
            View
          </el-button>
          <el-button
            v-if="row.status === 'DRAFT'"
            type="warning"
            size="small"
            @click="$emit('edit', row)"
          >
            Edit
          </el-button>
          <el-button
            v-if="row.status === 'DRAFT'"
            type="success"
            size="small"
            @click="$emit('submit', row)"
          >
            Submit
          </el-button>
          <el-button
            v-if="row.status === 'RETURNED'"
            type="warning"
            size="small"
            @click="$emit('resubmit', row)"
          >
            Resubmit
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-if="pagination"
      v-model:current-page="pagination.currentPage"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      :page-sizes="[10, 20, 30, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  tableData: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  showActions: {
    type: Boolean,
    default: true
  },
  pagination: {
    type: Object,
    default: () => ({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })
  }
});

const emit = defineEmits(['view', 'edit', 'submit', 'resubmit', 'sizeChange', 'currentChange']);

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

const handleSizeChange = (size) => {
  emit('sizeChange', size);
};

const handleCurrentChange = (page) => {
  emit('currentChange', page);
};
</script>

<style scoped>
.form-table-container {
  width: 100%;
}
</style>