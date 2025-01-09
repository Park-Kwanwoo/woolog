<script setup lang="ts">
import Post from "@/entity/post/Post";

const props = defineProps({
  post: Post
})

</script>

<template>

  <div class="title">
    <router-link :to="{name: 'read', params: { postId: props.post?.id }}"> {{ props.post.title }}</router-link>
  </div>

  <div class="create_at">Posted on {{ props.post.getDisplaySimpleCreatedAt() }}</div>

  <div class="nickname"> {{ props.post.nickname }}</div>

</template>

<style scoped>
.title {
  a {
    color: inherit;
    text-decoration: none;
    font-size: 1.4rem;
    font-weight: 400;

    &:hover {
      text-decoration: underline;
    }
  }
}

.create_at {
  margin-top: 0.5rem;
  font-size: 0.78rem;
  font-weight: 300;
}

.nickname {
  margin-top: 0.5rem;
  font-size: 0.78rem;
  font-weight: 300;
}
</style>
