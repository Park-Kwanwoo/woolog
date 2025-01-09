import "reflect-metadata";
import {createApp} from 'vue'

// Element-plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'

// Bootstrap
import 'bootstrap/dist/css/bootstrap-utilities.css'

// pinia
import persistedState from 'pinia-plugin-persistedstate'

// normalize
import 'normalize.css'
import {createPinia} from "pinia";

const app = createApp(App)
const pinia = createPinia()
pinia.use(persistedState)
app.use(pinia)

app.use(ElementPlus)
app.use(router)

app.mount('#app')
