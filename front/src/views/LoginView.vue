<script setup lang="ts">

import {reactive} from "vue";
import Login from "@/entity/member/Login";
import MemberRepository from "@/respository/MemberRepository";
import {container} from "tsyringe";
import {useTokenStore} from "@/stores/Token";

const state = reactive({
  login: new Login()
})
const MEMBER_REPOSITORY = container.resolve(MemberRepository);
function doLogin() {
  MEMBER_REPOSITORY.login(state.login);
}

</script>

<template>
  <el-row>
    <el-col :span="6" :offset="7">
      <el-form label-position="top">
        <el-form-item label="이메일">
          <el-input v-model="state.login.email"></el-input>
        </el-form-item>

        <el-form-item label="비밀번호">
          <el-input type="password" v-model="state.login.password"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="doLogin()">로그인</el-button>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<style scoped lang="scss">

</style>
