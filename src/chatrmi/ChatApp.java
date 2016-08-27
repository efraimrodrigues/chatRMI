/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatrmi;

import java.util.Optional;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Efraim Rodrigues
 */
public class ChatApp extends Application {

    private Cliente clnt;
    private TextArea msgTextArea;
    private Button buttonEnviar;
    Label mainMsg;
    private String lastClntMessage;

    public ChatApp() {
        String nome = "";
        lastClntMessage = "";

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Itendificação");
        dialog.setHeaderText("Identifique-se");
        dialog.setContentText("Por favor, digite seu nome:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            nome = result.get();
        }

        clnt = new Cliente(nome);

        mainMsg = new Label();
        mainMsg.setWrapText(true);
        mainMsg.setText("");
        mainMsg.setStyle("-fx-border-color: white;");

        buttonEnviar = new Button("Enviar");
        buttonEnviar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                clnt.enviaMensagem(msgTextArea.getText());
                System.out.println("Mensagens enviada.");
                msgTextArea.clear();
            }
        });

        //buttonEnviar.setPrefSize(100, 20);
        buttonEnviar.setMinSize(100, 20);

        msgTextArea = new TextArea();

        msgTextArea.setPrefHeight(2);
        msgTextArea.setWrapText(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                clnt.run();
            }
        }).start();

        

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                lastClntMessage = clnt.getNewMessage();
                System.out.print(lastClntMessage);

                mainMsg.setText(mainMsg.getText() + lastClntMessage);
            }

        }.start();

        /*Task task;
        task = new Task<Void>() {
            @Override
            public Void call() {
                //SIMULATE A FILE DOWNLOAD
                while (true) {
                    mainMsg.setText(mainMsg.getText() + lastClntMessage);
                }
            }
        };
        //task.setOnSucceeded(taskFinishEvent -> mainMsg.setText(mainMsg.getText() + clnt.getNewMessage()));
        new Thread(task).start();*/
    }

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");
        hbox.getChildren().addAll(msgTextArea);

        return hbox;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //mainTextArea.setEditable(false);
        BorderPane border = new BorderPane();
        HBox hBox = addHBox();

        StackPane stack = new StackPane();

        stack.getChildren().addAll(buttonEnviar);
        stack.setAlignment(Pos.CENTER_RIGHT);
        //stack.setStyle("align: right; margin-right: 0");
        StackPane.setMargin(buttonEnviar, new Insets(0, 25, 0, 25));
        stack.setStyle("-fx-background-color: #336699;");

        GridPane grid = new GridPane();

        hBox.prefWidthProperty().bind(primaryStage.widthProperty());
        msgTextArea.prefWidthProperty().bind(hBox.prefWidthProperty());

        grid.add(hBox, 0, 0);
        grid.add(stack, 1, 0);

        VBox root = new VBox(10);
        root.getChildren().addAll(mainMsg);
        root.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: #336699;");

        border.setBottom(grid);
        border.setCenter(root);

        Scene scene = new Scene(border, 800, 600);

        primaryStage.setTitle("Safe Chat 0.1");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        launch(args);
    }

}
