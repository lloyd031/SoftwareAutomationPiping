package application;
	
import java.io.IOException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import java.io.File;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage){
		
		
		try {
			//read file with custom extension
			
			/**
			 * File f=new File("C:\\Users\\elwie\\OneDrive\\Desktop\\Java\\SoftareAutomationRefrigerantPiping\\src\\application\\dim.htr");
			Scanner sc=new Scanner(f);
			while(sc.hasNextLine()) {
				System.out.println(sc.nextLine());
			}
			 */
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
	        BorderPane root;
			root = loader.load();
			Scene scene = new Scene(root, 800, 661);
	        
	        //primaryStage.setMaximized(true);
			primaryStage.setTitle("Refrigerant Piping Software Automation");
	        primaryStage.setScene(scene);
	        
	        primaryStage.show();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}
	
	public static void main(String[] args) {
		
		launch(args);
	}
}
