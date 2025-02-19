<script setup lang="ts">
import {useTokenStore} from "@/stores/TokenStore";
import {storeToRefs} from "pinia";
import {container} from "tsyringe";
import MemberRepository from "@/respository/MemberRepository";
import {onMounted, reactive, watch} from "vue";

const tokenStore = useTokenStore()
const { isAuthenticated } = storeToRefs(tokenStore)

const state = reactive({
  admin: false | null,
  isMember: false | null
})

const MEMBER_REPOSITORY = container.resolve(MemberRepository)

watch(
  () => isAuthenticated.value,
  () => {
    if (isAuthenticated.value) {
      MEMBER_REPOSITORY.getProfile()
        .then((memberProfile) => {
          state.admin = memberProfile.admin
          state.isMember = memberProfile.isMember;
        })
    }
  }
)

onMounted(() => {

  if (isAuthenticated.value) {
    MEMBER_REPOSITORY.getProfile()
      .then((memberProfile) => {
        state.admin = memberProfile.admin
        state.isMember = memberProfile.isMember;
      })
  }
})

function logout() {
  MEMBER_REPOSITORY.logout()
    .then(() => {
      state.admin = false
      state.isMember = false
    });
}
</script>

<template>
  <ul class="menus">
    <li class="menu">
      <router-link to="/">처음으로</router-link>
    </li>

    <li class="menu" v-if="!state.isMember">
      <router-link to="/signup">회원가입</router-link>
    </li>

    <li class="menu" v-if="state.admin">
      <router-link to="/write">글 작성</router-link>
    </li>

    <li class="menu" v-if="state.isMember">
      <router-link to="/mypage">마이페이지</router-link>
    </li>

    <li class="menu" v-if="!isAuthenticated">
      <router-link to="/login">로그인</router-link>
    </li>
    <li class="menu" v-else>
      <a href="#" @click="logout()">로그아웃</a>
    </li>
  </ul>
</template>

<style scoped lang="scss">
.menus {
  height: 20px;
  list-style: none;
  padding: 0;
  font-size: 0.88rem;
  font-weight: 300;
  text-align: center;
  margin: 0;
}

.menu {
  display: inline;
  margin-right: 1rem;

  &:last-child {
    margin-right: 0;
  }

  a {
    color: inherit;
  }
}
</style>
