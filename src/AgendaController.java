import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AgendaController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;
    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnAgregarTelefono;
    @FXML private Button btnAgregarDireccion;
    @FXML private Button btnEliminar;

    // TableView y sus columnas
    @FXML private TableView<ContactoCompleto> tableView;
    @FXML private TableColumn<ContactoCompleto, Integer> colId;
    @FXML private TableColumn<ContactoCompleto, String> colNombre;
    @FXML private TableColumn<ContactoCompleto, String> colTelefono;
    @FXML private TableColumn<ContactoCompleto, String> colDireccion;

    // Lista observable para el TableView
    private ObservableList<ContactoCompleto> listaContactos = FXCollections.observableArrayList();

    // Conexion a la base de datos
    private Connection conexion;
    private Metodos metodos;

    // Datos de conexion (ajusta según tu configuración)
    private static final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private static final String USER = "usuario1";
    private static final String PASSWORD = "superpassword";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar las columnas del TableView
        configurarTabla();

        // Inicializar conexión cuando se carga la interfaz
        inicializarConexion();

        // Configurar evento de selección en la tabla
        configurarSeleccionTabla();

        // Cargar datos iniciales
        cargarContactosEnTabla();
    }

    private void configurarTabla() {
        // Configurar las columnas para que tomen los datos correctos
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());

        colId.setPrefWidth(50);
        colNombre.setPrefWidth(150);
        colTelefono.setPrefWidth(120);
        colDireccion.setPrefWidth(200);

        // Asignar la lista observable al TableView
        tableView.setItems(listaContactos);
    }

    private void configurarSeleccionTabla() {
        // Permitir que al seleccionar una fila, se llenen los campos de texto
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Llenar los campos con los datos de la fila seleccionada
                txtNombre.setText(newSelection.getNombre());
                txtDireccion.setText(newSelection.getDireccion().equals("Sin direccion") ? "" : newSelection.getDireccion());
                txtTelefono.setText(newSelection.getTelefono().equals("Sin telefono") ? "" : newSelection.getTelefono());
            }
        });
    }

    private void inicializarConexion() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            metodos = new Metodos(conexion);
            mostrarMensaje("Conexion a la base de datos establecida exitosamente.");
        } catch (Exception e) {
            mostrarError("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void agregarContacto() {
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            txtNombre.requestFocus();
            return;
        }

        try {
            // Agregar la persona y obtener su ID
            int personaId = metodos.agregarPersona(nombre);
            mostrarMensaje("Contacto agregado exitosamente: " + nombre);

            // Si hay dirección, agregarla
            if (!direccion.isEmpty()) {
                metodos.agregarDireccion(personaId, direccion);
                mostrarMensaje("Direccion agregada al contacto.");
            }

            // Si hay teléfono, agregarlo
            if (!telefono.isEmpty()) {
                metodos.agregarTelefono(personaId, telefono);
                mostrarMensaje("Telefono agregado al contacto.");
            }

            limpiarCampos();
            cargarContactosEnTabla();

        } catch (SQLException e) {
            mostrarError("Error al agregar contacto: " + e.getMessage());
        }
    }

    @FXML
    private void editarContacto() {
        ContactoCompleto contactoSeleccionado = tableView.getSelectionModel().getSelectedItem();
        String nuevoNombre = txtNombre.getText().trim();
        String nuevaDireccion = txtDireccion.getText().trim();
        String nuevoTelefono = txtTelefono.getText().trim();

        if (contactoSeleccionado == null) {
            mostrarError("Seleccione un contacto de la tabla para editar.");
            return;
        }

        if (nuevoNombre.isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            txtNombre.requestFocus();
            return;
        }

        try {
            // Actualizar nombre de la persona
            metodos.editarPersona(contactoSeleccionado.getId(), nuevoNombre);

            // Manejar direccion
            if (!nuevaDireccion.isEmpty()) {
                List<Direccion> direcciones = metodos.listarDireccionesDePersona(contactoSeleccionado.getId());
                if (!direcciones.isEmpty() && !nuevaDireccion.equals(contactoSeleccionado.getDireccion())) {
                    // Editar la primera direccion si es diferente
                    metodos.editarDireccion(direcciones.get(0).getId(), nuevaDireccion);
                } else if (direcciones.isEmpty()) {
                    // Agregar nueva direccion si no tenía
                    metodos.agregarDireccion(contactoSeleccionado.getId(), nuevaDireccion);
                }
            }

            // Manejar telefono
            if (!nuevoTelefono.isEmpty()) {
                List<Telefono> telefonos = metodos.listarTelefonosDePersona(contactoSeleccionado.getId());
                if (!telefonos.isEmpty() && !nuevoTelefono.equals(contactoSeleccionado.getTelefono())) {
                    // Editar el primer telefono si es diferente
                    metodos.editarTelefono(telefonos.get(0).getId(), nuevoTelefono);
                } else if (telefonos.isEmpty()) {
                    // Agregar nuevo telefono si no tenía
                    metodos.agregarTelefono(contactoSeleccionado.getId(), nuevoTelefono);
                }
            }

            mostrarMensaje("Contacto editado exitosamente.");
            limpiarCampos();
            cargarContactosEnTabla();

        } catch (SQLException e) {
            mostrarError("Error al editar contacto: " + e.getMessage());
        }
    }

    @FXML
    private void agregarTelefono() {
        ContactoCompleto contactoSeleccionado = tableView.getSelectionModel().getSelectedItem();
        String telefono = txtTelefono.getText().trim();

        if (contactoSeleccionado == null) {
            mostrarError("Seleccione un contacto de la tabla para agregar telefono.");
            return;
        }

        if (telefono.isEmpty()) {
            mostrarError("Ingrese el número de telefono.");
            txtTelefono.requestFocus();
            return;
        }

        try {
            metodos.agregarTelefono(contactoSeleccionado.getId(), telefono);
            mostrarMensaje("Telefono agregado exitosamente.");

            txtTelefono.clear(); // Solo limpiar el campo de telefono
            cargarContactosEnTabla();

        } catch (SQLException e) {
            mostrarError("Error al agregar telefono: " + e.getMessage());
        }
    }

    @FXML
    private void agregarDireccion() {
        ContactoCompleto contactoSeleccionado = tableView.getSelectionModel().getSelectedItem();
        String direccion = txtDireccion.getText().trim();

        if (contactoSeleccionado == null) {
            mostrarError("Seleccione un contacto de la tabla para agregar direccion.");
            return;
        }

        if (direccion.isEmpty()) {
            mostrarError("Ingrese la direccion.");
            txtDireccion.requestFocus();
            return;
        }

        try {
            metodos.agregarDireccion(contactoSeleccionado.getId(), direccion);
            mostrarMensaje("Direccion agregada exitosamente.");

            txtDireccion.clear(); // Solo limpiar el campo de direccion
            cargarContactosEnTabla();

        } catch (SQLException e) {
            mostrarError("Error al agregar direccion: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarContacto() {
        ContactoCompleto contactoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (contactoSeleccionado == null) {
            mostrarError("Seleccione un contacto de la tabla para eliminar.");
            return;
        }

        // Confirmar eliminacion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminacion");
        alert.setHeaderText("¿Está seguro de eliminar este contacto?");
        alert.setContentText("Se eliminara: " + contactoSeleccionado.getNombre() +
                "\n\nEsta acción eliminará toda la informacion asociada (direcciones y telefonos).");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    metodos.eliminarPersona(contactoSeleccionado.getId());
                    mostrarMensaje("Contacto eliminado.");

                    limpiarCampos();
                    cargarContactosEnTabla();

                } catch (SQLException e) {
                    mostrarError("Error al eliminar contacto: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void eliminarTelefono() {
        ContactoCompleto contactoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (contactoSeleccionado == null) {
            mostrarError("Seleccione un contacto de la tabla para eliminar telefono.");
            return;
        }

        if (contactoSeleccionado.getTelefono().equals("Sin telefono")) {
            mostrarError("Este contacto no tiene telefonos para eliminar.");
            return;
        }

        try {
            List<Telefono> telefonos = metodos.listarTelefonosDePersona(contactoSeleccionado.getId());
            if (!telefonos.isEmpty()) {
                // Confirmar eliminacion
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmar eliminacion");
                alert.setHeaderText("¿Eliminar telefono?");
                alert.setContentText("Se eliminara: " + telefonos.get(0).getTelefono());

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            metodos.eliminarTelefono(telefonos.get(0).getId());
                            mostrarMensaje("Teléfono eliminado.");
                            cargarContactosEnTabla();
                        } catch (SQLException e) {
                            mostrarError("Error al eliminar telefono: " + e.getMessage());
                        }
                    }
                });
            }
        } catch (SQLException e) {
            mostrarError("Error al eliminar telefono: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarDireccion() {
        ContactoCompleto contactoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (contactoSeleccionado == null) {
            mostrarError("Seleccione un contacto de la tabla para eliminar direccion.");
            return;
        }

        if (contactoSeleccionado.getDireccion().equals("Sin dirección")) {
            mostrarError("Este contacto no tiene direcciones para eliminar.");
            return;
        }

        try {
            List<Direccion> direcciones = metodos.listarDireccionesDePersona(contactoSeleccionado.getId());
            if (!direcciones.isEmpty()) {
                // Confirmar eliminacion
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmar eliminacion");
                alert.setHeaderText("¿Eliminar direccion?");
                alert.setContentText("Se eliminara: " + direcciones.get(0).getDireccion());

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {
                            metodos.eliminarDireccion(direcciones.get(0).getId());
                            mostrarMensaje("Dirección eliminada.");
                            cargarContactosEnTabla();
                        } catch (SQLException e) {
                            mostrarError("Error al eliminar dirección: " + e.getMessage());
                        }
                    }
                });
            }
        } catch (SQLException e) {
            mostrarError("Error al eliminar dirección: " + e.getMessage());
        }
    }

    private void cargarContactosEnTabla() {
        try {
            listaContactos.clear();
            List<Persona> personas = metodos.listarPersonas();

            for (Persona persona : personas) {
                // Obtener direcciones y telefonos de la persona
                List<Direccion> direcciones = metodos.listarDireccionesDePersona(persona.getId());
                List<Telefono> telefonos = metodos.listarTelefonosDePersona(persona.getId());

                // Crear combinaciones de direccion-telefono
                if (direcciones.isEmpty() && telefonos.isEmpty()) {
                    // Sin direcciones ni teléfonos
                    listaContactos.add(new ContactoCompleto(
                            persona.getId(),
                            persona.getNombre(),
                            "Sin teléfono",
                            "Sin dirección"
                    ));
                } else if (direcciones.isEmpty()) {
                    // Sin direcciones, pero con teléfonos
                    for (Telefono telefono : telefonos) {
                        listaContactos.add(new ContactoCompleto(
                                persona.getId(),
                                persona.getNombre(),
                                telefono.getTelefono(),
                                "Sin dirección"
                        ));
                    }
                } else if (telefonos.isEmpty()) {
                    // Sin teléfonos, pero con direcciones
                    for (Direccion direccion : direcciones) {
                        listaContactos.add(new ContactoCompleto(
                                persona.getId(),
                                persona.getNombre(),
                                "Sin teléfono",
                                direccion.getDireccion()
                        ));
                    }
                } else {
                    // Con direcciones y teléfonos - crear todas las combinaciones
                    for (Direccion direccion : direcciones) {
                        for (Telefono telefono : telefonos) {
                            listaContactos.add(new ContactoCompleto(
                                    persona.getId(),
                                    persona.getNombre(),
                                    telefono.getTelefono(),
                                    direccion.getDireccion()
                            ));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            mostrarError("Error al cargar contactos en la tabla: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtDireccion.clear();
        txtTelefono.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String mensaje) {
        System.out.println("INFO: " + mensaje);
    }

    private void mostrarError(String error) {
        System.err.println("ERROR: " + error);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ha ocurrido un error");
        alert.setContentText(error);
        alert.showAndWait();
    }

    // Metodo para cerrar la conexion
    public void cerrarConexion() {
        if (metodos != null) {
            metodos.cerrarConexion();
        }
    }

    // Clase interna para mostrar un contacto completo en el TableView
    public static class ContactoCompleto {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty telefono;
        private final SimpleStringProperty direccion;

        public ContactoCompleto(int id, String nombre, String telefono, String direccion) {
            this.id = new SimpleIntegerProperty(id);
            this.nombre = new SimpleStringProperty(nombre);
            this.telefono = new SimpleStringProperty(telefono);
            this.direccion = new SimpleStringProperty(direccion);
        }

        // Getters para los valores
        public int getId() { return id.get(); }
        public String getNombre() { return nombre.get(); }
        public String getTelefono() { return telefono.get(); }
        public String getDireccion() { return direccion.get(); }

        // Properties para JavaFX
        public SimpleIntegerProperty idProperty() { return id; }
        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleStringProperty telefonoProperty() { return telefono; }
        public SimpleStringProperty direccionProperty() { return direccion; }
    }
}