package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.completion.CompletionUtil.DUMMY_IDENTIFIER;

/**
 * 服务全名有两种：
 * 1、服务调用：包名.动作#名称
 * 2、针对Entity的CRUD，动作#EntityFullName
 */
public class ServiceCallConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter {
    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {
        Project project = context.getProject();
        //获取输入的属性内容

        XmlElement xmlElement = context.getXmlElement();
        if(xmlElement == null) return new ArrayList<String>();

        String curStr = xmlElement.getText();

        String inputStr = MyStringUtils.EMPTY_STRING;
        if(curStr != null) {
            int equalIndex = curStr.indexOf("=");
            if(equalIndex>=0) {
                curStr = curStr.substring(equalIndex+2);
            }
            int index = curStr.indexOf(DUMMY_IDENTIFIER);
            if (index>=0){
                inputStr = curStr.substring(0, index);
            }
            return lookupService(project,inputStr);
        }
        return new ArrayList<String>();
    }


    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        final String serviceCallStr = value.getStringValue();
        if(MyStringUtils.isEmpty(serviceCallStr)) return PsiReference.EMPTY_ARRAY;

        String[] contentArray = serviceCallStr.split("#");
        if(contentArray.length!=2) return PsiReference.EMPTY_ARRAY;
        Project project = context.getProject();

        if(ServiceUtils.STANDARD_CRUD_COMMANDER.contains(contentArray[0])) {
            //针对Entity的标准CRUD
            int startOffset = contentArray[0].length()+2;

            return EntityUtils.createEntityFullNameReferences(project,element,contentArray[1],startOffset);
        }else{
            //标准的ServiceCall

            PsiReference[] result = new PsiReference[contentArray.length];


            return ServiceUtils.createServiceCallReferences(project,element,serviceCallStr,1);

        }


    }

    private List<String> lookupService(@NotNull Project project, @NotNull String inputStr){
        List<String> result = new ArrayList<>();
        int slashIndex = inputStr.indexOf('#');
        String[] slashSplit = inputStr.split("#");
        java.util.Set<String> entitySet =new HashSet<>();
        java.util.Set<String> serviceSet = new HashSet<>();
        java.util.Set<String> crudSet = new HashSet<>();
        if(slashIndex >= 0) {
            //#存在，需要进行进一步判断
            int pointIndex = slashSplit[0].lastIndexOf('.');
            if(pointIndex<0) {
                //标准操作,CRUD
                //操作不存在，则返回空
                if(! ServiceUtils.STANDARD_CRUD_COMMANDER.contains(slashSplit[0])) return result;
                //操作存在，则返回entityNameList，需要根据后半部分进行过滤
                if(slashSplit.length==1) {
                    entitySet.addAll(EntityUtils.getEntityFullNames(project,""));
                }else {
                    int backPointIndex = slashSplit[1].lastIndexOf('.');
                    if(backPointIndex<0) {
                        entitySet.addAll(EntityUtils.getEntityFullNames(project,""));
                    }else {
                        var packageName = slashSplit[1].substring(0, backPointIndex);
                        entitySet.addAll(EntityUtils.getEntityFullNames(project,packageName));
                    }
                }
                //将CRUD放到entityName前面，以便识别
                entitySet = entitySet.stream().map(name->slashSplit[0] + "#" + name).collect(Collectors.toSet());

            }else{
                //Service Call
                var className= slashSplit[0].substring(0, pointIndex);

                getServiceCallToSet(project,className,serviceSet);


            }
        }else {
            //#不存在，也需要进一步判断是否有“.”，没有这这个字符则需要将CRUD操作放入返回列表中
            int pointIndex = inputStr.lastIndexOf('.');
            if(pointIndex<0) {
                //没有，则取所有的className和CRUD
                crudSet.addAll(ServiceUtils.STANDARD_CRUD_COMMANDER);
                getServiceCallToSet(project,"",serviceSet);
            }else {
                //取类的过滤字符串
                var className= inputStr.substring(0, pointIndex);
                getServiceCallToSet(project,className,serviceSet);
//        serviceSet.addAll(ServiceUtils.findServiceClassNameSet(project,className));

            }
        }
        result.addAll(crudSet);
        result.addAll(entitySet);
        result.addAll(serviceSet);

        return result;
    }

    private void getServiceCallToSet(@NotNull Project project, @NotNull String filterClassName, @NotNull Set<String> resultSet){

        resultSet.addAll(ServiceUtils.findServiceClassNameSet(project,filterClassName));
        if (resultSet.size()==1) {
            //当前className是个完整的名称，则取该class下的所有服务
            resultSet.clear();
            resultSet.addAll(ServiceUtils.getServiceFullNameInClass(project,filterClassName));
        }
    }
}
