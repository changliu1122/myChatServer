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


axios.post 传参时， 后端接口 方法 参数要标注 @requestbody 不然格式不对