import {ElMessage} from "element-plus";
import router from "@/router";
import {inject, singleton} from "tsyringe";
import HttpRepository from "@/respository/HttpRepository";
import type HttpError from "@/http/HttpError";
import type Login from "@/entity/member/Login";
import {useTokenStore} from "@/stores/Token";
import {storeToRefs} from "pinia";

@singleton()
export default class MemberRepository {

  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {
  }

  public login(request: Login) {

    const tokenStore = useTokenStore();

    this.httpRepository.login({
      path: '/api/auth/login',
      body: request
    })
      .then((response) => {
        ElMessage.success('환영합니다.')
        tokenStore.setToken(response)
        router.replace('/')
      })
      .catch((e: HttpError) => {
        ElMessage.error(e);
      })
  }

  public logout() {
    return this.httpRepository.logout({
      path: '/api/auth/logout'
    })
      .then((response) => {

      })
  }
}
