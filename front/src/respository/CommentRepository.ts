import {inject, singleton} from "tsyringe";
import HttpRepository from "@/respository/HttpRepository";
import Comment from "@/entity/comment/Comment";
import {storeToRefs} from "pinia";
import {useTokenStore} from "@/stores/TokenStore";
import router from "@/router";
import {ElMessage} from "element-plus";
import ApiResponse from "@/response/ApiResponse";

@singleton()
export default class CommentRepository {

  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {
  }

  public getList(postId: number, page: number) {
    return this.httpRepository.getList<Comment>({
      path: `/api/posts/${postId}/comments?page=${page}&size=7`
    }, Comment)
  }

  public write(CommentWrite, postId: number) {
    const tokenStore = useTokenStore();
    const { token } = storeToRefs(tokenStore)

    return this.httpRepository.post({
      path: `/api/posts/${postId}/comments`,
      body: CommentWrite,
      headers: {
        Authorization: token.value
      }
    })
      .then((response) => {
        if (response.statusCode == 'ERROR') {
          ElMessage.error('댓글을 입력해주세요.')
        } else {
          ElMessage.success('작성 성공')
        }
      })
  }

  public delete(commentId: number) {

    const tokenStore = useTokenStore();
    const { token } = storeToRefs(tokenStore)

    return this.httpRepository.delete({
      path: `/api/comments/${commentId}`,
      headers: {
        Authorization: token.value
      }
    })
  }
}
