
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class UserChatArea{
	private static String serverIP = "192.168.43.121" ;
	private static int userPort = 9099;
	static boolean quit = false;
	private static Stage chatStage;
	static UserSend userSend;
	static TextArea msgArea;
	static VoiceSender voiceSender;
	static Button stop = new Button("Stop");
	
	public static void start(Stage stage,String name, String ques) {
		chatStage = stage;
		
		SimpleDateFormat fDate = new SimpleDateFormat ("hh:mm:ss aa ");

		Button btnSend = new Button("Send");
		Button btnQuit = new Button();
		
		Button capture = new Button("Capture");
	    
	    capture.setDisable(false);
	    stop.setDisable(true);
	    
	    Label lbVoice = new Label("Send voice");
	    
		msgArea = new TextArea();
		msgArea.setEditable(false);
		msgArea.setWrapText(true);
		
		TextField txtInput = new TextField();
		GridPane pane = new GridPane();
		HBox voicePane = new HBox(10);
		voicePane.getChildren().addAll(lbVoice,capture,stop);
		voicePane.setAlignment(Pos.BASELINE_RIGHT);
		pane.setPadding(new Insets(20));
		pane.setAlignment(Pos.CENTER_RIGHT);
		pane.add(msgArea, 0, 0);
		pane.add(txtInput, 0, 1);
		pane.add(btnSend, 1, 1);
		pane.add(voicePane, 0, 2);
		pane.add(btnQuit, 1, 3);
		pane.setStyle("-fx-background-color: white");
		
		pane.setPrefSize(500,500);
		pane.setVgap(10);
		msgArea.setPrefHeight(400);
		pane.setColumnSpan(msgArea, pane.REMAINING);
		pane.setHgrow(txtInput, Priority.ALWAYS);
		pane.setColumnSpan(voicePane, pane.REMAINING);
		pane.setHalignment(voicePane, HPos.RIGHT);
		
		
		chatStage.setScene(new Scene(pane));
		chatStage.setTitle("Live Chat");
		chatStage.show();
		chatStage.centerOnScreen();
		txtInput.requestFocus();
		
		voiceSender = null;
		
		btnSend.setStyle("-fx-background-color: grey;-fx-text-fill: white;");
		btnQuit.setStyle("-fx-background-color: red;");
		btnQuit.setText("Quit(X)");
		
		userClient(name, ques);
		//enable buttons only when agents responded
		txtInput.setDisable(true);
		btnSend.setDisable(true);
		while (msgArea.getText().equals("")){
			txtInput.setDisable(false);
			btnSend.setDisable(false);
		}

		btnSend.setOnAction(e->{
			//send text to agent and display sent text
			if (quit == false){
				userSend.send(txtInput.getText());
				msgArea.appendText(fDate.format(new Date()) + " You >" + txtInput.getText()+ "\n");
				txtInput.setText("");
			}
		});
		
		btnQuit.setOnAction(e->{
			
			//quit button
			if(!stop.isDisabled()){
				stop.fire();
			}
			
			//send termination signal
			
			if (quit == false){
				userSend.sendTerminate();
			}
			
			System.exit(0);
		});
		
		txtInput.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btnSend.fire();
	    	}
	    });
		
		btnSend.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btnSend.fire();
	    	}
	    });
		
		chatStage.setOnCloseRequest(e->{
			btnQuit.fire();
		});
		
		
		capture.setOnAction(e->{
			if (quit == false){
				capture.setDisable(true);
		    	stop.setDisable(false);
		    	voiceSender.captureAudio();
				userSend.send("Sending voice");
				msgArea.appendText(fDate.format(new Date()) + " You >Sending voice\n");

			}
	    });
	    
	    stop.setOnAction(e->{
	    	if (quit == false){
	    		capture.setDisable(false);
		    	stop.setDisable(true);
		    	voiceSender.stopaudioCapture = true;
		    	voiceSender.targetDataLine.close();
				userSend.send("Stop voice sending");
				msgArea.appendText(fDate.format(new Date()) + " You >Stop voice sending\n");

			}
	    	
	    });
		
		
	}
	
	private static void userClient(String userName, String question){
		//start connection to server
		Socket s;
		try {
			s = new Socket(serverIP,userPort);
			userSend = new UserSend(s, userName, question);	
			
			//start listening for messages 
			UserReceiveThread receive = new UserReceiveThread(s);
			receive.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new VoiceReceiver(3);
	}
}
