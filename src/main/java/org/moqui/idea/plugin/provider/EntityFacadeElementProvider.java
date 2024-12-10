package org.moqui.idea.plugin.provider;

import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlAttributeDescriptorsProvider;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.ArrayList;
import java.util.List;

public class EntityFacadeElementProvider implements XmlElementDescriptorProvider {

    @Override
    public @Nullable XmlElementDescriptor getDescriptor(XmlTag xmlTag) {
        if(MyDomUtils.isMoquiDataDefineTag(xmlTag)){
            return EntityFacadeElementDescriptor.of(xmlTag);
        }
        return null;
    }
}
