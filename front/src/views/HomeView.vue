<script setup lang="ts">
import axios from "axios";
import {ref} from "vue";

const posts = ref([])

axios.get("/api/posts?page=1&size=10")
  .then(response => {
    response.data.forEach((r) => {
      posts.value.push(r)
    })
  })

</script>

<template>
  <ul>
    <li v-for="post in posts" :key="post.id">
      <div class="title">
        <router-link :to="{name: 'read', params:{postId: post.id}}">{{ post.title }}</router-link>
      </div>
      <div class="content">
        {{ post.content }}
      </div>
    </li>
  </ul>
</template>

<style scoped lang="scss">
ul {
  list-style: none;
  padding: 0;

  li {
    margin-bottom: 1.3rem;

    .title {
      font-size: 1.2rem;
      a {
        text-decoration: none;
      }

      &:hover {
        text-decoration: underline;
      }
    }

    .content {
      font-size: 0.95rem;
      margin-top: 8px;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }
}


</style>
