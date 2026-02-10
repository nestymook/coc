<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h2>Dashboard</h2>
    </div>
    
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #409eff">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalForms }}</div>
            <div class="stat-label">Total Forms</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #67c23a">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.draftForms }}</div>
            <div class="stat-label">Draft Forms</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #e6a23c">
            <el-icon><Tickets /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.processingForms }}</div>
            <div class="stat-label">Processing Forms</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon" style="background: #f56c6c">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.returnedForms }}</div>
            <div class="stat-label">Returned Forms</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="recent-actions">
      <el-card>
        <template #header>
          <span class="card-title">Recent Actions</span>
        </template>
        
        <el-table
          :data="recentActions"
          style="width: 100%"
          size="small"
        >
          <el-table-column prop="formTitle" label="Form Title" min-width="200" />
          <el-table-column prop="action" label="Action" width="100">
            <template #default="{ row }">
              <el-tag size="small">{{ row.action }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="actionDate" label="Action Date" width="160" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '../store/auth';
import { useFormsStore } from '../store/forms';
import { Document, Tickets } from '@element-plus/icons-vue';

const authStore = useAuthStore();
const formsStore = useFormsStore();

const stats = ref({
  totalForms: 0,
  draftForms: 0,
  processingForms: 0,
  returnedForms: 0
});

const recentActions = ref([]);

const fetchStats = async () => {
  const userId = authStore.user?.id;
  if (userId) {
    const draftForms = await formsStore.fetchMyForms('DRAFT', userId);
    const processingForms = await formsStore.fetchMyForms('PROCESSING', userId);
    const completedForms = await formsStore.fetchMyForms('COMPLETED', userId);
    const returnedForms = await formsStore.fetchMyForms('RETURNED', userId);
    
    stats.value = {
      totalForms: draftForms.length + processingForms.length + completedForms.length + returnedForms.length,
      draftForms: draftForms.length,
      processingForms: processingForms.length,
      returnedForms: returnedForms.length
    };
  }
};

const fetchRecentActions = async () => {
  // Fetch recent actions for the user
  recentActions.value = [
    { formTitle: 'Leave Application', action: 'Submit', actionDate: '2024-01-15 10:30' },
    { formTitle: 'Business Trip Application', action: 'Return', actionDate: '2024-01-14 16:45' },
    { formTitle: 'Purchase Application', action: 'Complete', actionDate: '2024-01-13 09:20' }
  ];
};

onMounted(() => {
  fetchStats();
  fetchRecentActions();
});
</script>

<style scoped>
.dashboard-container {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.recent-actions {
  margin-bottom: 20px;
}

.card-title {
  font-size: 14px;
  font-weight: 500;
}
</style>