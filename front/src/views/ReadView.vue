<script setup lang="ts">

import axios from "axios";
import Post from "@/entity/Post";
import {reactive} from "vue";
import router from "@/router";

const props = defineProps({
  postId: {
    type: [Number, String],
    required: true
  }
})

const post = reactive<Post>({
  _id: Number,
  _title: "",
  _content: "",
});

axios.get<Post>(`/api/posts/${props.postId}`)
  .then(response => {
    post.id = response.data.id;
    post.title = response.data.title;
    post.content = response.data.content;
  })
  .catch(error => {
    console.log(error)
  })

const moveToEdit = () => {
  router.push({name: "edit", params: {postId: props.postId}})
}

</script>

<template>

  <el-row>
    <el-col>
      <h2 class="title">{{ post.title }}</h2>
    </el-col>
  </el-row>

  <el-row>
    <el-col>
      <div class="content">{{ post.content }}</div>
    </el-col>
  </el-row>

  <el-row>
    <el-col>
      <div class="d-flex justify-content-end">
        <el-button type="warning" @click="moveToEdit()">수정</el-button>
      </div>
    </el-col>
  </el-row>
</template>

<style scoped>
.title {
  font-size: 1.6rem;
  font-weight: 600;
}

.content {
  font-size: 0.9rem;
  margin-top: 8px;
  color: #7e7e7e;
  white-space: break-spaces;
  line-height: 1.5;
}
</style>
