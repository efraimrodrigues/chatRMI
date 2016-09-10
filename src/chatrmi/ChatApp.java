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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Efraim Rodrigues
 */
public class ChatApp extends Application {

    private Cliente clnt;
    private TextArea msgTextArea;
    private Button buttonEnviar;
    private ScrollPane scrollMsg;
    //Label mainMsg;
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
        
        clnt.adicionarUsuarioOnline(nome);
        
        clnt.enviaMensagem("Cheguei.");

        buttonEnviar = new Button("Enviar");
        buttonEnviar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                enviar();
                msgTextArea.clear();
            }
        });
        buttonEnviar.setMinSize(100, 20);

        msgTextArea = new TextArea();

        //msgTextArea.setMinHeight(3);
        msgTextArea.setPrefHeight(2);
        msgTextArea.setMaxHeight(4);
        msgTextArea.setWrapText(true);

        msgTextArea.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            if (key.getCode() == KeyCode.ENTER && !key.isShiftDown()) {
                enviar();
                msgTextArea.clear();
            } else if (key.getCode() == KeyCode.ENTER && key.isShiftDown()) {
                msgTextArea.setText(msgTextArea.getText() + "\n");
                msgTextArea.positionCaret(msgTextArea.getText().length());
            }
        }
        );

        scrollMsg = new ScrollPane();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (clnt) {
                    clnt.run();
                }
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
                        
                        newMessage.setStyle("-fx-background-color: #336699; -fx-border-color: white; -fx-alignment: top-right; -fx-column-halignment: right;");

                        newMessage.setAlignment(Pos.TOP_RIGHT);

                        messageBox.setAlignment(Pos.TOP_RIGHT);

                        newMessage.setText(lastClntMessage.trim().substring(lastClntMessage.indexOf(":") + 2));
                    } else {
                        newMessage.setText(lastClntMessage.trim());
                    }

                    messageBox.getChildren().addAll(newMessage, timeStamp);

                    root.getChildren().add(messageBox);

                    //System.out.println(lastClntMessage + " " + i);

                    scrollMsg.setVvalue(1.0);
                    scrollMsg.setHvalue(1.0);

                    i++;
                }
            }

        }.start();
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
        if (msgTextArea.getText().trim().length() > 0) {
            clnt.enviaMensagem(msgTextArea.getText().trim());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        primaryStage.getIcons().add(new Image("file:send.png"));
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle (WindowEvent t) {
                clnt.enviaMensagem("Fui.");
                
                clnt.removeUsuarioOnline(clnt.getNome());
                
                Platform.exit();
                System.exit(0);
            }
        });

        //mainTextArea.setEditable(false);
        BorderPane border = new BorderPane();
        Scene scene = new Scene(border, 800, 600);

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

        VBox.setVgrow(root, Priority.ALWAYS);

        //root.fillWidthProperty();
        //root.autosize();
        scrollMsg.setContent(root);

        //BorderPane msgPane = new BorderPane();
        //root.getChildren().add(msgPane);
        VBox test = new VBox();

        test.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: #336699;");

        test.getChildren().add(scrollMsg);

        scrollMsg.prefWidthProperty().bind(test.widthProperty());
        scrollMsg.prefHeightProperty().bind(test.heightProperty());

        scrollMsg.setFitToHeight(true);
        scrollMsg.setFitToWidth(true);

        //root.prefHeightProperty().bind(scrollMsg.heightProperty());
        //root.prefWidthProperty().bind(scrollMsg.widthProperty());
        border.setBottom(grid);
        border.setCenter(scrollMsg);

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
