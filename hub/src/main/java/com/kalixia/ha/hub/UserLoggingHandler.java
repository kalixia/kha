package com.kalixia.ha.hub;

import com.kalixia.grapi.codecs.shiro.ShiroHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import org.apache.shiro.subject.Subject;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
class UserLoggingHandler extends ChannelDuplexHandler {
    public static final String MDC_PROPERTY = "USER";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoggingHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Attribute<Subject> subjectAttribute = ctx.channel().attr(ShiroHandler.ATTR_SUBJECT);
        if (subjectAttribute != null && subjectAttribute.get() != null) {
            Object principal = subjectAttribute.get().getPrincipal();
            MDC.put(MDC_PROPERTY, principal);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        MDC.remove(MDC_PROPERTY);
    }

}
