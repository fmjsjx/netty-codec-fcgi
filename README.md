# netty-codec-fcgi
A Fast-CGI codec component based on netty-4.1.x.

## Quick Start:
### Client Side
FastCGIClient.java
```java
...
import com.github.fmjsjx.netty.codec.fcgi.DefaultFullFCGIRequest;
import com.github.fmjsjx.netty.codec.fcgi.FCGIParameterNames;
import com.github.fmjsjx.netty.codec.fcgi.FCGIParameterValues;
import com.github.fmjsjx.netty.codec.fcgi.FCGIRequestEncoder;
import com.github.fmjsjx.netty.codec.fcgi.FCGIResponseDecoder;
import com.github.fmjsjx.netty.codec.fcgi.FCGIServiceMode;
...
public class FastCGIClient {

...
    public static void main(String[] args) {
        FCGIRequestEncoder fcgiRequestEncoder = new FCGIRequestEncoder();
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap().group(group).channel(SocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(fcgiRequestEncoder, new FCGIResponseDecoder(FCGIServiceMode.SIMPLE),
                                new FastCGIClientHandler());
                    }
                });
        ChannelFutureListener listener = future -> {
            Channel ch = future.channel();
            if (future.isSuccess()) {
                // send request
                DefaultFullFCGIRequest request = new DefaultFullFCGIRequest(requestId);
                request.params().addParam(FCGIParameterNames.SCRIPT_FILENAME, "/path/to/script_name")
                        .addParam(FCGIParameterNames.REQUEST_METHOD, FCGIParameterValues.POST)
                        .addParam(FCGIParameterNames.CONTENT_TYPE, FCGIParameterValues.APPLICATION_JSON_UTF8)
                        .addParam(FCGIParameterNames.CONTENT_LENGTH, String.valueOf(content.readableBytes()))
                        .addParam(FCGIParameterNames.REMOTE_ADDR, clientIP);
                request.stdin(content);
                ch.writeAndFlush(request);
            } else {
                // connect failed
            }
        };
        ChannelFuture cf = b.connect(serverAddress);
        cf.addListener(listener);
    }
...
}
```

FastCGIClientHandler.java
```java
import static com.github.fmjsjx.netty.codec.fcgi.FCGIConstants.*;
import com.github.fmjsjx.netty.codec.fcgi.FCGIEndRequest;
import com.github.fmjsjx.netty.codec.fcgi.FCGIResponseHeaders;
import com.github.fmjsjx.netty.codec.fcgi.FCGIResponseUtil;
import com.github.fmjsjx.netty.codec.fcgi.FullFCGIResponse;
...

public class FastCGIClientHandler extends SimpleChannelInboundHandler<FullFCGIResponse> {

...
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullFCGIResponse msg) throws Exception {
        try {
            FCGIEndRequest endRequest = msg.endRequest();
            if (endRequest.protocolStatus() != FCGI_REQUEST_COMPLETE) {
                // error protocol status
                ctx.close();
                return;
            }
            if (msg.hasStderr()) {
                // has stderr, ...
                System.err.println(msg.stderr().content().toString(CharsetUtil.UTF_8));
                ctx.close();
                return;
            }
            ByteBuf content = msg.stdout().content();
            FCGIResponseHeaders headers = FCGIResponseUtil.decodeHeaders(content);
            ...         
        } finally {
            // in simple mode, just close socket connection when response received
            ctx.close();
        }
    }
...
}
```

### Server Side
FastCGIServer.java
```java
import com.github.fmjsjx.netty.codec.fcgi.FCGIRequestDecoder;
import com.github.fmjsjx.netty.codec.fcgi.FCGIResponseEncoder;
...

public class FastCGIServer {

...
    public static void main(String[] args) throws Exception {
        FCGIResponseEncoder fcgiResponseEncoder = new FCGIResponseEncoder();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap().group(bossGroup, workerGroup).channel(ServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(fcgiResponseEncoder, new FCGIRequestDecoder(), new FastCGIServerHandler());
                        }
                    });
            Channel serverChannel = b.bind(address).sync().channel();
            serverChannel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
...
}

```

FastCGIServerHandler.java
```java
import com.github.fmjsjx.netty.codec.fcgi.DefaultFullFCGIResponse;
import com.github.fmjsjx.netty.codec.fcgi.FCGIAbortRequest;
import com.github.fmjsjx.netty.codec.fcgi.FCGIGetValues;
import com.github.fmjsjx.netty.codec.fcgi.FCGIGetValuesResult;
import com.github.fmjsjx.netty.codec.fcgi.FCGIMessage;
import com.github.fmjsjx.netty.codec.fcgi.FullFCGIRequest;
...

public class FastCGIServerHandler extends SimpleChannelInboundHandler<FCGIMessage> {

...
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FCGIMessage msg) throws Exception {
        if (msg instanceof FullFCGIRequest) {
            requestReceived(ctx, (FullFCGIRequest) msg);
        } else if (msg instanceof FCGIAbortRequest) {
            // abort request received
            ctx.close();
        } else if (msg instanceof FCGIGetValues) {
            FCGIGetValuesResult result = new FCGIGetValuesResult();
            // do something and set result...
            ...
            ctx.writeAndFlush(result).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void requestReceived(ChannelHandlerContext ctx, FullFCGIRequest msg) {
        // do something and generate resposne content...
        ByteBuf content = ...;
        DefaultFullFCGIResponse response = new DefaultFullFCGIResponse(msg.requestId());
        response.stdout(content);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
...
}

```