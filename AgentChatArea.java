
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Date;

public class AgentChatArea{
	private static String serverIP = "192.168.43.121" ;
	private static int agentPort = 9088;
	private static AgentReceiveThread receive;
	private static Stage primaryStage;
	static Text sceneTitle;
	static TextArea txtArea1;
	static TextArea txtArea2;
	static boolean quit = false;
	static String[] connectedUser = new String[2];
	static AgentSend agentSend;
	static String agentName;
	static VoiceSender voiceSender1;
	static VoiceSender voiceSender2;
	static Button stop1 = new Button("Stop");
	static Button stop2 = new Button("Stop");
	static Button stop3 = new Button("Stop");
	
	
	public static void start(Stage pStage, String agent){
		//determine connected user by saving their socket in array according to chat window
		connectedUser[0] = "";
		connectedUser[1] = "";
		primaryStage = pStage;
		agentName = agent; 
		
		SimpleDateFormat fDate = new SimpleDateFormat ("hh:mm:ss aa ");
		
		GridPane gpane;
		Button btnSend1;
		Button btnSend2;
		Button btnSend3;
		Button btnSaveChat;
		Button btnQuit;
		TextField txtField1;
		TextField txtField2;
		TextField txtFieldBoth;
		
		
		gpane = new GridPane();
		gpane.setStyle("-fx-background-color: beige;");
		gpane.setPadding(new Insets(20));
		gpane.setVgap(10);
		gpane.getColumnConstraints().addAll(new ColumnConstraints(300),
				new ColumnConstraints(50),
				new ColumnConstraints(60),
				new ColumnConstraints(300),
				new ColumnConstraints(50));
		
		
		sceneTitle = new Text("Connecting to server...");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gpane.setHalignment(sceneTitle, HPos.CENTER);
        gpane.setColumnSpan(sceneTitle, gpane.REMAINING);
        gpane.add(sceneTitle, 0, 0);
		
		Text client1 = new Text("Client 1");
        client1.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        gpane.add(client1, 0 , 1);
        
        Text client2 = new Text("Client 2");
        client2.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        gpane.add(client2, 3, 1);
        
		txtArea1 = new TextArea();
		txtArea1.setPrefHeight(300);
		txtArea1.setStyle("-fx-border-color: black;");
		gpane.setColumnSpan(txtArea1, 2);
		txtArea1.setEditable(false);
		txtArea1.setWrapText(true);
		gpane.add(txtArea1, 0, 2);
		
		txtArea2 = new TextArea();
		txtArea2.setPrefHeight(300);
		txtArea2.setStyle("-fx-border-color: black;");
		txtArea2.setEditable(false);
		txtArea2.setWrapText(true);
		gpane.setColumnSpan(txtArea2, 2);
		gpane.add(txtArea2, 3, 2);

		btnSend1 = new Button();
		btnSend1.setStyle("-fx-background-color: grey;-fx-text-fill: white;");
		btnSend1.setText("Send");
		gpane.add(btnSend1, 1, 3);
		
		Button capture1 = new Button("Capture");
	    
	    capture1.setDisable(false);
	    stop1.setDisable(true);
	    
	    Label lbVoice1 = new Label("Send voice");
	    HBox voicePane1 = new HBox(10);
		voicePane1.getChildren().addAll(lbVoice1,capture1,stop1);
		voicePane1.setAlignment(Pos.BASELINE_RIGHT);
	    gpane.add(voicePane1, 0, 4);
	    gpane.setColumnSpan(voicePane1, 2);
	    gpane.setHalignment(voicePane1, HPos.RIGHT);
		
		btnSend2 = new Button();
		btnSend2.setStyle("-fx-background-color: grey;-fx-text-fill: white;");
		btnSend2.setText("Send");
		gpane.add(btnSend2, 4, 3);
		
		Button capture2 = new Button("Capture");
	    capture2.setDisable(false);
	    stop2.setDisable(true);
	    
	    Label lbVoice2 = new Label("Send voice");
	    HBox voicePane2 = new HBox(10);
		voicePane2.getChildren().addAll(lbVoice2,capture2,stop2);
		voicePane2.setAlignment(Pos.BASELINE_RIGHT);
	    gpane.add(voicePane2, 3, 4);
	    gpane.setColumnSpan(voicePane2, 2);
	    gpane.setHalignment(voicePane2, HPos.RIGHT);
		
		txtField1 = new TextField();
		txtField1.setStyle("-fx-border-color: black;");
		gpane.add(txtField1, 0, 3);
		
		txtField2 = new TextField();
		txtField2.setStyle("-fx-border-color: black;");
		gpane.add(txtField2, 3, 3);
		
		txtFieldBoth = new TextField();
		txtFieldBoth.setStyle("-fx-border-color: black;");
		txtFieldBoth.setPrefWidth(400);
		
		btnSend3 = new Button();
		btnSend3.setStyle("-fx-background-color: blue;-fx-text-fill: white;");
		btnSend3.setText("Send to both");
		
		btnSaveChat = new Button("Save Chats");
		btnSaveChat.setStyle("-fx-background-color: #2ff09f;-fx-text-fill: black;");
		
		HBox hPane = new HBox(5);
		hPane.getChildren().addAll(txtFieldBoth, btnSend3, btnSaveChat);
		hPane.setAlignment(Pos.CENTER);
		gpane.add(hPane, 0, 5);
		gpane.setColumnSpan(hPane, gpane.REMAINING);
		gpane.setHalignment(hPane, HPos.CENTER);
		
		btnQuit = new Button();
		btnQuit.setPrefWidth(70);
		btnQuit.setStyle("-fx-background-color: red;");
		btnQuit.setText("Quit(X)");
		gpane.setColumnSpan(btnQuit, gpane.REMAINING);
		gpane.setHalignment(btnQuit, HPos.RIGHT);
		gpane.add(btnQuit, 0, 7);
		
		Button capture3 = new Button("Capture");
	    capture3.setDisable(false);
	    stop3.setDisable(true);
	    
	    Label lbVoice3 = new Label("Send voice to both");
	    HBox voicePane3 = new HBox(10);
		voicePane3.getChildren().addAll(lbVoice3,capture3,stop3);
		voicePane3.setAlignment(Pos.CENTER);
	    gpane.add(voicePane3, 0, 6);
	    gpane.setHalignment(voicePane3, HPos.CENTER);
	    gpane.setColumnSpan(voicePane3, 4);
		
	    voiceSender1 = null;
	    voiceSender2 = null;
	    
		primaryStage.setScene(new Scene(gpane));
		primaryStage.setTitle("Agent Chat");
		primaryStage.show();
		primaryStage.centerOnScreen();
		agentClient();
		
		txtField1.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btnSend1.fire();
	    	}
	    });
		
		txtField2.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btnSend2.fire();
	    	}
	    });
		
		txtFieldBoth.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btnSend3.fire();
	    	}
	    });
		
		btnSend1.setOnAction(e->{
			//if there is user connected to chat window 1
			//send message to user
			if (!connectedUser[0].equals("")){
				agentSend.send(connectedUser[0], txtField1.getText());
				txtArea1.appendText(fDate.format(new Date()) + " You >" + txtField1.getText() + "\n");
				txtField1.setText("");
			}
		});
		
		btnSend2.setOnAction(e->{
			//if there is user connected to chat window 2
			//send message to user
			if (!connectedUser[1].equals("")){
				agentSend.send(connectedUser[1], txtField2.getText());
				txtArea2.appendText(fDate.format(new Date()) + " You >" + txtField2.getText()+ "\n");
				txtField2.setText("");
			}
		});
		
		btnSend3.setOnAction(e->{
			//combination of btnSend1 and btnSend2
			if (!connectedUser[0].equals("")){
				agentSend.send(connectedUser[0], txtFieldBoth.getText());
				txtArea1.appendText(fDate.format(new Date()) + " You >" + txtFieldBoth.getText()+ "\n");
			}
			if (!connectedUser[1].equals("")){
				agentSend.send(connectedUser[1], txtFieldBoth.getText());
				txtArea2.appendText(fDate.format(new Date()) + " You >" + txtFieldBoth.getText()+ "\n");
			}
			txtFieldBoth.setText("");
		});
		
		btnSaveChat.setOnAction(e->{
			//save chat only for chat windows that have user connected
			for (int i =0; i<2 ; i++){
				if (!connectedUser[i].equals("")){
					if (i ==0){
						saveChat(txtArea1.getText());
					}else if (i==1){
						saveChat(txtArea2.getText());
					}
				}
			}
			
		});
		
		btnQuit.setOnAction(e->{
			//informs connected users when agent quits
			quit = true;
			boolean empty = true;
			if(!stop1.isDisabled()){
				stop1.fire();
			}
			if(!stop2.isDisabled()){
				stop2.fire();
			}
			if(!stop3.isDisabled()){
				stop3.fire();
			}
			
			for (String user:connectedUser){
				if (!user.equals("")){
					empty = false;
					agentSend.sendTerminate(user);
				}
			}
			
			//if no user, inform server only
			if (empty == true){
				agentSend.sendTerminate("server");
			}
			
			//end agent
			try {
				agentSend.close();
				receive.join();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			System.exit(0);
		});
		
		primaryStage.setOnCloseRequest(e->{
			btnQuit.fire();
		});
		
		capture1.setOnAction(e->{
			if (quit == false){
				
		    	if (!connectedUser[0].equals("")){
		    		capture1.setDisable(true);
			    	stop1.setDisable(false);
		    		voiceSender1.captureAudio();
					agentSend.send(connectedUser[0],"Sending voice");
					txtArea1.appendText(fDate.format(new Date()) + " You >Sending voice\n");
		    	}
			}
	    });
	    
	    stop1.setOnAction(e->{
	    	if (quit == false){
	    		
		    	if (!connectedUser[0].equals("")){
		    		capture1.setDisable(false);
			    	stop1.setDisable(true);
		    		voiceSender1.stopaudioCapture = true;
			    	voiceSender1.targetDataLine.close();
					agentSend.send(connectedUser[0],"Stop voice sending");
					txtArea1.appendText(fDate.format(new Date()) + " You >Stop voice sending\n");
		    	}

			}
	    	
	    });
	    
	    capture2.setOnAction(e->{
			if (quit == false){
				
				if (!connectedUser[1].equals("")){
					capture2.setDisable(true);
			    	stop2.setDisable(false);
			    	voiceSender2.captureAudio();
					agentSend.send(connectedUser[1],"Sending voice");
					txtArea2.appendText(fDate.format(new Date()) + " You >Sending voice\n");
				}

			}
	    });
	    
	    stop2.setOnAction(e->{
	    	if (quit == false){
	    		
	    		if (!connectedUser[1].equals("")){
	    			capture2.setDisable(false);
			    	stop2.setDisable(true);
			    	voiceSender2.stopaudioCapture = true;
			    	voiceSender2.targetDataLine.close();
					agentSend.send(connectedUser[1],"Stop voice sending");
					txtArea2.appendText(fDate.format(new Date()) + " You >Stop voice sending\n");
	    		}

			}
	    	
	    });
	    capture3.setOnAction(e->{
			if (quit == false){
				
				if (!connectedUser[0].equals("")){
					capture3.setDisable(true);
			    	stop3.setDisable(false);
		    		capture1.setDisable(true);
			    	stop1.setDisable(true);
		    		voiceSender1.captureAudio();
					agentSend.send(connectedUser[0],"Sending voice");
					txtArea1.appendText(fDate.format(new Date()) + " You >Sending voice\n");
		    	}else if (!connectedUser[1].equals("")){
		    		capture3.setDisable(true);
			    	stop3.setDisable(false);
					capture2.setDisable(true);
			    	stop2.setDisable(true);
			    	voiceSender2.captureAudio();
					agentSend.send(connectedUser[1],"Sending voice");
					txtArea2.appendText(fDate.format(new Date()) + " You >Sending voice\n");
				}
		    	
			}
	    });
	    
	    stop3.setOnAction(e->{
	    	if (quit == false){
	    		capture3.setDisable(false);
		    	stop3.setDisable(true);
	    		if (!connectedUser[0].equals("")){
		    		capture1.setDisable(false);
			    	stop1.setDisable(true);
		    		voiceSender1.stopaudioCapture = true;
			    	voiceSender1.targetDataLine.close();
					agentSend.send(connectedUser[0],"Stop voice sending");
					txtArea1.appendText(fDate.format(new Date()) + " You >Stop voice sending\n");
		    	}else if (!connectedUser[1].equals("")){
	    			capture2.setDisable(false);
			    	stop2.setDisable(true);
			    	voiceSender2.stopaudioCapture = true;
			    	voiceSender2.targetDataLine.close();
					agentSend.send(connectedUser[1],"Stop voice sending");
					txtArea2.appendText(fDate.format(new Date()) + " You >Stop voice sending\n");
	    		}
			}
	    	
	    });
	    	
	}
	
	private static void agentClient(){
		//start connection to server
		Socket s;
		try{
			s = new Socket(serverIP,agentPort);
			//start listening for message 
			receive = new AgentReceiveThread(s);
			receive.start();
			
			//prepare to send message 
			agentSend = new AgentSend(s);	
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
	}
	

	public static void saveChat(String txt){
		//save the chat according to date_time_ip as file name
		String name = txt.split(" ")[3];
		SimpleDateFormat fDate = new SimpleDateFormat ("yyyy_MM_dd_hh_mm_aa_");
		
		try (PrintWriter write = new PrintWriter(new File(System.getProperty("user.dir") + "/" +fDate.format(new Date()) + name+".txt"));){
			write.println(txt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}	
	
}

