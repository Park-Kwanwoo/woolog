import {DateTimeFormatter, LocalDateTime} from "@js-joda/core";
import {Transform} from "class-transformer";

export default class Post {

  public id = 0
  public title = ''
  public content = ''
  public nickname = ''

  @Transform(({value}) => LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME), {toClassOnly: true})
  public create_at = LocalDateTime.now()

  public getDisplayCreatedAt() {
    return this.create_at.format(DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm'))
  }

  public getDisplaySimpleCreatedAt() {
    return this.create_at.format(DateTimeFormatter.ofPattern('yyyy-MM-dd'))
  }
}


