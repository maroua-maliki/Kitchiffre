package org.demo.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 890, 600);
        Image logo = new Image(getClass().getResourceAsStream("images/cap.png"));
        stage.getIcons().add(logo);
        stage.setTitle("KitChiffre");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String password = "123"; // choisis un mot de passe simple pour test
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Hash généré : " + hash);
        launch();
    }
}
