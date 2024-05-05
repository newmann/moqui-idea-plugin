package org.moqui.idea.plugin.action.entityManagement;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.MemberEntity;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.util.MyDomUtils;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class PendingViewEntityMemberEntityTableModel implements TableModel {
    private final List<String> columnNames = List.of("pending","entity-alias","entity-name","join-from-alias","join-optional","description");
    private final List<List<Object>> data = new ArrayList<List<Object>>();

    public PendingViewEntityMemberEntityTableModel(@NotNull Project  project, @NotNull List<MemberEntity> memberEntityList){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        for(MemberEntity memberEntity : memberEntityList) {
            List<Object> rowData = new ArrayList<>();
            if (moquiIndexService.getEntityOrViewEntity(MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName())).isEmpty()) {
                rowData.add(Boolean.TRUE);
            }else {
                rowData.add(Boolean.FALSE);
            }
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
        if((i==0) || (i==4)) { //column mutable
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
