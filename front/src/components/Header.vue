<template>
  <el-header class="header">
    <el-menu mode="horizontal" router>
      <el-menu-item index="/">Home</el-menu-item>
      <el-menu-item v-if="isAuthenticated" index="/write">글 작성</el-menu-item>
      <el-menu-item v-if="!isAuthenticated" index="/login">로그인</el-menu-item>
      <el-menu-item v-if="isAuthenticated" index="/login" @click="logout()">로그아웃</el-menu-item>
    </el-menu>
  </el-header>
</template>

<style scoped>
.header {
  padding: 0;
  height: 60px;
}
</style>
<script setup lang="ts">

import {useTokenStore} from "@/stores/Token";
import {storeToRefs} from "pinia";
import {container} from "tsyringe";
import MemberRepository from "@/respository/MemberRepository";

const tokenStore = useTokenStore()
const { isAuthenticated } = storeToRefs(tokenStore)

const MEMBER_REPOSITORY = container.resolve(MemberRepository)

function logout() {
  MEMBER_REPOSITORY.logout()
    .then((response) => {
      tokenStore.deleteToken()
      // 쿠키 삭제 vue-cookie 였나
      localStorage.setItem('token', null)
    });
}

</script>
