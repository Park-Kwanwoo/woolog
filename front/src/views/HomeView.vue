<script setup lang="ts">
import {onMounted, reactive} from "vue";
import {container} from "tsyringe";
import PostRepository from "@/respository/PostRepository";
import PostComponent from "@/components/PostComponent.vue";
import Paging from "@/entity/data/Paging";
import type Post from "@/entity/post/Post";

const POST_REPOSITORY = container.resolve(PostRepository)
type StateType = {
  postList: Paging<Post>
  keyword: string | null
}
const state = reactive<StateType>({
  postList: new Paging<Post>(),
  keyword: ''
})

function getList(page = 1) {
  POST_REPOSITORY.getList(page)
    .then((postList) => {
      state.postList = postList
    })
}

onMounted(() => {
  getList()
})

function search(page = 1) {
  POST_REPOSITORY.getSearchList(page, state.keyword)
    .then((postList) => {
      state.postList = postList;
    })
}

</script>

<template>
  <div class="d-flex justify-content-center">
    <div class="content w-50">
      <span class="totalCount">게시글 수: {{ state.postList.totalCount }}</span>

      <ul class="posts">
        <li v-for="post in state.postList.items" :key="post.id">
          <PostComponent :post="post"/>
        </li>
      </ul>

      <div class="d-flex justify-content-center">
        <el-pagination
          size="small"
          :background="true"
          v-model:current-page="state.postList.page"
          layout="prev, pager, next"
          :default-page-size="10"
          @current-change="(page) => getList(page)"
          :total="state.postList.totalCount"/>
      </div>

      <div class="search-div">
        <el-input type="text" v-model="state.keyword"/>
        <el-button @click="search()" type="primary">검색</el-button>
      </div>
    </div>
  </div>


</template>

<style scoped lang="scss">
.content {
  padding: 0 1rem 0 1rem;
  margin-bottom: 2rem;
}

.totalCount {
  font-size: 0.88rem;
}

.posts {
  list-style: none;
  padding: 0;

  li {
    margin-bottom: 2.4rem;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.search-div {
  display: grid;
  justify-content: center;
  grid-template-columns: 200px 0.2fr;
  margin-top: 50px;

  .el-button {
    margin-left: 10px;
  }
}
</style>
