<template>
  <div class="resource-table-wrapper">
    <div class="resource-table-header">
      <el-form :inline="true" class="resource-table-form">
        <el-form-item label="资源名：">
          <el-input v-model="searchName" placeholder="请输入" clearable style="width: 240px" />
        </el-form-item>
        <el-form-item label="状态：">
          <el-select v-model="searchStatus" placeholder="请选择" style="width: 200px">
            <el-option label="全部" value="" />
            <el-option label="已通过" value="已通过" />
            <el-option label="未通过" value="未通过" />
            <el-option label="审核中" value="审核中" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary">查询</el-button>
          <el-button>重置</el-button>
          <el-button type="link">展开 <el-icon><el-icon-arrow-down /></el-icon></el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" class="create-btn">+ 新建</el-button>
    </div>
    <el-table :data="resources" border style="margin-top: 24px; width: 80%">
      <el-table-column prop="id" label="id" width="80" align="center" />
      <el-table-column prop="name" label="资源名" align="center" />
      <el-table-column prop="creator" label="创建人" align="center" />
      <el-table-column prop="createdAt" label="创建时间" align="center" />
      <el-table-column prop="status" label="审核状态" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.status === '已通过'" type="success" effect="light">已通过</el-tag>
          <el-tag v-else-if="row.status === '未通过'" type="danger" effect="light">未通过</el-tag>
          <el-tag v-else type="warning" effect="light">审核中</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template #default>
          <el-button type="link" size="small">编辑</el-button>
          <el-button type="link" size="small">查看</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="resource-table-pagination">
      <el-pagination
        background
        layout="prev, pager, next, sizes, jumper, total"
        :total="resources.length"
        :page-size="10"
        :current-page="1"
        :page-sizes="[10, 20, 50, 100]"
        style="margin-top: 16px;"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import { ArrowDown as ElIconArrowDown } from '@element-plus/icons-vue'

defineProps({
  resources: {
    type: Array,
    required: true
  }
})

const searchName = ref('')
const searchStatus = ref('')
</script>

<style scoped>
.resource-table-wrapper {
  background: #fff;
  border-radius: 8px;
  padding: 32px 24px 24px 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.resource-table-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  flex-wrap: wrap;
}
.resource-table-form {
  flex: 1;
  min-width: 600px;
}
.resource-table-pagination {
  display: flex;
  justify-content: flex-end;
}
.create-btn {
  margin-top: 8px;
}
</style> 