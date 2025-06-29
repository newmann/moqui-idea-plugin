package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.AbstractTransition;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 1、指向本screen的transition.name
 * 2、指向某个screen文件：<dynamic-container id="FieldListContainer" transition="FieldList"/>
 * 3、还可以指向定义好的subscreens：案例：moqui-framework\runtime\component\SimpleScreens\screen\MyAccount\User\TimeEntries.xml
 *      subscreens-item name="EditTimeEntry"
 */
public class TransitionReferenceConverter implements CustomReferenceConverter<String> {



//    @Override
//    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
//        if(s==null) {
//            return super.getErrorMessage(s, context);
//        }else {
//            return "找不到[" + s + "]对应的transition定义。";
//        }
//    }
//
//    @Override
//    public @NotNull Collection<String> getVariants(ConvertContext context) {
//        return getTransitionList(context).stream().map(AbstractTransition::getName)
//                .map(MyDomUtils::getValueOrEmptyString).collect(Collectors.toSet());
//    }

//    @Override
//    public @Nullable String toString(@Nullable AbstractTransition transition, ConvertContext context) {
//        if (transition == null) return null;
//        return MyDomUtils.getValueOrEmptyString(transition.getName());
//    }

//    @Override
//    public @Nullable LookupElement createLookupElement(AbstractTransition transition) {
//        if(transition == null) {
//            return super.createLookupElement(transition);
//        }else {
//            String s = MyDomUtils.getValueOrEmptyString(transition.getName());
//            return LookupElementBuilder.create(s)
//                    .withCaseSensitivity(false);
//
//        }
//    }

//    @Override
//    public @Nullable PsiElement getPsiElement(@Nullable AbstractTransition resolvedValue) {
//        return super.getPsiElement(resolvedValue);
//    }

    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        return LocationUtils.createReferences(value,element,context);
    }
    /**
     * 根据当前位置找到所有可用的Transition
     *
     * @param context
     * @return
     */
//    private List<AbstractTransition> getTransitionList(ConvertContext context) {
//
//        List<AbstractTransition> result = new ArrayList<AbstractTransition>();
//
//
//        Screen screen = ScreenUtils.getCurrentScreen(context).orElse(null);
//        if(screen != null){
//            result.addAll(screen.getTransitionList());
//            result.addAll(screen.getTransitionIncludeList());
//        }
//        return result;
//    }
    /**
     * 根据当前位置对应的Transition
     * @param related
     * @param context
     * @return
     */
//    private Optional<AbstractTransition> getTransition(String related, ConvertContext context) {
//        List<AbstractTransition> transitionList = getTransitionList(context);
//        return transitionList.stream().filter(
//                item->{
//                    String str = MyDomUtils.getValueOrEmptyString(item.getName());
//                    return str.equals(related);
//                    }
//        ).findFirst();
//
//    }


}
