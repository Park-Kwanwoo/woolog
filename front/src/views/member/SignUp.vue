<script setup lang="ts">
import {reactive, ref, watch} from "vue";
import {type ComponentSize, ElMessage, type FormInstance, type FormRules} from "element-plus";
import Signup from "@/entity/member/Signup";
import {container} from "tsyringe";
import MemberRepository from "@/respository/MemberRepository";
import NicknameCheck from "@/request/NicknameCheck";
import EmailCheck from "@/request/EmailCheck";

interface Checker {
  nicknameBtn: boolean
  emailBtn: boolean
  passwordMatch: boolean
  nameValue: boolean
}

type StateType = {
  signup: Signup
}
const MEMBER_REPOSITORY = container.resolve(MemberRepository)
const formSize = ref<ComponentSize>('default')
const ruleFormRef = ref<FormInstance>()
const state = reactive<StateType>({
  signup: new Signup()
})

const checker = reactive<Checker>({
  nicknameBtn: false,
  emailBtn: false,
  passwordMatch: false,
  nameValue: false
})

const rules = reactive<FormRules<StateType>>({
  email: [
    {required: true, message: '이메일 형식으로 입력해주세요', trigger: 'blur'},
  ],
  name: [
    {required: true, message: '이름을 입력해주세요', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        const regex = /^[가-힣]{2,}$/

        if (!regex.test(state.signup.name)) {
          callback(new Error('이름을 다시 입력해주세요'));
        } else {
          checker.nameValue = true
          callback()
        }
      }
    }
  ],
  password: [
    {required: true, message: '비밀번호를 입력해주세요', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/

        if (value.length < 8) {
          callback(new Error('비밀번호는 최소 8자 이상이어야 합니다.'));
        } else if (!regex.test(value)) {
          console.log(regex.test(value))
          callback(new Error('비밀번호에는 최소 하나의 숫자 및 특수문자가 포함되어야 합니다.'));
        } else {
          callback();
        }
      }
    }
  ],
  passwordConfirm: [
    {required: true, message: '비밀번호를 입력해주세요', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (value !== state.signup.password) {
          callback(new Error('비밀번호가 일치하지 않습니다.'));
        } else {
          checker.passwordMatch = true
          callback();
        }
      }
    }
  ],
  nickname: [
    {required: true, message: '닉네임을 입력해주세요', trigger: 'blur'},
  ]
})
watch(
  [() => state.signup.nickname,
    () => state.signup.email],

  ([newNickname, newEmail],
   [oldNickname, oldEmail]) => {

    if (newNickname !== oldNickname) {
      checker.nicknameBtn = false
    }

    if (newEmail !== oldEmail) {
      checker.emailBtn = false
    }
  }
)

function checkDuplicateEmail() {

  const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
  const email = state.signup.email

  if (!regex.test(email)) {
    ElMessage.error('올바른 이메일 형식을 입력해주세요')
  } else {
    MEMBER_REPOSITORY.emailDuplicateCheck(new EmailCheck(email))
      .then((flag) => {
        checker.emailBtn = flag
      })
  }
}
function checkDuplicateNickname() {

  const regex = /^[A-Za-z0-9가-힣]+$/
  const nickname = state.signup.nickname

  if (!regex.test(nickname)) {
    ElMessage.error('다시 입력해주세요')
  } else {
    MEMBER_REPOSITORY.nickNameDuplicateCheck(new NicknameCheck(nickname))
      .then((flag) => {
        checker.nicknameBtn = flag
      })
  }
}

function notEmpty(): boolean {
  return checker.emailBtn && checker.nicknameBtn && checker.passwordMatch && checker.nameValue;
}

function signup() {
  if (notEmpty()) {
    MEMBER_REPOSITORY.signup(state.signup)
  } else {
    ElMessage.error('빠진 항목이 없는지 다시 확인해주세요')
  }
}
</script>

<template>
  <div class="container">
    <el-form
      ref="ruleFormRef"
      :model="state.signup"
      :inline="true"
      :rules="rules"
      label-width="auto"
      class="demo-form-inline"
      :size="formSize"
      status-icon
    >
      <el-form-item label="이메일" prop="email">
        <el-input v-model="state.signup.email" placeholder="이메일 형식으로 입력해주세요" clearable/>
        <el-button type="primary" @click="checkDuplicateEmail" :disabled="checker.emailBtn">중복검사</el-button>
      </el-form-item>

      <el-form-item label="이름" prop="name">
        <el-input v-model="state.signup.name" placeholder="특수문자를 제외한 이름을 입력해주세요" clearable/>
      </el-form-item>

      <el-form-item label="비밀번호" prop="password">
        <el-input v-model="state.signup.password" type="password" clearable/>
      </el-form-item>

      <el-form-item label="비밀번호 확인" prop="passwordConfirm">
        <el-input v-model="state.signup.passwordConfirm" type="password" placeholder="동일한 비밀번호를 입력해주세요" clearable/>
      </el-form-item>

      <el-form-item label="닉네임" prop="nickname">
        <el-input v-model="state.signup.nickname" placeholder="특수문자를 제외한 닉네임을 입력해주세요" clearable/>
        <el-button type="primary" @click="checkDuplicateNickname" :disabled="checker.nicknameBtn">중복검사</el-button>
      </el-form-item>

      <el-form-item class="signup">
        <el-button type="primary" @click="signup">회원가입</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped lang="scss">

.demo-form-inline {

  justify-content: center;

  .el-input {
    --el-input-width: 270px;
  }
}

.el-form {
  display: grid;
  row-gap: 1rem;
  column-gap: 1rem;
  align-items: center;
  text-align: center;

  .el-button {
    margin-left: 10px;
  }
}

</style>
