package org.moqui.idea.plugin.injector;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import org.intellij.plugins.intelliLang.inject.InjectorUtils;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.InjectGroovyUtils;

import java.util.List;

public class ServiceVisitor implements DomElementVisitor {
    private final Logger logger = Logger.getInstance(ServiceVisitor.class);
    private final List<InjectorUtils.InjectionInfo> list;
    ServiceVisitor(@NotNull List<InjectorUtils.InjectionInfo> list){
        this.list = list;
    }
    @Override
    public void visitDomElement(DomElement element) {
    }
    public void visit(InParameters inParameters){
//        logger.warn("执行了visitInParameters:" + inParameters.toString());
        InjectGroovyUtils.extractInParametersInjectionInfo(inParameters,list);
    }
    public void visit(OutParameters outParameters){
//        logger.warn("执行了visitOutParameters:" + outParameters.toString());
        InjectGroovyUtils.extractOutParametersInjectionInfo(outParameters,list);
    }
    public void visit(Log log){
//        logger.warn("执行了visitLog:" + log.toString());

    }
    public void visit(Set set){
        InjectGroovyUtils.extractSetInjectionInfo(set,list);
    }

    public void visit(Actions actions){
        actions.getSetList().forEach(set->{set.accept(this);});
        actions.getLogList().forEach(item-> item.accept(this));
    }

    public void visit(Service service) {
//        logger.warn("执行了visitService:" + service.toString());
        service.getInParameters().accept(this);
        //Out parameters不需要进行处理
//        service.getOutParameters().accept(this);

        service.getActions().accept(this);


    }

    public List<InjectorUtils.InjectionInfo> getList() {
        return list;
    }
}
