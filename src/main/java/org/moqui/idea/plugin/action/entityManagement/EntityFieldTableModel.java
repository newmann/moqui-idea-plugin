package org.moqui.idea.plugin.action.entityManagement;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.Field;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class EntityFieldTableModel implements TableModel {
    private final List<String> columnNames = List.of("name","type","is-pk","description");
    private final List<List<Object>> data = new ArrayList<List<Object>>();

    public EntityFieldTableModel(@NotNull List<AbstractField> fieldList){

        for(AbstractField abstractField : fieldList) {
            if(abstractField instanceof Field field) {
                List<Object> rowData = new ArrayList<>();
                rowData.add(field.getName().getStringValue());
                rowData.add(field.getType().getStringValue());
                rowData.add(field.getIsPk().getValue());
                rowData.add(field.getDescription().getValue());
                this.data.add(rowData);
            }
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
        if(i==2) { //column is-pk
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
