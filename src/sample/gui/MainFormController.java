package sample.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.models.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {
    @FXML
    public TableView mainTable;
    @FXML
    public ComboBox cmbFoodType;

    ObservableList<Class<? extends Food>> foodTypes = FXCollections.observableArrayList(
            Food.class,
            Vegetable.class,
            Candy.class,
            Bun.class
    );

    // создали экземпляр класса модели
    FoodModel foodModel = new FoodModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // теперь вызываем метод, вместо прямого присваивания
        // прям как со всякими кнопочками
        foodModel.addDataChangedListener(foods -> {
            mainTable.setItems(FXCollections.observableArrayList(foods));
        });

        //foodModel.load();

        // создаем столбец, указываем что столбец преобразует Food в String,
        // указываем заголовок колонки как "Название"
        TableColumn<Food, String> titleColumn = new TableColumn<>("Название");
        // указываем какое поле брать у модели Food
        // в данном случае title, кстати именно в этих целях необходимо было создать getter и setter для поля title
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        // тоже самое для калорийности
        TableColumn<Food, String> kkalColumn = new TableColumn<>("Калорийность");
        kkalColumn.setCellValueFactory(new PropertyValueFactory<>("kkal"));

        // добавляем столбец с описанием
        TableColumn<Food, String> descriptionColumn = new TableColumn<>("Описание");
        // если хотим что-то более хитрое выводить, то используем лямбда выражение
        descriptionColumn.setCellValueFactory(cellData -> {
            // плюс надо обернуть возвращаемое значение в обертку свойство
            return new SimpleStringProperty(cellData.getValue().getDescription());
        });

        // подцепляем столбцы к таблице
        mainTable.getColumns().addAll(titleColumn, kkalColumn, descriptionColumn);


        // привязали список
        cmbFoodType.setItems(foodTypes);
        // выбрали первый элемент в списке
        cmbFoodType.getSelectionModel().select(0);

        // переопределил метод преобразования имени класса в текст
        cmbFoodType.setConverter(new StringConverter<Class>() {
            @Override
            public String toString(Class object) {
                // просто перебираем тут все возможные варианты
                if (Food.class.equals(object)) {
                    return "Все";
                } else if (Vegetable.class.equals(object)) {
                    return "Овощи";
                } else if (Candy.class.equals(object)) {
                    return "Конфеты";
                } else if (Bun.class.equals(object)) {
                    return "Булочка";
                }
                return null;
            }
            @Override
            public Class fromString(String string) {
                return null;
            }
        });

        cmbFoodType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.foodModel.setFoodFilter((Class<? extends Food>) newValue);
        });
    }




    public void onAddClick(ActionEvent actionEvent) throws IOException {

        // эти три строчки создюат форму из fxml файлика
        // в принципе можно было бы обойтись
        // Parent root = FXMLLoader.load(getClass().getResource("FoodForm.fxml"));
        // но дальше вот это разбиение на три строки упростит нам жизнь
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FoodForm.fxml"));
        Parent root = loader.load();

        // ну а тут создаем новое окно
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        // указываем что оно модальное
        stage.initModality(Modality.WINDOW_MODAL);
        // указываем что оно должно блокировать главное окно
        // ну если точнее, то окно, на котором мы нажали на кнопку
        stage.initOwner(this.mainTable.getScene().getWindow());

        // сначала берем контроллер
        FoodFormController controller = loader.getController();
        // передаем модель
        controller.foodModel = foodModel;

        // открываем окно и ждем пока его закроют
        stage.showAndWait();


    }

    public void onEditClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FoodForm.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.mainTable.getScene().getWindow());

        // передаем выбранную еду
        FoodFormController controller = loader.getController();
        controller.setFood((Food) this.mainTable.getSelectionModel().getSelectedItem());
        controller.foodModel = foodModel; // передаем модель в контроллер

        stage.showAndWait();

    }

    public void onDeleteClick(ActionEvent actionEvent) {
        Food food = (Food) this.mainTable.getSelectionModel().getSelectedItem();

        // выдаем подтверждающее сообщение
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(String.format("Точно удалить %s?", food.getTitle()));

        // если пользователь нажал OK
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            foodModel.delete(food.id); // тут вызываем метод модели, и передаем идентификатор
        }
    }

    public void onSaveToFileClick(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные");
        fileChooser.setInitialDirectory(new File("."));


        // тут вызываем диалог для сохранения файла
        File file = fileChooser.showSaveDialog(mainTable.getScene().getWindow());

        if (file != null) {
            foodModel.saveToFile(file.getPath());
        }

    }

    public void onLoadFromFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить данные");
        fileChooser.setInitialDirectory(new File("."));

        // а тут диалог для открытия файла
        File file = fileChooser.showOpenDialog(mainTable.getScene().getWindow());

        if (file != null) {
            foodModel.loadFromFile(file.getPath());
        }
    }
}