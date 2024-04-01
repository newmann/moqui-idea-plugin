package org.moqui.idea.plugin.dom.converter;

import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.util.EntityUtils;

import java.util.Collection;

/**
 * 对应到Entity的名称
 */
@Deprecated
public class EntityNameConverter extends ResolvingConverter<String> {
    @Override
    public @Nullable String fromString(@Nullable @NonNls String s, ConvertContext context) {
        return s;
    }

    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {
        return EntityUtils.getEntityAttributes(context.getProject(), Entity.ATTR_ENTITY_NAME,"");
    }

    @Override
    public @Nullable String toString(@Nullable String s, ConvertContext context) {
        return s;
    }
}
