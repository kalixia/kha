package com.kalixia.netty.rest;

import com.kalixia.netty.rest.codecs.jaxrs.UriTemplateUtils;
import com.squareup.java.JavaWriter;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static com.squareup.java.JavaWriter.stringLiteral;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

public class JaxRsMethodGenerator {
    private final Filer filer;
    private final Messager messager;
    private final JaxRsAnalyzer analyzer = new JaxRsAnalyzer();

    public JaxRsMethodGenerator(Filer filer, Messager messager) {
        this.filer = filer;
        this.messager = messager;
    }

    public String generateHandlerClass(String resourceClassName, PackageElement resourcePackage,
                                        String uriTemplate, JaxRsMethodInfo method) {
        Writer handlerWriter = null;
        try {
            // TODO: only uppercase the first character
            String resourceFQN = resourcePackage.toString() + '.' + resourceClassName;
            String methodNameCamel = method.getMethodName()/*.toUpperCase()*/;
            String handlerClassName = String.format("%s_%s_Handler", resourceFQN, methodNameCamel);
            JavaFileObject handlerFile = filer.createSourceFile(handlerClassName);
            handlerWriter = handlerFile.openWriter();
            JavaWriter writer = new JavaWriter(handlerWriter);
            writer
                    .emitPackage(resourcePackage.toString())
                            // add imports
                    .emitImports("com.kalixia.netty.rest.ApiRequest")
                    .emitImports("com.kalixia.netty.rest.ApiResponse")
                    .emitImports("com.kalixia.netty.rest.codecs.jaxrs.GeneratedJaxRsMethodHandler")
                    .emitImports("com.kalixia.netty.rest.codecs.jaxrs.UriTemplateUtils")
                    .emitImports("com.kalixia.netty.rest.codecs.jaxrs.converters.Converters")
                    .emitImports("com.fasterxml.jackson.databind.ObjectMapper")
//                        .emitImports("io.netty.channel.ChannelHandler.Sharable")
                    .emitImports("io.netty.buffer.Unpooled")
                    .emitImports("io.netty.handler.codec.http.HttpMethod")
                    .emitImports("io.netty.handler.codec.http.HttpResponseStatus")
                    .emitImports("javax.ws.rs.core.MediaType")
                    .emitImports("java.util.Map")
                    .emitImports(Generated.class.getName())
                    .emitEmptyLine()
                            // begin class
                    .emitJavadoc(String.format("Netty handler for JAX-RS resource {@link %s#%s}.", resourceClassName,
                            method.getMethodName()))
                    .emitAnnotation(Generated.class.getSimpleName(), stringLiteral(StaticAnalysisCompiler.GENERATOR_NAME))
//                        .emitAnnotation("Sharable")
                    .beginType(handlerClassName, "class", PUBLIC | FINAL, null, "GeneratedJaxRsMethodHandler")
                            // add delegate to underlying JAX-RS resource
                    .emitJavadoc("Delegate for the JAX-RS resource")
                    .emitField(resourceClassName, "delegate", PRIVATE,
                            String.format("new %s()", resourceClassName))
                    .emitField("ObjectMapper", "objectMapper", PRIVATE | FINAL)
                    .emitField("String", "URI_TEMPLATE", PRIVATE | STATIC | FINAL, stringLiteral(uriTemplate))
            ;
            generateConstructor(writer, handlerClassName);
            generateMatchesMethod(writer, method);
            generateHandleMethod(writer, method);
            // end class
            writer.endType();

            return handlerClassName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (handlerWriter != null) {
                try {
                    handlerWriter.close();
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Can't close generated source file");
                }
            }
        }
    }

    private JavaWriter generateConstructor(JavaWriter writer, String handlerClassName) throws IOException {
        return writer
                .emitEmptyLine()
                .beginMethod(null, handlerClassName, PUBLIC, "ObjectMapper", "objectMapper")
                .emitStatement("this.objectMapper = objectMapper")
                .endMethod();
    }

    private JavaWriter generateMatchesMethod(JavaWriter writer, JaxRsMethodInfo methodInfo)
            throws IOException {
        writer
                .emitEmptyLine()
                .emitAnnotation(Override.class)
                .beginMethod("boolean", "matches", PUBLIC, "ApiRequest", "request");

        // check against HTTP method
        writer.emitStatement("boolean verbMatches = HttpMethod.%s.equals(request.method())", methodInfo.getVerb());

        // check against URI template
        if (UriTemplateUtils.hasParameters(methodInfo.getUriTemplate()))
            writer.emitStatement("boolean uriMatches = UriTemplateUtils.extractParameters(URI_TEMPLATE, request.uri()).size() > 0");
        else
            writer.emitStatement("boolean uriMatches = %s.equals(request.uri())", stringLiteral(methodInfo.getUriTemplate()));

        // return result
        writer.emitStatement("return verbMatches && uriMatches");

        return writer.endMethod();
    }

    private JavaWriter generateHandleMethod(JavaWriter writer, JaxRsMethodInfo methodInfo)
            throws IOException {
        writer
                .emitEmptyLine()
                .emitAnnotation(Override.class)
                .beginMethod("ApiResponse", "handle", PUBLIC, "ApiRequest", "request");

        // analyze @PathParam annotations
        Map<String, String> parametersMap = analyzer.analyzePathParamAnnotations(methodInfo);

        writer.beginControlFlow("try");

        // check if JAX-RS resource method has parameters; if so extract them from URI
        if (methodInfo.hasParameters()) {
            writer.emitStatement("Map<String, String> parameters = UriTemplateUtils.extractParameters(URI_TEMPLATE, request.uri())");
            // extract each parameter
            for (JaxRsParamInfo parameter : methodInfo.getParameters()) {
                String uriTemplateParameter = parametersMap.get(parameter.getName());
                if (uriTemplateParameter == null) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Missing binding to parameter '%s'", parameter.getName()),
                            parameter.getElement());
                }

                TypeMirror type = parameter.getType();
                if (String.class.getName().equals(type.toString())) {
                    writer.emitStatement("String %s = parameters.get(\"%s\")",
                            parameter.getName(), uriTemplateParameter);
                } else if (type.toString().startsWith("java.lang")) {
                    String shortName = type.toString().substring(type.toString().lastIndexOf('.') + 1);
                    writer.emitStatement("%s %s = %s.parse%s(parameters.get(\"%s\"))",
                            shortName, parameter.getName(), shortName, shortName, uriTemplateParameter);
                } else if (type.getKind().isPrimitive()) {
                    char firstChar = type.toString().charAt(0);
                    String shortName = Character.toUpperCase(firstChar) + type.toString().substring(1);
                    writer.emitStatement("%s %s = %s.parse%s(parameters.get(\"%s\"))",
                            type, parameter.getName(), shortName, shortName, uriTemplateParameter);
                } else {
                    writer.emitStatement("%s %s = Converters.fromString(%s.class, parameters.get(\"%s\"))",
                            type, parameter.getName(), type, uriTemplateParameter);
                }
            }
        }

        // call JAX-RS resource method
        if (methodInfo.hasParameters()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < methodInfo.getParameters().size(); i++) {
                JaxRsParamInfo paramInfo = methodInfo.getParameters().get(i);
                builder.append(paramInfo.getName());
                if (i + 1 < methodInfo.getParameters().size())
                    builder.append(", ");
            }
            if (methodInfo.hasReturnType())
                writer.emitStatement("Object result = delegate.%s(%s)", methodInfo.getMethodName(), builder.toString());
            else
                writer.emitStatement("delegate.%s(%s)", methodInfo.getMethodName(), builder.toString());
        } else if (methodInfo.hasReturnType()) {
            writer.emitStatement("Object result = delegate.%s()", methodInfo.getMethodName());
        } else {
            writer.emitStatement("delegate.%s()", methodInfo.getMethodName());
        }

        // convert result only if there is one
        if (methodInfo.hasReturnType()) {
            writer.emitStatement("byte[] content = objectMapper.writeValueAsBytes(result)")
                    // return ApiResponse object
                    .emitStatement("return new ApiResponse(request.id(), HttpResponseStatus.OK, " +
                            "Unpooled.wrappedBuffer(content), MediaType.APPLICATION_JSON)");
        } else {
            writer.emitStatement("return new ApiResponse(request.id(), HttpResponseStatus.NO_CONTENT, " +
                    "Unpooled.EMPTY_BUFFER, MediaType.APPLICATION_JSON)");
        }

        writer
                .nextControlFlow("catch (IllegalArgumentException e)")
                .emitStatement("return new ApiResponse(request.id(), HttpResponseStatus.BAD_REQUEST, " +
                        "Unpooled.copiedBuffer(e.getMessage().getBytes()), MediaType.TEXT_PLAIN)")
                .nextControlFlow("catch (Exception e)")
                .emitStatement("return new ApiResponse(request.id(), HttpResponseStatus.INTERNAL_SERVER_ERROR, " +
                        "Unpooled.copiedBuffer(e.getMessage().getBytes()), MediaType.TEXT_PLAIN)")
                .endControlFlow();


        return writer.endMethod();
    }
}
