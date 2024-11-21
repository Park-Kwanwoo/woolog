export default class Post {

  private readonly _id: number
  private _title: string
  private _content: string


  constructor(id: number, title: string, content: string) {
    this._id = id;
    this._title = title;
    this._content = content;
  }


  get id(): number {
    return this._id;
  }

  get title(): string {
    return this._title;
  }

  get content(): string {
    return this._content;
  }
}
