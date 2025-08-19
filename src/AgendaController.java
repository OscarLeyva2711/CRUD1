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
    @FXML private Button btnEliminar;

    // TableView y sus columnas
    @FXML private TableView<ContactoCompleto> tableView;
    @FXML private TableColumn<ContactoCompleto, Integer> colId;
    @FXML private TableColumn<ContactoCompleto, String> colNombre;
    @FXML private TableColumn<ContactoCompleto, String> colTelefono;
    @FXML private TableColumn<ContactoCompleto, String> colDireccion;

    // Lista observable para el TableView
    private ObservableList<ContactoCompleto> listaContactos = FXCollections.observableArrayList();

    // Conexión a la base de datos
    private Connection conexion;
    private Metodos metodos;

    // Datos de conexión
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

        // Ajustar el ancho de las columnas
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
                txtDireccion.setText(newSelection.getDireccion());
                txtTelefono.setText(newSelection.getTelefono().equals("Sin teléfono") ? "" : newSelection.getTelefono());
            }
        });
    }

    private void inicializarConexion() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            metodos = new Metodos(conexion);
            mostrarMensaje("Conexión a la base de datos establecida exitosamente.");
        } catch (Exception e) {
            mostrarError("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void agregarContacto() {
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty()) {
            mostrarError("El nombre y la dirección son obligatorios.");
            return;
        }

        try {
            // Agregar la persona
            metodos.agregarPersona(nombre, direccion);
            mostrarMensaje("Contacto agregado exitosamente: " + nombre);

            // Si hay teléfono, agregarlo también
            if (!telefono.isEmpty()) {
                // Obtener el ID de la persona recién creada
                List<Persona> personas = metodos.listarPersonas();
                int personaId = personas.get(personas.size() - 1).getId(); // El último agregado
                metodos.agregarTelefono(personaId, telefono);
                mostrarMensaje("Teléfono agregado al contacto.");
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

        if (nuevoNombre.isEmpty() || nuevaDireccion.isEmpty()) {
            mostrarError("El nombre y la dirección son obligatorios.");
            return;
        }

        try {
            // Actualizar datos de la persona (nombre y dirección)
            metodos.editarPersona(contactoSeleccionado.getId(), nuevoNombre, nuevaDireccion);

            // Si hay teléfono y es diferente al actual, actualizarlo también
            if (!nuevoTelefono.isEmpty() && !nuevoTelefono.equals(contactoSeleccionado.getTelefono())) {
                // Buscar el ID del teléfono para actualizarlo
                List<Telefono> telefonos = metodos.listarTelefonosDePersona(contactoSeleccionado.getId());
                if (!telefonos.isEmpty()) {
                    // Si ya tiene teléfono, editar el primero encontrado
                    metodos.editarTelefono(telefonos.get(0).getId(), nuevoTelefono);
                } else {
                    // Si no tenía teléfono, agregar uno nuevo
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
            mostrarError("Seleccione un contacto de la tabla para agregar teléfono.");
            return;
        }

        if (telefono.isEmpty()) {
            mostrarError("Ingrese el número de teléfono.");
            return;
        }

        try {
            metodos.agregarTelefono(contactoSeleccionado.getId(), telefono);
            mostrarMensaje("Teléfono agregado exitosamente.");

            limpiarCampos();
            cargarContactosEnTabla();

        } catch (SQLException e) {
            mostrarError("Error al agregar teléfono: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarContacto() {
        ContactoCompleto contactoSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (contactoSeleccionado == null) {
            mostrarError("Seleccione un contacto de la tabla para eliminar.");
            return;
        }

        // Confirmar eliminación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminacion");
        alert.setHeaderText("Esta seguro de eliminar este contacto?");
        alert.setContentText("Se eliminara: " + contactoSeleccionado.getNombre());

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

    private void cargarContactosEnTabla() {
        try {
            listaContactos.clear();
            List<Persona> personas = metodos.listarPersonas();

            for (Persona persona : personas) {
                // Obtener teléfonos de la persona
                List<Telefono> telefonos = metodos.listarTelefonosDePersona(persona.getId());

                if (telefonos.isEmpty()) {
                    // Si no tiene teléfonos, mostrar "Sin teléfono"
                    listaContactos.add(new ContactoCompleto(
                            persona.getId(),
                            persona.getNombre(),
                            "Sin teléfono",
                            persona.getDireccion()
                    ));
                } else {
                    // Si tiene teléfonos, crear una fila por cada teléfono
                    for (Telefono telefono : telefonos) {
                        listaContactos.add(new ContactoCompleto(
                                persona.getId(),
                                persona.getNombre(),
                                telefono.getTelefono(),
                                persona.getDireccion()
                        ));
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

    // Método para cerrar la conexión (llamado desde el main)
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    // Clase interna para representar un contacto completo en el TableView
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