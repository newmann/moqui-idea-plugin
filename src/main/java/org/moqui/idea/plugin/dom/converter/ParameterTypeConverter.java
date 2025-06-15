package org.moqui.idea.plugin.dom.converter;

import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class ParameterTypeConverter extends ResolvingConverter.StringConverter{
    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext convertContext) {
        List<String> variantList = new ArrayList<>();
        if(convertContext.getTag() == null) return variantList;
        if(convertContext.getTag().getName().equals(Parameter.TAG_NAME)) {
            variantList.add("String");
            variantList.add("Timestamp");
            variantList.add("Time");
            variantList.add("Date");
            variantList.add("Integer");
            variantList.add("Long");
            variantList.add("Float");
            variantList.add("Double");
            variantList.add("BigDecimal");
            variantList.add("BigInteger");
            variantList.add("Boolean");
            variantList.add("Object");
            variantList.add("Blob");
            variantList.add("Clob");
            variantList.add("Collection");
            variantList.add("List");
            variantList.add("Map");
            variantList.add("Set");
            variantList.add("Node");
        }
        return variantList;
    }
}
