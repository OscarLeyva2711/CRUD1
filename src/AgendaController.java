import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.List;

public class AgendaController {
    @FXML private TextField txtNombre, txtTelefono, txtDireccion;
    @FXML private TableView<ContactoTabla> tableView;
    @FXML private TableColumn<ContactoTabla, Integer> colId;
    @FXML private TableColumn<ContactoTabla, String> colNombre;
    @FXML private TableColumn<ContactoTabla, String> colTelefono;
    @FXML private TableColumn<ContactoTabla, String> colDireccion;

    private AgendaService service = new AgendaService();
    private ObservableList<ContactoTabla> contactosTabla = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> cell.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colTelefono.setCellValueFactory(cell -> cell.getValue().telefonoProperty());
        colDireccion.setCellValueFactory(cell -> cell.getValue().direccionProperty());

        tableView.setItems(contactosTabla);
        cargarTabla();

        // Listener para llenar TextField al seleccionar un contacto
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        txtNombre.setText(newValue.getNombre());
                        txtTelefono.setText(newValue.getTelefono().equals("Sin teléfono") ? "" : newValue.getTelefono());
                        txtDireccion.setText(newValue.getDireccion().equals("Sin dirección") ? "" : newValue.getDireccion());
                    } else {
                        txtNombre.clear();
                        txtTelefono.clear();
                        txtDireccion.clear();
                    }
                }
        );
    }

    private void cargarTabla() {
        contactosTabla.clear();
        List<Persona> personas = service.listarPersonas();
        for (Persona p : personas) {
            // Inicializa listas si son null
            if (p.getTelefonos() == null) p.setTelefonos(FXCollections.observableArrayList());
            if (p.getDirecciones() == null) p.setDirecciones(FXCollections.observableArrayList());

            p.getTelefonos().addAll(service.listarTelefonos(p.getId()));
            p.getDirecciones().addAll(service.listarDirecciones(p.getId()));

            String tel = p.getTelefonosTexto().isEmpty() ? "Sin teléfono" : p.getTelefonosTexto();
            String dir = p.getDireccionesTexto().isEmpty() ? "Sin dirección" : p.getDireccionesTexto();

            contactosTabla.add(new ContactoTabla(p.getId(), p.getNombre(), tel, dir));
        }
    }

    @FXML
    private void agregarContacto() {
        String nombre = txtNombre.getText().trim();
        if(nombre.isEmpty()) return;

        Persona p = service.agregarPersona(nombre);
        if(p == null) return;

        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if(!telefono.isEmpty()) service.agregarTelefono(new Telefono(0, p.getId(), telefono));
        if(!direccion.isEmpty()) service.agregarDireccion(new Direccion(0, p.getId(), direccion));

        limpiarCampos();
        cargarTabla();
    }

    @FXML
    private void editarContacto() {
        ContactoTabla selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String nuevoNombre = txtNombre.getText().trim();
        String nuevoTelefono = txtTelefono.getText().trim();
        String nuevaDireccion = txtDireccion.getText().trim();

        // 1️⃣ Editar nombre
        if (!nuevoNombre.isEmpty() && !nuevoNombre.equals(selected.getNombre())) {
            service.editarPersona(selected.getId(), nuevoNombre);
        }

        // Editar telefono
        List<Telefono> telefonos = service.listarTelefonos(selected.getId());
        if (!nuevoTelefono.isEmpty()) {
            if (!telefonos.isEmpty()) {
                if (!nuevoTelefono.equals(telefonos.get(0).getNumero())) {
                    service.editarTelefono(telefonos.get(0).getId(), nuevoTelefono);
                }
            } else {
                service.agregarTelefono(new Telefono(0, selected.getId(), nuevoTelefono));
            }
        }

        // Editar direccion
        List<Direccion> direcciones = service.listarDirecciones(selected.getId());
        if (!nuevaDireccion.isEmpty()) {
            if (!direcciones.isEmpty()) {
                if (!nuevaDireccion.equals(direcciones.get(0).getDescripcion())) {
                    service.editarDireccion(direcciones.get(0).getId(), nuevaDireccion);
                }
            } else {
                service.agregarDireccion(new Direccion(0, selected.getId(), nuevaDireccion));
            }
        }

        limpiarCampos();
        cargarTabla();
    }

    @FXML
    private void eliminarContacto() {
        ContactoTabla selected = tableView.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        service.eliminarPersona(selected.getId());
        limpiarCampos();
        cargarTabla();
    }

    @FXML
    private void agregarTelefono() {
        ContactoTabla selected = tableView.getSelectionModel().getSelectedItem();
        if(selected == null || txtTelefono.getText().trim().isEmpty()) return;

        service.agregarTelefono(new Telefono(0, selected.getId(), txtTelefono.getText().trim()));
        txtTelefono.clear();
        cargarTabla();
    }

    @FXML
    private void agregarDireccion() {
        ContactoTabla selected = tableView.getSelectionModel().getSelectedItem();
        if(selected == null || txtDireccion.getText().trim().isEmpty()) return;

        service.agregarDireccion(new Direccion(0, selected.getId(), txtDireccion.getText().trim()));
        txtDireccion.clear();
        cargarTabla();
    }

    @FXML
    private void eliminarTelefono() {
        ContactoTabla selected = tableView.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        List<Telefono> telefonos = service.listarTelefonos(selected.getId());
        if(!telefonos.isEmpty()) service.eliminarTelefono(telefonos.get(0).getId());
        cargarTabla();
    }

    @FXML
    private void eliminarDireccion() {
        ContactoTabla selected = tableView.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        List<Direccion> direcciones = service.listarDirecciones(selected.getId());
        if(!direcciones.isEmpty()) service.eliminarDireccion(direcciones.get(0).getId());
        cargarTabla();
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        tableView.getSelectionModel().clearSelection();
    }

    // Clase interna para la tabla
    public static class ContactoTabla {
        private SimpleIntegerProperty id;
        private SimpleStringProperty nombre, telefono, direccion;

        public ContactoTabla(int id, String nombre, String telefono, String direccion) {
            this.id = new SimpleIntegerProperty(id);
            this.nombre = new SimpleStringProperty(nombre);
            this.telefono = new SimpleStringProperty(telefono);
            this.direccion = new SimpleStringProperty(direccion);
        }

        public int getId() { return id.get(); }
        public SimpleIntegerProperty idProperty() { return id; }
        public String getNombre() { return nombre.get(); }
        public SimpleStringProperty nombreProperty() { return nombre; }
        public String getTelefono() { return telefono.get(); }
        public SimpleStringProperty telefonoProperty() { return telefono; }
        public String getDireccion() { return direccion.get(); }
        public SimpleStringProperty direccionProperty() { return direccion; }
    }
}