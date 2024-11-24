package org.moqui.idea.plugin.action.entityManagement;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Relationship;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class EntityRelationshipTableModel implements TableModel {
    private final List<String> columnNames = List.of("type","title","related","fk-name", "short-alias","mutable","description");
    private final List<List<Object>> data = new ArrayList<List<Object>>();

    public EntityRelationshipTableModel(@NotNull List<Relationship> relationshipList){

        for(Relationship relationship : relationshipList) {
            List<Object> rowData = new ArrayList<>();
            rowData.add(relationship.getType().getStringValue());
            rowData.add(relationship.getTitle().getStringValue());
            rowData.add(relationship.getRelated().getStringValue());
            rowData.add(relationship.getFkName().getStringValue());
            rowData.add(relationship.getShortAlias().getStringValue());
            rowData.add(relationship.getMutable().getValue());
            rowData.add(relationship.getDescription().getValue());
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
        if(i==5) { //column mutable
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
