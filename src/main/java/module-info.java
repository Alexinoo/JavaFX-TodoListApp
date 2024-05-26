module com.example.sampletodolistapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.timbuchalka.todolist to javafx.fxml;
    exports com.timbuchalka.todolist;
}