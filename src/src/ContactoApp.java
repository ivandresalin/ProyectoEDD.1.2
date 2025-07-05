package src;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ContactoApp extends Application {

    private CircularDoubleLinkedList<Contacto> listaContactos;
    private ObservableList<Contacto> observableContactos;

    private Label labelNombre;
    private VBox detallesBox;
    private ImageView fotoContacto;
    private Stage ventanaPrincipal;
    private Button btnFotoAnterior;
    private Button btnFotoSiguiente;

    private ListView<Contacto> listViewContactos;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ventanaPrincipal = primaryStage;

        listaContactos = GestorPersistencia.cargarContactos();

        if (listaContactos.getTamanio() == 0) {
            inicializarContactosDeEjemplo();
        }

        // Crear la lista observable para la ListView (pestaña lateral)
        observableContactos = FXCollections.observableArrayList();
        actualizarObservableContactos();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Pestaña lateral con lista de contactos
        listViewContactos = new ListView<>(observableContactos);
        listViewContactos.setPrefWidth(150);
        listViewContactos.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Contacto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreCompleto());
                }
            }
        });

        listViewContactos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                listaContactos.setActual(newSel);
                mostrarContacto(newSel);
            }
        });

        root.setLeft(listViewContactos);

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(10));

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);

        fotoContacto = new ImageView();
        fotoContacto.setFitWidth(100);
        fotoContacto.setFitHeight(100);
        fotoContacto.setPreserveRatio(true);

        labelNombre = new Label("Nombre del contacto");
        labelNombre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox fotoNav = new HBox(5);
        fotoNav.setAlignment(Pos.CENTER);
        btnFotoAnterior = new Button("←");
        btnFotoSiguiente = new Button("→");
        fotoNav.getChildren().addAll(btnFotoAnterior, fotoContacto, btnFotoSiguiente);

        topBox.getChildren().addAll(fotoNav, labelNombre);

        detallesBox = new VBox(10);
        detallesBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(detallesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-padding: 10;");

        centerBox.getChildren().addAll(topBox, scrollPane);

        root.setCenter(centerBox);

        HBox botonesNav = new HBox(10);
        botonesNav.setAlignment(Pos.CENTER);
        botonesNav.setPadding(new Insets(10));

        Button btnAnterior = new Button("←");
        Button btnSiguiente = new Button("→");
        Button btnAgregar = new Button("＋");
        Button btnEditar = new Button("✎");
        Button btnEliminar = new Button("🗑");
        Button btnOrdenarApellidoNombre = new Button("Ordenar Apellido y Nombre");
        Button btnOrdenarTipo = new Button("Ordenar por Tipo");
        Button btnOrdenarRedes = new Button("Ordenar por Redes Sociales");
        Button btnFiltrar = new Button("Filtrar");

        botonesNav.getChildren().addAll(
            btnAnterior, btnSiguiente, btnAgregar, btnEditar, btnEliminar,
            btnOrdenarApellidoNombre, btnOrdenarTipo, btnOrdenarRedes, btnFiltrar
        );

        root.setBottom(botonesNav);

        // Botones funcionalidad
        btnAnterior.setOnAction(e -> {
            Contacto anterior = listaContactos.anterior();
            if (anterior != null) {
                mostrarContacto(anterior);
                listViewContactos.getSelectionModel().select(anterior);
            }
        });

        btnSiguiente.setOnAction(e -> {
            Contacto siguiente = listaContactos.siguiente();
            if (siguiente != null) {
                mostrarContacto(siguiente);
                listViewContactos.getSelectionModel().select(siguiente);
            }
        });

        btnAgregar.setOnAction(e -> {
            new ContactoEditable(listaContactos, ventanaPrincipal).mostrarFormularioNuevo();
            actualizarObservableContactos();
        });

        btnEditar.setOnAction(e -> {
            Contacto actual = listaContactos.getActual();
            if (actual != null) {
                new ContactoEditable(listaContactos, ventanaPrincipal).mostrarFormularioEdicion(actual);
                actualizarObservableContactos();
            }
        });

        btnEliminar.setOnAction(e -> {
            eliminarContacto();
            actualizarObservableContactos();
        });

        btnOrdenarApellidoNombre.setOnAction(e -> {
            listaContactos.ordenar(ComparadoresContacto.porApellidoNombre);
            actualizarObservableContactos();
            mostrarContacto(listaContactos.getActual());
            listViewContactos.getSelectionModel().select(listaContactos.getActual());
        });

        btnOrdenarTipo.setOnAction(e -> {
            listaContactos.ordenar(ComparadoresContacto.porTipo);
            actualizarObservableContactos();
            mostrarContacto(listaContactos.getActual());
            listViewContactos.getSelectionModel().select(listaContactos.getActual());
        });

        btnOrdenarRedes.setOnAction(e -> {
            listaContactos.ordenar(ComparadoresContacto.porRedesSociales);
            actualizarObservableContactos();
            mostrarContacto(listaContactos.getActual());
            listViewContactos.getSelectionModel().select(listaContactos.getActual());
        });

        btnFiltrar.setOnAction(e -> {
            FiltroContactos.mostrarFiltro(ventanaPrincipal, listaContactos, contacto -> {
                listaContactos.setActual(contacto);
                mostrarContacto(contacto);
                actualizarObservableContactos();
                listViewContactos.getSelectionModel().select(contacto);
            });
        });

        btnFotoAnterior.setOnAction(e -> mostrarFotoAnterior());
        btnFotoSiguiente.setOnAction(e -> mostrarFotoSiguiente());

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            GestorPersistencia.guardarContactos(listaContactos);
        });

        Scene scene = new Scene(root, 760, 640);  // Más ancho para la lista lateral
        primaryStage.setScene(scene);
        primaryStage.setTitle("Agenda Móvil");
        primaryStage.show();

        // Seleccionar el primer contacto al iniciar
        if (!observableContactos.isEmpty()) {
            listViewContactos.getSelectionModel().select(0);
            mostrarContacto(observableContactos.get(0));
            listaContactos.setActual(observableContactos.get(0));
        }
    }

    private void actualizarObservableContactos() {
        observableContactos.clear();
        Nodo<Contacto> nodo = listaContactos.getCabeza();
        if (nodo != null) {
            for (int i = 0; i < listaContactos.getTamanio(); i++) {
                observableContactos.add(nodo.getDato());
                nodo = nodo.getSiguiente();
            }
        }
    }

    private void mostrarContacto(Contacto contacto) {
        if (contacto != null) {
            labelNombre.setText(contacto.getNombreCompleto());
            mostrarFoto(contacto);
            detallesBox.getChildren().clear();

            detallesBox.getChildren().addAll(
                crearSeccion("📞 Teléfonos:", contacto.getTelefonos()),
                crearSeccion("✉️ Emails:", contacto.getEmails()),
                crearSeccion("📍 Direcciones:", contacto.getDirecciones()),
                crearSeccion("🎉 Fechas de interés:", contacto.getFechasDeInteres()),
                crearSeccion("🌐 Redes Sociales:", contacto.getRedesSociales()),
                crearSeccionContactosRelacionados("🤝 Contactos Relacionados:", contacto)
            );
        } else {
            labelNombre.setText("Sin contactos");
            detallesBox.getChildren().clear();
            fotoContacto.setImage(null);
        }
    }

    private VBox crearSeccion(String titulo, CircularDoubleLinkedList<Atributo> lista) {
        VBox box = new VBox(3);
        Label labelTitulo = new Label(titulo);
        labelTitulo.setStyle("-fx-font-weight: bold;");
        box.getChildren().add(labelTitulo);

        if (lista != null && lista.getTamanio() > 0) {
            Nodo<Atributo> actual = lista.getCabeza();
            for (int i = 0; i < lista.getTamanio(); i++) {
                Atributo a = actual.getDato();
                box.getChildren().add(new Label("- " + a.getNombre() + " (" + a.getDescripcion() + ")"));
                actual = actual.getSiguiente();
            }
        }

        return box;
    }

    private VBox crearSeccionContactosRelacionados(String titulo, Contacto contacto) {
    VBox box = new VBox(3);
    Label labelTitulo = new Label(titulo);
    labelTitulo.setStyle("-fx-font-weight: bold;");
    box.getChildren().add(labelTitulo);

    Nodo<Contacto> nodo = contacto.getContactosRelacionados().getCabeza();
    int tam = contacto.getContactosRelacionados().getTamanio();
    for (int i = 0; i < tam; i++) {
        Contacto rel = nodo.getDato();
        Button btnContactoRelacionado = new Button(rel.getNombreCompleto());
        btnContactoRelacionado.setMaxWidth(Double.MAX_VALUE);
        btnContactoRelacionado.setOnAction(e -> {
            listaContactos.setActual(rel);  // Cambia el contacto actual
            mostrarContacto(rel);           // Muestra el contacto seleccionado
            listViewContactos.getSelectionModel().select(rel); // También actualiza la lista
        });
        box.getChildren().add(btnContactoRelacionado);
        nodo = nodo.getSiguiente();
    }


    return box;
}

    private void mostrarFoto(Contacto contacto) {
        if (contacto.getFotos().getTamanio() > 0) {
            Foto foto = contacto.getFotos().getActual();
            fotoContacto.setImage(new Image("file:" + foto.getRuta()));
        } else {
            fotoContacto.setImage(null);
        }
    }

    private void mostrarFotoAnterior() {
        Contacto contacto = listaContactos.getActual();
        if (contacto != null && contacto.getFotos().getTamanio() > 0) {
            contacto.getFotos().anterior();
            mostrarFoto(contacto);
        }
    }

    private void mostrarFotoSiguiente() {
        Contacto contacto = listaContactos.getActual();
        if (contacto != null && contacto.getFotos().getTamanio() > 0) {
            contacto.getFotos().siguiente();
            mostrarFoto(contacto);
        }
    }
    private void inicializarContactosDeEjemplo() {
        ContactoPersonal p1 = new ContactoPersonal("Ana", "Lopez");
        p1.addTelefono("0991234567", "Personal");
        p1.addEmail("ana.lopez@gmail.com", "Principal");
        p1.addDireccion("Av. 9 de Octubre", "Casa");
        p1.addFechaDeInteres("1992-03-15", "Cumpleaños");
        p1.addRedSocial("Instagram", "@ana_lopez");
        listaContactos.add(p1);

        ContactoEmpresa e1 = new ContactoEmpresa("EcoServicios S.A.");
        e1.addTelefono("022223334", "Central");
        e1.addEmail("info@ecoservicios.com", "Corporativo");
        e1.addDireccion("Km 10 Vía a la Costa", "Oficina");
        e1.addRedSocial("LinkedIn", "ecoservicios");
        listaContactos.add(e1);

        ContactoPersonal p2 = new ContactoPersonal("Luis", "Martínez");
        p2.addTelefono("0987654321", "WhatsApp");
        p2.addEmail("luism@gmail.com", "Alternativo");
        p2.addDireccion("Cdla. Los Almendros", "Depto");
        p2.addFechaDeInteres("1995-06-20", "Cumpleaños");
        p2.addRedSocial("Facebook", "luis.martinez");
        listaContactos.add(p2);

        ContactoPersonal p3 = new ContactoPersonal("Carla", "Ramos");
        p3.addTelefono("0971234567", "Personal");
        p3.addEmail("carla.ramos@hotmail.com", "Trabajo");
        p3.addDireccion("Cdla. Bellavista", "Casa");
        p3.addFechaDeInteres("1990-12-01", "Cumpleaños");
        p3.addRedSocial("Instagram", "@carlita.r");
        listaContactos.add(p3);

        ContactoEmpresa e2 = new ContactoEmpresa("LogiTransporte Cía. Ltda.");
        e2.addTelefono("022211000", "Oficina");
        e2.addEmail("contacto@logitrans.com", "Logística");
        e2.addDireccion("Av. Barcelona", "Sucursal");
        listaContactos.add(e2);

        ContactoPersonal p4 = new ContactoPersonal("Pedro", "Suárez");
        p4.addTelefono("0965544332", "Movistar");
        p4.addEmail("pedro.suarez@gmail.com", "Trabajo");
        p4.addDireccion("Guayacanes", "Depto");
        p4.addRedSocial("LinkedIn", "pedrosuarez");
        listaContactos.add(p4);

        ContactoPersonal p5 = new ContactoPersonal("María", "Fernández");
        p5.addTelefono("0956677884", "Claro");
        p5.addEmail("maria.f@gmail.com", "Personal");
        p5.addDireccion("Sauces 9", "Casa");
        p5.addFechaDeInteres("1988-10-22", "Cumpleaños");
        p5.addRedSocial("Facebook", "maria.fernandez");
        listaContactos.add(p5);

        ContactoEmpresa e3 = new ContactoEmpresa("CyberTech");
        e3.addTelefono("042212121", "Soporte");
        e3.addEmail("soporte@cybertech.com", "Técnico");
        e3.addDireccion("Cdla. Kennedy", "Principal");
        listaContactos.add(e3);

        ContactoPersonal p6 = new ContactoPersonal("Esteban", "Jiménez");
        p6.addTelefono("0943219876", "Personal");
        p6.addEmail("esteban.j@hotmail.com", "Principal");
        p6.addDireccion("Mirador del Norte", "Depto");
        p6.addFechaDeInteres("1999-11-11", "Cumpleaños");
        p6.addRedSocial("Instagram", "@estebanj");
        listaContactos.add(p6);

        ContactoPersonal p7 = new ContactoPersonal("Camila", "Rodríguez");
        p7.addTelefono("0938899776", "Movistar");
        p7.addEmail("camila.r@gmail.com", "Trabajo");
        p7.addDireccion("Urb. Las Orquídeas", "Casa");
        p7.addRedSocial("Facebook", "camila.rdz");
        listaContactos.add(p7);

        ContactoEmpresa e4 = new ContactoEmpresa("Industrias del Pacífico");
        e4.addTelefono("043344556", "Ventas");
        e4.addEmail("ventas@pacifico.com", "Corporativo");
        e4.addDireccion("Zona Industrial", "Bodega");
        listaContactos.add(e4);

        ContactoPersonal p8 = new ContactoPersonal("Fernando", "Mendoza");
        p8.addTelefono("0921122334", "Claro");
        p8.addEmail("fer.mendoza@gmail.com", "Principal");
        p8.addDireccion("Via Daule", "Depto");
        p8.addRedSocial("Twitter", "@fernando_m");
        listaContactos.add(p8);

        ContactoPersonal p9 = new ContactoPersonal("Lucía", "Vera");
        p9.addTelefono("0919988776", "Personal");
        p9.addEmail("lucia.vera@yahoo.com", "Trabajo");
        p9.addDireccion("La Puntilla", "Casa");
        p9.addFechaDeInteres("1996-05-03", "Graduación");
        listaContactos.add(p9);

        ContactoEmpresa e5 = new ContactoEmpresa("Constructora Andina");
        e5.addTelefono("045566778", "Oficina");
        e5.addEmail("info@constructoraandina.com", "General");
        e5.addDireccion("Vía Samborondón", "Matriz");
        listaContactos.add(e5);

        ContactoPersonal p10 = new ContactoPersonal("Jorge", "Ortiz");
        p10.addTelefono("0900887766", "Personal");
        p10.addEmail("jorgeo@gmail.com", "Alternativo");
        p10.addDireccion("Urdesa Central", "Casa");
        p10.addFechaDeInteres("1985-09-09", "Cumpleaños");
        listaContactos.add(p10);
        
        p1.addContactoRelacionado(p2);
        p2.addContactoRelacionado(p3);
        p4.addContactoRelacionado(p1);
        p5.addContactoRelacionado(p3);
        p6.addContactoRelacionado(p7);
        p9.addContactoRelacionado(p8);
        e2.addContactoRelacionado(p2);
        e3.addContactoRelacionado(p6);
        
    }
       
    private void eliminarContacto() {
        Contacto contacto = listaContactos.getActual();
        if (contacto != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar a " + contacto.getNombreCompleto() + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(respuesta -> {
                if (respuesta == ButtonType.YES) {
                    listaContactos.remove(contacto);
                    actualizarObservableContactos();
                    if (listaContactos.getTamanio() > 0) {
                        mostrarContacto(listaContactos.getActual());
                        listViewContactos.getSelectionModel().select(listaContactos.getActual());
                    } else {
                        labelNombre.setText("Sin contactos");
                        detallesBox.getChildren().clear();
                        fotoContacto.setImage(null);
                    }
                }
            });
        }
    }
}
