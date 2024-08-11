package org.moqui.idea.plugin.util;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.inject.InjectorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.GroovyLanguage;
import org.moqui.idea.plugin.dom.model.*;

import java.util.List;

public class InjectGroovyUtils {
    private InjectGroovyUtils() {
        throw new UnsupportedOperationException();
    }

    public static String getDefaultImportPackages(){
        return System.lineSeparator() +
                "import static org.moqui.util.ObjectUtilities.*" +
                System.lineSeparator() +
                "import static org.moqui.util.CollectionUtilities.*" +
                System.lineSeparator() +
                "import static org.moqui.util.StringUtilities.*" +
                System.lineSeparator() +
                "import java.sql.Timestamp" +
                System.lineSeparator() +
                "import java.sql.Time" +
                System.lineSeparator() +
                "import java.time.*" +
                System.lineSeparator() +
                "import org.moqui.Moqui" +
                System.lineSeparator() +
                "import org.moqui.context.*" +
                System.lineSeparator() +
                "def ec = Moqui.getExecutionContext()" +
                System.lineSeparator() +
                "def context=[String : Object]" +
                System.lineSeparator() +
                "def result = [String : Object]"+
                System.lineSeparator()
                ;
    }

    public static InjectorUtils.InjectionInfo createInjectInfo(@NotNull XmlAttributeValue attributeValue,
                                                               @NotNull String prefix, @NotNull String suffix){
        PsiLanguageInjectionHost host = (PsiLanguageInjectionHost) attributeValue;
        prefix = " ";
        suffix = System.lineSeparator();
        TextRange textRange = MyDomUtils.createAttributeTextRange(attributeValue);
        InjectedLanguage injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(), prefix, suffix, false);
        return (new InjectorUtils.InjectionInfo(host, injectedLanguage, textRange));
    }
    public static void extractServiceHeaderInjectionInfo(@NotNull Service service, @NotNull List<InjectorUtils.InjectionInfo> list) {
        XmlAttributeValue verbAttributeValue = service.getVerb().getXmlAttributeValue();
        if(verbAttributeValue == null ) return;
        PsiLanguageInjectionHost host = (PsiLanguageInjectionHost)verbAttributeValue;

//        if(service.getXmlTag() == null) return;
//        XmlText xmlText = MyDomUtils.getXmlTagFirstChildXmlText(service.getXmlTag()).orElse(null);
//        if(xmlText == null) return;
//        PsiLanguageInjectionHost host = (PsiLanguageInjectionHost)xmlText;

        String prefix = getDefaultImportPackages();
        String suffix = System.lineSeparator();

        TextRange textRange= new TextRange(0,0);
        InjectedLanguage injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(),prefix,suffix,false);
        list.add(new InjectorUtils.InjectionInfo(host,injectedLanguage,textRange));

    }
    public static void extractInParametersInjectionInfo(@NotNull InParameters inParameters, @NotNull List<InjectorUtils.InjectionInfo> list){
        for(Parameter parameter: inParameters.getParameterList()) {
            XmlAttributeValue nameAttributeValue = parameter.getName().getXmlAttributeValue();
            if(nameAttributeValue == null) continue;


//            String parameterName = MyDomUtils.getValueOrEmptyString(parameter.getName());

            PsiLanguageInjectionHost host ;
            String prefix,suffix;
            TextRange textRange;
            InjectedLanguage injectedLanguage;
            XmlAttributeValue defaultValueAttributeValue = parameter.getDefaultValue().getXmlAttributeValue();
            if(defaultValueAttributeValue == null) {

                XmlAttributeValue defaultAttributeValue = parameter.getDefault().getXmlAttributeValue();
                if(defaultAttributeValue == null) {
                    //定义变量
//                    prefix = "def ";
//                    suffix = System.lineSeparator();
//                    textRange = MyDomUtils.createAttributeTextRange(nameAttributeValue);
//                    host = (PsiLanguageInjectionHost) nameAttributeValue;
//
//                    injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(), prefix, suffix, false);
//                    list.add(new InjectorUtils.InjectionInfo(host, injectedLanguage, textRange));
                    list.add(createInjectInfo(nameAttributeValue,"def ",System.lineSeparator()));
                }else {
                    //name部分
//                    prefix = "def ";
//                    suffix = " ";
//                    textRange =MyDomUtils.createAttributeTextRange(nameAttributeValue);
//                    host = (PsiLanguageInjectionHost) nameAttributeValue;
//                    injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(),prefix,suffix,false);
//                    list.add(new InjectorUtils.InjectionInfo(host,injectedLanguage,textRange));
                    list.add(createInjectInfo(nameAttributeValue,"def "," "));

                    //default 部分
//                    prefix = "=\"${";
//                    suffix = "}\" "+System.lineSeparator();
//                    textRange = MyDomUtils.createAttributeTextRange(defaultAttributeValue);
//                    host = (PsiLanguageInjectionHost) defaultAttributeValue;
//                    injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(),prefix,suffix,false);
//                    list.add(new InjectorUtils.InjectionInfo(host,injectedLanguage,textRange));

                    list.add(createInjectInfo(defaultAttributeValue,"=\"${","}\" "+System.lineSeparator()));
                }
            }else {
                //name部分
//                prefix = "def ";
//                suffix = " ";
//                textRange =MyDomUtils.createAttributeTextRange(nameAttributeValue);
//                host = (PsiLanguageInjectionHost) nameAttributeValue;
//                injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(),prefix,suffix,false);
//                list.add(new InjectorUtils.InjectionInfo(host,injectedLanguage,textRange));
                list.add(createInjectInfo(nameAttributeValue,"def "," "));
                //defaultValue 部分
//                prefix = "=\"";
//                suffix = "\" "+System.lineSeparator();
//                textRange = MyDomUtils.createAttributeTextRange(defaultValueAttributeValue);
//                host = (PsiLanguageInjectionHost) defaultValueAttributeValue;
//                injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(),prefix,suffix,false);
//                list.add(new InjectorUtils.InjectionInfo(host,injectedLanguage,textRange));
                list.add(createInjectInfo(defaultValueAttributeValue,"=\"","\" "+System.lineSeparator()));
            }


        }


    }
    public static void extractOutParametersInjectionInfo(@NotNull OutParameters outParameters, @NotNull List<InjectorUtils.InjectionInfo> list) {
        for (Parameter parameter : outParameters.getParametersList()) {
            XmlAttributeValue nameAttributeValue = parameter.getName().getXmlAttributeValue();
            if (nameAttributeValue == null) continue;


//            String parameterName = MyDomUtils.getValueOrEmptyString(parameter.getName());

//            PsiLanguageInjectionHost host;
//            String prefix, suffix;
//            TextRange textRange;
//            InjectedLanguage injectedLanguage;
            XmlAttributeValue defaultAttributeValue = parameter.getDefaultValue().getXmlAttributeValue();
            if (defaultAttributeValue == null) {
//                prefix = "def ";
//                suffix = System.lineSeparator();
//                textRange = MyDomUtils.createAttributeTextRange(nameAttributeValue);
//                host = (PsiLanguageInjectionHost) nameAttributeValue;
//
//                injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(), prefix, suffix, false);
//                list.add(new InjectorUtils.InjectionInfo(host, injectedLanguage, textRange));
                list.add(createInjectInfo(nameAttributeValue,"def ",System.lineSeparator()));

            } else {
                //name部分
//                prefix = "def ";
//                suffix = " ";
//                textRange = MyDomUtils.createAttributeTextRange(nameAttributeValue);
//                host = (PsiLanguageInjectionHost) nameAttributeValue;
//                injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(), prefix, suffix, false);
//                list.add(new InjectorUtils.InjectionInfo(host, injectedLanguage, textRange));
                list.add(createInjectInfo(nameAttributeValue,"def "," "));
                //defaultValue 部分
//                prefix = "=";
//                suffix = System.lineSeparator();
//                textRange = MyDomUtils.createAttributeTextRange(nameAttributeValue);
//                host = (PsiLanguageInjectionHost) defaultAttributeValue;
//                injectedLanguage = InjectedLanguage.create(GroovyLanguage.INSTANCE.getID(), prefix, suffix, false);
//                list.add(new InjectorUtils.InjectionInfo(host, injectedLanguage, textRange));
                list.add(createInjectInfo(defaultAttributeValue,"=",System.lineSeparator()));
            }


        }
    }

    /**
     *
     * @param set
     * @param list
     */
    public static void extractSetInjectionInfo(@NotNull org.moqui.idea.plugin.dom.model.Set set, @NotNull List<InjectorUtils.InjectionInfo> list) {
        /**
         * <#macro set>
         *     <#if .node["@set-if-empty"]?has_content && .node["@set-if-empty"] == "false">
         *     _temp_internal = <#if .node["@type"]?has_content>basicConvert</#if>(
         *              <#if .node["@from"]?has_content>${.node["@from"]}<#elseif .node["@value"]?has_content>"""${.node["@value"]}"""<#else>null</#if>
         *              <#if .node["@default-value"]?has_content> ?: """${.node["@default-value"]}"""</#if>
         *              <#if .node["@type"]?has_content>, "${.node["@type"]}"</#if>)
         *     if (!isEmpty(_temp_internal)) ${.node["@field"]} = _temp_internal
         *     <#else>
         *     ${.node["@field"]} = <#if .node["@type"]?has_content>basicConvert</#if>(
         *              <#if .node["@from"]?has_content>${.node["@from"]}<#elseif .node["@value"]?has_content>"""${.node["@value"]}"""<#else>null</#if>
         *              <#if .node["@default-value"]?has_content> ?: """${.node["@default-value"]}"""</#if>
         *              <#if .node["@type"]?has_content>, "${.node["@type"]}"</#if>)
         *     </#if>
         * </#macro>

         */
        XmlAttributeValue fieldXmlAttributeValue = set.getField().getXmlAttributeValue();
        if(fieldXmlAttributeValue == null) return;
        XmlAttributeValue valueAttributeValue = set.getValue().getXmlAttributeValue();
        XmlAttributeValue typeAttributeValue = set.getType().getXmlAttributeValue();
        XmlAttributeValue fromAttributeValue = set.getFrom().getXmlAttributeValue();
        XmlAttributeValue defaultValueAttributeValue = set.getDefaultValue().getXmlAttributeValue();
        XmlAttributeValue setIfEmptyAttributeValue = set.getSetIfEmpty().getXmlAttributeValue();



        if(typeAttributeValue == null) {

            if (fromAttributeValue != null) {
                //field attribute
                list.add(createInjectInfo(fieldXmlAttributeValue," ","=("));
                list.add(createInjectInfo(fromAttributeValue," ", " "));
            }else {
                if (valueAttributeValue != null) {
                    //field attribute
                    list.add(createInjectInfo(fieldXmlAttributeValue," ","=("));
                    list.add(createInjectInfo(valueAttributeValue," \"", "\""));
                }else {
                    //field attribute
                    list.add(createInjectInfo(fieldXmlAttributeValue," ","=(null"));
                }
            }
        }else {

        }
    }

}
