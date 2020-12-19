package sample.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.models.*;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class FoodFormController  implements Initializable {

    public FoodModel foodModel;
    public ChoiceBox cmbFoodType;

    public TextField txtFoodTitle;
    public TextField txtFoodKkal;

    public VBox vegetablePane;
    public CheckBox chkIsFresh;

    public HBox candyPane;
    public ChoiceBox cmbCandyType;

    public VBox cookiePane;
    public CheckBox chkwithRaisins;
    public CheckBox chkwithMilk;
    public CheckBox chkwithCinnamon;
    public CheckBox chkwithPoppy;

    final String FOOD_VEGETABLE = "Овощ";
    final String FOOD_CANDY = "Конфета";
    final String FOOD_COOKIE = "Булочка";


    private Integer id = null; // добавили поле под идентификатор

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cmbFoodType.setItems(FXCollections.observableArrayList(
                FOOD_VEGETABLE,
                FOOD_CANDY,
                FOOD_COOKIE
        ));

        cmbFoodType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // вызываю новую функцию
            updatePanes((String) newValue);
        });

        // добавляем все три типа шоколада в комобобокс
        cmbCandyType.getItems().setAll(
                Candy.Type.Fondant,
                Candy.Type.Jelly,
                Candy.Type.Souffle,
                Candy.Type.Chocolate,
                Candy.Type.Truffles
        );

        // и используем метод setConverter,
        // чтобы типы объекты рендерились как строки
        cmbCandyType.setConverter(new StringConverter<Candy.Type>() {
            @Override
            public String toString(Candy.Type object) {
                // просто указываем как рендерить
                switch (object) {
                    case Fondant:
                        return "Помадка";
                    case Jelly:
                        return "Желе";
                    case Souffle:
                        return "Суфле";
                    case Chocolate:
                        return "Шоколад";
                    case Truffles:
                        return "Трюфель";
                }
                return null;
            }

            @Override
            public Candy.Type fromString(String string) {
                // этот метод не трогаем так как наш комбобкос имеет фиксированный набор элементов
                return null;
            }
        });


        // вызываю новую функцию при инициализации формы
        updatePanes("");
    }

    public void updatePanes(String value) {
        this.vegetablePane.setVisible(value.equals(FOOD_VEGETABLE));
        this.vegetablePane.setManaged(value.equals(FOOD_VEGETABLE));
        this.candyPane.setVisible(value.equals(FOOD_CANDY));
        this.candyPane.setManaged(value.equals(FOOD_CANDY));
        this.cookiePane.setVisible(value.equals(FOOD_COOKIE));
        this.cookiePane.setManaged(value.equals(FOOD_COOKIE));
    }

    public void setFood(Food food) {
        // делаем так что если объект редактируется, то нельзя переключать тип
        this.cmbFoodType.setDisable(food != null);

        // присвоим значение идентификатора,
        // если передали еду то есть food != null, то используем food.id
        // иначе запихиваем в this.id значение null
        this.id = food != null ? food.id : null;

        if (food != null) {
            // ну а тут стандартное заполнение полей в соответствии с переданной едой
            this.txtFoodKkal.setText(String.valueOf(food.getKkal()));
            this.txtFoodTitle.setText(food.getTitle());

            if (food instanceof Vegetable) { // если фрукт
                this.cmbFoodType.setValue(FOOD_VEGETABLE);
                this.chkIsFresh.setSelected(((Vegetable) food).isFresh);
            } else if (food instanceof Bun) { // если булочка
                this.cmbFoodType.setValue(FOOD_COOKIE);
                this.chkwithRaisins.setSelected(((Bun) food).withRaisins);
                this.chkwithMilk.setSelected(((Bun) food).withMilk);
                this.chkwithCinnamon.setSelected(((Bun) food).withCinnamon);
                this.chkwithPoppy.setSelected(((Bun) food).withPoppy);
            } else if (food instanceof Candy) { // если шоколад
                this.cmbFoodType.setValue(FOOD_CANDY);
                this.cmbCandyType.setValue(((Candy) food).type);
            }
        }
    }

    public Food getFood() {
        Food result = null;
        int kkal = Integer.parseInt(this.txtFoodKkal.getText());
        String title = this.txtFoodTitle.getText();

        switch ((String)this.cmbFoodType.getValue()) {
            case FOOD_CANDY:
                result = new Candy(kkal, title, (Candy.Type)this.cmbCandyType.getValue());
                break;
            case FOOD_COOKIE:
                result = new Bun(
                        kkal,
                        title,
                        this.chkwithRaisins.isSelected(),
                        this.chkwithMilk.isSelected(),
                        this.chkwithCinnamon.isSelected(),
                        this.chkwithPoppy.isSelected()
                );
                break;
            case FOOD_VEGETABLE:
                result = new Vegetable(kkal, title, this.chkIsFresh.isSelected());
                break;
        }
        return result;
    }

    public void onSaveClick(javafx.event.ActionEvent actionEvent) {

        // проверяем передали ли идентификатор
        if (this.id != null) {
            // если передавали значит у нас редактирование
            // собираем еду с формы
            Food food = getFood();
            // подвязываем переданный идентификатор к собранной с формы еды
            food.id = this.id;
            // отправляем в модель на изменение
            this.foodModel.edit(food);
        } else {
            // ну а если у нас добавление, просто добавляем объект
            this.foodModel.add(getFood());
        }
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void onCancelClick(javafx.event.ActionEvent actionEvent) {

        // закрываем окно к которому привязана кнопка
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }
}
