package org.moqui.idea.plugin.util;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.inject.InjectorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.GroovyLanguage;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexService;
import org.moqui.idea.plugin.service.IndexServiceParameter;
import org.moqui.idea.plugin.service.MoquiIndexService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InjectGroovyUtils {
    private InjectGroovyUtils() {
        throw new UnsupportedOperationException();
    }

    public static  Map<String,String> PARAMETER_TYPE_GROOVY_VARIABLE = Map.ofEntries(
            Map.entry("Map","def %s=[:]"),
            Map.entry("Set","def %s=[] as Set"),
            Map.entry("List","def %s=[:]"),
            Map.entry("id","String %s"),
            Map.entry("","String %s"),
            Map.entry("text-indicator","String %s"),
            Map.entry("text-short","String %s"),
            Map.entry("text-medium","String %s"),
            Map.entry("text-long","String %s"),
            Map.entry("number-integer","Integer %s"),
            Map.entry("number-decimal","Decimal %s"),
            Map.entry("date-time","Date %s")
    );

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
                "def context=[ : ]" +
                System.lineSeparator() +
                "def result = [ : ]"+
                System.lineSeparator()
                ;
    }

    /**
     * 获取当前element所在的Service的in和out参数定义，
     * @param element
     * @return
     */
    public static String getServiceInAndOutParameterDefine(@NotNull PsiElement element){
        Optional<Service> serviceOptional = MyDomUtils.getLocalDomElementByPsiElement(element,Service.class);
        return serviceOptional.map(service ->{
            MoquiIndexService moquiIndexService = element.getProject().getService(MoquiIndexService.class);
            Optional<IndexService> indexServiceOptional = moquiIndexService.getIndexServiceByFullName(ServiceUtils.getFullNameFromService(service));
            return indexServiceOptional.map(indexService -> {
                StringBuilder stringBuilder = new StringBuilder();

                indexService.getInParameterMap().forEach((key,value)->{
                    stringBuilder.append(getVariableDefineFromIndexServiceParameter(value));
                });
                indexService.getOutParameterMap().forEach((key,value)->{
                    stringBuilder.append(getVariableDefineFromIndexServiceParameter(value));
                });
                return stringBuilder.toString();
            }).orElse(MyStringUtils.EMPTY_STRING);

        }).orElse(MyStringUtils.EMPTY_STRING);

    }

    /**
     * 考虑到groovy的灵活性，只需要定义到第一层变量，第二层及以后的变量很少使用
     * @param parameter
     * @return
     */
    private static String getVariableDefineFromIndexServiceParameter(IndexServiceParameter parameter){
        String result;
        if(PARAMETER_TYPE_GROOVY_VARIABLE.containsKey(parameter.getType())) {
            result = PARAMETER_TYPE_GROOVY_VARIABLE.get(parameter.getType());
        }else {
            result = parameter.getType()+ " %s";
        }
        return String.format(result,parameter.getParameterName()) + System.lineSeparator();

//        switch(parameter.getType()) {
//            case "Map"->{
//                    return "def " + parameter.getParameterName()+"=[:]" + System.lineSeparator();
//            }
//            case "Set"->{return "def " + parameter.getParameterName()+"= [] as Set" + System.lineSeparator();}
//            case "List"->{return "def " + parameter.getParameterName()+"=[]" + System.lineSeparator();}
//            case MyStringUtils.EMPTY_STRING -> { return "String "+ parameter.getParameterName()+ System.lineSeparator();}
//            case "id"->{return "String "+ parameter.getParameterName()+ System.lineSeparator();}
//            case "text-indicator"->{return "Char(1) "+ parameter.getParameterName()+ System.lineSeparator();}
//            case "text-short"->{return "String "+ parameter.getParameterName()+ System.lineSeparator();}
//            case "text-medium"->{return "String "+ parameter.getParameterName()+ System.lineSeparator();}
//            case "text-long"->{return "String "+ parameter.getParameterName()+ System.lineSeparator();}
//            case "number-integer"->{return "Integer "+ parameter.getParameterName()+ System.lineSeparator();}
//            case "number-decimal "->{return "Decimal "+ parameter.getParameterName()+ System.lineSeparator();}
//
//            case "date-time"->{return "Date "+ parameter.getParameterName()+ System.lineSeparator();}
//
//            default -> { return parameter.getType() +" " + parameter.getParameterName()+ System.lineSeparator(); }
//        }
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
