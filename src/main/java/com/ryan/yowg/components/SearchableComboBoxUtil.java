package com.ryan.yowg.components;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.util.function.Function;

public class SearchableComboBoxUtil {

    public static <T> void makeSearchable(ComboBox<T> comboBox, ObservableList<T> originalItems, Function<T, String> nameExtractor) {
        comboBox.setEditable(true);

        FilteredList<T> filteredItems = new FilteredList<>(originalItems, p -> true);
        comboBox.setItems(filteredItems);

        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            T selected = comboBox.getSelectionModel().getSelectedItem();
            if (selected != null && nameExtractor.apply(selected).equalsIgnoreCase(newValue)) {
                return;
            }

            filteredItems.setPredicate(item -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String name = nameExtractor.apply(item);
                return name != null && name.toLowerCase().contains(newValue.toLowerCase());
            });

            if (!comboBox.isShowing() && comboBox.getEditor().isFocused()) {
                comboBox.show();
            }
        });

        comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return object != null ? nameExtractor.apply(object) : "";
            }

            @Override
            public T fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                return originalItems.stream()
                        .filter(item -> nameExtractor.apply(item).equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(comboBox.getSelectionModel().getSelectedItem());
            }
        });
    }
}
