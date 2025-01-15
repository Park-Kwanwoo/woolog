import type {HttpRequestConfig} from "@/http/AxiosHttpClient";
import AxiosHttpClient from "@/http/AxiosHttpClient";
import {inject, singleton} from "tsyringe";
import {plainToInstance} from "class-transformer";
import Null from "@/entity/data/Null";
import Paging from "@/entity/data/Paging";

@singleton()
export default class HttpRepository {

  constructor(@inject(AxiosHttpClient) private readonly httpClient: AxiosHttpClient) {
  }

  public get<T>(config: HttpRequestConfig, clazz: { new(...args: any[]) }): Promise<T> {
    return this.httpClient.request({...config, method: 'GET'})
      .then((response) => plainToInstance(clazz, response.data))
  }

  public getList<T>(config: HttpRequestConfig, clazz: { new(...args: any[]) }): Promise<Paging<T>> {
    return this.httpClient.request({...config, method: 'GET'})
      .then((response) => {
        const paging = plainToInstance<Paging<T>, any>(Paging, response.data)
        const items = plainToInstance<T, any>(clazz, response.data.items)
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
      .then((response) => {
      })
  }
}
