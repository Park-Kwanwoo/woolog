import {ElMessage} from "element-plus";
import router from "@/router";
import {inject, singleton} from "tsyringe";
import HttpRepository from "@/respository/HttpRepository";
import type HttpError from "@/http/HttpError";
import type Login from "@/entity/member/Login";
import {useTokenStore} from "@/stores/TokenStore";
import {storeToRefs} from "pinia";
import MemberProfile from "@/entity/member/MemberProfile"
import NicknameEdit from "@/request/NicknameEdit";
import PasswordEdit from "@/request/PasswordEdit";

@singleton()
export default class MemberRepository {

  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {
  }

  public login(request: Login) {

    const tokenStore = useTokenStore();

    return this.httpRepository.login({
      path: '/api/auth/login',
      body: request
    })
      .then((response) => {

        const statusCode = response.data.statusCode
        if (statusCode === 'ERROR') {
          ElMessage.error(response.data.message)
        } else {
          ElMessage.success('환영합니다.')
          tokenStore.setToken(response.headers['authorization'])
          router.replace('/')
        }
      })
      .catch((e: HttpError) => {
        ElMessage.error(e);
      })
  }

  public logout() {

    const tokenStore = useTokenStore();

    return this.httpRepository.logout({
      path: '/api/auth/logout'
    })
      .then(() => {
        tokenStore.deleteToken()
        ElMessage.success('로그아웃 되었습니다.')
        router.replace('/')
      })
  }

  public getProfile() {

    const tokenStore = useTokenStore();
    const { token } = storeToRefs(tokenStore)

    return this.httpRepository.get<MemberProfile>({
      path: '/api/members',
      headers: {
        Authorization: token.value
      }
    }, MemberProfile)
      .then((response) => {
        return response
      })
  }

  public editNickname(request: NicknameEdit) {

    const tokenStore = useTokenStore();
    const { token } = storeToRefs(tokenStore)

    return this.httpRepository.patch({
      path: `/api/members/nickname`,
      body: request,
      headers: {
        Authorization: token.value
      }
    })
      .then((response) => {
        return response
      })
  }

  public editPassword(request: PasswordEdit) {

    const tokenStore = useTokenStore();
    const { token } = storeToRefs(tokenStore)

    return this.httpRepository.patch({
      path: `/api/members/password`,
      body: request,
      headers: {
        Authorization: token.value
      }
    })
      .then((response) => {
        return response
      })
  }
}
