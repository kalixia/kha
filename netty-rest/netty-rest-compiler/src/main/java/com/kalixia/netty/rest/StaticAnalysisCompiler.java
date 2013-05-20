package com.kalixia.netty.rest;

import com.kalixia.netty.rest.codecs.jaxrs.UriTemplateUtils;
import com.squareup.java.JavaWriter;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.squareup.java.JavaWriter.stringLiteral;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

@SupportedAnnotationTypes({ "javax.ws.rs.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StaticAnalysisCompiler extends AbstractProcessor {
    public static final String GENERATOR_NAME = "netty-rest";
    private Filer filer;
    private Elements elementUtils;
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment environment) {
        super.init(environment);
        this.filer = environment.getFiler();
        this.elementUtils = environment.getElementUtils();
        this.messager = environment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> resources = roundEnv.getElementsAnnotatedWith(Path.class);

        for (Element resource : resources) {
            // only keep classes
            if (!resource.getKind().isClass())
                continue;

            // extract class being analyzed
            PackageElement resourcePackage = elementUtils.getPackageOf(resource);
            String resourceClassName = resource.getSimpleName().toString();

            List<? extends Element> enclosedElements = resource.getEnclosedElements();
            for (Element elem : enclosedElements) {
                if (!ElementKind.METHOD.equals(elem.getKind()))
                    continue;
                ExecutableElement methodElement = (ExecutableElement) elem;

                // figure out if @GET, @POST, @DELETE, @PUT, etc are annotated on the method
                String verb = extractVerb(elem);
                if (verb == null)
                    continue;
                String uriTemplate = extractUriTemplate(resource, elem);
                String methodName = elem.getSimpleName().toString();
                String returnType = methodElement.getReturnType().toString();
                List<JaxRsParamInfo> parameters = extractParameters(methodElement);
                JaxRsMethodInfo methodInfo = new JaxRsMethodInfo(elem, verb, uriTemplate, methodName, returnType, parameters);
                generateHandlerClass(resourceClassName, resourcePackage, uriTemplate, methodInfo);
            }
        }
        return false;
    }

    private String extractVerb(Element elem) {
        Annotation annotation;

        // check for GET method
        annotation = elem.getAnnotation(GET.class);
        if (annotation != null)
            return HttpMethod.GET;

        // check for POST method
        annotation = elem.getAnnotation(POST.class);
        if (annotation != null)
            return HttpMethod.POST;

        // check for PUT method
        annotation = elem.getAnnotation(PUT.class);
        if (annotation != null)
            return HttpMethod.PUT;

        // check for DELETE method
        annotation = elem.getAnnotation(DELETE.class);
        if (annotation != null)
            return HttpMethod.DELETE;

        // check for HEAD method
        annotation = elem.getAnnotation(HEAD.class);
        if (annotation != null)
            return HttpMethod.HEAD;

        // check for OPTIONS method
        annotation = elem.getAnnotation(OPTIONS.class);
        if (annotation != null)
            return HttpMethod.OPTIONS;

        return null;
    }

    private String extractUriTemplate(Element resource, Element element) {
        Path resourcePath = resource.getAnnotation(Path.class);
        Path elementPath = element.getAnnotation(Path.class);
        if (resourcePath == null) {
            return elementPath == null ? "" : elementPath.value();
        } else {
            return elementPath == null ? resourcePath.value() : resourcePath.value() + '/' + elementPath.value();
        }
    }

    private List<JaxRsParamInfo> extractParameters(ExecutableElement method) {
        List<? extends VariableElement> parameters = method.getParameters();
        List<JaxRsParamInfo> parametersInfo = new ArrayList<>();
        for (VariableElement parameter : parameters) {
            String name = parameter.getSimpleName().toString();
            TypeMirror type = parameter.asType();
            JaxRsParamInfo paramInfo = new JaxRsParamInfo(name, type, parameter);
            parametersInfo.add(paramInfo);
        }
        return parametersInfo;
    }

    private void generateHandlerClass(String resourceClassName, PackageElement resourcePackage,
                                      String uriTemplate, JaxRsMethodInfo method) {
        Writer handlerWriter = null;
        try {
            // TODO: only uppercase the first character
            String resourceFQN = resourcePackage.toString() + '.' + resourceClassName;
            String methodNameCamel = method.getMethodName().toUpperCase();
            String handlerClassName = resourceFQN + methodNameCamel + "Handler";
            JavaFileObject handlerFile = filer.createSourceFile(handlerClassName);
            System.out.printf("About to generate file %s%n", handlerFile.getName());
            handlerWriter = handlerFile.openWriter();
            JavaWriter writer = new JavaWriter(handlerWriter);
            writer
                    .emitPackage(resourcePackage.toString())
                            // add imports
                    .emitImports("com.kalixia.netty.rest.ApiRequest")
                    .emitImports("com.kalixia.netty.rest.ApiResponse")
                    .emitImports("com.kalixia.netty.rest.codecs.jaxrs.GeneratedJaxRsMethodHandler")
                    .emitImports("com.kalixia.netty.rest.codecs.jaxrs.UriTemplateUtils")
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
                    .emitAnnotation(Generated.class.getSimpleName(), stringLiteral(GENERATOR_NAME))
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

    public JavaWriter generateHandleMethod(JavaWriter writer, JaxRsMethodInfo methodInfo)
            throws IOException {
        writer
                .emitEmptyLine()
                .emitAnnotation(Override.class)
                .beginMethod("ApiResponse", "handle", PUBLIC, "ApiRequest", "request");

        // analyze @PathParam annotations
        Map<String, String> parametersMap = analyzePathParamAnnotations(methodInfo);

        // check if JAX-RS resource method has parameters; if so extract them from URI
        if (methodInfo.hasParameters()) {
            writer.emitStatement("Map<String,String> parameters = UriTemplateUtils.extractParameters(URI_TEMPLATE, request.uri())");
            // extract each parameter
            for (JaxRsParamInfo parameter : methodInfo.getParameters()) {
                writer.emitStatement("String %s = parameters.get(\"%s\")",
                        parameter.getName(), parametersMap.get(parameter.getName()));
            }
        }

        writer.beginControlFlow("try");

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
                .nextControlFlow("catch (Exception e)")
                .emitStatement("return new ApiResponse(request.id(), HttpResponseStatus.INTERNAL_SERVER_ERROR, " +
                        "Unpooled.copiedBuffer(e.getMessage().getBytes()), MediaType.TEXT_PLAIN)")
                .endControlFlow();


        return writer.endMethod();
    }

    /**
     * Build a map whose key is the name of the parameter of the JAX-RS resource and whose value is the @PathParam value
     * @param methodInfo the metamodel for the method
     * @return the map of associated parameters
     */
    private Map<String, String> analyzePathParamAnnotations(JaxRsMethodInfo methodInfo) {
        Map<String, String> parametersToUriTemplateParameter = new HashMap<>();
        for (JaxRsParamInfo paramInfo : methodInfo.getParameters()) {
            PathParam pathParam = paramInfo.getElement().getAnnotation(PathParam.class);
            if (pathParam != null) {
                parametersToUriTemplateParameter.put(paramInfo.getName(), pathParam.value());
            }
        }
        return parametersToUriTemplateParameter;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptyList();
    }

}
