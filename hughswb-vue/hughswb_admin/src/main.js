import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import "./assets/css/index.css";
import "./assets/css/iconfont.css";
import config from "./assets/js/config";
import axios from "axios";
import VueAxios from "vue-axios";
import ECharts from "vue-echarts";
import "echarts/lib/chart/line";
import "echarts/lib/chart/pie";
import "echarts/lib/chart/bar";
import "echarts/lib/chart/map";
import "echarts/lib/component/tooltip";
import "echarts/lib/component/legend";
import "echarts/lib/component/title";
import mavonEditor from "mavon-editor";
import "mavon-editor/dist/css/index.css";
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import VueCalendarHeatmap from "vue-calendar-heatmap";
import dayjs from "dayjs";
import tagCloud from "./components/tag-cloud";
import './views/iconfont.css'
import './views/font.css'
import Vuesax from 'vuesax'
import 'vuesax/dist/vuesax.css'
import Avue from '@smallwei/avue';
import '@smallwei/avue/lib/index.css';

Vue.prototype.config = config;
Vue.use(mavonEditor);
Vue.use(tagCloud);
Vue.use(VueCalendarHeatmap);
Vue.component("v-chart", ECharts);
Vue.use(VueAxios, axios);
Vue.use(ElementUI);
Vue.use(Vuesax)
Vue.use(Avue);
Vue.config.productionTip = false;
Vue.prototype.$moment = dayjs;

Vue.filter("date", function(value, formatStr = "YYYY-MM-DD") {
  return dayjs(value).format(formatStr);
});

Vue.filter("dateTime", function(value, formatStr = "YYYY-MM-DD HH:mm:ss") {
  return dayjs(value).format(formatStr);
});

NProgress.configure({
  easing: "ease", // 动画方式
  speed: 500, // 递增进度条的速度
  showSpinner: false, // 是否显示加载ico
  trickleSpeed: 200, // 自动递增间隔
  minimum: 0.3 // 初始化时的最小百分比
});

router.beforeEach((to, from, next) => {
  NProgress.start();
  if (to.path == "/login") {
    next();
  } else if (!store.state.userId) {
    next({ path: "/login" });
  } else {
    next();
  }
});

router.afterEach(() => {
  NProgress.done();
});

// 响应拦截器
axios.interceptors.response.use(
  function(response) {
    switch (response.data.code) {
      case 40001:
        Vue.prototype.$message({
          type: "error",
          message: response.data.message
        });
        router.push({ path: "/login" });
        break;
      case 50000:
        Vue.prototype.$message({
          type: "error",
          message: response.data.message
        });
        break;
    }
    return response;
  },
  function(error) {
    return Promise.reject(error);
  }
);
// 请求拦截器
axios.interceptors.request.use(function (config) {
     //注意使用token的时候需要引入cookie方法或者用本地localStorage等方法，推荐js-cookie
       const token = localStorage.getItem('token');; //这里取token之前，你肯定需要先拿到token,存一下
       if (token) {
        config.headers.Authorization = token; //如果要求携带在请求头中
       }
      // 在发送请求之前做些什么
      return config;
    }, function (error) {
      // 对请求错误做些什么
      return Promise.reject(error);
    });
  
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
