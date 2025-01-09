export default class Paging<T> {

  public page = 0
  public size = 10
  public totalCount = 0
  public items: T[] = []

  public setItems(items: T[]) {
    this.items = items
  }
}
