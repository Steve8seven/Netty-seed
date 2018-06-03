package netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;


public class NioClient {
    private Selector selector;
    public void init(String ip,int port) throws IOException {
        SocketChannel channel=SocketChannel.open();
        channel.configureBlocking(false);

        this.selector= SelectorProvider.provider().openSelector();
        channel.connect(new InetSocketAddress(ip,port));
        channel.register(selector,SelectionKey.OP_CONNECT);

    }
    public void working() throws IOException {
        while (true){
            if (!selector.isOpen()){
                break;
            }
            selector.select();
            Iterator<SelectionKey> ite=this.selector.selectedKeys().iterator();
            while (ite.hasNext()){
                SelectionKey key=ite.next();
                ite.remove();
                //连接事件
                if (key.isConnectable()){
                    connect(key);
                }else if(key.isReadable()){
                    read(key);
                }
            }

        }
    }
    /*
    连接
     */
    public void connect(SelectionKey key) throws IOException {
        SocketChannel channel=(SocketChannel)key.channel();
        //如果正在连接,则连接完成
        if(channel.isConnectionPending()){
            channel.finishConnect();

        }
        channel.configureBlocking(false);
        channel.write(ByteBuffer.wrap(new String("HELLO-server" ).getBytes()));
        channel.register(this.selector,SelectionKey.OP_READ);
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel channel=(SocketChannel)key.channel();
        //读取缓冲区
        ByteBuffer bb=ByteBuffer.allocate(1000);
        channel.read(bb);
        byte[] data=bb.array();
        String msg=new String(data).trim();
        System.out.println("客户端收到信息:"+msg);
        channel.close();
        key.selector().close();
    }

    public static void main(String[] args) throws IOException {
        NioClient nioClient=new NioClient();
        nioClient.init("127.0.0.1",65500);
        nioClient.working();
    }
}
