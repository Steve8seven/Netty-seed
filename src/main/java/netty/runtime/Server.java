package netty.runtime;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        //1 接收客户端连接的工作组
        EventLoopGroup boss = new NioEventLoopGroup();
        //2 用于接收客户端连接读写操作的线程工作组
        EventLoopGroup work = new NioEventLoopGroup();

        //3 辅助我们创建netty服务
        ServerBootstrap server = new ServerBootstrap();
        server.group(boss, work)                                  //绑定两个工作线程组
                .channel(NioServerSocketChannel.class)            //设置NIO的模式
                .option(ChannelOption.SO_BACKLOG, 1024)     // 设置TCP缓冲区
                .option(ChannelOption.SO_RCVBUF, 32*1024)   // 设置接受数据的缓存大小
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.SO_SNDBUF, 32*1024)
                .childHandler(new ChannelInitializer<SocketChannel>(){ // 初始化绑定服务通道
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        // 为通道进行初始化： 数据传输过来的时候会进行拦截和执行
                        sc.pipeline().addLast(new ReadTimeoutHandler(5));
                        sc.pipeline().addLast(new ServerHandler());
                    }
                });

        ChannelFuture cf = server.bind(8765).sync();

        cf.channel().closeFuture().sync();
        work.shutdownGracefully();
        boss.shutdownGracefully();

    }

}
