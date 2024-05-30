package com.timbuchalka.todolist;

import com.timbuchalka.datamodel.TodoData;
import com.timbuchalka.datamodel.TodoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Controller {
  private List<TodoItem> todoItems;

  @FXML
  private ListView<TodoItem> todoListView;

  @FXML
  private TextArea itemDetailsTextArea;

  @FXML
  private Label deadlineLabel;

  @FXML
  private BorderPane mainBorderPane;

  public void initialize(){
    /*
    TodoItem item1 = new TodoItem("Mail birthday card","Buy a 30th birthday card for John",
            LocalDate.of(2024, Month.APRIL,25));

    TodoItem item2 = new TodoItem("Doctor's Appointment","See Dr. Smith at 123 Main Street. Bring paperwork",
              LocalDate.of(2024, Month.MAY,28));

    TodoItem item3 = new TodoItem("Finish Design Proposal for client","I promised Mike I'd email website mock ups by Friday",
              LocalDate.of(2016, Month.MAY,30));

    TodoItem item4 = new TodoItem("Pick up Dough at the Train station","Dough is arriving on March 23 on the 5 o'clock train",
              LocalDate.of(2016, Month.MARCH,23));

    TodoItem item5 = new TodoItem("Pick Up Dry Cleaning","The clothes should be ready by Wednesday",
              LocalDate.of(2016, Month.APRIL,20));
    todoItems = new ArrayList<>();
    todoItems.add(item1);
    todoItems.add(item2);
    todoItems.add(item3);
    todoItems.add(item4);
    todoItems.add(item5);
    TodoData.getInstance().setTodoItems(todoItems);
    */

    todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
      @Override
      public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem oldValue, TodoItem newValue) {
        if (newValue != null){
          TodoItem item = todoListView.getSelectionModel().getSelectedItem();
          itemDetailsTextArea.setText(item.getDetails());
          DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d, yyyy");
          deadlineLabel.setText(dtf.format(item.getDeadline()));
        }
      }
    });

    //todoListView.getItems().setAll(todoItems);
    todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
    todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    todoListView.getSelectionModel().selectFirst();
  }

  public void handleClickListView(){
      TodoItem item = todoListView.getSelectionModel().getSelectedItem();
      //System.out.println("The selected item is: "+item);
     // itemDetailsTextArea.setText(item.getDetails());
     // StringBuilder sb = new StringBuilder(item.getDetails());
     // sb.append("\n\n\n\n");
     // sb.append("Due: " );
     // sb.append(item.getDeadline().toString());
     // itemDetailsTextArea.setText(sb.toString());
      itemDetailsTextArea.setText(item.getDetails());
      deadlineLabel.setText(item.getDeadline().toString());
  }

  @FXML
  public void showNewItemDialog(){
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.initOwner(mainBorderPane.getScene().getWindow());
    dialog.setTitle("Add New Todo Item");
    dialog.setHeaderText("Use this dialog to create a new todo item");
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
    try{
      //Parent root =  FXMLLoader.load(getClass().getResource("todoItemDialog.fxml"));
      dialog.getDialogPane().setContent(fxmlLoader.load());

    }catch (IOException e){
      System.out.println("Couldn't load the dialog");
      e.printStackTrace();
      return;
    }
    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK){
       DialogController controller = fxmlLoader.getController();
       TodoItem newTodo = controller.processResults();
       todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
       todoListView.getSelectionModel().select(newTodo);
      // System.out.println("Ok pressed");
    }else
      System.out.println("Cancel Pressed");
  }
}