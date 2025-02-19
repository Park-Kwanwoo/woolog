<script setup lang="ts">

import {container} from "tsyringe";
import MemberRepository from "@/respository/MemberRepository";
import {onMounted, reactive, ref} from "vue";
import MemberInfo from "@/entity/member/MemberInfo";
import {ElMessage} from "element-plus";
import NicknameEdit from "@/request/NicknameEdit";
import PasswordEdit from "@/request/PasswordEdit";

type StateType = {
  memberInfo: MemberInfo,
  nicknameEdit: NicknameEdit,
  passwordEdit: PasswordEdit,
  beforeNickname: string | null
}

const state = reactive<StateType>({
  memberInfo: new MemberInfo(),
  nicknameEdit: new NicknameEdit(),
  passwordEdit: new PasswordEdit(),
  beforeNickname: ''
})

const MEMBER_REPOSITORY = container.resolve(MemberRepository)

onMounted(() => {
  MEMBER_REPOSITORY.getProfile()
    .then((memberProfile) => {
      state.memberInfo = memberProfile
    })
})


const flagNickname = ref(false)
const flagPassword = ref(false)

function visibleEditNicknameElement() {
  flagNickname.value = true
  state.beforeNickname = state.memberInfo.nickname

}
function hideEditNicknameElement() {
  flagNickname.value = false
  state.memberInfo.nickname = state.beforeNickname
}

function editNickname() {

  const regex = /^[A-Za-z0-9가-힣]+$/
  state.nicknameEdit.nickname = state.memberInfo.nickname

  if (!regex.test(state.nicknameEdit.nickname)) {
    ElMessage.error('공백 및 특수문자 제외한 닉네임을 입력해주세요.')
  } else {
    MEMBER_REPOSITORY.editNickname(state.nicknameEdit)
      .then((response) => {
        const statusCode = response.statusCode
        if (statusCode === 'ERROR') {
          ElMessage.error('닉네임 변경에 실패했습니다.')
        } else {
          state.memberInfo = response.data
          flagNickname.value = false
        }
      })
  }
}

function visibleEditPasswordElement() {
  flagPassword.value = true

}
function hideEditPasswordElement() {
  flagPassword.value = false;
  state.passwordEdit = ''
}

function editPassword() {

  const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/

  if (!regex.test(state.passwordEdit.password)) {
    ElMessage.error('비밀번호는 최소 8글자, 하나 이상의 숫자, 특수문자를 포함해야 합니다.')
  } else {
    MEMBER_REPOSITORY.editPassword(state.passwordEdit)
      .then((response) => {
        const statusCode = response.statusCode
        if (statusCode === 'ERROR') {
          ElMessage.error('비밀번호 변경에 실패했습니다.')
        } else {
          state.memberInfo = response.data
          flagPassword.value = false
        }
      })
  }

}
</script>

<template>
  <div class="d-flex w-10 justify-content-center">
    <h2>회원 정보</h2>
  </div>
  <div class="d-flex justify-content-center">
    <section class="section border flex-column w-50 p-3">

      <div class="w-100 mb-3 div-grid">
        <div class="d-flex align-items-center">
          이메일
        </div>
        <div class="flex-fill d-flex align-items-center justify-content-center email-div">
          {{ state.memberInfo.email }}
        </div>
      </div>

      <div class="w-100 mb-3 div-grid">
        <div class="d-flex align-items-center">
          이름
        </div>
        <div class="flex-fill d-flex align-items-center justify-content-center email-div">
          {{ state.memberInfo.name }}
        </div>
      </div>

      <div class="w-100 mb-3 div-grid">
        <div class="d-flex align-items-center">
          닉네임
        </div>
        <div v-if="!flagNickname" class="flex-fill d-flex align-items-center justify-content-center email-div">
          {{ state.memberInfo.nickname }}
        </div>
        <div v-else class="flex-fill d-flex align-items-center justify-content-center email-div">
          <div>
            <el-input type="text" v-model="state.memberInfo.nickname"/>
          </div>
        </div>
        <div v-if="flagNickname" class="d-flex">
          <div class="flex-fill">
            <el-button @click="hideEditNicknameElement" type="danger">취소</el-button>
          </div>
          <div class="flex-fill">
            <el-button @click="editNickname" type="primary">수정</el-button>
          </div>
        </div>
        <div class="d-flex align-items-center justify-content-end email-option">
          <el-button type="primary" @click="visibleEditNicknameElement" v-if="!flagNickname">수정</el-button>
        </div>
      </div>

      <div class="w-100 div-grid">
        <div class="d-flex align-items-center">
          비밀번호
        </div>
        <div v-if="!flagPassword" class="flex-fill d-flex align-items-center justify-content-center password-div">
          <span class="password">비밀번호를 입력해주세요</span>
        </div>
        <div v-else class="flex-fill d-flex align-items-center justify-content-center password-div">
          <div>
            <el-input type="password" v-model="state.passwordEdit.password" placeholder="비밀번호를 입력해주세요"/>
          </div>
        </div>

        <div v-if="flagPassword" class="d-flex">
          <div class="flex-fill">
            <el-button @click="hideEditPasswordElement" type="danger">취소</el-button>
          </div>
          <div class="flex-fill">
            <el-button @click="editPassword" type="primary">수정</el-button>
          </div>
        </div>

        <div class="d-flex align-items-center justify-content-end password-option">
          <el-button type="primary" @click="visibleEditPasswordElement" v-if="!flagPassword">수정</el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">

.section {
  border-radius: 20px;
}

.div-grid {
  width: 10%;
  display: grid;
  grid-template-columns: 120px 1fr 120px;
  row-gap: 1rem;
  column-gap: 1rem;
  align-items: center;
  text-align: center;
}

.password {
  font-weight: lighter;
}
</style>
