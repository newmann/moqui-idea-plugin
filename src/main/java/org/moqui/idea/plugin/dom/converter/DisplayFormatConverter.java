package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.util.EntityFacadeXmlUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class DisplayFormatConverter extends ResolvingConverter.StringConverter{
    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext convertContext) {
        List<String> variantList = new ArrayList<>();

        variantList.add("MM/yyyy");
        variantList.add("yyyy-MM-dd");
        variantList.add("yyyy-MM-dd HH:mm");
        variantList.add("yyyy-MM-dd HH:mm:ss");
        variantList.add("yyyy-MM-dd HH:mm:ss.SSS z");


        variantList.add("0000");
        variantList.add("0.00");
        variantList.add("0.000");
        variantList.add("0.00#");
        variantList.add("#,##0.00");
        variantList.add("#,##0.###");




        return variantList;
    }
}
