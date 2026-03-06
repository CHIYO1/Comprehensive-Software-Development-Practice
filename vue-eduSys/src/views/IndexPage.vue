<template>
  <MainSideBar />
  <div style="height: 100%;background-color: #fafafa;">
    <router-view></router-view>
  </div>
  <HomeView v-if="isRouteActive"></HomeView>
  <!-- 悬浮球 -->
  <div class="floating-ball" @click="handleClick" @mouseenter="isHovering = true" @mouseleave="isHovering = false">
    <el-icon :size="24" :class="{ 'bounce': isHovering }">
      <QuestionFilled />
    </el-icon>
    <span v-if="isHovering" class="tooltip">"需要帮助吗？点我和学习助手聊天"</span>
  </div>
  <div style="pointer-events: none">

    <LearningAssistant v-model:visible="dialogVisible" />
  </div>
</template>

<script setup>
import MainSideBar from '@/components/MainSideBar.vue';
import { computed } from 'vue';
import { useRoute } from 'vue-router'
import HomeView from './HomeView.vue';

const route = useRoute()
const isRouteActive = computed(() => {
  return route.name === "index";
})

// 悬浮球
import { ref } from 'vue'
import { QuestionFilled } from '@element-plus/icons-vue'
import LearningAssistant from '@/components/gpt/LearningAssistant.vue'
const dialogVisible = ref(false);

const isHovering = ref(false)

const handleClick = () => {
  dialogVisible.value = !dialogVisible.value;
}
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

.floating-ball {
  position: fixed;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  background-color: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 9999;
  transition: all 0.3s ease;
  color: white;
}

.floating-ball:hover {
  background-color: #79bbff;
  box-shadow: 0 2px 20px 0 rgba(64, 158, 255, 0.5);
}

.tooltip {
  position: absolute;
  right: 60px;
  white-space: nowrap;
  background-color: #303133;
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(10px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.bounce {
  animation: bounce 0.5s infinite alternate;
}

@keyframes bounce {
  from {
    transform: translateY(0);
  }

  to {
    transform: translateY(-5px);
  }
}
</style>