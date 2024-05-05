package org.moqui.idea.plugin.action.entityManagement;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.MemberRelationship;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class PendingViewEntityMemberRelationshipTableModel implements TableModel {
    private final List<String> columnNames = List.of("pending","entity-alias","relationship","join-from-alias","join-optional","description");
    private final List<List<Object>> data = new ArrayList<List<Object>>();

    public PendingViewEntityMemberRelationshipTableModel(@NotNull Project  project, @NotNull List<MemberRelationship> memberRelationshipList){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        for(MemberRelationship memberRelationship : memberRelationshipList) {
            List<Object> rowData = new ArrayList<>();
            ViewEntity viewEntity = (ViewEntity) memberRelationship.getParent();
            if (moquiIndexService.getEntityOrViewEntity(moquiIndexService.getViewEntityMemberRelationshipEntityName(viewEntity,memberRelationship).orElse(MyStringUtils.EMPTY_STRING)).isEmpty()) {
                rowData.add(Boolean.TRUE);
            }else {
                rowData.add(Boolean.FALSE);
            }

            rowData.add(memberRelationship.getEntityAlias().getStringValue());
            rowData.add(memberRelationship.getRelationship().getStringValue());
            rowData.add(memberRelationship.getJoinFromAlias().getStringValue());
            rowData.add(memberRelationship.getJoinOptional().getValue());
            rowData.add(memberRelationship.getDescription().getValue());
            this.data.add(rowData);
        }
    }
    @Override
    public int getRowCount() {
        return this.data.size();
    }


    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Nls
    @Override
    public String getColumnName(int i) {
        return columnNames.get(i);
    }

    @Override
    public Class<?> getColumnClass(int i) {
        if((i==0) || (i==6)) { //column mutable
            return Boolean.class;
        }else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return data.get(row).get(column);
    }

    @Override
    public void setValueAt(Object o, int i, int i1) {

    }

    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {

    }

    @Override
    public void removeTableModelListener(TableModelListener tableModelListener) {

    }
}
