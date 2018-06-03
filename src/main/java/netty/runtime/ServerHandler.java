package netty.runtime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("-------通道激活------");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //NIO通信（传输数据是buffer对象）
        ByteBuf buf = (ByteBuf)msg;
        byte[] request = new byte[buf.readableBytes()];
        buf.readBytes(request);
        String body = new String(request, "utf-8");
        System.out.println("服务器:" + body);

        String response = "我是返回的数据";
        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.err.println("--------数据读取完毕----------");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.err.println("--------数据读异常----------: ");
        cause.printStackTrace();
        ctx.close();
    }

}
