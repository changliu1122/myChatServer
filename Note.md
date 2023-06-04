id generator strategy:
    UUID: mac address + time stamp, 并发不高  分布式唯一 too long, not monotonic increasing, need modification, insertion could be slow
    Snowflake: 分布式唯一 无规则递增 高并发
    autoincrement: 分库分表 会产生问题 id overlap

雪花ID + bigint auto increment 类型主键 小规模 不能分库分表 所以 用下面的。
雪花ID + varchar类型主键 reason：
        为什么不用Long作为雪花ID的存储类型呢？因为前端Js的number存储最大值9007199254740992 （2 的 53次方 -1），
        而long的最大值9223372036854775807。前端会有精度丢失，然后和后端的ID对不上的问题。当然也可以通过Java代码来转，
        可是这样做的代价还不如直接数据库用varchar.


每一个功能都要经过 xml（看看 有没有对应的数据库方法）-mapper 中对应的接口对应 xml 文件 - services interface and impl -- controller

vo class for display user information without leek sensitive info, zB password will not send to front-end.

Beanutil.copyproperties copy properties of an object to make another object of other class


1.axios.post with data 传参时， 后端接口方法 参数要标注 @requestbody 且只有第一个参数可以接收到， axios 传过来的参数 java 会封装成 一个类 并对应赋值， 所以@requestbody 只能标记一个参数
2.axios.post with params 传参数, 后端接口方法列表中必须写对应的formal param，一一对应，每一个都要标注@requestparams

@RequestParam注解，默认接收Content-Type: application/x-www-form-urlencoded编码格式的数据
@RequestBody注解，默认接收JSON类型格式的数据。


if SpringUtil.applicationContext is null (springutil class does not work), 
add bean manually in MyChatServerApplication --- or do not forget add @componet on springutil class
