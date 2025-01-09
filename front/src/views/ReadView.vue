<script setup lang="ts">

import {onMounted, reactive} from "vue";
import {container} from "tsyringe";
import PostRepository from "@/respository/PostRepository";
import Post from "@/entity/post/Post";
import Comments from "@/components/Comments.vue";
import Paging from "@/entity/data/Paging";
import Comment from "@/entity/comment/Comment";
import {ElMessageBox} from "element-plus";
import router from "@/router";

const props = defineProps({
  postId: {
    type: [Number, String],
    required: true
  }
})

const POST_REPOSITORY = container.resolve(PostRepository)

type StateType = {
  post: Post,
  commentList: Paging<Comment>
}

const state = reactive<StateType>({
  post: new Post(),
  commentList: new Paging<Comment>()
})

function getPost() {
  POST_REPOSITORY.get(props.postId)
    .then((post: Post) => {
      state.post = post
    })
    .catch((e) => {
      console.error(e)
    })
}

onMounted(() => {
  getPost()
})

function edit() {

}

function remove() {
  ElMessageBox.confirm('정말로 삭제하시겠습니까?', 'Warning', {
    title: '삭제',
    cancelButtonText: '취소',
    confirmButtonText: '삭제',
    type: 'warning',
  })
    .then(() => {
      POST_REPOSITORY.delete(props.postId)
        .then(() => {
          router.replace("/")
        })
    })
}

</script>

<template>

  <el-row>
    <el-col :span="15" :offset="5">
      <div>
        <div class="title"> {{ state.post.title }}</div>
        <div class="create_at"> {{state.post.nickname}} | {{ state.post?.getDisplayCreatedAt() }}</div>
      </div>
    </el-col>
  </el-row>
  <el-row>
    <el-col :span="15" :offset="5">
      <div class="content">
        {{ state.post?.content }}
      </div>

      <div class="footer">
        <div class="edit"><router-link :to="{name: 'edit', params: {postId: props.postId} }">수정</router-link></div>
        <div class="delete" @click="remove()">삭제</div>
      </div>
    </el-col>
  </el-row>

  <el-row class="comments">
    <el-col :span="15" :offset="5">
      <Comments :postId="props.postId"/>
    </el-col>
  </el-row>
</template>

<style scoped lang="scss">

.title {
  font-size: 1.8rem;
  font-weight: 400;
}

.create_at {
  margin-top: 0.5rem;
  font-size: 0.78rem;
  font-weight: 300;
}

.content {
  margin-top: 1.88rem;
  font-weight: 300;

  word-break: break-all;
  white-space: break-spaces;
  line-height: 1.4;
  min-height: 5rem;
}

.footer {
  margin-top: 1rem;
  display: flex;
  font-size: 0.78rem;
  justify-content: flex-end;
  gap: 0.8rem;

  .edit {
    &:hover {
      text-decoration: underline;
    }
  }

  .delete {
    color: red;
    &:hover {
      text-decoration: underline;
    }
  }
}

.comments {
  margin-top: 4.8rem;
}
</style>
