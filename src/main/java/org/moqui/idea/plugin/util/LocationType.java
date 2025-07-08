package org.moqui.idea.plugin.util;

public enum LocationType {
    Unknown, //根据location无法判断具体类型的，需要根据所在的context，进一步判断，
    ComponentFile, //component://SimpleScreens/template/party/PartyForms.xml（指向一个文件 ）
    ComponentFileContent, //component://SimpleScreens/screen/SimpleScreens/Search.xml#SearchOptions（指向文件中的某个tag），
    RelativeUrl,//../EditFacility，同一个目录下的screen文件，
    WebUrl,
    TransitionLevelName, //不仅仅是Transition，还包括当前Screen的下级Screen
    DynamicPath,    // Location中包含有${}的变量
    CamelPath,//location="moquiservice:moqui.example.ExampleServices.targetCamelExample"
    ClassPath,//location="classpath://service/org/moqui/impl/BasicServices.xml"，service-file、service-include中使用
    //     和component类似，对应到某个文件，只是没有指定component名称，但应该也是唯一的
    AbsoluteUrl,// 类似这种路径//hmadmin/User/EditUser，需要找到所在component的MoquiConf.xml中定义的菜单入口，顺着subscreen的定义路径，找到对应的screen定义文件
    //${appRoot}/ServiceAgreement/ServiceLocation  ${appRoot}为变量，一般在所在组建的一级入口screen文件中定义，
    //apps/system/Security/UserAccount/UserAccountDetail //apps是特定写法，等同于//system/Security/UserAccount/UserAccountDetail
}
