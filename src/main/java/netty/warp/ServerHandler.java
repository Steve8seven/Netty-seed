package netty.warp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	/**
	 * 当我们通道进行激活的时候 触发的监听方法
	 */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    
    	System.err.println("--------通道激活------------");
    }
	
    /**
     * 当我们的通道里有数据进行读取的时候 触发的监听方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx /*NETTY服务上下文*/, Object msg /*实际的传输数据*/) throws Exception {
//    	try{
    		//do something with msg
    		System.err.println(msg);
        	//NIO通信（传输的数据是什么? ----> buffer对象）
        	ByteBuf buf = (ByteBuf)msg;
        	byte[] request = new byte[buf.readableBytes()];
        	buf.readBytes(request);
        	String body = new String(request, "utf-8");
        	System.out.println("服务器: " + body); 
        	
        	//ByteBuf
        	String response = "我是返回的数据!!$_";
        	ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
        	//添加addListener 可以触发关闭通道监听事件
        	//.addListener(ChannelFutureListener.CLOSE);        	
    		
//    	} finally {
//    		ReferenceCountUtil.release(msg);
//    	}
    	


    	
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       System.err.println("--------数据读取完毕----------");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	System.err.println("--------数据读异常----------");
        ctx.close();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
