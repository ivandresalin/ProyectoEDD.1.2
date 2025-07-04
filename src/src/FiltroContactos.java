/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

/**
 *
 * @author ivand
 */
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Predicate;
import javafx.geometry.Insets;

public class FiltroContactos {

    public static void mostrarFiltro(Stage ventanaPrincipal, CircularDoubleLinkedList<Contacto> listaContactos, java.util.function.Consumer<Contacto> mostrarContacto) {
        Stage ventanaFiltro = new Stage();
        ventanaFiltro.setTitle("Filtrar Contactos");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Filtros
        TextField filtroNombre = new TextField();
        filtroNombre.setPromptText("Nombre o Apellido contiene...");

        ChoiceBox<String> filtroTipo = new ChoiceBox<>();
        filtroTipo.getItems().addAll("Todos", "Persona", "Empresa");
        filtroTipo.setValue("Todos");

        Spinner<Integer> spinnerMinAtributos = new Spinner<>(0, 50, 0);
        spinnerMinAtributos.setEditable(true);

        Button btnAplicar = new Button("Aplicar Filtro");

        ListView<Contacto> listaFiltrada = new ListView<>();

        // Mostrar resultado
        btnAplicar.setOnAction(e -> {
            listaFiltrada.getItems().clear();
            Nodo<Contacto> actual = listaContactos.getCabeza();
            int total = listaContactos.getTamanio();
            for (int i = 0; i < total; i++) {
                Contacto c = actual.getDato();

                Predicate<Contacto> coincideNombre = x ->
                    x.getNombreCompleto().toLowerCase().contains(filtroNombre.getText().toLowerCase());

                Predicate<Contacto> coincideTipo = x -> {
                    if (filtroTipo.getValue().equals("Todos")) return true;
                    if (filtroTipo.getValue().equals("Persona")) return x instanceof ContactoPersonal;
                    return x instanceof ContactoEmpresa;
                };

                Predicate<Contacto> coincideAtributos = x ->
                    x.getTotalAtributos() >= spinnerMinAtributos.getValue();

                if (coincideNombre.test(c) && coincideTipo.test(c) && coincideAtributos.test(c)) {
                    listaFiltrada.getItems().add(c);
                }

                actual = actual.getSiguiente();
            }
        });

        listaFiltrada.setOnMouseClicked(event -> {
            Contacto seleccionado = listaFiltrada.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                mostrarContacto.accept(seleccionado);
                ventanaFiltro.close();
            }
        });

        root.getChildren().addAll(
            new Label("Filtrar por nombre o apellido:"), filtroNombre,
            new Label("Filtrar por tipo:"), filtroTipo,
            new Label("Mínimo de atributos:"), spinnerMinAtributos,
            btnAplicar,
            new Label("Resultados:"),
            listaFiltrada
        );

        Scene scene = new Scene(root, 300, 500);
        ventanaFiltro.setScene(scene);
        ventanaFiltro.initOwner(ventanaPrincipal);
        ventanaFiltro.show();
    }
}
