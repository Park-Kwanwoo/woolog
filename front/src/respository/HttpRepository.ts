import type {HttpRequestConfig} from "@/http/AxiosHttpClient";
import AxiosHttpClient from "@/http/AxiosHttpClient";
import {inject, singleton} from "tsyringe";
import {plainToInstance} from "class-transformer";
import Null from "@/entity/data/Null";
import Paging from "@/entity/data/Paging";
import ApiResponse from "@/response/ApiResponse";
import {ElMessage} from "element-plus";

@singleton()
export default class HttpRepository {

  constructor(@inject(AxiosHttpClient) private readonly httpClient: AxiosHttpClient) {
  }

  public get<T>(config: HttpRequestConfig, clazz: { new(...args: any[]) }): Promise<ApiResponse<T>> {
    return this.httpClient.request({...config, method: 'GET'})
      .then((response) => {

        const apiResponse = plainToInstance<ApiResponse<T>, any>(ApiResponse, response.data)
        const statusCode = apiResponse.statusCode
        if (statusCode === 'ERROR') {
          ElMessage.error('잘못된 접근입니다.')
        } else {
          return plainToInstance(clazz, apiResponse.data)
        }
      })

  }

  public getList<T>(config: HttpRequestConfig, clazz: { new(...args: any[]) }): Promise<ApiResponse<Paging<T>>> {
    return this.httpClient.request({...config, method: 'GET'})
      .then((response) => {

        const apiResponse = plainToInstance<ApiResponse<Paging<T>, any>, any>(ApiResponse, response.data)
        const paging = plainToInstance<Paging<T>, any>(Paging, apiResponse.data)
        const items = plainToInstance<T, any>(clazz, paging.items)
        paging.setItems(items)
        return paging
      })
  }

  public post<T>(config: HttpRequestConfig, clazz: { new(...args: any[]) } | null = null): Promise<T> {
    return this.httpClient.request({...config, method: 'POST'})
      .then((response) => plainToInstance(clazz !== null ? clazz : Null, response.data))
  }

  public patch<T>(config: HttpRequestConfig, clazz: { new(...args: any[]) } | null = null): Promise<T> {
    return this.httpClient.request({...config, method: 'PATCH'})
      .then((response) => plainToInstance(clazz !== null ? clazz : Null, response.data))
  }

  public delete<T>(config: HttpRequestConfig, clazz: { new(...args: any[]) } | null = null): Promise<T> {
    return this.httpClient.request({...config, method: 'DELETE'})
      .then((response) => plainToInstance(clazz !== null ? clazz : Null, response.data))
  }

  public login(config: HttpRequestConfig) {
    return this.httpClient.request({...config, method: 'POST'})
      .then((response) => {
        return response;
      })
  }

  public logout(config: HttpRequestConfig) {
    return this.httpClient.request({...config, method: 'GET'})
      .then(() => {
      })
  }
}
