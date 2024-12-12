package org.moqui.idea.plugin.provider;

import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlAttributeDescriptorsProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.EntityFacadeFieldAttributeDescriptor;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 无效，转到XmlElementDescriptor中进行处理
 */
@Deprecated
public class EntityFacadeAttributeNameProvider implements XmlAttributeDescriptorsProvider {

    @Override
    public XmlAttributeDescriptor[] getAttributeDescriptors(XmlTag xmlTag) {
        List<XmlAttributeDescriptor> resultList = new ArrayList<>();
        if(MyDomUtils.isMoquiDataDefineTag(xmlTag)){
            IndexEntity indexEntity = EntityUtils.getIndexEntityByName(xmlTag.getProject(),xmlTag.getName()).orElse(null);
            if(indexEntity != null){
                indexEntity.getFieldList().forEach(field -> {resultList.add(EntityFacadeFieldAttributeDescriptor.of(field));});
            }
        }

        return resultList.toArray(XmlAttributeDescriptor.EMPTY);
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(String s, XmlTag xmlTag) {
        return null;
    }
}
