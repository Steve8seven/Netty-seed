package netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


public class AIOClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        final AsynchronousSocketChannel channel=AsynchronousSocketChannel.open();
        channel.connect(new InetSocketAddress("127.0.0.1", 65500), null, new CompletionHandler<Void, Object>() {
            @Override
            public void completed(Void result, Object attachment) {

                try {
                    final ByteBuffer buffer=ByteBuffer.allocate(1024);
                    channel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            buffer.flip();
                            System.out.println(new String(buffer.array()));
                            try{
                                channel.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void failed(Throwable exc, Object attachment) {

            }
        });
        //主线程结束,这里等待 上述处理全部完成
        Thread.sleep(1000);
    }
}
