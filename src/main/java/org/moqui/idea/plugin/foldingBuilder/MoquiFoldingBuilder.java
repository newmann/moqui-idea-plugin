package org.moqui.idea.plugin.foldingBuilder;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageImportStatements;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.XmlElementVisitor;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.FormList;
import org.moqui.idea.plugin.dom.model.FormSingle;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoquiFoldingBuilder extends FoldingBuilderEx {

    //
    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {

        if(!(root instanceof XmlFile file)) return FoldingDescriptor.EMPTY_ARRAY;
        XmlTag rootTag = file.getRootTag();
        if (rootTag == null) return FoldingDescriptor.EMPTY_ARRAY;
        if(! rootTag.getName().equals(Screen.TAG_NAME)) return FoldingDescriptor.EMPTY_ARRAY;


        List<XmlTag> formSingleList = MyDomUtils.getSubTagList(rootTag,FormSingle.TAG_NAME);
        List<XmlTag> formListList = MyDomUtils.getSubTagList(rootTag, FormList.TAG_NAME);

//        FoldingGroup group = FoldingGroup.newGroup("Moqui");
        List<FoldingDescriptor> descriptorList = new ArrayList<>();

        formSingleList.forEach(item->{
            descriptorList.add(new FoldingDescriptor(item,item.getTextRange().getStartOffset(),item.getTextRange().getEndOffset()
                    ,null,"Single Form : "+ item.getAttribute(FormSingle.ATTR_NAME).getValue()));
        });
        formListList.forEach(item->{
            descriptorList.add(new FoldingDescriptor(item,item.getTextRange().getStartOffset(),item.getTextRange().getEndOffset()
                    ,null,"List Form : "+ item.getAttribute(FormList.ATTR_NAME).getValue()));
        });


        return descriptorList.toArray(FoldingDescriptor.EMPTY_ARRAY);
    }
//
////    @Override
////    public @Nullable String getPlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
////        return super.getPlaceholderText(node, range);
////    }
//
    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
//        System.out.println("getPlaceholderText:"+node.getPsi().toString());
//        if(node.getPsi() instanceof XmlTag xmlTag) {
//            System.out.println("getPlaceholderText xmlTag Name:"+xmlTag.getName());
//            if(xmlTag.getName().equals(FormSingle.TAG_NAME)) {
//                return "form-single: " + xmlTag.getAttribute(FormSingle.ATTR_NAME).getValue();
//            }
//        }

        return null;
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}
