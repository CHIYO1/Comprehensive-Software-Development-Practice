<template>
  <MainSideBar />
  <div style="height: 100%-60;background-color: #fafafa;">
    <router-view></router-view>

  </div>
</template>

<script setup>
import MainSideBar from '@/components/MainSideBar.vue';

</script>


<style scoped>
:deep(.el-dialog__footer) {
  padding-top: 0px;
}

:deep(.el-dialog__header) {
  padding-bottom: 0px;
}

:deep(.el-collapse-item__header:hover) {
  background-color: lightblue;
}
</style>