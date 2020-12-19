package sample.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FoodModel {
    ArrayList<Food> foodList = new ArrayList<>();
    private int counter = 1;

    // поле фильтр, по умолчанию используем базовый класс
    //
    Class<? extends Food> foodFilter = Food.class;

    // Создаем наш любимый функциональный интерфейс
    // с помощью него мы организуем общение между моделью и контроллером
    public interface DataChangedListener {
        void dataChanged(ArrayList<Food> foodList);
    }

    private ArrayList<DataChangedListener> dataChangedListeners = new ArrayList<>();
    // добавляем метод который позволяет привязать слушателя
    public void addDataChangedListener(DataChangedListener listener) {
        // ну просто его в список добавляем
        this.dataChangedListeners.add(listener);
    }

    public void load() {
        foodList.clear();

        // скопипастили код из контроллера
        this.add(new Vegetable(100, "Огурец", true),false);
        this.add(new Candy(200, "конфета Катюха", Candy.Type.Souffle),false);
        this.add(new Bun(300, "сладкая булочка с Молоком", true, true, false, false),false);

        this.emitDataChanged();

    }

    public void add(Food food, boolean emit) {
        food.id = counter; // присваиваем id еды, значение счетчика
        counter += 1; // увеличиваем счетчик на единицу

        this.foodList.add(food);

        if (emit) {
            this.emitDataChanged();
        }
    }

    public void add(Food food) {
        add(food, true);
    }

    public void delete(int id) {
        for (int i = 0; i< this.foodList.size(); ++i) {
            // ищем в цикле еду с данным айдишником
            if (this.foodList.get(i).id == id) {
                // если нашли то удаляем
                this.foodList.remove(i);
                break;
            }
        }

        // оповещаем об изменениях
        this.emitDataChanged();
    }

    private void emitDataChanged() {

        for (DataChangedListener listener : dataChangedListeners) {
            ArrayList<Food> filteredList = new ArrayList<>(
                    foodList.stream() // запускаем стрим
                            .filter(food -> foodFilter.isInstance(food)) // фильтруем по типу
                            .collect(Collectors.toList()) // превращаем в список
            );
            listener.dataChanged(filteredList); // подсовывам сюда список
        }
    }

    public void edit(Food food) {
        // ищем объект в списке
        for (int i = 0; i< this.foodList.size(); ++i) {
            // чтобы id совпадал с id переданным форме
            if (this.foodList.get(i).id == food.id) {
                // если нашли, то подменяем объект
                this.foodList.set(i, food);
                break;
            }
        }

        this.emitDataChanged();
    }

    public void saveToFile(String path) {

        // открываем файл для чтения
        try (Writer writer =  new FileWriter(path)) {
            // создаем сериализатор
            ObjectMapper mapper = new ObjectMapper();
            // записываем данные списка foodList в файл
            mapper.writerFor(new TypeReference<ArrayList<Food>>() { }) // указали какой тип подсовываем
                    .withDefaultPrettyPrinter() // кстати эта строчка чтобы в файлике все красиво печаталось
                    .writeValue(writer, foodList); // а это непосредственно запись

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String path) {
        try (Reader reader =  new FileReader(path)) {
            // создаем сериализатор
            ObjectMapper mapper = new ObjectMapper();

            // читаем из файла
            foodList = mapper.readerFor(new TypeReference<ArrayList<Food>>() { })
                    .readValue(reader);

            // рассчитываем счетчик как максимальное значение id плюс 1
            this.counter = foodList.stream()
                    .map(food -> food.id)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // оповещаем что данные загрузились
        this.emitDataChanged();
    }

    public void setFoodFilter(Class<? extends Food> foodFilter) {
        this.foodFilter = foodFilter;

        this.emitDataChanged();
    }

}

