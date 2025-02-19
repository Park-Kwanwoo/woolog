import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import WriteView from "@/views/post/WriteView.vue";
import ReadView from "@/views/post/ReadView.vue";
import EditView from "@/views/post/EditView.vue";
import LoginView from "@/views/LoginView.vue";
import MyPage from "@/views/member/MyPage.vue";
import SignUp from "@/views/member/SignUp.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: "/write",
      name: "write",
      component: WriteView
    },
    {
      path: "/read/:postId",
      name: "read",
      component: ReadView,
      props: true
    },
    {
      path: "/edit/:postId",
      name: "edit",
      component: EditView,
      props: true
    },
    {
      path: "/login",
      name: "login",
      component: LoginView,
      props: true
    },
    {
      path: "/mypage",
      name: "mypage",
      component: MyPage
    },
    {
      path: "/signup",
      name: "signup",
      component: SignUp
    }
  ],
})

export default router
