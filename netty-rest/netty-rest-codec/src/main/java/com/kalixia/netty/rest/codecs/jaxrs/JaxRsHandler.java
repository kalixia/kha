package com.kalixia.netty.rest.codecs.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.netty.rest.ApiRequest;
import com.kalixia.netty.rest.ApiResponse;
import com.kalixia.netty.rest.codecs.jaxrs.converters.Converters;
import eu.infomas.annotation.AnnotationDetector;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ChannelHandler.Sharable
public class JaxRsHandler extends ChannelInboundMessageHandlerAdapter<ApiRequest> {
    /** A map of paths as strings to underlying JAX-RS resource method to be called. */
    private static Map<Pattern, Method> uriTemplateToMethod;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsHandler.class);

    static {
        try {
            final Set<String> resourceClassNames = new HashSet<>();
            final AnnotationDetector.Reporter jaxRsResourcesReporter = new AnnotationDetector.TypeReporter() {
                @Override
                @SuppressWarnings("unchecked")
                public Class<? extends Annotation>[] annotations() { return new Class[]{Path.class}; }

                @Override
                public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
                    resourceClassNames.add(className);
                }
            };
            final AnnotationDetector cf = new AnnotationDetector(jaxRsResourcesReporter);
            cf.detect();

            // scan each JAX-RS resource for URI templates
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

    public JaxRsHandler(ObjectMapper objectMapper) {
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

                // check if the HttpMethod matches
                HttpMethod annotatedHttpMethod = null;
                Annotation[] methodAnnotations = method.getAnnotations();
                for (Annotation annotation : methodAnnotations) {
                    annotatedHttpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
                    if (annotatedHttpMethod != null);
                        break;
                }

                if (request.method().name().equalsIgnoreCase(annotatedHttpMethod.value())) {
                    // extract parameters from the uri
                    parameters = new ArrayList<Object>();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    LOGGER.debug("Found matching JAX-RS resource {}", method);
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        String parameterAsString = matcher.group(i);
                        Object parameter = Converters.fromString(parameterTypes[i - 1], parameterAsString);
                        LOGGER.debug("Extracted path parameter {}", parameter);
                        parameters.add(parameter);
                    }
                } else {
                    method = null;
                }
            }
        }
        if (method == null) {
            LOGGER.info("Could not locate a JAX-RS resource for path '{}' and method {}",
                    request.uri(), request.method());
            // TODO: return a 404 error
            return;
        }

        // invoke the JAX-RS resource
        LOGGER.debug("Invoking method {} with parameters {}", method, parameters);
        Object result;

        try {
            if (parameters.size() > 0) {
                result = method.invoke(method.getDeclaringClass().newInstance(),
                        parameters.toArray(new Object[]{parameters.size()}));
            } else {
                result = method.invoke(method.getDeclaringClass().newInstance());
            }
        } catch (Exception e) {
            LOGGER.error("Can't invoke JAX-RS resource", e);
            ctx.write(new ApiResponse(request.id(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.wrappedBuffer("Unexpected error".getBytes("UTF-8")), MediaType.TEXT_PLAIN));
            return;
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
