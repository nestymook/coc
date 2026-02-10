<template>
  <div class="comments-table-container">
    <el-table
      :data="comments"
      style="width: 100%"
      size="small"
    >
      <el-table-column prop="content" label="Comment" min-width="300" />
      <el-table-column prop="authorName" label="Author" width="120" />
      <el-table-column prop="createdAt" label="Date" width="160" />
    </el-table>
  </div>
</template>

<script setup>
const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  }
});
</script>

<style scoped>
.comments-table-container {
  width: 100%;
}
</style>