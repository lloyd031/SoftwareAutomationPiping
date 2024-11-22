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
	private boolean compdragged=false;
	private double anchorX, anchorY;
	private double anchorAngleX=0;
	private double anchorAngleY=0;
	private double tempX=0;
	private double tempY=0;
	private double roomHieght=20;
	private final DoubleProperty angleX=new SimpleDoubleProperty(0);
	private final DoubleProperty angleY=new SimpleDoubleProperty(45);
	private DoubleProperty acuX=null;
	private DoubleProperty acuY=null;
    private Group acu;
    private double width=50;
    private double height=20;
    private double length=70;
    private int selectedwall=2;
    private Box[] n;
    private PointLight acuL;
    private double acuw=10.5;
    private double acuh=2.2;
    private double acud=2;
	@FXML
    private SubScene threeDModel;
	
	@FXML 
    private BorderPane borderPane;
	@FXML 
    private TextField txtlength,txtwidth,txtheight,txtevapx,txtevapy;
    
	@FXML
    private Button btndim;
	

	
	@FXML
    private Button btnevap;
	
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
         n=new Box[4];
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
        initializeLocationAcu();
        initAcuDragged();
    	btndim.setOnMouseClicked(event -> {
    		width=Double.parseDouble(txtwidth.getText())*10;
    		length=Double.parseDouble(txtlength.getText())*10;
    		height=Double.parseDouble(txtheight.getText())*10;
    		root3D.getChildren().clear();
    		loadModel(root3D);
    		//setLoc();
    		//initlocation();
    		initializeLocationAcu();
    		initAcuDragged();
    	});
    	
    	cbboxwall.setOnAction(event -> {
            System.out.println("Selected value: " + cbboxwall.getSelectionModel().getSelectedIndex());
            root3D.getChildren().removeAll(acu,acuL);
            selectedwall=cbboxwall.getSelectionModel().getSelectedIndex();
            acu=setAcu(selectedwall);
            btnevap.requestFocus();
            System.out.println("acu : "+acu.getTranslateZ());
            addAcul(root3D);
            initializeLocationAcu();
            initAcuDragged();
            root3D.getChildren().add(acu);
        });
       
       btnevap.setOnAction(e->{
    	   //initlocation();
       });
       
      
       btnevap.setOnKeyPressed(e->{
     	   int j=(selectedwall==3 || selectedwall==0)?-1:1;
     	   double a =Double.parseDouble(txtevapx.getText());
     	   switch(e.getCode()) {
     	   case D:
     		   if(selectedwall==2 || selectedwall ==3) {
     			   
     			   acuX.set(acuX.get()+0.25);
     		   }else {
     		   }
     		   break;
     	   case A:
     		   if(selectedwall==2 || selectedwall ==3) {
     		   }else {
     		   }
     		   break;
     	   case W:
     		  acuY.set(acuY.get()-0.25);
     		   break;
     	   case S:
     		   break;
     	   default:
     		   break;
     	   
     	   }
     	   setLoc();
        });
    	
    }
   
    private void initAcuDragged() {
    	int i=(selectedwall==2 || selectedwall==1)?1:-1;
    	acu.setOnMousePressed(e->{
    		compdragged=true;
    		anchorX=e.getSceneX();
    		anchorY=e.getSceneY();
    		tempX=acuX.get();
    		tempY=acuY.get();
    	});
    	if(selectedwall==2 || selectedwall ==3) {
    		acu.translateXProperty().bind(acuX);
    		
    	}else {
    		acu.translateZProperty().bind(acuX);
    	}
    	
    	double boundx = width/2*i-acuw/2*i;
    	double boundz = length/2*i-acuw/2*i;
    	double boundy = height/-2 + 1.20;
    	System.out.print("Dfdffd "+boundy);
    	acu.setOnMouseDragged(e->{
    	setLoc();
    	double newX=tempX-(anchorX-e.getSceneX())/10*i;
    	double newY=tempY-(anchorY-e.getSceneY())/10;
    	if((newX>boundx*-1 && newX<boundx+0.1 && selectedwall==2) || (newX>boundx && newX<boundx*-1+0.1 && selectedwall==3) ) {
    		acuX.set(newX);
    	}else if((newX>boundz*-1 && newX<boundz+0.1 && selectedwall==1) || (newX>boundz && newX<boundz*-1+0.1 && selectedwall==0)) {
    		acuX.set(newX);
    	}
    	acu.translateYProperty().bind(acuY);
    	if(newY>=boundy-0.05 && newY<=(boundy+1.20)*-1) {
    		acuY.set(newY);
    	}
    	
    	});
    	
    	
    	acu.setOnMouseReleased(e->{
    		compdragged=false;
    	});
    }
    private void setLoc() {
    	double cux=(selectedwall==2 || selectedwall==3)?(acu.getTranslateX()-width/-2-acuw/2)/10:(acu.getTranslateZ()-length/-2-acuw/2)/10;
 	    txtevapx.setText(String.format("%.2f",cux));
 	    double cuy=(acu.getTranslateY()-(height/-2+1.20))/10;
 	    
 	    
 	    txtevapy.setText(String.format("%.2f", cuy));
    }
    private void initializeLocationAcu()
    {
    	this.acuX=new SimpleDoubleProperty((-width/-2-acuw/2)/10);
    	this.acuY=new SimpleDoubleProperty(0);
    	setLoc();
    }
    private void initlocation() {
    	double evapx =Double.parseDouble(txtevapx.getText())*10;
	  	double evapy =Double.parseDouble(txtevapy.getText())*10;
    	if(selectedwall==2 || selectedwall ==3) {
    		
    	  	   acu.translateXProperty().set((n[selectedwall].getTranslateX()-width/2+acuw/2)+evapx);
    	}else{
    		acu.translateZProperty().set((n[selectedwall].getTranslateZ()-length/2+acuw/2)+evapx);
    	}
    	acu.translateYProperty().set((height)/-2+1.25+evapy);
    }
    
    private void loadModel(Group root3D) {
    	
		root3D.getChildren().addAll(drawRoom(width,height,length));
        root3D.getChildren().addAll(prepareLightSource());
        
        acu=setAcu(selectedwall);
        addAcul(root3D);
        root3D.getChildren().add(acu);
		System.out.println("GGG");
    }
    private void addAcul(Group root) {
    	int i=(selectedwall==2 || selectedwall==1)?1:-1;
    	root.getChildren().remove(acuL);
    	acuL=new PointLight();
        acuL.setColor(Color.valueOf("#001217"));
 		Node a=this.n[selectedwall];
 		if(selectedwall==2 || selectedwall ==3) {
 			acuL.getTransforms().add(new Translate(a.getTranslateX()+3.5*i,a.getTranslateY(),a.getTranslateZ()));
 		}else {
 			acuL.getTransforms().add(new Translate(a.getTranslateX(),a.getTranslateY(),a.getTranslateZ()+3.5*i));
 		}
 		root.getChildren().add(acuL);
    }
    private Group setAcu(int i) {
    	Node wall=this.n[i];
    	int z=(i==2 || i==0)?-1:1;
    	int deg=(i==2)?0:(i==3)?180:(i==0)?90:270;
    	//int deg=()?:;
    	
    	
 		
 		PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.valueOf("#b0b2b4"));
        
        Box box=new Box(acuw,acuh,1);
		box.translateZProperty().set(0-(acud-0.5)/2);
	    box.translateYProperty().set(wall.getTranslateY());
	    box.setMaterial(material);
	    
	    
	    
	    //box.setRotationAxis(Rotate.Y_AXIS);
		//box.setRotate(180);
	    
	    Cylinder c=new Cylinder(0.5,acuw);
        c.translateZProperty().set(box.getTranslateZ());
	    c.translateYProperty().set(box.getTranslateY()-box.getHeight()/2);
	    c.setRotationAxis(Rotate.X_AXIS);
	    c.setRotationAxis(Rotate.Z_AXIS);
		c.setRotate(90);
		c.setMaterial(material);
		
		Cylinder c1=new Cylinder(0.5,acuw);
        c1.translateZProperty().set(box.getTranslateZ());
	    c1.translateYProperty().set(box.getTranslateY()+box.getHeight()/2);
	    c1.setRotationAxis(Rotate.X_AXIS);
	    c1.setRotationAxis(Rotate.Z_AXIS);
		c1.setRotate(90);
		c1.setMaterial(material);
		
		Box box1=new Box(acuw,0.3,acud);
		box1.translateZProperty().set(box.getTranslateZ()+acud/2);
	    box1.translateYProperty().set((box.getTranslateY()-box.getHeight()/2-0.5)+0.3/2);
	    box1.setMaterial(material);
	    
	    Box box2=new Box(acuw,0.3,acud);
		box2.translateZProperty().set(box.getTranslateZ()+acud/2);
	    box2.translateYProperty().set((box.getTranslateY()+box.getHeight()/2+0.5)-0.3/2);
	    box2.setMaterial(material);
	    
	    Box box3=new Box(0.3,acuh+1,acud);
		box3.translateZProperty().set(box.getTranslateZ()+acud/2);
		box3.translateYProperty().set(box.getTranslateY());
		box3.translateXProperty().set((box.getTranslateX()-box.getWidth()/2)+0.3/2);
		box3.setMaterial(material);
		
		Box box4=new Box(0.3,acuh+1,acud);
		box4.translateZProperty().set(box.getTranslateZ()+acud/2);
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
	    Box fl=new Box(acuw-1.5,0.3,1.1);
	    fl.translateZProperty().set(box.getTranslateZ());
	    fl.translateYProperty().set(box.getTranslateY()+(box.getHeight()/2));
	    fl.setMaterial(m);
	    
	    
	    Group acu=new Group();
	    acu.getChildren().addAll(box,c,c1,box1,box2,box3,box4,img,fl);
	    acu.setRotationAxis(Rotate.Y_AXIS);
	    acu.setRotate(deg);

	    if(i==2 || i==3) {
	    	acu.translateZProperty().set(wall.getTranslateZ()+((acud+0.5)/2)*z);
	    	
	    }else if(i==0 || i ==1) {
	    	acu.translateXProperty().set(wall.getTranslateX()+((acud+0.5)/2)*z);
	    }
	    System.out.println("acu : "+wall.getTranslateZ());
	    acu.setOnMouseClicked(e->{
	    	   btnevap.requestFocus();
	       });
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
			if(compdragged==false) {
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
		});
		
		
		
	}
}
/**/
