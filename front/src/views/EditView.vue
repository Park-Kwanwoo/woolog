<script setup lang="ts">

import axios from "axios";
import Post from "@/entity/Post";
import {reactive} from "vue";
import router from "@/router";

const props = defineProps({
  postId: {
    type: {Number, String},
    required: true
  }
})

const post = reactive<Post>({
  _id: Number,
  _title: "",
  _content: ""
})

axios.get<Post>(`/api/posts/${props.postId}`)
  .then(response => {
    post.id = response.data.id;
    post.title = response.data.title;
    post.content = response.data.content;
  })
  .catch(error => {
    console.log(error)
  })

const edit = () => {
  axios.patch(`/api/posts/${props.postId}`, post.valueOf())
    .then(() => {
      router.replace({name: 'read', params: {postId: props.postId}})
    })
}
</script>

<template>
  <div class="mt-5">
    <el-input v-model="post.title" type="text" placeholder="제목을 입력해주세요."/>
  </div>

  <div class="mt-2">
    <el-input v-model="post.content" type="textarea"></el-input>
  </div>

  <div class="mt-2 d-flex justify-content-end">
    <el-button type="primary" @click="edit()">수정완료</el-button>
  </div>
</template>

<style scoped lang="scss">

</style>
