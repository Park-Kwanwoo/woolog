export default class ApiResponse<T> {

  public statusCode = ''
  public message = ''
  public data: T[] = []
}
