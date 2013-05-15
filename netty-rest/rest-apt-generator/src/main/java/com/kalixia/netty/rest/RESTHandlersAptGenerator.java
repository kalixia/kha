package com.kalixia.netty.rest;

import com.squareup.java.JavaWriter;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.squareup.java.JavaWriter.stringLiteral;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;

@SupportedAnnotationTypes({ "javax.ws.rs.*" /*, "dagger.Provides", "dagger.Module" */ })
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RESTHandlersAptGenerator extends AbstractProcessor {
    public static final String GENERATOR_NAME = "netty-rest";
    private ProcessingEnvironment environment;
    private Filer filer;
    private Elements elementUtils;
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment environment) {
        this.environment = environment;
        this.filer = environment.getFiler();
        this.elementUtils = environment.getElementUtils();
        this.messager = environment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> resources = roundEnv.getRootElements();
        for (Element resource : resources) {
            PackageElement resourcePackage = elementUtils.getPackageOf(resource);
            String resourceClassName = resource.getSimpleName().toString();
            String resourceFQN = resourcePackage.toString() + '.' + resourceClassName;
            // HACK: why should I skip generated files like this??
            if (resourceClassName.toString().endsWith("Handler"))
                continue;
            System.out.printf("About to process JAX-RS resource '%s'%n", resourceClassName);
            Writer handlerWriter = null;
            try {
                String handlerClassName = resourceFQN + "Handler";
                JavaFileObject handlerFile = filer.createSourceFile(handlerClassName, resource);
                System.out.printf("About to generate file %s%n", handlerFile.getName());
                handlerWriter = handlerFile.openWriter();
                JavaWriter writer = new JavaWriter(handlerWriter);
                writer
                        .emitPackage(resourcePackage.toString())
                        // add imports
                        .emitImports("com.kalixia.netty.rest.ApiRequest")
                        .emitImports("io.netty.channel.ChannelHandlerContext")
                        .emitImports("io.netty.channel.ChannelHandler.Sharable")
                        .emitImports("io.netty.handler.codec.MessageToMessageDecoder")
                        .emitImports("io.netty.buffer.MessageBuf")
                        .emitImports(Generated.class.getName())
                        .emitEmptyLine()
                        // begin class
                        .emitJavadoc(String.format("Netty handler for JAX-RS resource {@link %s}.", resourceClassName))
                        .emitAnnotation(Generated.class.getSimpleName(), stringLiteral(GENERATOR_NAME))
                        .emitAnnotation("Sharable")
                        .beginType(handlerClassName, "class", PUBLIC | FINAL, "MessageToMessageDecoder<ApiRequest>")
                        // add delegate to underlying JAX-RS resource
                        .emitJavadoc("Delegate for the JAX-RS resource")
                        .emitField(resourceClassName.toString(), "delegate", PRIVATE,
                                String.format("new %s()", resourceClassName));
//                generateAcceptInboundMessage(writer);
                generateDecodeMethod(writer);
                // end class
                writer.endType();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (handlerWriter != null) {
                    try {
                        handlerWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return false;
    }

    private JavaWriter generateAcceptInboundMessage(JavaWriter writer) throws IOException {
        return writer
                // add acceptInboundMessage() method
                .emitEmptyLine()
                .emitJavadoc("Checks if the path %s matches one declared by the resource", "/dummy")   // TODO: use real param value
                .emitAnnotation(Override.class.getSimpleName())
                .beginMethod("boolean", "acceptInboundMessage", PUBLIC, "Object", "msg")
                // method implementation
                .emitStatement("return true")
                .endMethod()
                .emitEmptyLine();
    }

    private JavaWriter generateDecodeMethod(JavaWriter writer) throws IOException {
        return writer
                // add decode() method
                .emitEmptyLine()
                .emitJavadoc("Process {@link ApiRequest}s for path %s", "/dummy")                       // TODO: use real param value
                .emitAnnotation(Override.class.getSimpleName())
                .beginMethod("void", "decode", PROTECTED,
                        "ChannelHandlerContext", "ctx", "ApiRequest", "msg", "MessageBuf<Object>", "out")
                        // method implementation
                .emitStatement("out.add(msg)")
                .endMethod()
                .emitEmptyLine();
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptyList();
    }

}
