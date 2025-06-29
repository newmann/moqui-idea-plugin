package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.Collection;

/**
 * link->url
 * condition-response->url ，
 * conditional-response->url ，
 * default-response->url ，
 * error-response->url ，
 * 1、指向该screen的transition.name
 * 2、指向相对路径的Screen的xml文件，<default-response url="../../Order/FindOrder"/>
 * 3、指向外部链接，  <link url="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#query-string-syntax"
 *                 url-type="plain" link-type="anchor" target-window="_blank" text="Search String Reference"/>
 * 4、指向动态菜单路径：<default-response url="//${appRoot}/Accounting/FinancialAccount/FindFinancialAccount"/>
 */
@Deprecated
public class UrlConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter<String> {
//    @Override
//    public @Nullable String fromString(@Nullable  String s, ConvertContext context) {
//        if(s == null) return null;
//        if(ScreenUtils.isValidTransitionFormat(s)) {
//            return ScreenUtils.getTransitionByName(s,context).map(item-> s).orElse(null);
//        }else {
//            return s;
//        }
//
//    }


    @Override
    public @NotNull Collection<String> getVariants(ConvertContext context) {
        return ScreenUtils.getAbstractTransitionListFromConvertContext(context).stream()
                .map(item-> {
                    return MyDomUtils.getXmlAttributeValueString(item.getName()).orElse(MyStringUtils.EMPTY_STRING);
                })
                .toList();
    }


    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        return LocationUtils.createReferences(value, element, context);

//        String url = value.getStringValue();
//        if (url == null) return PsiReference.EMPTY_ARRAY;
//        LocationUtils.Location location = new LocationUtils.Location(context.getProject(),url);
//
//
//        if(ScreenUtils.isValidTransitionFormat(url)) {
//            Optional<AbstractTransition> optTransition = ScreenUtils.getTransitionByName(url,context);
//            if (optTransition.isEmpty()) return PsiReference.EMPTY_ARRAY;
//
//            final AbstractTransition transition = optTransition.get();
//            PsiReference[] psiReferences = new PsiReference[1];
//            psiReferences[0] = new PsiRef(element,
//                    new TextRange(1,
//                            url.length()+1),
//                    transition.getName().getXmlAttributeValue());
//
//            return psiReferences;
//
//        }else {
//            return PsiReference.EMPTY_ARRAY;
//        }

    }

    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        return new HtmlBuilder()
                .append("当前的url")
                .append("[" + s+"]")
                .append("为Transition，但在本Screen中找不到对应的定义。")
                .toString();
    }
    //    /**
//     * 根据当前位置找到所有可用的Transition
//     *
//     * @param context
//     * @return
//     */
//    private List<TransitionAbstract> getTransitionList(ConvertContext context) {
//
//        List<TransitionAbstract> result = new ArrayList<TransitionAbstract>();
//
//
//        Screen screen = ScreenUtils.getCurrentScreen(context).orElse(null);
//        if(screen != null){
//            result.addAll(screen.getTransitionList());
//            result.addAll(screen.getTransitionIncludeList());
//        }
//        return result;
//    }
//    /**
//     * 根据当前位置对应的Transition
//     * @param related
//     * @param context
//     * @return
//     */
//    private Optional<TransitionAbstract> getTransition(String related, ConvertContext context) {
//        List<TransitionAbstract> transitionList = getTransitionList(context);
//        return transitionList.stream().filter(
//                item->{
//                    String str = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
//                            .orElse(MyStringUtils.EMPTY_STRING);
//                    return str.equals(related);
//                    }
//        ).findFirst();
//
//    }


}
