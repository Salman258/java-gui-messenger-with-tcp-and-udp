
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class UserClient extends Application {

	@Override 
	public void start(Stage clientStage) {
	    
	    GridPane clientPane = new GridPane();
	    clientPane.setAlignment(Pos.CENTER);
	    clientPane.setPadding(new Insets(50));
	    clientPane.setHgap(35);
	    clientPane.setVgap(15);
	    clientPane.setStyle("-fx-background-color: white");
	    
	    Label lbUsername = new Label("Name:");
	    lbUsername.setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD, 18));
	    clientPane.add(lbUsername, 0, 0);
	    
	    TextField txtUsername = new TextField();
	    clientPane.add(txtUsername, 1, 0);
	    
	    Label lbQuestion = new Label("Question/Inquiry:");
	    lbQuestion.setFont(Font.font("Tahoma", FontWeight.SEMI_BOLD, 18));
	    clientPane.add(lbQuestion, 0, 1);
	    
	    TextField txtQuestion = new TextField();
	    clientPane.add(txtQuestion, 1, 1);
	    txtQuestion.setPrefWidth(280);
	    
	    Label lbError = new Label();
	    lbError.setStyle("-fx-text-fill: red");;
	    
	    Button btnStart = new Button("Start Chat");
	    btnStart.setStyle("-fx-background-color: #99ff99");
	    btnStart.setPrefWidth(150);
	    
	    HBox hBox = new HBox(10);
	    hBox.setAlignment(Pos.CENTER_RIGHT);
	    clientPane.setColumnSpan(hBox, clientPane.REMAINING);
	    hBox.getChildren().addAll(lbError, btnStart);
	    clientPane.add(hBox, 0, 4);
	    
	    
	    clientStage.setTitle("User Inquiry"); 
	    clientStage.setScene(new Scene(clientPane)); 
	    clientStage.show(); 
	    
	    
	    btnStart.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btnStart.fire();
	    	}
	    });
	    
	    btnStart.setOnAction(e->{
	    	lbError.setText("");
	    	lbUsername.setStyle("-fx-text-fill: black;");
	    	lbQuestion.setStyle("-fx-text-fill: black;");
	    	if (isValidName(txtUsername.getText())){
	        	if (!txtQuestion.getText().equals("")){
	        		try {
	        			//validate inputs and starts chat window
						UserChatArea.start(clientStage,txtUsername.getText(), txtQuestion.getText());
					} catch (Exception e1) {
						
					}
	        	}
	        	else{
	        		lbError.setText("Enter something to ask");
	        	}
	        }else{
	        	lbError.setText("Incorrect name format");
	        }
	    });
	    
	    txtUsername.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btnStart.fire();
	    	}
	    });
	    
	    txtQuestion.setOnKeyPressed(e ->{
	    	if (e.getCode() == KeyCode.ENTER){
	    		btnStart.fire();
	    	}
	    });
	    
	    
	}
  
	private static boolean isValidName(String s){
		//check if name is in valid format
		String regex="[A-Za-z]+";
		return s.matches(regex)&&s.length()>1&&s.length()<21;
	}
  
	public static void main(String[] args) {
		//main method to launch User Client
	  launch(args);
	}
} 
