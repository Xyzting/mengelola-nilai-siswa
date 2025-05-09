package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AppController  {
  @FXML private TableView<Student> table;
  @FXML private TableColumn<Student, String> nameCol;
  @FXML private TableColumn<Student, String> nimCol;
  @FXML private TableColumn<Student, Double> n1Col;
  @FXML private TableColumn<Student, Double> n2Col;
  @FXML private TableColumn<Student, Double> n3Col;
  @FXML private TableColumn<Student, Double> avgCol;
  @FXML private TextField searchField;
  @FXML private Label infoLabel;
  @FXML private Button clearSearchButton;
  @FXML private ComboBox<String> sortCriteriaCombo;

  private StudentManager manager = new StudentManager();
  private void showAlert(String message) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Informasi");
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
  }

  @FXML
  public void initialize() {
      clearSearchButton.setVisible(false);

      sortCriteriaCombo.getItems().addAll("Ascending", "Descending");

      sortCriteriaCombo.setOnAction(event -> {
          String selected = sortCriteriaCombo.getSelectionModel().getSelectedItem();
          sortTable(selected);
      });

      table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      nimCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNim()));
      nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
      n1Col.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNilai1()));
      n2Col.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNilai2()));
      n3Col.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNilai3()));
      avgCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAverage()));


      try {
          manager.loadFromFile("siswa.csv");
          refreshTable();
      } catch (IOException e) {
          infoLabel.setText("Gagal membaca file: " + e.getMessage());
      }
  }

  private void refreshTable() {
      table.setItems(FXCollections.observableArrayList(manager.getStudents()));
      infoLabel.setText("Total: " + manager.totalSiswa() + ", Rata-rata: " + String.format("%.2f", manager.averageNilai()));
  }

  private void sortTable(String sortby) {
    if (sortby == null) return;

    Comparator<Student> comparator = null;

    if (sortby.equals("Ascending")) {
        comparator = Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER);
    } else if (sortby.equals("Descending")) {
        comparator = Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER).reversed();
    }


    if (comparator != null) {
      List<Student> sortedList = new ArrayList<>(manager.getStudents());
      sortedList.sort(comparator);

      ObservableList<Student> sortedObservableList = FXCollections.observableArrayList(sortedList);
      table.setItems(sortedObservableList);  
  }
}
  
  @FXML
  public void onSearch() {
      String keyword = searchField.getText().trim();
      Student result = manager.binarySearch(keyword);
      if (result != null) {
        ObservableList<Student> filtered = FXCollections.observableArrayList();
        filtered.add(result);
        table.setItems(filtered);
        clearSearchButton.setVisible(true);
      } else if (result == null && !"".equals(keyword)) {
        table.setItems(FXCollections.observableArrayList());
        infoLabel.setText("Data tidak ditemukan");
        clearSearchButton.setVisible(true);
      }else {
        refreshTable();
        clearSearchButton.setVisible(false);
      }
  }

  @FXML
  public void onClearSearch() {
      searchField.clear();
      refreshTable();
      clearSearchButton.setVisible(false);
  }


  @FXML
  public void onAdd() {
      Dialog<Student> dialog = new Dialog<>();
      dialog.setTitle("Tambah Siswa Baru");
      dialog.setHeaderText("Masukkan data siswa");

      TextField nameField = new TextField();
      TextField nimField = new TextField();
      TextField n1Field = new TextField();
      TextField n2Field = new TextField();
      TextField n3Field = new TextField();

      GridPane grid = new GridPane();
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(20, 150, 10, 10));
      grid.addRow(0, new Label("Nama:"), nameField);
      grid.addRow(1, new Label("NIM:"), nimField);
      grid.addRow(2, new Label("Nilai 1:"), n1Field);
      grid.addRow(3, new Label("Nilai 2:"), n2Field);
      grid.addRow(4, new Label("Nilai 3:"), n3Field);

      dialog.getDialogPane().setContent(grid);
      ButtonType addButtonType = new ButtonType("Tambah", ButtonBar.ButtonData.OK_DONE);
      dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

      Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);

      final Student[] resultStudent = new Student[1];

      addButton.addEventFilter(ActionEvent.ACTION, event -> {
          try {
              String name = nameField.getText().trim();
              String nim = nimField.getText().trim();
              double n1 = Double.parseDouble(n1Field.getText().trim());
              double n2 = Double.parseDouble(n2Field.getText().trim());
              double n3 = Double.parseDouble(n3Field.getText().trim());

              if (name.isEmpty() || nim.isEmpty()) {
                  showAlert("Nama dan NIM tidak boleh kosong!");
                  event.consume();
                  return;
              }

              resultStudent[0] = new Student(name, nim, n1, n2, n3);
          } catch (NumberFormatException ex) {
              showAlert("Nilai harus berupa angka.");
              event.consume(); 
          }
      });

      dialog.setResultConverter(dialogButton -> {
          if (dialogButton == addButtonType) {
              return resultStudent[0];
          }
          return null;
      });

      Optional<Student> result = dialog.showAndWait();
      result.ifPresent(student -> {
          manager.addStudent(student);
          try {
            manager.saveToFile("siswa.csv");
            refreshTable();
          } catch (IOException e) {
            infoLabel.setText("Gagal menyimpan file");
          }
      });
  }
}