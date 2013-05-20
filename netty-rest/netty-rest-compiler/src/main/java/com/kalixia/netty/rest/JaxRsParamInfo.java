package com.kalixia.netty.rest;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import java.util.List;

class JaxRsParamInfo {
    private final String name;
    private final TypeMirror type;
    private final List<? extends AnnotationMirror> annotations;

    JaxRsParamInfo(String name, TypeMirror type, List<? extends AnnotationMirror> annotations) {
        this.name = name;
        this.type = type;
        this.annotations = annotations;
    }

    String getName() {
        return name;
    }

    TypeMirror getType() {
        return type;
    }

    List<? extends AnnotationMirror> getAnnotations() {
        return annotations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JaxRsParamInfo{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", annotations=").append(annotations);
        sb.append('}');
        return sb.toString();
    }
}
