package org.moqui.idea.plugin.provider;

import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.util.EntityFacadeElementDescriptor;
import org.moqui.idea.plugin.util.MyDomUtils;

public class EntityFacadeElementProvider implements XmlElementDescriptorProvider {

    @Override
    public @Nullable XmlElementDescriptor getDescriptor(XmlTag xmlTag) {
        if(MyDomUtils.isMoquiDataDefineTag(xmlTag)){
            return EntityFacadeElementDescriptor.of(xmlTag);
        }
        return null;
    }
}
