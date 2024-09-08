package org.moqui.idea.plugin.action.flowManagement.widget;

public enum FlowNodeType {
    START("start",1),
    END("end",2),
    ASSIGNMENT("assignment",3),
    IF("if",4),
    LOOP("loop",5),
    SERVICE_CALL("service_call",6),
    SCRIPT("script",7),
    ENTITY_FIND("entity_find",8),
    ENTITY_CREATE("entity_create",9),
    ENTITY_UPDATE("entity_update",10),
    ENTITY_DELETE("entity_delete",11),
    CONDITION("condition",12),
    SCENE("scene",13),
    COLLECT("collect",14),
    BREAK("break",15),
    CONTINUE("continue",16),
    ENTITY_FIND_ONE("entity_find_one",17),
    ENTITY_ACTION("entity_action",18),
    ;



    private final String name;
    private final int id;
    FlowNodeType(String name, int id) {
        this.name = name;
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
