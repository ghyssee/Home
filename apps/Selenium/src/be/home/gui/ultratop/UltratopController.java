package be.home.gui.ultratop;

import be.home.common.utils.DateUtils;
import be.home.gui.common.DataList;
import be.home.model.M3uTO;
import be.home.selenium.SeleniumTest;
import be.home.selenium.ultratop.UltratopList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class UltratopController implements Initializable {
    @FXML
    private DatePicker datePicker;

    @FXML
    final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {

        public DateCell call(final DatePicker datePicker) {
            return new DateCell() {
                @Override public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    /*
                    if (MonthDay.from(item).equals(MonthDay.of(9, 25))) {
                        setTooltip(new Tooltip("Happy Birthday!"));
                        setStyle("-fx-background-color: #ff4444;");
                    }
                    if (item.equals(LocalDate.now().plusDays(1))) {
                        // Tomorrow is too soon.
                        setDisable(true);
                    }*/
                    if (item.isAfter(LocalDate.now())){
                        setDisable(true);
                    }
                    else if (item.getDayOfWeek() != DayOfWeek.SATURDAY){
                        setDisable(true);
                    }
                    else {
                        getStyleClass().add("calendarSaturday");
                    }
                }
            };
        }
    };
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        datePicker.setDayCellFactory(dayCellFactory);
        Calendar cal = DateUtils.getFirstSaturdayOfMonth();
        LocalDate date = DateUtils.convertCalendarToLocalDate(cal);
        datePicker.setValue(date);
    }

    @FXML
    private void handleButton1Action(ActionEvent event) {

        LocalDate localDate = datePicker.getValue();
        System.out.println(localDate.toString());
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        System.out.println(localDate + "\n" + instant + "\n" + date);
        UltratopList ultratop = new UltratopList();
        showResultBox(new Date(), new ArrayList<>());

        /*try {
            List<M3uTO> list = ultratop.start(date);
            showResultBox(date, list);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    private void showResultBox( Date date, List<M3uTO> list){

        TableView<Object> table = new TableView<>();
        List<M3uTO> list2 = new ArrayList<>();
        list.add( new M3uTO(null, "1", "Smith", "jacob.smith@example.com"));
        list.add( new M3uTO(null, "200", "Johnson", "isabella.johnson@example.com"));

        DataList dataList = new DataList();
        dataList.add("track", "Track", 10, DataList.TEXT_ALIGN_RIGHT);
        dataList.add("song", "Song", 200, null);
        dataList.add("artist", "Artist", 200, null);
        dataList.setList(list);
        dataList.setMinWidth(450);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ultratop");
        alert.setResizable(true);
        alert.getDialogPane().setMinSize(100, 100);
        alert.setHeaderText("Date: " + DateUtils.formatDate(date, DateUtils.DD_MM_YYYYY) + "\n" + list.size() + " Songs found");
        alert.getDialogPane().setExpandableContent(new ScrollPane(dataList.getTableView()));
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            System.out.println("Yes pressed");
        }
    }

    private void showResultBox3(){

        TableView<M3uTO> table = new TableView<>();
        List<M3uTO> list = new ArrayList<>();
        list.add( new M3uTO(null, "1", "Smith", "jacob.smith@example.com"));
        list.add( new M3uTO(null, "2", "Johnson", "isabella.johnson@example.com"));
        ObservableList<M3uTO> data2 = FXCollections.observableArrayList(list);

        TableColumn firstNameCol = new TableColumn("Track");
        firstNameCol.setMinWidth(10);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<M3uTO, String>("track"));
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn lastNameCol = new TableColumn("Song");
        lastNameCol.setMinWidth(50);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<M3uTO, String>("song"));
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn emailCol = new TableColumn("Artist");
        emailCol.setMinWidth(100);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<M3uTO, String>("artist"));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        table.setItems(data2);
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
        table.setMinWidth(400L);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ultratop");
        alert.setHeaderText("Songs found");
        String tmp = "line1;\nline2;\n";
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        alert.getDialogPane().setExpandableContent(table);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            System.out.println("Yes pressed");
        }
    }

    private void showResultBox2(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ultratop");
        alert.setHeaderText("Songs found");
        String tmp = "line1;\nline2;\n";
        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(tmp)));
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            System.out.println("Yes pressed");
        }
    }

}
