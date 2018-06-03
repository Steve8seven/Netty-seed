package netty.warp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Client {

	public static void main(String[] args) throws Exception {
		//ONE:
		//1 线程工作组
		EventLoopGroup work = new NioEventLoopGroup();
		
		//TWO:
		//3 辅助类。用于帮助我们创建NETTY服务
		Bootstrap b = new Bootstrap();
		b.group(work)	//绑定工作线程组
		 .channel(NioSocketChannel.class)	//设置NIO的模式
		 // 初始化绑定服务通道
		 .handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				
				//1.定义特殊字符进行分割
				ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
				sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
				
				//2.定长
//				sc.pipeline().addLast(new FixedLengthFrameDecoder(5));
				//3.字符串 并不能作为TCP拆包和粘包的解决方案
//				sc.pipeline().addLast(new StringEncoder());
//				sc.pipeline().addLast(new StringDecoder());
				sc.pipeline().addLast(new ClientHandler());
			}
		 });
		
		ChannelFuture cf =  b.connect("127.0.0.1", 8765).syncUninterruptibly();
		Unpooled.copiedBuffer("hello netty!-1$_".getBytes());
		cf.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty!-1$_".getBytes()));
		cf.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty!-2$_".getBytes()));
		cf.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty!-3$_".getBytes()));
		cf.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty!-5$_".getBytes()));
		
		
		//释放连接
		cf.channel().closeFuture().sync();
		work.shutdownGracefully();
	}
}
