<script setup lang="ts">

import {container} from "tsyringe";
import CommentRepository from "@/respository/CommentRepository";
import {onMounted, reactive} from "vue";
import Paging from "@/entity/data/Paging";
import Comment from "@/entity/comment/Comment";
import CommentComponent from "@/components/CommentComponent.vue";
import CommentWrite from "@/entity/comment/CommentWrite";

const props = defineProps({
  postId: {
    type: [Number, String],
    required: true
  }
})

type StateType = {
  commentList: Paging<Comment>,
  commentWrite: CommentWrite
}
const COMMENT_REPOSITORY = container.resolve(CommentRepository)

const state = reactive<StateType>({
  commentList: new Paging<Comment>(),
  commentWrite: new CommentWrite()
})

onMounted(() => {
  getComments()
})

function getComments(page = 1) {
  COMMENT_REPOSITORY.getList(props.postId, page)
    .then((commentList) => {
      state.commentList = commentList
    })
}

function writeComment() {
  COMMENT_REPOSITORY.write(state.commentWrite, props.postId)
    .then(() => {
      state.commentWrite.content = ''
      getComments()
    })
}

function remove(...args) {
  const commentId = args[0].commentId;
  COMMENT_REPOSITORY.delete(commentId)
    .then((response) => {
      const statusCode = response.statusCode

      if (statusCode !== 'ERROR') {
        getComments()
      }
    })
}

</script>

<template>
  <div class="totalCount">댓글 수: {{ state.commentList.totalCount }}</div>
  <div class="write">
    <div class="form">
      <div class="content-wrap">
        <h2 class="content">댓글</h2>
        <el-input id="content" v-model="state.commentWrite.content" placeholder="내용을 입력하세요." type="textarea" :rows="5"
                  :autosize="{ minRows: 5, maxRows: 4 }"></el-input>
      </div>
    </div>

    <el-button type="primary" class="button" @click="writeComment()">등록하기</el-button>
  </div>

  <ul class="comments">
    <li class="comment" v-for="comment in state.commentList.items" :key="comment.id">
      <CommentComponent :comment="comment" @remove="remove"/>
    </li>
  </ul>
  <div class="d-flex justify-content-center">
    <el-pagination
      size="small"
      :background="true"
      v-model:current-page="state.commentList.page"
      layout="prev, pager, next"
      :default-page-size="7"
      @current-change="(page) => getComments(page)"
      :total="state.commentList.totalCount"/>
  </div>
</template>

<style scoped lang="scss">
.totalCount {
  font-size: 1.4rem;
}

.write {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .form {
    display: flex;
    gap: 12px;
    margin-top: 10px;

    .content-wrap {
      flex-grow: 1;

      .content {
        font-size: 0.88rem;
      }
    }
  }

  .button {
    width: 100px;
    align-self: flex-end;
  }
}

label {
  font-size: 0.78rem;
}

.comments {
  margin-top: 3rem;
  list-style: none;
  padding: 0;

  .comment {
    margin-bottom: 2.4rem;

    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>
