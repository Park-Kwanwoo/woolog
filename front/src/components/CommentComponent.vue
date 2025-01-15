<script setup lang="ts">

import Comment from "@/entity/comment/Comment";

const props = defineProps({
  comment: Comment,
})

const emit = defineEmits(['remove'])

function remove(commentId) {
  emit('remove', { commentId })
}
</script>

<template>

  <div class="content">
    <el-text readonly="readonly">{{props.comment.content}}</el-text>
  </div>

  <div class="commentInfo"> {{ props.comment.nickname }} | {{ props.comment.getDisplaySimpleCreatedAt() }}</div>
  <div class="remove" @click="remove(props.comment?.id)">삭제</div>
</template>

<style scoped>

.content {
  font-size: 1.8rem;
  font-weight: 400;
  text-align: left;
}

.commentInfo {
  margin-top: 0.5rem;
  font-size: 0.78rem;
  font-weight: 300;
  text-align: right;
}

.remove {
  font-size: 0.65rem;
  text-align: right;
  color: red;
  &:hover {
    text-decoration: underline;
  }
}
</style>
