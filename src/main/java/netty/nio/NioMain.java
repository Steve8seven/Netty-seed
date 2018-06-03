package netty.nio;

public class NioMain {
    public static void main(String[] args) throws Exception {
        NioServer nioServer=new NioServer();
        nioServer.startServer();
    }
}
