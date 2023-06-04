package org.mychat.mychat_server;


import org.mychat.mychat_server.netty.ChatServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


//integrate netty into springboot
@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() == null){
            try {
                ChatServer.getInstance().run();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

