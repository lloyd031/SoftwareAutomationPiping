package application;

import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
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
    private Group acu;
    private Node[] n;
    private PointLight acuL;
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
	
	@FXML
	private ComboBox<String> cbboxwall;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	cbboxref.setItems(FXCollections.observableArrayList("R-22","R-134a","R-407","R-410"));
    	cbboxwall.setItems(FXCollections.observableArrayList("Wall-1","Wall-2","Wall-3","Wall-4"));
        cbboxwall.getSelectionModel().select(2);
        Group root3D = new Group();
         n=new Node[4];
        loadModel(root3D);
        
        
        
        threeDModel.setRoot(root3D);
        // Set camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setFarClip(1500);
		camera.setNearClip(1);
		camera.translateXProperty().set(0);
		camera.translateYProperty().set(-200);
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
    	
    	cbboxwall.setOnAction(event -> {
            System.out.println("Selected value: " + cbboxwall.getSelectionModel().getSelectedIndex());
            root3D.getChildren().removeAll(acu,acuL);
            acu=setAcu(cbboxwall.getSelectionModel().getSelectedIndex());
            System.out.println("acu : "+acu.getTranslateZ());
            addAcul(root3D);
            root3D.getChildren().add(acu);
        });
    }
    
    private void loadModel(Group root3D) {
    	double width=Double.parseDouble(txtwidth.getText())*10;
		double length=Double.parseDouble(txtlength.getText())*10;
		double height=Double.parseDouble(txtheight.getText())*10;
		root3D.getChildren().addAll(drawRoom(width,height,length));
        root3D.getChildren().addAll(prepareLightSource());
        acu=setAcu(cbboxwall.getSelectionModel().getSelectedIndex());
        addAcul(root3D);
        root3D.getChildren().add(acu);
		System.out.println("GGG");
    }
    private void addAcul(Group root) {
    	root.getChildren().remove(acuL);
    	acuL=new PointLight();
        acuL.setColor(Color.valueOf("#001217"));
 		Node a=this.n[cbboxwall.getSelectionModel().getSelectedIndex()];
 		acuL.getTransforms().add(new Translate(a.getTranslateX(),a.getTranslateY(),a.getTranslateZ()));
 		root.getChildren().add(acuL);
    }
    private Group setAcu(int i) {
    	Node wall=this.n[i];
    	int z=(i==2 || i==0)?-1:1;
    	int deg=(i==2)?0:(i==3)?180:(i==0)?90:270;
    	//int deg=()?:;
    	double w=10.5;
    	double h=2.2;
    	double d=2;
    	
 		
 		PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.valueOf("#b0b2b4"));
        
        Box box=new Box(w,h,1);
		box.translateZProperty().set(0-(d-0.5)/2);
	    box.translateYProperty().set(wall.getTranslateY());
	    box.setMaterial(material);
	    
	    
	    
	    //box.setRotationAxis(Rotate.Y_AXIS);
		//box.setRotate(180);
	    
	    Cylinder c=new Cylinder(0.5,w);
        c.translateZProperty().set(box.getTranslateZ());
	    c.translateYProperty().set(box.getTranslateY()-box.getHeight()/2);
	    c.setRotationAxis(Rotate.X_AXIS);
	    c.setRotationAxis(Rotate.Z_AXIS);
		c.setRotate(90);
		c.setMaterial(material);
		
		Cylinder c1=new Cylinder(0.5,w);
        c1.translateZProperty().set(box.getTranslateZ());
	    c1.translateYProperty().set(box.getTranslateY()+box.getHeight()/2);
	    c1.setRotationAxis(Rotate.X_AXIS);
	    c1.setRotationAxis(Rotate.Z_AXIS);
		c1.setRotate(90);
		c1.setMaterial(material);
		
		Box box1=new Box(w,0.3,d);
		box1.translateZProperty().set(box.getTranslateZ()+d/2);
	    box1.translateYProperty().set((box.getTranslateY()-box.getHeight()/2-0.5)+0.3/2);
	    box1.setMaterial(material);
	    
	    Box box2=new Box(w,0.3,d);
		box2.translateZProperty().set(box.getTranslateZ()+d/2);
	    box2.translateYProperty().set((box.getTranslateY()+box.getHeight()/2+0.5)-0.3/2);
	    box2.setMaterial(material);
	    
	    Box box3=new Box(0.3,h+1,d);
		box3.translateZProperty().set(box.getTranslateZ()+d/2);
		box3.translateYProperty().set(box.getTranslateY());
		box3.translateXProperty().set((box.getTranslateX()-box.getWidth()/2)+0.3/2);
		box3.setMaterial(material);
		
		Box box4=new Box(0.3,h+1,d);
		box4.translateZProperty().set(box.getTranslateZ()+d/2);
		box4.translateYProperty().set(box.getTranslateY());
		box4.translateXProperty().set((box.getTranslateX()+box.getWidth()/2)-0.3/2);
		box4.setMaterial(material);
		  
		
		Image icon=new Image("/resources/power-on .png");
	    ImageView img=new ImageView(icon);
	    img.setFitWidth(0.5);
	    img.setFitHeight(0.5);
	    img.translateYProperty().set(box.getTranslateY()-box.getHeight()/3);
	    img.translateXProperty().set(box.getTranslateX()+box.getWidth()/3);
	    img.translateZProperty().set(box.getTranslateZ()-0.54);
	    
        
	    PhongMaterial m = new PhongMaterial();
        m.setDiffuseColor(Color.valueOf("#2e4357"));
        m.setSpecularColor(Color.WHITE);
	    Box fl=new Box(w-1.5,0.3,1.1);
	    fl.translateZProperty().set(box.getTranslateZ());
	    fl.translateYProperty().set(box.getTranslateY()+(box.getHeight()/2));
	    fl.setMaterial(m);
	    
	    
	    Group acu=new Group();
	    acu.getChildren().addAll(box,c,c1,box1,box2,box3,box4,img,fl);
	    acu.setRotationAxis(Rotate.Y_AXIS);
	    acu.setRotate(deg);
	    if(i==2 || i==3) {
	    	acu.translateZProperty().set(wall.getTranslateZ()+((d+0.5)/2)*z);
	    }else if(i==0 || i ==1) {
	    	acu.translateXProperty().set(wall.getTranslateX()+((d+0.5)/2)*z);
	    }
	    System.out.println("acu : "+wall.getTranslateZ());
    	return  acu;
	    //return new Node[] {acu,l,r,b,img,pLight};
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
    	//whlxyz
    	double wall=0.2032/2;
    	Box platform= prepareBox(roomWidth+5,roomLength+5,0.5,0,0.75,0,0);
		Box floor= prepareBox(roomWidth,roomLength,1.5,0,0,0,1);
		Box wall1= prepareBox(wall,roomLength+wall*2,roomHieght+0.75,(roomWidth/2)+wall/2,roomHieght/2*-1,0,0);
		Box wall2= prepareBox(wall,roomLength+wall*2,roomHieght+0.75,(roomWidth/2*-1)-wall/2,roomHieght/2*-1,0,0);
		Box wall3= prepareBox(roomWidth,wall,roomHieght+0.75,0,roomHieght/2*-1,roomLength/2+wall/2,0);
		Box wall4= prepareBox(roomWidth,wall,roomHieght+0.75,0,roomHieght/2*-1,roomLength/-2-wall/2,0);
		Box wall5= prepareBox(wall,roomLength+wall*4,roomHieght+0.75,wall1.getTranslateX()+wall,roomHieght/2*-1,0,2);
		Box wall6= prepareBox(wall,roomLength+wall*4,roomHieght+0.75,wall2.getTranslateX()-wall,roomHieght/2*-1,0,2);
		Box wall7= prepareBox(roomWidth+wall*4,wall,roomHieght+0.75,0,roomHieght/2*-1,wall3.getTranslateZ()+wall,2);
		Box wall8= prepareBox(roomWidth+wall*4,wall,roomHieght+0.75,0,roomHieght/2*-1,wall4.getTranslateZ()-wall,2);
		Box wall9= prepareBox(wall,roomLength+wall*2,0.1,(roomWidth/2)+wall/2,roomHieght*-1-0.75/2,0,2);
		Box wall10= prepareBox(wall,roomLength+wall*2,0.1,(roomWidth/2*-1)-wall/2,roomHieght*-1-0.75/2,0,2);
		Box wall11=prepareBox(roomWidth,wall,0.1,0,roomHieght*-1-0.75/2,roomLength/2+wall/2,2);
		Box wall12= prepareBox(roomWidth,wall,0.1,0,roomHieght*-1-0.75/2,roomLength/-2-wall/2,2);
		wall1.setOnMouseClicked(event ->{
			int c=event.getClickCount();
			if(c>1) {
				System.out.println("wall1 clicked");
			}
			
		});
		 System.out.println("wasfds dragged " + wall9.getTranslateY());
		wall1.setOnMouseDragged(event -> {
			walldragged=true;
		    System.out.println("wall1 dragged " + event.getScreenX());
		});
		wall1.setOnMouseReleased(even ->{
			 System.out.println("wall1 release");
			walldragged=false;
		});
		n[0]=wall1;
		n[1]=wall2;
		n[2]=wall3;
		n[3]=wall4;
		return new Node[] { platform,floor,wall1,wall2,wall3,wall4,wall5,wall6,wall7,wall8,wall9,wall10,wall11,wall12};
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