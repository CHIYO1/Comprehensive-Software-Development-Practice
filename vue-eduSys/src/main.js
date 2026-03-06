import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus';
import '../node_modules/element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'; // 导入中文语言包
import VueECharts from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart, RadarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent, ToolboxComponent } from 'echarts/components'

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  RadarChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  ToolboxComponent
])

const app = createApp(App);
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate) // 持久化插件
app.use(pinia);
app.use(router);
app.use(ElementPlus, {
    locale: zhCn,
});
app.component('v-chart', VueECharts)
app.mount('#app');