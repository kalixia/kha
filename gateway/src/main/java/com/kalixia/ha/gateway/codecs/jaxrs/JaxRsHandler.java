package com.kalixia.ha.gateway.codecs.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.gateway.ApiRequest;
import com.kalixia.ha.gateway.ApiResponse;
import com.kalixia.ha.gateway.codecs.jaxrs.converters.Converters;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JaxRsHandler extends ChannelInboundMessageHandlerAdapter<ApiRequest> {
    /** A map of paths as strings to underlying JAX-RS resource method to be called. */
    private static Map<Pattern, Method> uriTemplateToMethod;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsHandler.class);

    static {
        try {
            // initialize Scannotation database
            URL[] urls = ClasspathUrlFinder.findClassPaths();
            AnnotationDB db = new AnnotationDB();
            db.scanArchives(urls);
            // scan each JAX-RS resource for URI templates
            Set<String> resourceClassNames = db.getAnnotationIndex().get(Path.class.getName());
            uriTemplateToMethod = new HashMap<Pattern, Method>();
            for (String resourceName : resourceClassNames) {
                LOGGER.info("Found JAX-RS resource '{}'", resourceName);
                Class clazz = Class.forName(resourceName);
                Path resourcePath = (Path) clazz.getAnnotation(Path.class);
                for (Method method : clazz.getDeclaredMethods()) {
                    Path methodPath = method.getAnnotation(Path.class);
                    if (methodPath == null)
                        continue;
                    String uriTemplate;
                    if ("/".equals(methodPath.value())) {
                        uriTemplate = resourcePath.value();
                    } else {
                        uriTemplate = resourcePath.value() + '/' + methodPath.value();
                    }
                    Pattern pattern = UriTemplateUtils.extractRegexPattern(uriTemplate);
                    LOGGER.debug("Found URI template {}. Compiled into {} regex.", uriTemplate, pattern);
                    uriTemplateToMethod.put(pattern, method);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Can't scan classes for JAX-RS annotations", e);
            uriTemplateToMethod = Collections.emptyMap();
        } catch (ClassNotFoundException e) {
            LOGGER.error("Can't load JAX-RS resource class", e);
            uriTemplateToMethod = Collections.emptyMap();
        }
    }

    public JaxRsHandler(ObjectMapper objectMapper)
            throws IOException, ClassNotFoundException, AnnotationDB.CrossReferenceException {
        super(ApiRequest.class);
        this.objectMapper = objectMapper;
    }

    /**
     * Locate and call the appropriate JAX-RS resource.
     * Write back an {@link ApiResponse} object encapsulating the result of the underlying JAX-RS resource.
     * @param ctx
     * @param request
     * @throws Exception
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, ApiRequest request) throws Exception {
        // locate the JAX-RS resource based on the requested path
        Method method = null;
        List<Object> parameters = null;
        for (Pattern pattern : uriTemplateToMethod.keySet()) {
            Matcher matcher = pattern.matcher(request.uri());
            if (matcher.matches()) {
                method = uriTemplateToMethod.get(pattern);
                parameters = new ArrayList<>();
                Class<?>[] parameterTypes = method.getParameterTypes();
                LOGGER.debug("Found matching JAX-RS resource {}", method);
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String parameterAsString = matcher.group(i);
                    Object parameter = Converters.fromString(parameterTypes[i - 1], parameterAsString);
                    LOGGER.debug("Extracted path parameter {}", parameter);
                    parameters.add(parameter);
                }
            }
        }
        if (method == null) {
            LOGGER.info("Could not locate a JAX-RS resource for path '{}'", request.uri());
            // TODO: return a 404 error
            return;
        }

        // invoke the JAX-RS resource
        LOGGER.debug("Invoking method {} with parameters {}", method, parameters);
        Object result;
        if (parameters.size() > 0) {
            result = method.invoke(method.getDeclaringClass().newInstance(),
                parameters.toArray(new Object[]{parameters.size()}));
        } else {
            try {
                result = method.invoke(method.getDeclaringClass().newInstance());
            } catch (Exception e) {
                ctx.write(new ApiResponse(request.id(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
                        Unpooled.wrappedBuffer("Unexpected error".getBytes("UTF-8")), MediaType.TEXT_PLAIN));
                return;
            }
        }

        // TODO: figure out the serialization stuff (meaning what kind of content to produce)!
        byte[] content = objectMapper.writeValueAsBytes(result);

        // TODO: find a way to do this without in-memory serialization (prefer streams instead)
        ctx.write(new ApiResponse(request.id(), HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content), MediaType.APPLICATION_JSON));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Can't call JAX-RS resource", cause);
    }
}
