<template>
  <div class="visual-page">
    <el-row :gutter="24">
      <el-col :span="12">
        <el-card class="visual-card" shadow="hover">
          <div class="visual-title">教师活跃度统计</div>
          <v-chart :option="teacherActiveOption" autoresize style="height:320px" />
          <div class="ai-analysis">
            <div class="ai-title">AI分析</div>
            <div class="ai-content">本周教师活跃度高峰出现在周三，建议在高峰时段推送教研活动，提升整体参与度。周末活跃度较低，可考虑增加激励机制。</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="visual-card" shadow="hover">
          <div class="visual-title">学生活跃度统计</div>
          <v-chart :option="studentActiveOption" autoresize style="height:320px" />
          <div class="ai-analysis">
            <div class="ai-title">AI分析</div>
            <div class="ai-content">学生活跃度周末明显下降，建议增加周末趣味活动或作业激励，提升学习积极性。工作日活跃度稳定，学习氛围良好。</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="24" style="margin-top: 24px;">
      <el-col :span="12">
        <el-card class="visual-card" shadow="hover">
          <div class="visual-title">教学效率指数</div>
          <v-chart :option="efficiencyOption" autoresize style="height:320px" />
          <div class="ai-analysis">
            <div class="ai-title">AI建议</div>
            <div class="ai-content">部分学科备课耗时偏高，建议优化备课模板，提升整体教学效率。关注通过率持续偏低的学科，适时调整教学策略。</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="visual-card" shadow="hover">
          <div class="visual-title">学生学习效果分析</div>
          <v-chart :option="studentEffectOption" autoresize style="height:320px" />
          <div class="ai-analysis">
            <div class="ai-title">AI建议</div>
            <div class="ai-content">平均正确率呈上升趋势，但高频错误知识点集中在"函数应用"，建议针对性补充练习。整体学习效果良好，继续保持。</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 教师活跃度（柱状+折线）
const teacherActiveOption = ref({
  tooltip: { 
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  legend: { 
    data: ['使用次数', '活跃教师数'],
    top: 10
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: { 
    type: 'category', 
    data: ['周一','周二','周三','周四','周五','周六','周日'],
    axisLabel: {
      color: '#666'
    }
  },
  yAxis: [
    { 
      type: 'value',
      name: '使用次数',
      axisLabel: {
        color: '#666'
      }
    },
    { 
      type: 'value',
      name: '活跃教师数',
      axisLabel: {
        color: '#666'
      }
    }
  ],
  series: [
    { 
      name: '使用次数', 
      type: 'bar', 
      data: [120, 132, 201, 134, 90, 30, 20], 
      barWidth: 32,
      itemStyle: {
        color: '#409EFF'
      }
    },
    { 
      name: '活跃教师数', 
      type: 'line', 
      yAxisIndex: 1,
      data: [10, 12, 20, 15, 8, 3, 2], 
      smooth: true,
      itemStyle: {
        color: '#67C23A'
      },
      lineStyle: {
        color: '#67C23A'
      }
    }
  ]
})

// 学生活跃度（柱状+折线）
const studentActiveOption = ref({
  tooltip: { 
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  legend: { 
    data: ['使用次数', '活跃学生数'],
    top: 10
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: { 
    type: 'category', 
    data: ['周一','周二','周三','周四','周五','周六','周日'],
    axisLabel: {
      color: '#666'
    }
  },
  yAxis: [
    { 
      type: 'value',
      name: '使用次数',
      axisLabel: {
        color: '#666'
      }
    },
    { 
      type: 'value',
      name: '活跃学生数',
      axisLabel: {
        color: '#666'
      }
    }
  ],
  series: [
    { 
      name: '使用次数', 
      type: 'bar', 
      data: [320, 332, 401, 334, 390, 120, 80], 
      barWidth: 32,
      itemStyle: {
        color: '#E6A23C'
      }
    },
    { 
      name: '活跃学生数', 
      type: 'line', 
      yAxisIndex: 1,
      data: [50, 52, 60, 55, 48, 20, 10], 
      smooth: true,
      itemStyle: {
        color: '#F56C6C'
      },
      lineStyle: {
        color: '#F56C6C'
      }
    }
  ]
})

// 教学效率指数（雷达图）
const efficiencyOption = ref({
  tooltip: {},
  legend: { 
    data: ['平均耗时(分钟)'],
    top: 10
  },
  radar: {
    indicator: [
      { name: '备课耗时', max: 120 },
      { name: '修正耗时', max: 120 },
      { name: '练习设计耗时', max: 120 },
      { name: '练习修正耗时', max: 120 },
      { name: '通过率', max: 100 }
    ],
    radius: '65%',
    center: ['50%', '55%']
  },
  series: [
    {
      name: '教学效率',
      type: 'radar',
      data: [
        { 
          value: [80, 60, 70, 50, 60], 
          name: '平均耗时(分钟)',
          itemStyle: {
            color: '#409EFF'
          },
          areaStyle: {
            color: 'rgba(64, 158, 255, 0.2)'
          }
        }
      ]
    }
  ]
})

// 学生学习效果（折线+饼图）
const studentEffectOption = ref({
  tooltip: { 
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  legend: { 
    data: ['平均正确率'],
    top: 10
  },
  grid: {
    left: '3%',
    right: '25%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: { 
    type: 'category', 
    data: ['第1周','第2周','第3周','第4周','第5周'],
    axisLabel: {
      color: '#666'
    }
  },
  yAxis: { 
    type: 'value', 
    min: 0, 
    max: 100, 
    axisLabel: { 
      formatter: '{value}%',
      color: '#666'
    } 
  },
  series: [
    { 
      name: '平均正确率', 
      type: 'line', 
      data: [60, 65, 70, 75, 80], 
      smooth: true, 
      areaStyle: {
        color: 'rgba(103, 194, 58, 0.2)'
      },
      itemStyle: {
        color: '#67C23A'
      },
      lineStyle: {
        color: '#67C23A'
      }
    },
    { 
      name: '高频错误知识点', 
      type: 'pie',
      radius: ['40%', '60%'],
      center: ['80%', '30%'],
      label: { 
        formatter: '{b}: {d}%',
        color: '#666'
      },
      data: [
        { value: 40, name: '函数应用', itemStyle: { color: '#F56C6C' } },
        { value: 30, name: '几何证明', itemStyle: { color: '#E6A23C' } },
        { value: 20, name: '概率统计', itemStyle: { color: '#409EFF' } },
        { value: 10, name: '数列极限', itemStyle: { color: '#67C23A' } }
      ]
    }
  ]
})
</script>

<style scoped>
.visual-page {
  padding: 32px 16px;
  background-color: #fafafa;
  min-height: calc(100vh - 60px);
}

.visual-card {
  margin-bottom: 24px;
  min-height: 420px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.visual-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #303133;
  padding: 0 8px;
}

.ai-analysis {
  margin-top: 18px;
  background: #f6faff;
  border-left: 4px solid #409eff;
  padding: 12px 18px;
  border-radius: 6px;
  margin: 18px 8px 0 8px;
}

.ai-title {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 4px;
  font-size: 14px;
}

.ai-content {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .visual-page {
    padding: 16px 8px;
  }
  
  .visual-card {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .visual-page {
    padding: 8px 4px;
  }
  
  .visual-title {
    font-size: 16px;
  }
  
  .ai-content {
    font-size: 13px;
  }
}
</style> 