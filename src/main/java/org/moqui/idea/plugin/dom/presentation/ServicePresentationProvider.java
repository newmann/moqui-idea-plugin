package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;

public class ServicePresentationProvider extends PresentationProvider<Service> {
  @Override
  public @Nullable Icon getIcon(Service service) {

    if(service.getType().exists()) {
      switch(MyDomUtils.getValueOrEmptyString(service.getType())) {
        case "entity-auto"->{return MyIcons.ServiceTypeEntityAuto;}

        case "script"->{return MyIcons.ServiceTypeScript;}

        case "java"->{return MyIcons.ServiceTypeJava;}

        case "interface"->{return MyIcons.ServiceTypeInterface;}

        case "remote-json-rpc"->{return MyIcons.ServiceTypeRemoteJsonRpc;}

        case "remote-rest"->{return MyIcons.ServiceTypeRemoteRest;}

        case "camel"->{return MyIcons.ServiceTypeCamel;}

        default->{return MyIcons.ServiceTypeInline;}

      }
    }else {
      return MyIcons.ServiceTypeInline;
    }

  }

  @Nullable
  @Override
  public String getName(Service coordinates) {
    String name  = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(MyStringUtils.EMPTY_STRING);
    if(name.equals(MyStringUtils.EMPTY_STRING)) {
      String serviceName = StringUtil.notNullize(coordinates.getVerb().getStringValue(), MyStringUtils.UNKNOWN);
      if (coordinates.getNoun().exists()) {
        serviceName = serviceName + '#'
                + StringUtil.notNullize(coordinates.getNoun().getStringValue(), MyStringUtils.UNKNOWN);
      }

      return serviceName;
    }else {
      return MyStringUtils.formatPresentationName(Service.TAG_NAME,name);
    }
  }
}
