package com.timbuchalka.todolist;

import com.timbuchalka.datamodel.TodoData;
import com.timbuchalka.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField shortDescriptionField;

    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker deadlinePicker;

    public TodoItem processResults(){
        String shortDesc= shortDescriptionField.getText().trim();
        String details= detailsArea.getText().trim();
        LocalDate deadlineValue= deadlinePicker.getValue();

        TodoItem newItem = new TodoItem(shortDesc,details,deadlineValue);
        TodoData.getInstance().addTodoItem(newItem);
        return newItem;
    }
}
