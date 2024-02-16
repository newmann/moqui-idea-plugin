package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import icons.MoquiIcons;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;

public class ServicePresentationProvider extends PresentationProvider<Service> {

  private static final String UNKNOWN = "<N/A>";

  @Override
  public @Nullable Icon getIcon(Service service) {

    if(service.getServiceType().exists()) {
      switch(service.getServiceType().getStringValue()) {
        case "entity-auto":
          return MoquiIcons.ServiceTypeEntityAuto;
        case "script":
          return MoquiIcons.ServiceTypeScript;
        case "java":
          return MoquiIcons.ServiceTypeJava;
        case "interface":
          return MoquiIcons.ServiceTypeInterface;
        case "remote-json-rpc":
          return MoquiIcons.ServiceTypeRemoteJsonRpc;
        case "remote-rest":
          return MoquiIcons.ServiceTypeRemoteRest;
        case "camel":
          return MoquiIcons.ServiceTypeCamel;
        default:
          return MoquiIcons.ServiceTypeInline;
      }
    }else {
      return MoquiIcons.ServiceTypeInline;
    }

  }

  @Nullable
  @Override
  public String getName(Service coordinates) {
    String name  = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(MyStringUtils.EMPTY_STRING);
    if(name.equals(MyStringUtils.EMPTY_STRING)) {
      String serviceName = StringUtil.notNullize(coordinates.getVerb().getStringValue(), UNKNOWN);
      if (coordinates.getNoun().exists()) {
        serviceName = serviceName + '#'
                + StringUtil.notNullize(coordinates.getNoun().getStringValue(), UNKNOWN);
      }

      return serviceName;
    }else {
      return MyStringUtils.formatPresentationName(Service.TAG_NAME,name);
    }
  }
}
