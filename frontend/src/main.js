import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import en from 'element-plus/es/locale/lang/en';

import App from './App.vue';
import router from './router';
import { useAuthStore } from './store/auth';

const app = createApp(App);

// Register Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(createPinia());
app.use(router);

// Load user from localStorage on app start
const authStore = useAuthStore();
authStore.loadUser();

app.use(ElementPlus, {
  locale: en,
});

app.mount('#app');
