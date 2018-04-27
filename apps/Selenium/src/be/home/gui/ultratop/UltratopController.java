package be.home.gui.ultratop;

import be.home.common.utils.DateUtils;
import be.home.selenium.SeleniumTest;
import be.home.selenium.ultratop.UltratopList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

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
        /*
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete  ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            System.out.println("Yes pressed");
        }*/

        try {
            ultratop.start(date);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
