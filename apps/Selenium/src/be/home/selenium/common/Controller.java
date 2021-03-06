package be.home.selenium.common;

import be.home.common.utils.DateUtils;
import be.home.selenium.SeleniumTest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller  implements Initializable {

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
        SeleniumTest ultratop = new SeleniumTest();
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

    private ObjectProperty<Person> person = new SimpleObjectProperty<Person>(this, "person");
    public final Person getPerson() { return person.get(); }
    public final void setPerson(Person value) { person.set(value); }
    public final ObjectProperty<Person> personProperty() { return person; }

    public class Person {
        String person;
        int year;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}
