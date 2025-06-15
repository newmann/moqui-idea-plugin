package org.moqui.idea.plugin.dom.converter;

import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.util.EntityUtils;

import java.util.Collection;

/**
 * 所有已经定义的package，包含Entity和ViewEntity
 */
public class AllPackageConverter extends ResolvingConverter<String> {
    @Override
    public @Nullable String fromString(@Nullable String s, ConvertContext context) {
        return s;
    }

    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {
        return EntityUtils.getEntityAndViewEntityAttributes(context.getProject(), Entity.ATTR_PACKAGE,"");
    }

    @Override
    public @Nullable String toString(@Nullable String s, ConvertContext context) {
        return s;
    }
}
