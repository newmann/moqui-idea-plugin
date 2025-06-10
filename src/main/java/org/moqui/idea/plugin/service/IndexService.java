package org.moqui.idea.plugin.service;

import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.dom.model.ServiceInclude;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.*;

public final class IndexService extends AbstractIndex {
    private final String verb;
    private final String noun;

    private final String className;
    private final String packageName;
    private final String fullName;
    private final String type;
    private final Service service;
    private final String functionName;

    private final ServiceInclude serviceInclude;
    private Map<String, IndexServiceParameter> inParameterMap = new HashMap<>();
    private Map<String, IndexServiceParameter> outParameterMap = new HashMap<>();
    IndexService(@NotNull ServiceInclude serviceInclude,@NotNull Service includeService){
        this.serviceInclude = serviceInclude;
        this.service = includeService;

        this.verb = MyDomUtils.getValueOrEmptyString(serviceInclude.getVerb());
        this.noun = MyDomUtils.getValueOrEmptyString(serviceInclude.getNoun());
        this.type = MyStringUtils.EMPTY_STRING;
        XmlElement xmlElement = serviceInclude.getXmlElement();
        if(xmlElement == null) {
            this.className = MyStringUtils.EMPTY_STRING;
        }else {
            Optional<String> filePathOptional = MyDomUtils.getFilePathByPsiElement(xmlElement);
            this.className = filePathOptional.map(s -> ServiceUtils.extractClassNameFromPath(s)
                    .orElse(MyStringUtils.EMPTY_STRING)).orElse(MyStringUtils.EMPTY_STRING);
        }
        this.packageName = getPackageNameFromClassName(this.className);
        this.functionName = this.verb + ServiceUtils.SERVICE_NAME_HASH + this.noun;

        this.fullName =  this.className + ServiceUtils.SERVICE_NAME_DOT
                +this.functionName;



    }
    IndexService(@NotNull Service service){
        this.serviceInclude = null;
        this.service =service;
        this.verb = MyDomUtils.getValueOrEmptyString(service.getVerb());
        this.noun = MyDomUtils.getValueOrEmptyString(service.getNoun());
        this.type = MyDomUtils.getValueOrEmptyString(service.getType());
        XmlElement xmlElement = service.getXmlElement();
        if(xmlElement == null) {
            this.className = MyStringUtils.EMPTY_STRING;
        }else {
            Optional<String> filePathOptional = MyDomUtils.getFilePathByPsiElement(xmlElement);
            this.className = filePathOptional.map(s -> ServiceUtils.extractClassNameFromPath(s)
                    .orElse(MyStringUtils.EMPTY_STRING)).orElse(MyStringUtils.EMPTY_STRING);
        }
        this.packageName = getPackageNameFromClassName(this.className);
        this.functionName = this.verb + ServiceUtils.SERVICE_NAME_HASH + this.noun;

        this.fullName =  this.className + ServiceUtils.SERVICE_NAME_DOT
                +this.functionName;

    }

    public String getFullName(){
        return this.fullName;
    }
    public String getVerb(){
        return this.verb;
    }
    public String getNoun(){
        return this.noun;
    }
    public String getClassName(){
        return this.className;
    }
    public String getPackageName(){return this.packageName;}

    public Boolean isServiceInclude() {
        return this.serviceInclude != null;
    }

    public ServiceInclude getServiceInclude() {
        return serviceInclude;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Service getService(){return this.service;}

    public String getType() {
        return type;
    }

    public void setInParameterMap(Map<String, IndexServiceParameter> inParameterMap){
        this.inParameterMap = inParameterMap;
    }
    public void setOutParameterMap(Map<String, IndexServiceParameter> outParameterMap){
        this.outParameterMap = outParameterMap;
    }

    public Map<String, IndexServiceParameter> getInParameterMap() {
        return inParameterMap;
    }

    public Map<String, IndexServiceParameter> getOutParameterMap() {
        return outParameterMap;
    }

    public @NotNull List<String> getInParametersNameList(){
        return inParameterMap.keySet().stream().toList();

    }
    public @NotNull List<String> getOutParametersNameList(){
        return outParameterMap.keySet().stream().toList();

    }
    public List<IndexServiceParameter> getInParametersAbstractFieldList(){
        return new ArrayList<>(inParameterMap.values());

    }
    public Optional<IndexServiceParameter> getInParametersByName(@NotNull String parameterName){
        return Optional.ofNullable(this.inParameterMap.get(parameterName));

    }
    public Optional<IndexServiceParameter> getOutParametersByName(@NotNull String parameterName){
        return Optional.ofNullable(this.outParameterMap.get(parameterName));

    }

    public List<IndexServiceParameter> getOutParametersAbstractFieldList(){
        return new ArrayList<>(outParameterMap.values());
    }

    public boolean isThisService(@NotNull String name){
        return this.fullName.equals(name);
    }

    private @NotNull String getPackageNameFromClassName(@NotNull String className) {
        int index = className.lastIndexOf(ServiceUtils.SERVICE_NAME_DOT);
        if(index >0) {
            return className.substring(0,index);
        }else {
            return MyStringUtils.EMPTY_STRING;
        }
    }
}
