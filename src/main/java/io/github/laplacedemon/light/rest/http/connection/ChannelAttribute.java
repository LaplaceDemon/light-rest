package io.github.laplacedemon.light.rest.http.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ChannelAttribute {
    public final static AttributeKey<IOSession> IOSESSION_KEY = AttributeKey.valueOf("iosession");
    
    public final static IOSession getIOSession(final Channel channel) { 
        Attribute<IOSession> attr = channel.attr(IOSESSION_KEY);
        return attr.get();
    }

    public static IOSession getIOSession(final ChannelHandlerContext ctx) {
        Attribute<IOSession> attr = ctx.channel().attr(IOSESSION_KEY);
        return attr.get();
    }
    
    public static void initSession(final Channel channel) {
    	Attribute<IOSession> attr = channel.attr(ChannelAttribute.IOSESSION_KEY);
        attr.set(new IOSession(channel));
    }
    
}
