package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;



public class MainController implements Initializable{
	private boolean walldragged=false;
	private double anchorX, anchorY;
	private double anchorAngleX=0;
	private double anchorAngleY=0;
	private double roomHieght=20;
	private double roomWidth=50;
	private double roomLength=70;
	private final DoubleProperty angleX=new SimpleDoubleProperty(0);
	private final DoubleProperty angleY=new SimpleDoubleProperty(45);

	@FXML
    private SubScene threeDModel;
	
	@FXML 
    private BorderPane borderPane;
	@FXML 
    private TextField txtlength,txtwidth,txtheight;
    
	@FXML
    private Button btndim;
	
	@FXML
    private ComboBox<String> cbboxref;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	cbboxref.setItems(FXCollections.observableArrayList("R-22","R-134a","R-407","R-410"));
    	
        Group root3D = new Group();
        loadModel(root3D);
        
        threeDModel.setRoot(root3D);
        // Set camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setFarClip(1500);
		camera.setNearClip(1);
		camera.translateXProperty().set(12);
		camera.translateYProperty().set(-190);
		camera.translateZProperty().set(-200);
		camera.setRotationAxis(Rotate.X_AXIS);
		camera.setRotate(-45)    ;
        //camera.translateZProperty().set(-1000);
		threeDModel.widthProperty().bind(borderPane.widthProperty());
		threeDModel.heightProperty().bind(borderPane.heightProperty());
        threeDModel.setFill(Color.valueOf("#212830"));
        threeDModel.setCamera(camera);
        initMouseControl(root3D, threeDModel,camera);
    	btndim.setOnMouseClicked(event -> {
    		root3D.getChildren().clear();
    		loadModel(root3D);
    	});
    }
    
    private void loadModel(Group root3D) {
    	double width=Double.parseDouble(txtwidth.getText());
		double length=Double.parseDouble(txtlength.getText());
		double height=Double.parseDouble(txtheight.getText());
		
		root3D.getChildren().addAll(drawRoom(width,height,length));
        root3D.getChildren().addAll(prepareLightSource());
        root3D.getChildren().addAll(setAcu(0,length, height, width));
		System.out.println("GGG");
    }
    private Node[] setAcu(int plane, double rl, double rh, double rw) {
    	
    	PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.valueOf("#b0b2b4"));
        Box acu=new Box(15,5,2);
        
	    acu.translateZProperty().set(rl/2-1);
	    acu.translateYProperty().set(rh/2*-1);
	    acu.setMaterial(material);
	    Box l=new Box(1,1.5,2);
	    l.translateZProperty().set(acu.getTranslateZ());
	    l.translateYProperty().set(acu.getTranslateY()+3);
	    l.translateXProperty().set(acu.getTranslateX()-acu.getWidth()/2+.5);
	    l.setMaterial(material);
	    Box r=new Box(1,1.5,2);
	    r.translateZProperty().set(acu.getTranslateZ());
	    r.translateYProperty().set(acu.getTranslateY()+3);
	    r.translateXProperty().set(acu.getTranslateX()+acu.getWidth()/2-.5);
	    r.setMaterial(material);
	    Box b=new Box(15,0.5,2);
	    b.translateZProperty().set(acu.getTranslateZ());
	    b.translateYProperty().set(acu.getTranslateY()+3.5);
	    b.setMaterial(material);
	    
	    return new Node[] {acu,l,r,b};
    }
	private Box prepareBox(double width, double height, double length,double x, double y, double z, int floor) {
		PhongMaterial material = new PhongMaterial();
	
		
		if(floor==1) {
			material.setDiffuseMap(new Image(getClass().getResourceAsStream("/resources/wood.jpg")));
			material.setSpecularColor(Color.WHITE);
		}else if(floor==2)
		{
		 material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/gray.jpg")));
		}else {
			material.setDiffuseColor(Color.GREY);
			//material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/white.jpg")));
		}
		
		
		
		Box box= new Box(width,length, height);
		box.translateYProperty().set(y);
		box.translateXProperty().set(x);
		box.translateZProperty().set(z);
		box.setMaterial(material);
		
		return box;
	}
  
    
    private Node[] drawRoom(double roomWidth, double roomHieght, double roomLength) {
    	
		Box platform= prepareBox(roomWidth+8,roomLength+8,1,0,0,0,0);
		Box floor= prepareBox(roomWidth,roomLength,1.5,0,0,0,1);
		Box wall1= prepareBox(1.5,roomLength,roomHieght,(roomWidth/2),roomHieght/2*-1,0,0);
		Box wall2= prepareBox(1.5,roomLength,roomHieght,(roomWidth/2*-1),roomHieght/2*-1,0,0);
		Box wall3= prepareBox(roomWidth+1.5,1.5,roomHieght,0,roomHieght/2*-1,roomLength/2,0);
		Box wall4= prepareBox(roomWidth+1.5,1.5,roomHieght,0,roomHieght/2*-1,roomLength/2*-1,0);
		Box wall5= prepareBox(1,roomLength,roomHieght,roomWidth/2+0.3,roomHieght/2*-1,0,2);
		Box wall6= prepareBox(1.5,roomLength+2,roomHieght,(roomWidth/2*-1)-0.3,roomHieght/2*-1,0,2);
		Box wall7= prepareBox(roomWidth+0.2+1.5,1.5,roomHieght,0,roomHieght/2*-1,roomLength/2+0.3,2);
		Box wall8= prepareBox(roomWidth+0.2+1.5,1.5,roomHieght,0,roomHieght/2*-1,roomLength/2*-1-0.3,2);
		Box wall9= prepareBox(1.5,roomLength,.1,(roomWidth/2*-1),roomHieght*-1,0,2);
		Box wall10= prepareBox(1.5,roomLength,.1,(roomWidth/2),roomHieght*-1,0,2);
		Box wall11= prepareBox(roomWidth+1.5,1.5,.1,0,roomHieght*-1,roomLength/2,2);
		Box wall12= prepareBox(roomWidth+1.5,1.5,.1,0,roomHieght*-1,roomLength/2*-1,2);
		wall1.setOnMouseClicked(event ->{
			int c=event.getClickCount();
			if(c>1) {
				System.out.println("wall1 clicked");
			}
			
		});
		wall1.setOnMouseDragged(event -> {
			walldragged=true;
		    System.out.println("wall1 dragged " + event.getScreenX());
		});
		wall1.setOnMouseReleased(even ->{
			 System.out.println("wall1 release");
			walldragged=false;
		});
		return new Node[] {platform, floor, wall1, wall2, wall3, wall4, wall5, wall6, wall7, wall8, wall9, wall10, wall11, wall12};
	}
	private Node[] prepareLightSource() {
		AmbientLight amLight= new AmbientLight();
		amLight.setColor(Color.valueOf("#424242"));
		PointLight pLight=new PointLight();
		pLight.setColor(Color.WHITE);
		pLight.getTransforms().add(new Translate(0,roomHieght*-1-10,0));
		PointLight pLight1=new PointLight();
		pLight1.setColor(Color.valueOf("#424242"));
		pLight1.getTransforms().add(new Translate(80,-5,-100));
		
		Sphere sphere=new Sphere(2);
		sphere.getTransforms().setAll(pLight1.getTransforms());
		
		return new Node[] {pLight,amLight,pLight1};
	}
	private void initMouseControl(Group group, SubScene scene,Camera cam) {
		Rotate rotateX;
		Rotate rotateY;
		rotateY=new Rotate(0,Rotate.Y_AXIS);
		 rotateX=new Rotate(0, Rotate.X_AXIS);
		scene.setOnMousePressed(event ->{
			anchorX=event.getSceneX();
			anchorY=event.getSceneY();
			anchorAngleX=angleX.get();
			anchorAngleY=angleY.get();
			
		});
		
		scene.setOnMouseDragged(event -> {
			if(walldragged==false) {
				angleX.set(anchorAngleX-(anchorY-event.getSceneY()));
				angleY.set(anchorAngleY+(anchorX-event.getSceneX()));
			}
		});
		rotateY.angleProperty().bind(angleY);
		//rotateX.angleProperty().bind(angleX);
		group.getTransforms().addAll(rotateY,rotateX);
		
		scene.addEventHandler(ScrollEvent.SCROLL, event ->{
			double movement=event.getDeltaY();
			group.translateZProperty().set(group.getTranslateZ()+movement/2*-1);
			group.translateYProperty().set(group.getTranslateY()+movement/2*-1);
			System.out.println(group.getTranslateZ()+","+group.getTranslateY());
		});
		
		
	}
}