import {inject, singleton} from "tsyringe";
import HttpRepository from "@/respository/HttpRepository";
import type PostWrite from "@/entity/post/PostWrite";
import {useTokenStore} from "@/stores/Token";
import {storeToRefs} from "pinia";
import {ElMessage} from "element-plus";
import router from "@/router";
import Post from "@/entity/post/Post";

@singleton()
export default class PostRepository {

  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {
  }

  public write(request: PostWrite) {

    const tokenStore = useTokenStore();
    const {token} = storeToRefs(tokenStore);
    return this.httpRepository.post({
      path: '/api/posts',
      body: request,
      headers: {
        Authorization: token.value
      }
    })
      .then((response) => {

        const statusCode = response.statusCode
        if (statusCode === 'ERROR') {
          ElMessage.error('제목과 내용을 입력해주세요.')
        } else {
          ElMessage.success('작성 성공')
          router.replace('/')
        }

      })
      .catch((e) => {
        console.log(e);
      })
  }

  public get(postId: number) {
    return this.httpRepository.get<Post>({path: `/api/posts/${postId}`}, Post)
  }

  public getList(page: number) {
    return this.httpRepository.getList<Post>({
      path: `/api/posts?page=${page}&size=10`
    }, Post)
  }

  public edit(request: PostWrite, postId: number) {

    const tokenStore = useTokenStore();
    const {token} = storeToRefs(tokenStore);

    return this.httpRepository.patch({
      path: `/api/posts/${postId}`,
      body: request,
      headers: {
        Authorization: token.value
      }
    })
      .then((res) => {
        ElMessage.success('수정 성공')
        router.replace(`/read/${postId}`)
      })
      .catch((e) => {
        ElMessage.error(e)
      })
  }

  public delete(postId: number) {

    const tokenStore = useTokenStore();
    const {token} = storeToRefs(tokenStore);

    return this.httpRepository.delete({
      path: `/api/posts/${postId}`,
      headers: {
        Authorization: token.value
      }
    })
  }
}
