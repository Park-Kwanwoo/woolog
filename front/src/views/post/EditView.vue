<script setup lang="ts">

import {onMounted, reactive} from "vue";
import PostWrite from "@/entity/post/PostWrite";
import {container} from "tsyringe";
import PostRepository from "@/respository/PostRepository";
import router from "@/router";

const props = defineProps({
  postId: {
    type: {Number, String},
    required: true
  }
})

const POST_REPOSITORY = container.resolve(PostRepository)

type StateType = {
  postEdit: PostWrite
}

const state = reactive<StateType>({
  postEdit: new PostWrite()
})

onMounted(() => {
  POST_REPOSITORY.get(props.postId)
    .then((post) => {
      state.postEdit = post
    })
})

function edit() {
  POST_REPOSITORY.edit(state.postEdit, props.postId)
    .then(() => {
      router.replace(`/read/${props.postId}`)
    })
}

</script>

<template>
  <div class="mt-5">
    <el-input v-model="state.postEdit.title" readonly type="text" placeholder="제목을 입력해주세요."/>
  </div>

  <div class="mt-2">
    <el-input v-model="state.postEdit.content" type="textarea"></el-input>
  </div>

  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="edit()">수정완료</el-button>
  </div>
</template>

<style scoped lang="scss">

</style>
