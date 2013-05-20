package com.kalixia.netty.rest;

import javax.lang.model.element.Element;
import java.util.List;

class JaxRsMethodInfo {
    private final Element element;
    private final String verb;
    private final String uriTemplate;
    private final String methodName;
    private final String returnType;
    private final List<JaxRsParamInfo> parameters;

    JaxRsMethodInfo(Element element, String verb, String uriTemplate, String methodName, String returnType,
                    List<JaxRsParamInfo> parameters) {
        this.element = element;
        this.verb = verb;
        this.uriTemplate = uriTemplate;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    Element getElement() {
        return element;
    }

    String getVerb() {
        return verb;
    }

    String getUriTemplate() {
        return uriTemplate;
    }

    String getMethodName() {
        return methodName;
    }

    String getReturnType() {
        return returnType;
    }

    List<JaxRsParamInfo> getParameters() {
        return parameters;
    }

    boolean hasParameters() {
        return getParameters().size() > 0;
    }

    boolean hasReturnType() {
        return !"void".equals(returnType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JaxRsMethodInfo{");
        sb.append("element=").append(element);
        sb.append(", verb='").append(verb).append('\'');
        sb.append(", uriTemplate='").append(uriTemplate).append('\'');
        sb.append(", methodName='").append(methodName).append('\'');
        sb.append(", returnType='").append(returnType).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append('}');
        return sb.toString();
    }
}
