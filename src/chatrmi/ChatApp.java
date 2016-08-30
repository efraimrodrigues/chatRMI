/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatrmi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private VBox root;
    //private GridPane messagesOther;
    //private GridPane messagesMy;
    private Integer i = 0;

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
                enviar();
            }
        });

        //buttonEnviar.setPrefSize(100, 20);
        buttonEnviar.setMinSize(100, 20);

        msgTextArea = new TextArea();

        //msgTextArea.setMinHeight(3);
        msgTextArea.setPrefHeight(2);
        msgTextArea.setMaxHeight(4);
        msgTextArea.setWrapText(true);
        
        msgTextArea.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.ENTER && !key.isShiftDown()) {
                enviar();
                msgTextArea.clear();
                msgTextArea.positionCaret(0);
            } else if (key.getCode() == KeyCode.ENTER && key.isShiftDown()) {
                msgTextArea.setText(msgTextArea.getText() + "\n");
                msgTextArea.positionCaret(msgTextArea.getText().length());
            }
        }
        );

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

                if (!lastClntMessage.equals("")) {
                    Label newMessage = new Label();

                    newMessage.setWrapText(true);

                    newMessage.setStyle("-fx-border-color: white;");

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");

                    Date date = new Date();

                    Label timeStamp = new Label(dateFormat.format(date));

                    timeStamp.setStyle("-fx-font-size: 10px;");

                    VBox messageBox = new VBox();

                    messageBox.setAlignment(Pos.TOP_LEFT);

                    String outro = lastClntMessage.substring(0, lastClntMessage.indexOf(":"));

                    if (outro.equals(clnt.getNome())) {
                        newMessage.setPadding(new Insets(0, 1, 0, 0));
                        //newMessage.setStyle("align: right;");
                        newMessage.setStyle("-fx-background-color: #336699; -fx-border-color: white; -fx-alignment: top-right; -fx-column-halignment: right;");
                        //newMessage.setAlignment(Pos.CENTER_RIGHT);
                        //newMessage.alignmentProperty().set(Pos.BASELINE_RIGHT);
                        System.out.println("Eu msm.");

                        newMessage.setAlignment(Pos.TOP_RIGHT);

                        messageBox.setAlignment(Pos.TOP_RIGHT);

                        newMessage.setText(lastClntMessage.trim().substring(lastClntMessage.indexOf(":") + 2));
                    } else {
                        newMessage.setText(lastClntMessage.trim());
                    }

                    messageBox.getChildren().addAll(newMessage, timeStamp);

                    root.getChildren().add(messageBox);

                    System.out.print(lastClntMessage + " " + i);

                    i++;
                }
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

    public void enviar() {
        if (msgTextArea.getText().length() > 0) {
            clnt.enviaMensagem(msgTextArea.getText());
            System.out.println("Mensagens enviada.");
            msgTextArea.clear();
            msgTextArea.positionCaret(0);
        }
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

        root = new VBox();

        //root.getChildren().addAll(mainMsg);
        root.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: #336699;");

        //BorderPane msgPane = new BorderPane();
        //root.getChildren().add(msgPane);
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
