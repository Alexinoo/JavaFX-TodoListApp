package com.timbuchalka.todolist;

import com.timbuchalka.datamodel.TodoData;
import com.timbuchalka.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

  @FXML
  private ContextMenu listContextMenu;

  @FXML
  private ToggleButton filterToggleButton;

  private FilteredList<TodoItem> filteredList;

  private Predicate<TodoItem> wantAllItems;
  private Predicate<TodoItem> wantTodaysItems;

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

    listContextMenu = new ContextMenu();
    MenuItem deleteMenuItem = new MenuItem("Delete");
    deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        deleteItem(item);
      }
    });
    listContextMenu.getItems().addAll(deleteMenuItem);

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
    //todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());

    wantAllItems = new Predicate<TodoItem>() {
      @Override
      public boolean test(TodoItem todoItem) {
        return true;
      }
    };

    wantTodaysItems = new Predicate<TodoItem>() {
      @Override
      public boolean test(TodoItem todoItem) {
        return (todoItem.getDeadline().equals(LocalDate.now()));
      }
    };
    filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), wantAllItems);
    SortedList<TodoItem> sortedList = new SortedList<>(filteredList,new Comparator<TodoItem>() {
      @Override
      public int compare(TodoItem o1, TodoItem o2) {
        return o1.getDeadline().compareTo(o2.getDeadline());
      }
    });
    //todoListView.setItems(TodoData.getInstance().getTodoItems());
    todoListView.setItems(sortedList);
    todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    todoListView.getSelectionModel().selectFirst();

    //Cell Factory
    todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
      @Override
      public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
        ListCell<TodoItem> cell = new ListCell<>(){
          @Override
          protected void updateItem(TodoItem todoItem, boolean empty) {
            super.updateItem(todoItem, empty);
            if (empty){
              setText(null);
            }else{
              setText(todoItem.getShortDescription());
              if (todoItem.getDeadline().isBefore(LocalDate.now().plusDays(1))){
                setTextFill(Color.RED);
              } else if (todoItem.getDeadline().equals(LocalDate.now().plusDays(1))) {
                setTextFill(Color.GREEN);

              }
            }
          }
        };

        cell.emptyProperty().addListener(
                (obs , wasEmpty , isNowEmpty) -> {
                  if(isNowEmpty){
                    cell.setContextMenu(null);
                  }else {
                    cell.setContextMenu(listContextMenu);
                  }
                });
        return cell;
      }
    });
  }

  @FXML
  public void handleKeyPressed(KeyEvent keyEvent){
    TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
    if (selectedItem != null){
      if (keyEvent.getCode().equals(KeyCode.DELETE)){
        deleteItem(selectedItem);
      }
    }
  }

  @FXML
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
       //todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
       todoListView.getSelectionModel().select(newTodo);
      // System.out.println("Ok pressed");
    }else
      System.out.println("Cancel Pressed");
  }

  public void deleteItem(TodoItem item){
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Todo Item");
    alert.setHeaderText("Delete item: "+item.getShortDescription());
    alert.setContentText("Are you sure? Press OK to confirm, Cancel to back out..");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK){
      TodoData.getInstance().deleteTodoItem(item);
    }

  }

  @FXML
  public void handleFilterButton(){
    TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
    if (filterToggleButton.isSelected()){
      filteredList.setPredicate(wantTodaysItems);
      if (filteredList.isEmpty()){
        itemDetailsTextArea.clear();
        deadlineLabel.setText("");
      } else if (filteredList.contains(selectedItem)) {
        todoListView.getSelectionModel().select(selectedItem);
      }else {
        todoListView.getSelectionModel().selectFirst();
      }
    }else{
      filteredList.setPredicate(wantAllItems);
      todoListView.getSelectionModel().select(selectedItem);
    }
  }

  @FXML
  public void handleExit(){
    Platform.exit();
  }
}