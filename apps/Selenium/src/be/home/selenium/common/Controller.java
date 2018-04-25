package be.home.selenium.common;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller  implements Initializable {

    @FXML
    private ComboBox<String> cmb1;
    @FXML
    private TextField myDate;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<Integer> yearSpinner;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
       cmb1.getItems().clear();
        //cmb1.getItems().addAll("Option A", "Option B", "Option C");
        //cmbMonth.getSelectionModel().select("Option B");
       yearSpinner.getValueFactory().setValue(2018);
    }

    @FXML
    private void handleButton1Action(ActionEvent event) {

        System.out.println(myDate.getText());
        LocalDate localDate = datePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        System.out.println(localDate + "\n" + instant + "\n" + date);

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
