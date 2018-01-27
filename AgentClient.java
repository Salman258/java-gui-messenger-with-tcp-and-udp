
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AgentClient extends Application {
	
	private static boolean signedIn;
	final static String filename = "Credentials.txt";
	private static int loginAttempt;
	private static String serverIP = "localhost" ;
	
    public static void main(String[] args) {
    	//main to start Agent Client
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
    	Text actionTarget;
    	GridPane grid;
    	Text sceneTitle;
    	Label userName;
    	TextField userTextField;
    	Label pw;
    	PasswordField pwBox;
    	Button btn;
    	HBox hBox;
    	loginAttempt = 4;
    	
        primaryStage.setTitle("Login");
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(20);
        grid.setPadding(new Insets(50,50,50,50));
        grid.setStyle("-fx-background-color: white");

        sceneTitle = new Text("Agent Login");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        pw = new Label("Password:");
        grid.add(pw, 0, 2);

        pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        actionTarget = new Text();
        actionTarget.setStyle("-fx-font:10 Arial");
        btn = new Button("Sign in");
        btn.setMinWidth(90);
        btn.setStyle("-fx-background-color:#cccccc;");
        
        hBox = new HBox(10);
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().addAll(actionTarget,btn);
        grid.add(hBox, 0, 4);
        grid.setColumnSpan(hBox, grid.REMAINING);

        primaryStage.setScene(new Scene(grid));
        primaryStage.sizeToScene();
        primaryStage.show();

        btn.setOnAction(e->{
        	//authentication for login
        	//use rmi to look for login credentials

        	signedIn = false;
        	loginAttempt = loginAttempt - 1;
        	try{
        		Registry registry=LocateRegistry.getRegistry(serverIP);
    			AuthenticationInterface login = (AuthenticationInterface) registry.lookup("loginObject");
    			if (login.userAuth(userTextField.getText(), pwBox.getText())==1){	
    				signedIn = true;
    			}else{
    				actionTarget.setFill(Color.RED);
    				actionTarget.setText("INVALID LOGIN. (" + loginAttempt + " attempts left)");
    			}
			}
			catch (Exception ex){
				actionTarget.setFill(Color.RED);
				actionTarget.setText("INVALID LOGIN. (" + loginAttempt + " attempts left)");
			}
			
			
			if (loginAttempt  < 0){
				actionTarget.setFill(Color.RED);
				actionTarget.setText("UNABLE TO LOGIN. (0 attempts left)");
			}
			else
			{
				if ((signedIn == true)){
	        		//start chat window
	        		AgentChatArea.start(primaryStage, userTextField.getText());
	        		userTextField.setText("");
	        		pwBox.setText("");
	        	}
			}
			
        });
        
        
        btn.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btn.fire();
	    	}
	    });
        
        userTextField.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btn.fire();
	    	}
	    });
        
        pwBox.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btn.fire();
	    	}
	    });

    }

}
