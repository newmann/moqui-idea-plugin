package org.moqui.idea.plugin.action.entityManagement;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.MemberEntity;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class ViewEntityMemberEntityTableModel implements TableModel {
    private final List<String> columnNames = List.of("entity-alias","entity-name","join-from-alias","join-optional","description");
    private final List<List<Object>> data = new ArrayList<List<Object>>();

    public ViewEntityMemberEntityTableModel(@NotNull List<MemberEntity> memberEntityList){
        for(MemberEntity memberEntity : memberEntityList) {
            List<Object> rowData = new ArrayList<>();
            rowData.add(memberEntity.getEntityAlias().getStringValue());
            rowData.add(memberEntity.getEntityName().getStringValue());
            rowData.add(memberEntity.getJoinFromAlias().getStringValue());
            rowData.add(memberEntity.getJoinOptional().getValue());
            rowData.add(memberEntity.getDescription().getValue());
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
        if(i==3) { //column join-optional
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
