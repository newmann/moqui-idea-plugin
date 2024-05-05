package org.moqui.idea.plugin.action.entityManagement;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.Alias;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.Field;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class ViewEntityFieldTableModel implements TableModel {
    private final List<String> columnNames = List.of("name","field","entity-alias","description");
    private final List<List<Object>> data = new ArrayList<List<Object>>();

    public ViewEntityFieldTableModel(@NotNull List<AbstractField> fieldList){

        for(AbstractField abstractField : fieldList) {
            if(abstractField instanceof Alias alias) {
                List<Object> rowData = new ArrayList<>();
                rowData.add(alias.getName().getStringValue());
                rowData.add(alias.getField().getStringValue());
                rowData.add(alias.getEntityAlias().getStringValue());
                rowData.add(alias.getDescription().getValue());
                this.data.add(rowData);
            }
            if(abstractField instanceof Field field) {
                List<Object> rowData = new ArrayList<>();
                rowData.add(field.getName().getStringValue());
                rowData.add(field.getName().getStringValue());
                Entity entity = (Entity) field.getParent();
                rowData.add(entity.getEntityName().getStringValue());
//                rowData.add(field.getField().getStringValue());
//                rowData.add(field.getEntityAlias().getStringValue());
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
        return String.class;

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
