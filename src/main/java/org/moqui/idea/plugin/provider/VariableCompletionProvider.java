package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.insertHandler.AutoShowByCharInsertHandler;
import org.moqui.idea.plugin.insertHandler.ClearTailInsertHandler;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 为xml中变量提供支持
 * 有点复杂，还没有更好的办法来处理
 */
public class VariableCompletionProvider extends CompletionProvider<CompletionParameters> {
//    private final  static Logger LOGGER = Logger.getInstance(ServiceCallCompletionProvider.class);
    public static VariableCompletionProvider of(){
        return new VariableCompletionProvider() ;
    }
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> VARIABLE_PATTERN =
            PlatformPatterns.psiElement()
                    .inside(
                            XmlPatterns.xmlAttributeValue().andOr(
                                XmlPatterns.xmlAttributeValue(ECondition.ATTR_FROM)
                                        .inside(
                                                XmlPatterns.xmlTag().withLocalName(ECondition.TAG_NAME)
                                        ),
                                XmlPatterns.xmlAttributeValue(If.ATTR_CONDITION)
                                                        .inside(
                                                                XmlPatterns.xmlTag().withLocalName(If.TAG_NAME)
                                                        )
                            )
                    );

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet result) {

        PsiElement psiElement = parameters.getPosition();
        if(! MyDomUtils.isMoquiProject(psiElement.getProject())) return;

        addLookup(getVariableList(psiElement),result);

    }

    private void addLookup(@NotNull List<VariableLookupItem> lookupItemList,@NotNull CompletionResultSet result){
        lookupItemList.forEach(item->{
            result.addElement(LookupElementBuilder.create(item.name)
                            .withTypeText(item.type)
                    .withCaseSensitivity(true)
                    .withTailText(item.fromTagName)
            );
        });
    }

    public static final class VariableLookupItem{
        public static VariableLookupItem of(@NotNull String name, @Nullable  String type, @NotNull String fromTagName, @Nullable XmlAttributeValue fromAttributeValue){
            return new VariableLookupItem(name,type,fromTagName,fromAttributeValue);

        }
        VariableLookupItem(@NotNull String name, @Nullable  String type, @NotNull String fromTagName, @Nullable XmlAttributeValue fromAttributeValue){
            this.name =name;
            this.type = type;
            this.fromTagName = fromTagName;
            this.fromAttributeValue = fromAttributeValue;
        }
        public String name;//名称
        public String type;//类型，可以不存在
        public String fromTagName;//所在的tag 名称
        public XmlAttributeValue fromAttributeValue;//所在的xml属性位置，
    }

    /**
     * 从当前的PsiElement出发，找到所有已经定义的variable
     * 当前PisElement一般就是需要输入变量的XmlAttributeValue
     * 查找逻辑，以Service为例：
     * 1、向前查找同级别的兄弟Tag，获取定义的variable
     * 2、向上找到父级别的Tag，获取父级别定义的variable
     * 3、从父级别Tag开始向前找同级别的兄弟Tag，获取定义的Variable
     * 4、重复查找，一直找到根Tag，service中是action，
     * 5、获取service的in和out参数
     * @param psiElement 当前PsiElement
     * @return List<VariableLookupItem>
     */
    public List<VariableLookupItem> getVariableList(@NotNull PsiElement psiElement){
        List<VariableLookupItem> result = new ArrayList<>();
//        if(! (psiElement instanceof XmlAttributeValue xmlAttributeValue)) return result;
        XmlTag curTag = MyDomUtils.getParentTag(psiElement).orElse(null);
        if(curTag == null) return result;

        while(true) {

            XmlTag siblingTag = MyDomUtils.getPreSiblingXmlTag(curTag).orElse(null);
            while (siblingTag != null) {
                extractVariableFromTag(siblingTag, result);
                siblingTag = MyDomUtils.getPreSiblingXmlTag(siblingTag).orElse(null);
            }
            curTag = MyDomUtils.getParentTag(curTag).orElse(null);

            if(curTag == null) break;
            if( curTag.getName().equals(Action.TAG_NAME)) break;
        }

        return new ArrayList<>();
    }

    /**
     * set --> field
     * entity-find-one --> value-field
     * entity-find --> list
     * entity-find-count --> count-field
     *
     * entity-find-related-one --> to-value-field
     * entity-find-related --> to-list
     * entity-make-value --> value-field
     *
     * entity-set --> map ( exclude context)
     *
     * iterate --> entry ,key
     * AllOperations
     *
     * if
     * IfCombineConditions
     *
     * while
     * IfOtherOperations
     *
     * then\else-if\else\or\and\not
     * AllOperations
     * @param xmlTag
     * @param result
     */
    public void extractVariableFromTag(@NotNull XmlTag xmlTag, @NotNull List<VariableLookupItem> result){
        switch (xmlTag.getName()) {
            case Set.TAG_NAME -> {
                MyDomUtils.getLocalDomElementByPsiElement(xmlTag, Set.class).ifPresent(item -> result.add(VariableLookupItem.of(
                        MyDomUtils.getValueOrEmptyString(item.getField()),
                        null,
                        Set.TAG_NAME,
                        item.getField().getXmlAttributeValue()
                )));
            }
            case EntityFindOne.TAG_NAME -> {
                MyDomUtils.getLocalDomElementByPsiElement(xmlTag, EntityFindOne.class).ifPresent(item -> result.add(VariableLookupItem.of(
                        MyDomUtils.getValueOrEmptyString(item.getValueField()),
                        "EntityValue",
                        EntityFindOne.TAG_NAME,
                        item.getValueField().getXmlAttributeValue()
                )));

            }
            case EntityFind.TAG_NAME -> {
                MyDomUtils.getLocalDomElementByPsiElement(xmlTag, EntityFind.class).ifPresent(item -> result.add(VariableLookupItem.of(
                        MyDomUtils.getValueOrEmptyString(item.getList()),
                        "EntityValueList",
                        EntityFind.TAG_NAME,
                        item.getList().getXmlAttributeValue()
                )));

            }
            case EntityFindCount.TAG_NAME -> {
                MyDomUtils.getLocalDomElementByPsiElement(xmlTag, EntityFindCount.class).ifPresent(item -> result.add(VariableLookupItem.of(
                        MyDomUtils.getValueOrEmptyString(item.getCountField()),
                        null,
                        EntityFind.TAG_NAME,
                        item.getCountField().getXmlAttributeValue()
                )));

            }

            case EntityFindRelatedOne.TAG_NAME -> {
                MyDomUtils.getLocalDomElementByPsiElement(xmlTag, EntityFindRelatedOne.class).ifPresent(item -> result.add(VariableLookupItem.of(
                        MyDomUtils.getValueOrEmptyString(item.getToValueField()),
                        "EntityValue",
                        EntityFindRelatedOne.TAG_NAME,
                        item.getToValueField().getXmlAttributeValue()
                )));

            }
            case EntityFindRelated.TAG_NAME -> {
                MyDomUtils.getLocalDomElementByPsiElement(xmlTag, EntityFindRelated.class).ifPresent(item -> result.add(VariableLookupItem.of(
                        MyDomUtils.getValueOrEmptyString(item.getToList()),
                        "EntityValueList",
                        EntityFind.TAG_NAME,
                        item.getToList().getXmlAttributeValue()
                )));

            }
            case EntityMakeValue.TAG_NAME -> {
                MyDomUtils.getLocalDomElementByPsiElement(xmlTag, EntityMakeValue.class).ifPresent(item -> result.add(VariableLookupItem.of(
                        MyDomUtils.getValueOrEmptyString(item.getValueField()),
                        "EntityValue",
                        EntityFindRelatedOne.TAG_NAME,
                        item.getValueField().getXmlAttributeValue()
                )));
            }

        }
    }

}
