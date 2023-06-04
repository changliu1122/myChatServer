package org.mychat.mychat_server.netty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * singleton pattern 这里我们用 单例模式， 使得仅有这个类自己可以创建自己的唯一的instance
 * 注意事项：getInstance() 方法中需要使用同步锁 synchronized (Singleton.class) 防止多线程同时进入造成 instance 被多次实例化。
 *          构造函数是私有的
 */

@Component
public class ChatServer {

    //创建一个 Singleton 类
    private static class Singleton{
        static ChatServer instance = new ChatServer();
    }

    //从 singleton 类获取唯一的对象
    public static ChatServer getInstance(){
        return Singleton.instance;
    }


    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture future;

    //constructor
    public ChatServer(){
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer());

    }

    public void run(){
        this.future = serverBootstrap.bind(8889);
    }

}
