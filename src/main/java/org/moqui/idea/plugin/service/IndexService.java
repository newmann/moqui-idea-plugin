package org.moqui.idea.plugin.service;

import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.*;

public final class IndexService {
    private final String verb;
    private final String noun;

    private final String className;
    private final String packageName;
    private final String fullName;
    private final String type;
    private final Service service;


    private Map<String, IndexServiceParameter> inParameterMap = new HashMap<>();
    private Map<String, IndexServiceParameter> outParameterMap = new HashMap<>();

    IndexService(@NotNull Service service){
        this.service =service;
        this.verb = MyDomUtils.getValueOrEmptyString(service.getVerb());
        this.noun = MyDomUtils.getValueOrEmptyString(service.getNoun());
        this.type = MyDomUtils.getValueOrEmptyString(service.getType());
        XmlElement xmlElement = service.getXmlElement();
        if(xmlElement == null) {
            this.className = MyStringUtils.EMPTY_STRING;
        }else {
            this.className = ServiceUtils.extractClassNameFromPath(xmlElement.getContainingFile().getVirtualFile().getPath())
                    .orElse(MyStringUtils.EMPTY_STRING);
        }
        this.packageName = getPackageNameFromClassName(this.className);
        this.fullName =  this.className + ServiceUtils.SERVICE_NAME_DOT
                +this.verb + ServiceUtils.SERVICE_NAME_DELIMITER + this.noun;

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

    public Service getService(){return this.service;}

    public void setInParameterMap(Map<String, IndexServiceParameter> inParameterMap){
        this.inParameterMap = inParameterMap;
    }
    public void setOutParameterMap(Map<String, IndexServiceParameter> outParameterMap){
        this.outParameterMap = outParameterMap;
    }



    public Optional<List<String>> getInParametersNameList(){
        return Optional.of(inParameterMap.keySet().stream().toList());

    }
    public Optional<List<String>> getOutParametersNameList(){
        return Optional.of(outParameterMap.keySet().stream().toList());

    }
    public Optional<List<IndexServiceParameter>> getInParametersAbstractFieldList(){
        return Optional.of(new ArrayList<>(inParameterMap.values()));

    }
    public Optional<List<IndexServiceParameter>> getOutParametersAbstractFieldList(){
        return Optional.of(new ArrayList<>(outParameterMap.values()));
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
