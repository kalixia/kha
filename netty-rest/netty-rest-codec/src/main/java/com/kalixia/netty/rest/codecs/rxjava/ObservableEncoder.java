package com.kalixia.netty.rest.codecs.rxjava;

import com.kalixia.netty.rest.ApiResponse;
import io.netty.buffer.BufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import rx.Observable;
import rx.Observer;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.TRANSFER_ENCODING;

/**
 * Encoder transforming RxJava's {@link Observable} into many HTTP objects.
 * <p>
 * This encoder transforms {@link Observable}s into many {@link io.netty.handler.codec.http.HttpMessage}s,
 * hence sends chunked HTTP responses.
 */
@ChannelHandler.Sharable
public class ObservableEncoder extends MessageToMessageEncoder<Observable<?>> {

    @Override
    @SuppressWarnings("unchecked")
    protected void encode(final ChannelHandlerContext ctx, final Observable<?> observable, final MessageBuf<Object> out)
            throws Exception {
        observable.subscribe(new Observer() {

            @Override
            public void onNext(Object args) {
                // return ApiResponse object as-is
                if (args instanceof ApiResponse) {
                    // TODO: figure out which HTTP status to send!
                    DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    HttpHeaders.setTransferEncodingChunked(response);
                    out.add(response);

                    // TODO: figure out how to process the result as content
                    DefaultHttpContent chunk = new DefaultHttpContent(Unpooled.wrappedBuffer("test".getBytes()));
                    out.add(chunk);

                    /*
                    FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                            HttpVersion.HTTP_1_1,       // TODO: reply with the expectations from the request
                            apiResponse.status(),
                            apiResponse.content());
                    // insert usual HTTP headers
                    httpResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, apiResponse.content().readableBytes());
                    httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, apiResponse.contentType());
                    httpResponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    // insert request ID header
                    if (apiResponse.id() != null) {
                        httpResponse.headers().set("X-Api-Request-ID", apiResponse.id().toString());
                    }
                    out.add(httpResponse);
                    */
                    return;
                }
                // TODO: otherwise convert them first! figure out what to add: ApiResponse? raw object?
            }

            @Override
            public void onCompleted() {
                DefaultLastHttpContent lastChunk = new DefaultLastHttpContent();
                ctx.write(lastChunk);
            }

            @Override
            public void onError(Exception e) {
            }

        });

    }

}
