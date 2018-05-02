package be.home.gui.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.List;


public class DataList  {

    private List<TableColumn> tableColumns = new ArrayList<>();
    private TableView<Object> table = new TableView<>();
    public static String TEXT_ALIGN_RIGHT = "-fx-alignment: CENTER_RIGHT;";
    public DataList(){
    }

    public void add(String field, String title, double size, String style){
        TableColumn tableColumn = new TableColumn(title);
        tableColumn.setMinWidth(size);
        tableColumn.setCellValueFactory(
                new PropertyValueFactory(field));
        if (style != null) {
            tableColumn.setStyle(style);
        }
        tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumns.add(tableColumn);
        table.getColumns().add(tableColumn);
    }

    public List<TableColumn> getTableColumns(){
        return this.tableColumns;
    }

    public void setList(List<?> list){
        ObservableList<Object> data = FXCollections.observableArrayList(list);
        table.setItems(data);
    }

    public TableView getTableView(){
        return this.table;
    }

    public void setMinWidth(double widht){
        table.setMinWidth(400L);
    }
}
