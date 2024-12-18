package org.moqui.idea.plugin.injector;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import com.intellij.psi.xml.XmlText;
import org.intellij.plugins.intelliLang.inject.InjectorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.GroovyLanguage;
import org.moqui.idea.plugin.dom.model.Script;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 由于将所有actions内容都考虑进来需要定制基于xml的PsiLanguageInjectionHost，比较复杂，先只考虑script部分的脚本。
 * 其他单行的groovy脚本通过moqui-xml-injections.xml来实现。
 *
 */
public class MoquiGroovyToXmlInjector implements MultiHostInjector {
    private static final Logger logger = Logger.getInstance(MoquiGroovyToXmlInjector.class);

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if(shouldInject(context)) {
//            logger.warn("线程名称：" + Thread.currentThread().getName()+"; context:" + context.getText());
            doActionsInject(registrar,context);

        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
//        return List.of(XmlText.class);
        return Collections.singletonList(XmlTag.class);
    }

    private static boolean shouldInject(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();

        if(ServiceUtils.isServicesFile(psiFile)
                || ScreenUtils.isScreenFile(psiFile)
                || EecaUtils.isEecasFile(psiFile)
                || SecaUtils.isSecasFile(psiFile)
        ) {
            if (element instanceof XmlTag xmlTag) {
                return xmlTag.getName().equals(Script.TAG_NAME);
//                return xmlTag.getName().equals(Service.TAG_NAME);
            }
        }
        return false;
    }
    private static void doServiceInject(@NotNull MultiHostRegistrar registrar,@NotNull PsiElement context) {
        Service service = MyDomUtils.getLocalDomElementByXmlTag((XmlTag) context,Service.class).orElse(null);
        if(service == null) return;
        if(service.getXmlTag() == null) return;

        List<InjectorUtils.InjectionInfo> list = new ArrayList<>();


        ServiceVisitor serviceVisitor = new ServiceVisitor(list);
        service.accept(serviceVisitor);

        if(!list.isEmpty()) {
            //插入缺省的部分
            InjectGroovyUtils.extractServiceHeaderInjectionInfo(service,list);

            //需要对list进行排序，按所在host的先后次序排列，否则报错
            list.sort(new Comparator<InjectorUtils.InjectionInfo>() {
                @Override
                public int compare(InjectorUtils.InjectionInfo injectionInfo, InjectorUtils.InjectionInfo t1) {
                    int firstOffset = MyDomUtils.getPsiElementOffsetOrZero(injectionInfo.host().getContext());
                    int secondOffset = MyDomUtils.getPsiElementOffsetOrZero(t1.host().getContext());
                    return firstOffset-secondOffset;
                }
            });

            InjectorUtils.registerInjection(GroovyLanguage.INSTANCE, context.getContainingFile(), list, registrar);
        }

////        PsiLanguageInjectionHost host = context.getLanguageInjectionHost();
////        PsiLanguageInjectionHost host = PsiTreeUtil.getParentOfType(context, PsiLanguageInjectionHost.class);
//        PsiLanguageInjectionHost host ;
//
//        TextRange serviceTextRange = service.getXmlTag().getTextRange();
//
//        registrar.startInjecting(GroovyLanguage.INSTANCE);
//
//        String prefixString =getDefaultImportPacakges();
////        prefixString = prefixString+System.lineSeparator() +
////                "def "+getServiceFunctionName(service)+"(){" + System.lineSeparator();
//        //注入函数头
////        PsiElement firstPsiElement = service.getXmlTag().getPrevSibling();
////        if(firstPsiElement instanceof XmlText firstHost){
////            host = (PsiLanguageInjectionHost) firstHost;
////            registrar.addPlace(prefixString, System.lineSeparator() ,
////                    host,
////                    new TextRange(0, 0));
////        }else {
//            XmlText firstXmlText = MyDomUtils.getXmlTagFirstChildXmlText(service.getXmlTag()).orElse(null);
//            if(firstXmlText == null) {return;}
//
//            host = (PsiLanguageInjectionHost) firstXmlText;
//
//            registrar.addPlace(prefixString, System.lineSeparator() ,
//                    host,
//                    new TextRange(0, 0))
//            ;
////        }
//        for(Parameter parameter: service.getInParameters().getParameterList()) {
//            if(parameter.getName().getXmlAttributeValue() == null) continue;
//            String parameterName = MyDomUtils.getValueOrEmptyString(parameter.getName());
//            TextRange nameTextRange = parameter.getName().getXmlAttributeValue().getTextRange();
//            host = (PsiLanguageInjectionHost) parameter.getName().getXmlAttributeValue();
//            XmlAttributeValue defaultValue = parameter.getDefaultValue().getXmlAttributeValue();
//            if(defaultValue == null) {
//                registrar.addPlace("def ", System.lineSeparator(), host,
//                        new TextRange(1, parameterName.length()+1));
//            }else {
//                registrar.addPlace("def ", "", host,
//                        new TextRange(1, parameterName.length()));
//                registrar.addPlace("=",System.lineSeparator(),
//                        (PsiLanguageInjectionHost) defaultValue,
//                        new TextRange(1,defaultValue.getTextLength()));
//            }
//        }
//
//        for(Parameter parameter: service.getOutParameters().getParametersList()) {
//            if(parameter.getName().getXmlAttributeValue() == null) continue;
//            String parameterName = MyDomUtils.getValueOrEmptyString(parameter.getName());
//            TextRange nameTextRange = parameter.getName().getXmlAttributeValue().getTextRange();
//            host = (PsiLanguageInjectionHost) parameter.getName().getXmlAttributeValue();
//            registrar.addPlace("def ",System.lineSeparator(),host,
//                    new TextRange(1,parameterName.length()+1));
//
//        }
//        for(Set set: service.getActions().getSetList()) {
//            if(set.getField().getXmlAttributeValue() == null) continue;
//            String fieldString = MyDomUtils.getValueOrEmptyString(set.getField());
//            String valueString = MyDomUtils.getValueOrEmptyString(set.getValue());
//
//            TextRange fieldTextRange = set.getField().getXmlAttributeValue().getTextRange();
////            TextRange valueTextRange = set.getValue().getXmlAttributeValue().getTextRange();
//            host = (PsiLanguageInjectionHost) set.getField().getXmlAttributeValue();
//            registrar.addPlace(" "," = ",host,
//                            new TextRange(1,fieldString.length()+1));
//
//            XmlAttributeValue valueAttributeValue = set.getValue().getXmlAttributeValue();
//            if(valueAttributeValue !=null) {
//                host = (PsiLanguageInjectionHost) valueAttributeValue;
//                registrar.addPlace("\"", "\""+System.lineSeparator(), host,
//                        new TextRange(1, valueAttributeValue.getValue().length()+1));
//            }else {
//                XmlAttributeValue fromAttributeValue = set.getFrom().getXmlAttributeValue();
//                if(fromAttributeValue != null) {
//                    host = (PsiLanguageInjectionHost) fromAttributeValue;
//                    registrar.addPlace(" ", System.lineSeparator(), host,
//                            new TextRange(1, fromAttributeValue.getValue().length()+1));
//
//                }
//            }
//
//        }
//        for(Log log: service.getActions().getLogList()) {
//            XmlAttributeValue levelAttributeValue = log.getLevel().getXmlAttributeValue();
//            XmlAttributeValue messageAttributeValue = log.getMessage().getXmlAttributeValue();
//            if(messageAttributeValue == null) continue;
//            if(levelAttributeValue != null) {
//                host = (PsiLanguageInjectionHost) levelAttributeValue;
//                registrar.addPlace("ec.logger.log(", ",", host,
//                        new TextRange(1, levelAttributeValue.getValue().length()+1));
//
//                host = (PsiLanguageInjectionHost) messageAttributeValue;
//                registrar.addPlace("\"", "\", null)"+ System.lineSeparator(), host,
//                        new TextRange(1, messageAttributeValue.getValue().length()+1));
//
//            }else {
//                host = (PsiLanguageInjectionHost) messageAttributeValue;
//                registrar.addPlace("ec.logger.log(info, \"", "\", null)"+ System.lineSeparator(), host,
//                        new TextRange(1, messageAttributeValue.getValue().length()+1));
//
//            }
//
//
//        }
//
        //注入函数尾

//        PsiElement nextPsiElement = service.getXmlTag().getNextSibling();
//        if(nextPsiElement instanceof XmlText lastHost){
//            String lastString = "}";
//            host = (PsiLanguageInjectionHost) lastHost;
//            registrar.addPlace(System.lineSeparator(), lastString,
//                            host,
//                            new TextRange(0,0));
//        }

//        registrar.doneInjecting();

    }
    private static void doScriptInject(@NotNull MultiHostRegistrar registrar,@NotNull PsiElement context) {
        Script script = MyDomUtils.getLocalDomElementByXmlTag((XmlTag) context,Script.class).orElse(null);
        if(script == null) return;
        if(script.getXmlTag() == null) return;
        XmlText[] textElementArray = script.getXmlTag().getValue().getTextElements();
        if(textElementArray.length == 0) return;

        PsiLanguageInjectionHost host = (PsiLanguageInjectionHost) script.getXmlTag().getValue().getTextElements()[0];

        String inAndOutParameter = InjectGroovyUtils.getServiceInAndOutParameterDefine(context).orElse(null);

        String sbPrefix = InjectGroovyUtils.getDefaultImportPackages();

        XmlTagValue xmlTagValue = script.getXmlTag().getValue();
        String xmlText = xmlTagValue.getText();
        int beginOffset = 0;
        int endOffset = xmlText.length();

        if(xmlText.startsWith("<![CDATA[")) {//length 为 9
            beginOffset = beginOffset + 9;
        }
        if(xmlText.endsWith("]]>")) { //lenght 为 3
            endOffset = endOffset -3;
        }

        TextRange textRange = new TextRange(beginOffset, endOffset);
        registrar
                .startInjecting(GroovyLanguage.INSTANCE)
                .addPlace(sbPrefix + inAndOutParameter,null,
                        host,
                        textRange)
                .doneInjecting();

    }
    private void doActionsInject(@NotNull MultiHostRegistrar registrar,@NotNull PsiElement context){
//        doServiceInject(registrar,context);

        doScriptInject(registrar,context);


    }



    private static String getServiceFunctionName(@NotNull Service service) {
        return MyDomUtils.getValueOrEmptyString(service.getVerb())+
                MyDomUtils.getValueOrEmptyString(service.getNoun());
    }



}
