import "reflect-metadata";
import {createApp} from 'vue'

// Element-plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'

// Bootstrap
import 'bootstrap/dist/css/bootstrap-utilities.css'

// normalize
import 'normalize.css'
import {createPinia} from "pinia";

const app = createApp(App)

app.use(createPinia())
app.use(ElementPlus)
app.use(router)

app.mount('#app')
