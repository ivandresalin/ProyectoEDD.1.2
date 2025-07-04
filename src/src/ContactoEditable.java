package src;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ContactoEditable {

    private CircularDoubleLinkedList<Contacto> listaContactos;
    private Stage ventanaPrincipal;
    private Stage ventanaEditar;

    public ContactoEditable(CircularDoubleLinkedList<Contacto> listaContactos, Stage ventanaPrincipal) {
        this.listaContactos = listaContactos;
        this.ventanaPrincipal = ventanaPrincipal;
    }

    private static class CampoEditable {
        CircularDoubleLinkedList<TextField> camposValor = new CircularDoubleLinkedList<>();
        CircularDoubleLinkedList<TextField> camposDescripcion = new CircularDoubleLinkedList<>();
        VBox contenedor = new VBox(5);

        CampoEditable(String titulo, CircularDoubleLinkedList<Atributo> existentes, String promptValor, String promptDesc) {
            Label label = new Label(titulo);
            label.setStyle("-fx-font-weight: bold;");
            contenedor.getChildren().add(label);

            Nodo<Atributo> nodo = existentes.getCabeza();
            for (int i = 0; i < existentes.getTamanio(); i++) {
                Atributo attr = nodo.getDato();
                TextField tfValor = new TextField(attr.getNombre());
                tfValor.setPromptText(promptValor);
                TextField tfDesc = new TextField(attr.getDescripcion());
                tfDesc.setPromptText(promptDesc);
                camposValor.add(tfValor);
                camposDescripcion.add(tfDesc);
                HBox fila = new HBox(5, tfValor, tfDesc);
                contenedor.getChildren().add(fila);
                nodo = nodo.getSiguiente();
            }

            TextField nuevoValor = new TextField();
            nuevoValor.setPromptText(promptValor);
            TextField nuevaDesc = new TextField();
            nuevaDesc.setPromptText(promptDesc);
            Button btnAgregar = new Button("+");

            btnAgregar.setOnAction(e -> {
                if (!nuevoValor.getText().trim().isEmpty()) {
                    camposValor.add(nuevoValor);
                    camposDescripcion.add(nuevaDesc);
                    contenedor.getChildren().add(camposValor.getTamanio(),
                        new HBox(5, nuevoValor, nuevaDesc));
                    TextField nuevo1 = new TextField();
                    nuevo1.setPromptText(promptValor);
                    TextField nuevo2 = new TextField();
                    nuevo2.setPromptText(promptDesc);
                    contenedor.getChildren().remove(btnAgregar.getParent());
                    HBox nuevaFila = new HBox(5, nuevo1, nuevo2, btnAgregar);
                    contenedor.getChildren().add(nuevaFila);
                }
            });

            HBox filaNueva = new HBox(5, nuevoValor, nuevaDesc, btnAgregar);
            contenedor.getChildren().add(filaNueva);
        }

        public VBox getContenedor() {
            return contenedor;
        }

        public CircularDoubleLinkedList<Atributo> obtenerAtributos() {
            CircularDoubleLinkedList<Atributo> resultado = new CircularDoubleLinkedList<>();
            Nodo<TextField> nodoValor = camposValor.getCabeza();
            Nodo<TextField> nodoDesc = camposDescripcion.getCabeza();

            for (int i = 0; i < camposValor.getTamanio(); i++) {
                String val = nodoValor.getDato().getText().trim();
                String desc = nodoDesc.getDato().getText().trim();
                if (!val.isEmpty()) {
                    resultado.add(new Atributo(val, desc.isEmpty() ? "-" : desc));
                }
                nodoValor = nodoValor.getSiguiente();
                nodoDesc = nodoDesc.getSiguiente();
            }
            return resultado;
        }
    }

    private VBox crearSeccionRelacionados(Contacto actual, CircularDoubleLinkedList<Contacto> lista) {
        VBox box = new VBox(5);
        Label label = new Label("🤝 Contactos Relacionados:");
        label.setStyle("-fx-font-weight: bold;");
        box.getChildren().add(label);

        Nodo<Contacto> nodo = lista.getCabeza();
        for (int i = 0; i < lista.getTamanio(); i++) {
            Contacto c = nodo.getDato();
            if (actual == null || !c.equals(actual)) {
                CheckBox check = new CheckBox(c.getNombreCompleto());
                check.setUserData(c);
                if (actual != null) {
                    Nodo<Contacto> relNodo = actual.getContactosRelacionados().getCabeza();
                    for (int j = 0; j < actual.getContactosRelacionados().getTamanio(); j++) {
                        if (relNodo.getDato().equals(c)) {
                            check.setSelected(true);
                            break;
                        }
                        relNodo = relNodo.getSiguiente();
                    }
                }
                box.getChildren().add(check);
            }
            nodo = nodo.getSiguiente();
        }

        return box;
    }

    public void mostrarFormularioNuevo() {
        ventanaEditar = new Stage();
        ventanaEditar.setTitle("Nuevo Contacto");

        VBox formulario = new VBox(10);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");

        TextField txtApellido = new TextField();
        txtApellido.setPromptText("Apellido");

        ComboBox<String> tipoCombo = new ComboBox<>();
        tipoCombo.getItems().addAll("Personal", "Empresa");
        tipoCombo.setValue("Personal");

        tipoCombo.setOnAction(e -> {
            boolean esPersonal = tipoCombo.getValue().equals("Personal");
            txtApellido.setDisable(!esPersonal);
            if (!esPersonal) txtApellido.clear();
        });

        CampoEditable telefonos = new CampoEditable("📞 Teléfonos:", new CircularDoubleLinkedList<>(), "Teléfono", "Descripción");
        CampoEditable emails = new CampoEditable("✉️ Emails:", new CircularDoubleLinkedList<>(), "Email", "Descripción");
        CampoEditable direcciones = new CampoEditable("📍 Direcciones:", new CircularDoubleLinkedList<>(), "Dirección", "Descripción");
        CampoEditable fechas = new CampoEditable("🎉 Fechas de interés:", new CircularDoubleLinkedList<>(), "Fecha", "Descripción");
        CampoEditable redes = new CampoEditable("🌐 Redes Sociales:", new CircularDoubleLinkedList<>(), "Red Social", "Descripción");

        Label labelFotos = new Label("Fotos: 0");
        Button btnAgregarFoto = new Button("Agregar Foto");
        CircularDoubleLinkedList<Foto> fotosTemp = new CircularDoubleLinkedList<>();

        btnAgregarFoto.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar foto");
            File archivo = fileChooser.showOpenDialog(ventanaEditar);
            if (archivo != null) {
                fotosTemp.add(new Foto(archivo.getAbsolutePath(), "Foto añadida"));
                labelFotos.setText("Fotos: " + fotosTemp.getTamanio());
            }
        });

        VBox relacionadosBox = crearSeccionRelacionados(null, listaContactos);

        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        btnGuardar.setOnAction(event -> {
            Contacto nuevo;
            if (tipoCombo.getValue().equals("Personal")) {
                nuevo = new ContactoPersonal(txtNombre.getText(), txtApellido.getText());
            } else {
                nuevo = new ContactoEmpresa(txtNombre.getText());
            }

            nuevo.getTelefonos().vaciar();
            nuevo.getTelefonos().agregarTodo(telefonos.obtenerAtributos());
            nuevo.getEmails().vaciar();
            nuevo.getEmails().agregarTodo(emails.obtenerAtributos());
            nuevo.getDirecciones().vaciar();
            nuevo.getDirecciones().agregarTodo(direcciones.obtenerAtributos());
            nuevo.getFechasDeInteres().vaciar();
            nuevo.getFechasDeInteres().agregarTodo(fechas.obtenerAtributos());
            nuevo.getRedesSociales().vaciar();
            nuevo.getRedesSociales().agregarTodo(redes.obtenerAtributos());

            Nodo<Foto> nodoFoto = fotosTemp.getCabeza();
            for (int i = 0; i < fotosTemp.getTamanio(); i++) {
                nuevo.addFoto(nodoFoto.getDato());
                nodoFoto = nodoFoto.getSiguiente();
            }

            for (javafx.scene.Node nodeRel : relacionadosBox.getChildren()) {
                if (nodeRel instanceof CheckBox check && check.isSelected()) {
                    Contacto relacionado = (Contacto) check.getUserData();
                    nuevo.addContactoRelacionado(relacionado);
                }
            }

            listaContactos.add(nuevo);
            ventanaEditar.close();
        });

        btnCancelar.setOnAction(event -> ventanaEditar.close());

        formulario.getChildren().addAll(
            new Label("Tipo de contacto:"), tipoCombo,
            new Label("Nombre:"), txtNombre,
            new Label("Apellido:"), txtApellido,
            telefonos.getContenedor(),
            emails.getContenedor(),
            direcciones.getContenedor(),
            fechas.getContenedor(),
            redes.getContenedor(),
            relacionadosBox,
            btnAgregarFoto, labelFotos,
            btnGuardar, btnCancelar
        );

        formulario.setPrefHeight(1500); // Para activar scroll si es necesario

        ScrollPane scrollPane = new ScrollPane(formulario);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Scene scene = new Scene(scrollPane, 500, 900);
        ventanaEditar.setScene(scene);
        ventanaEditar.show();
    }

    public void mostrarFormularioEdicion(Contacto contacto) {
        ventanaEditar = new Stage();
        ventanaEditar.setTitle("Editar Contacto");

        VBox formulario = new VBox(10);
        TextField txtNombre = new TextField(contacto.getNombre());

        TextField txtApellido = contacto instanceof ContactoPersonal
                ? new TextField(((ContactoPersonal) contacto).getApellido())
                : new TextField();

        txtApellido.setDisable(!(contacto instanceof ContactoPersonal));
        txtApellido.setPromptText("Apellido");

        CampoEditable telefonos = new CampoEditable("📞 Teléfonos:", contacto.getTelefonos(), "Teléfono", "Descripción");
        CampoEditable emails = new CampoEditable("✉️ Emails:", contacto.getEmails(), "Email", "Descripción");
        CampoEditable direcciones = new CampoEditable("📍 Direcciones:", contacto.getDirecciones(), "Dirección", "Descripción");
        CampoEditable fechas = new CampoEditable("🎉 Fechas de interés:", contacto.getFechasDeInteres(), "Fecha", "Descripción");
        CampoEditable redes = new CampoEditable("🌐 Redes Sociales:", contacto.getRedesSociales(), "Red Social", "Descripción");

        Label labelFotos = new Label("Fotos actuales: " + contacto.getFotos().getTamanio());
        Button btnAgregarFoto = new Button("Agregar Foto");

        btnAgregarFoto.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar foto");
            File archivo = fileChooser.showOpenDialog(ventanaEditar);
            if (archivo != null) {
                contacto.addFoto(new Foto(archivo.getAbsolutePath(), "Foto añadida"));
                labelFotos.setText("Fotos actuales: " + contacto.getFotos().getTamanio());
            }
        });

        VBox relacionadosBox = crearSeccionRelacionados(contacto, listaContactos);

        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        btnGuardar.setOnAction(event -> {
            contacto.setNombre(txtNombre.getText());
            if (contacto instanceof ContactoPersonal) {
                ((ContactoPersonal) contacto).setApellido(txtApellido.getText());
            }

            contacto.getTelefonos().vaciar();
            contacto.getTelefonos().agregarTodo(telefonos.obtenerAtributos());
            contacto.getEmails().vaciar();
            contacto.getEmails().agregarTodo(emails.obtenerAtributos());
            contacto.getDirecciones().vaciar();
            contacto.getDirecciones().agregarTodo(direcciones.obtenerAtributos());
            contacto.getFechasDeInteres().vaciar();
            contacto.getFechasDeInteres().agregarTodo(fechas.obtenerAtributos());
            contacto.getRedesSociales().vaciar();
            contacto.getRedesSociales().agregarTodo(redes.obtenerAtributos());

            contacto.getContactosRelacionados().vaciar();
            for (javafx.scene.Node node : relacionadosBox.getChildren()) {
                if (node instanceof CheckBox check && check.isSelected()) {
                    Contacto relacionado = (Contacto) check.getUserData();
                    contacto.addContactoRelacionado(relacionado);
                }
            }

            ventanaEditar.close();
        });

        btnCancelar.setOnAction(event -> ventanaEditar.close());

        formulario.getChildren().addAll(
            new Label("Nombre:"), txtNombre,
            new Label("Apellido:"), txtApellido,
            telefonos.getContenedor(),
            emails.getContenedor(),
            direcciones.getContenedor(),
            fechas.getContenedor(),
            redes.getContenedor(),
            relacionadosBox,
            btnAgregarFoto, labelFotos,
            btnGuardar, btnCancelar
        );

        formulario.setPrefHeight(1500);

        ScrollPane scrollPane = new ScrollPane(formulario);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Scene scene = new Scene(scrollPane, 500, 900);
        ventanaEditar.setScene(scene);
        ventanaEditar.show();
    }
}
