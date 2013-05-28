package com.kalixia.netty.rest.codecs.rxjava;

import com.kalixia.netty.rest.ObservableApiResponse;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Encoder transforming RxJava's {@link Observable} into many HTTP objects.
 * <p>
 * This encoder transforms {@link Observable}s into many {@link io.netty.handler.codec.http.HttpMessage}s,
 * hence sends chunked HTTP responses.
 */
@ChannelHandler.Sharable
public class ObservableEncoder extends MessageToMessageEncoder<ObservableApiResponse<?>> {

    @Override
    @SuppressWarnings("unchecked")
    protected void encode(final ChannelHandlerContext ctx, final ObservableApiResponse<?> apiResponse, final MessageBuf<Object> out)
            throws Exception {
        // TODO: figure out which HTTP status to send!
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, apiResponse.status());
        HttpHeaders.setTransferEncodingChunked(response);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, apiResponse.contentType());
        response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        // insert request ID header
        if (apiResponse.id() != null) {
            response.headers().set("X-Api-Request-ID", apiResponse.id().toString());
        }
        out.add(response);

        Subscription subscription = apiResponse.observable().subscribe(new Observer() {

            @Override
            public void onNext(Object args) {
                // TODO: figure out how to process the result as content
                DefaultHttpContent chunk = new DefaultHttpContent(Unpooled.wrappedBuffer("test".getBytes()));
                out.add(chunk);
            }

            @Override
            public void onCompleted() {
                DefaultLastHttpContent lastChunk = new DefaultLastHttpContent();
                out.add(lastChunk);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

        });

        subscription.unsubscribe();

    }

}
