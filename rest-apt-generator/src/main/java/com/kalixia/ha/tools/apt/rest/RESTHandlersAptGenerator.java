package com.kalixia.ha.tools.apt.rest;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.ws.rs.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RESTHandlersAptGenerator implements Processor {
    private static final Set<String> supportedAnnotations = new HashSet<>();

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.emptySet();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        System.out.println("In init!");
        System.out.println("Processing environment: " + processingEnv);
//        supportedAnnotations.add(Path.class.getName());
        supportedAnnotations.add("*");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            System.out.println("Should process " + typeElement);
        }
        return false;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptyList();
    }
}
