<script setup lang="ts">

import {ref} from "vue";
import axios from "axios";
import router from "@/router";

const title = ref("")
const content = ref("")

const write = function () {
  axios.post("/api/posts", {
    title: title.value,
    content: content.value
  })
    .then(() => {
      router.replace({name: "home"})
    })
}


</script>

<template>
  <div class="mt-5">
    <el-input v-model="title" type="text" placeholder="제목을 입력해주세요."/>
  </div>

  <div class="mt-2">
    <el-input v-model="content" type="textarea"></el-input>
  </div>

  <div class="mt-2">
    <div class="d-flex justify-content-end">
      <el-button type="primary" @click="write()">작성</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>
