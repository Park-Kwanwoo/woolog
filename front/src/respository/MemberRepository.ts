import {ElMessage} from "element-plus";
import router from "@/router";
import {inject, singleton} from "tsyringe";
import HttpRepository from "@/respository/HttpRepository";
import type HttpError from "@/http/HttpError";
import type Login from "@/entity/member/Login";
import {useTokenStore} from "@/stores/Token";
import {storeToRefs} from "pinia";
import MemberProfile from "@/entity/member/MemberProfile";

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
      .then((response) => {
        localStorage.removeItem("token")
        tokenStore.deleteToken()
      })
  }

  public getProfile() {

    const tokenStore = useTokenStore();
    const { token } = storeToRefs(tokenStore)

    return this.httpRepository.get({
      path: '/api/members',
      headers: {
        Authorization: token.value
      }
    }, MemberProfile)
      .then((response) => {
        return response
      })
  }
}
