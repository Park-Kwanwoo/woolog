import type {AxiosError, AxiosInstance, AxiosResponse} from "axios";
import axios from "axios";
import HttpError from "@/http/HttpError";
import {singleton} from "tsyringe";

export type HttpRequestConfig = {
  method: 'GET' | 'POST' | 'PATCH' | 'DELETE'
  path: string,
  params?: any,
  body?: any,
  headers: {
    Authorization?: string
  }
}

@singleton()
export default class AxiosHttpClient {

  private readonly client: AxiosInstance = axios.create({
    timeout: 300000,
    timeoutErrorMessage: '타임아웃',
    withCredentials: true
  })
  public async request(config: HttpRequestConfig) {
    return this.client.request({
      method: config.method,
      url: config.path,
      params: config.params,
      data: config.body,
      headers: config.headers
    })
      .then((res: AxiosResponse) => {
        return res
      })
      .catch((e: AxiosError) => {
        return Promise.reject(new HttpError(e))
      })
  }
}
