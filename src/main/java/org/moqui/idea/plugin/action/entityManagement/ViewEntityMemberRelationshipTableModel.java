package org.moqui.idea.plugin.action.entityManagement;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.MemberRelationship;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class ViewEntityMemberRelationshipTableModel implements TableModel {
    private final List<String> columnNames = List.of("entity-alias","relationship","join-from-alias","join-optional","description");
    private final List<List<Object>> data = new ArrayList<>();

    public ViewEntityMemberRelationshipTableModel(@NotNull List<MemberRelationship> memberRelationshipList){
        for(MemberRelationship memberRelationship : memberRelationshipList) {
            List<Object> rowData = new ArrayList<>();

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
        if(i==6) { //column mutable
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
