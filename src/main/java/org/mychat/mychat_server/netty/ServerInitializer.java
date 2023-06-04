package org.mychat.mychat_server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // add http codec
        pipeline.addLast(new HttpServerCodec());
        // handle data stream on http
        pipeline.addLast(new ChunkedWriteHandler());
        //aggregate http message as request or respond
        pipeline.addLast(new HttpObjectAggregator(1024*64));

//        /**
//         * 针对客户端，如果在1分钟时间内没有向服务端发送读写心跳（ALL），则主动断开连接
//         * 如果有读空闲和写空闲，则不做任何处理
//         */
//        pipeline.addLast(new IdleStateHandler(8,10,12));
//        //自定义的空闲状态检测的handler
//        pipeline.addLast(new HeartBeatHandler());

        /**
         * 本handler 会帮你处理一些繁重复杂的事情
         * 会帮你处理握手动作：handshaking（close、ping、pong） ping+pong = 心跳
         * 对于websocket 来讲，都是以frams 进行传输的，不同的数据类型对应的frams 也不同
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //自定义的handler  -> ChanHandler class does the real job
        pipeline.addLast(new ChatHandler());
    }
}
