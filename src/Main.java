import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private AgendaController controller;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("agenda.fxml"));
            Parent root = loader.load();

            // Obtener referencia del controlador
            controller = loader.getController();

            // Configurar la escena
            Scene scene = new Scene(root, 650, 750);
            primaryStage.setTitle("Agenda de Contactos - JavaFX");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // Configurar evento de cierre para cerrar la conexion de BD
            primaryStage.setOnCloseRequest(e -> {
                if (controller != null) {
                    controller.cerrarConexion();
                }
            });

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error al cargar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}