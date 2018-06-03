package netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class AIOServer {
    //首先需要适用异步通道
    public final static int PORT=65500;
    private AsynchronousServerSocketChannel server;
    public AIOServer() throws IOException {
        server=AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(PORT));
    }
    public void start(){
        System.out.println("Server listen on" +PORT);
        //注册事件和事件完成过后的处理器
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                final ByteBuffer buffer=ByteBuffer.allocate(1024);
                System.out.println(Thread.currentThread().getName());
                Future<Integer> writeResult=null;
                try{
                    result.read(buffer).get(100, TimeUnit.SECONDS);
                    buffer.flip();
                    writeResult=result.write(buffer);
                }catch (InterruptedException|ExecutionException e){
                    e.printStackTrace();
                }catch (TimeoutException e){
                    e.printStackTrace();
                }finally {
                    try {
                        server.accept(null,this);
                        writeResult.get();
                        result.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed:"+exc);
            }


        });

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new AIOServer().start();
        while (true){
            Thread.sleep(1000);
        }
    }


}

