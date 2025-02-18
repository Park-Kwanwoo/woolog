<script setup lang="ts">

import {reactive} from "vue";
import PostWrite from "@/entity/post/PostWrite";
import {container} from "tsyringe";
import PostRepository from "@/respository/PostRepository";

const state = reactive({
  postWrite: new PostWrite()
})

const POST_REPOSITORY = container.resolve(PostRepository);


function write() {
  POST_REPOSITORY.write(state.postWrite)
}
</script>

<template>
  <div class="mt-5">
    <el-input v-model="state.postWrite.title" type="text" placeholder="제목을 입력해주세요."/>
  </div>

  <div class="mt-2">
    <el-input v-model="state.postWrite.content" type="textarea" rows="15" alt="내용"/>
  </div>

  <div class="mt-2">
    <div class="d-flex justify-content-end">
      <el-button type="primary" @click="write()">작성</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>
