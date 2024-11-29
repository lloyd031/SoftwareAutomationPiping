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
	private DoubleProperty compX=null;
	private DoubleProperty compY=null;
    private Group acu;
    private Group cmp;
    private double width=50;
    private double height=20;
    private double length=70;
    private int selectedwall=2;
    private Box[] n;
    private Box[] n2;
    private PointLight acuL;
    private double acuw=8.4;
    private double acuh=2;
    private double acud=1.7;
    
    private double compw=8.7;
    private double comph=6.5 ;
    private double compd=3.3;
    private double wall=2.032/2;
    private double selectedcomp=1;
	@FXML
    private SubScene threeDModel;
	
	@FXML 
    private BorderPane borderPane;
	@FXML 
    private TextField txtlength,txtwidth,txtheight,txtevapx,txtevapy,txtcompx,txtcompy;
    
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
         n2=new Box[4];
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
            root3D.getChildren().removeAll(acu,acuL,cmp);
            selectedwall=cbboxwall.getSelectionModel().getSelectedIndex();
            acu=setAcu();
            cmp=setCompressor();
            btnevap.requestFocus();
            System.out.println("acu : "+acu.getTranslateZ());
            addAcul(root3D);
            initializeLocationAcu();
            initAcuDragged();
            root3D.getChildren().addAll(acu,cmp);
        });
       
       btnevap.setOnAction(e->{
    	   //initlocation();
       });
       
      
       
    	
    }
    private void prepdragproperty(double x, double y, int sc, double tempx, double tempy) {
    	compdragged=true;
		selectedcomp=sc;
		anchorX=x;
		anchorY=y;
		if(sc==0) {
			tempX=acuX.get();
			tempY=acuY.get();
		}else {
			tempX=compX.get();
			tempY=compY.get();
		}
    }
    private void initAcuDragged() {
    	int i=(selectedwall==2 || selectedwall==1)?1:-1;
    	acu.setOnMousePressed(e->{
    		prepdragproperty(e.getX(),e.getY(),0,acuX.get(),acuY.get());
    	});
    	
    	cmp.setOnMousePressed(e->{
    		prepdragproperty(e.getX(),e.getY(),1,compX.get(),compY.get());
    	});
    	if(selectedwall==2 || selectedwall ==3) {
    		
    			acu.translateXProperty().bind(acuX);
    			cmp.translateXProperty().bind(compX);
    		
    		
    	}else {
    		acu.translateZProperty().bind(acuX);
    		cmp.translateZProperty().bind(compX);
    	}
    	
    	
    	acu.setOnMouseDragged(e->{
    		double boundx = width/2*i-acuw/2*i;
        	double boundz = length/2*i-acuw/2*i;
        	if(selectedcomp==0) {
        		setLoc();
            	double newX=tempX-(anchorX-e.getSceneX())/10*i;
            	double newY=tempY-(anchorY-e.getSceneY())/10;
            	 if((newX>boundx*-1-0.1 && newX<boundx && selectedwall==2) || (newX>boundx-0.1 && newX<boundx*-1 && selectedwall==3) ) {
            		acuX.set(newX);
            	}else if((newX>boundz*-1-0.1 && newX<boundz && selectedwall==1) || (newX>boundz-0.1 && newX<boundz*-1 && selectedwall==0)) {
            		acuX.set(newX);
            	}
            	 
            	acu.translateYProperty().bind(acuY);
            	if(((Math.round((newY-3.2/2)*10.0)/10.0)-(Math.round(((height+0.75)/-2)*10.0)/10.0))>=0 && ((Math.round((newY+3.2/2)*10.0)/10.0)+(Math.round(((height-0.75)/-2)*10.0)/10.0))<=0) {
            		acuY.set(newY);
            	}
        	}
        	
        	});
    	cmp.setOnMouseDragged(e->{
    		double boundx = width/2*i-compw/2*i+(wall*2)*i;
        	double boundz = length/2*i-compw/2*i+(wall*2)*i;
        	if(selectedcomp==1) {
        		setLoc();
            	double newX=tempX+(anchorX-e.getSceneX())/10*i;
            	double newY=tempY-(anchorY-e.getSceneY())/10;
            	if((newX>boundx*-1 && newX<boundx && selectedwall==2) || (newX>boundx && newX<boundx*-1 && selectedwall==3) ) {
            		compX.set(newX);
            	}else if((newX>boundz*-1 && newX<boundz && selectedwall==1) || (newX>boundz && newX<boundz*-1 && selectedwall==0)) {
            		compX.set(newX);
            	}
            	cmp.translateYProperty().bind(compY);
            	if(((Math.round((newY-comph/2)*10.0)/10.0)-(Math.round(((height+0.75)/-2)*10.0)/10.0))>=0 && ((Math.round((newY+comph/2)*10.0)/10.0)+(Math.round(((height-0.75)/-2)*10.0)/10.0))<=0) {
            		compY.set(newY);
            	}
            	compY.set(newY);
        	}
        	
        	});
    	
    	
    	acu.setOnMouseReleased(e->{
    		compdragged=false;
    	});
    	cmp.setOnMouseReleased(e->{
    		compdragged=false;
    	});
    }
    //this line -0.04 has no exact reference but rather a result of trial and error in choosing b
    private void setLoc() {
    	if(selectedcomp==0) {
    		double cux=(selectedwall==2 || selectedwall==3)?(width/2-(acu.getTranslateX()+acuw/2))/10:((length/2)-(acu.getTranslateZ()+acuw/2))/10;
     	    txtevapx.setText(String.format("%.2f",cux));
     	    double cuy=(((Math.round((acu.getTranslateY()-3.2/2)*10.0)/10.0)-(Math.round(((height+0.75)/-2)*10.0)/10.0)))/10;
     	    
     	    txtevapy.setText(String.format("%.2f", cuy));
    	}else {
    		double cux=(selectedwall==2 || selectedwall==3)?(cmp.getTranslateX()-(width+wall*4)/-2-compw/2)/10:(cmp.getTranslateZ()-(length+wall*4)/-2-compw/2)/10;
    		txtcompx.setText(String.format("%.2f",cux));
     	    double cuy=(((Math.round((cmp.getTranslateY()-comph/2)*10.0)/10.0)-(Math.round(((height+0.75)/-2)*10.0)/10.0)))/10;
     	   txtcompy.setText(String.format("%.2f", cuy));
    	}
    }
    private void initializeLocationAcu()
    {
    	this.acuX=new SimpleDoubleProperty((-width/-2-acuw/2)/10);
    	this.acuY=new SimpleDoubleProperty(0);
    	this.compX=new SimpleDoubleProperty((-width/-2-compw/2)/10);
    	this.compY=new SimpleDoubleProperty(0);
    	
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
        
        acu=setAcu();
        cmp=setCompressor();
        addAcul(root3D);
        root3D.getChildren().addAll(acu,cmp);
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
    
    
    private Group setAcu() {
    	int i=selectedwall;
    	Node wall=this.n[i];
    	int z=(i==2 || i==0)?-1:1;
    	int deg=(i==2)?0:(i==3)?180:(i==0)?90:270;
 		PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.valueOf("#b0b2b4"));
        Box box=drawBox(0,acuw,acuh,1,0,wall.getTranslateY(),-(acud-0.5)/2);
	    Cylinder c=drawCylinder(0,acuw,box.getTranslateY()-box.getHeight()/2,box.getTranslateZ(),90);
		Cylinder c1=drawCylinder(0,acuw,box.getTranslateY()+box.getHeight()/2,box.getTranslateZ(),90);
		Box box1=drawBox(0,acuw,0.3,acud,0,(box.getTranslateY()-box.getHeight()/2-0.5)+0.3/2,box.getTranslateZ()+acud/2);
		Box box2=drawBox(0,acuw,0.3,acud,0,(box.getTranslateY()+box.getHeight()/2+0.5)-0.3/2,box.getTranslateZ()+acud/2);
		Box box3=drawBox(0,0.3,acuh+1,acud,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+acud/2);
		Box box4=drawBox(0,0.3,acuh+1,acud,(box.getTranslateX()+box.getWidth()/2)-0.3/2,box.getTranslateY(),box.getTranslateZ()+acud/2);
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
	    	acu.translateZProperty().set(wall.getTranslateZ()+((acud+0.5+this.wall)/2)*z);
	    	
	    }else if(i==0 || i ==1) {
	    	acu.translateXProperty().set(wall.getTranslateX()+((acud+0.5+this.wall)/2)*z);
	    }
	    System.out.println("acu : "+wall.getTranslateZ());
	    acu.setOnMouseClicked(e->{
	    	   btnevap.requestFocus();
	       });
    	return  acu;
	    //return new Node[] {acu,l,r,b,img,pLight};
    }
 
    private Group setCompressor() {
    	int i=selectedwall;
    	Node wall=this.n2[i];
    	int z=(i==2 || i==0)?-1:1;
    	int deg=(i==2)?180:(i==3)?0:(i==0)?270:90;
        Box box=drawBox(1,compw,comph-1,1,0,(height+0.75)/-2+0.5,-(compd-0.5)/2);
	    Cylinder c=drawCylinder(1,compw,box.getTranslateY()-box.getHeight()/2,box.getTranslateZ(),90);
		Cylinder c1=drawCylinder(1,compw,box.getTranslateY()+box.getHeight()/2,box.getTranslateZ(),90);
        Box box1=drawBox(1,compw,1,compd,0,(box.getTranslateY()-box.getHeight()/2-0.5)+1/2+0.5,box.getTranslateZ()+compd/2);
        Box box2=drawBox(1,compw,1,compd,0,(box.getTranslateY()+box.getHeight()/2+0.5)-1/2-0.5,box.getTranslateZ()+compd/2);
        Box box3=drawBox(2,0.3,comph-0.5,compd,(box.getTranslateX()-box.getWidth()/2)+0.2,box.getTranslateY(),box.getTranslateZ()+compd/2);
		Box box4=drawBox(1,0.3,comph,compd,(box.getTranslateX()+box.getWidth()/2)-0.3/2,box.getTranslateY(),box.getTranslateZ()+compd/2);
		Box box5=drawBox(1,0.3,comph-0.5,0.5,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+compd-0.25);
		Box box6=drawBox(1,0.3,comph-0.5,0.5,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+compd/2);
		Cylinder f=new Cylinder(2.5,1);
		f.translateYProperty().set(box.getTranslateY());
		f.translateXProperty().set(-1.5/2);
		f.setRotationAxis(Rotate.Z_AXIS);
		f.setRotationAxis(Rotate.X_AXIS);
		f.setRotate(90);
		f.translateZProperty().set(box.getTranslateZ()-0.05);
		PhongMaterial m = new PhongMaterial();
		m.setDiffuseMap(new Image(getClass().getResourceAsStream("/resources/fan.png")));
		m.setDiffuseColor(Color.WHITE);
	    f.setMaterial(m);
		Group compressor=new Group();
	    compressor.getChildren().addAll(box,c,c1,box1,box2,box3,box4,box5,box6,f);
	    compressor.setRotationAxis(Rotate.Y_AXIS);
	    compressor.setRotate(deg);
	    if(i==2 || i==3) {
	    	compressor.translateZProperty().set(wall.getTranslateZ()-(compd+0.55+this.wall)/2*z);
	    	
	    }else if(i==0 || i ==1) {
	    	compressor.translateXProperty().set(wall.getTranslateX()-(compd+0.55+this.wall)/2*z);
	    }
	    compressor.setOnMouseClicked(e->{
	    	   btnevap.requestFocus();
	       });
	    
    	return  compressor;
    }
	private Box drawBox(int comp, double w, double h, double d, double x, double y, double z) {
		PhongMaterial material = new PhongMaterial();
		Box b=new Box(w,h,d);
		b.translateZProperty().set(z);
	    b.translateYProperty().set(y);
	    b.translateXProperty().set(x);
	    if(comp==1) {
			material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/black1.jpg")));
		}else if(comp==2) {
			material.setDiffuseColor(Color.valueOf("#888989"));
		}else{
			material.setDiffuseColor(Color.valueOf("#b0b2b4"));
		}
	    b.setMaterial(material);
		return b;
	}
    private Cylinder drawCylinder(int comp, double w, double y, double z, double deg) {
    	PhongMaterial material = new PhongMaterial();
    	Cylinder c=new Cylinder(0.5,w);
    	c.translateYProperty().set(y);
    	c.translateZProperty().set(z);
    	c.setRotationAxis(Rotate.X_AXIS);
	    c.setRotationAxis(Rotate.Z_AXIS);
		c.setRotate(deg);
		if(comp==1) {
			material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/black1.jpg")));
		}else {
			material.setDiffuseColor(Color.valueOf("#b0b2b4"));
		}
		c.setMaterial(material);
    	return c;
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
    	
    	
    	Box platform= prepareBox(roomWidth+15,roomLength+15,0.5,0,0,0,0);
		Box floor= prepareBox(roomWidth,roomLength,1.5,0,0.4,0,1);
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
		n2[0]=wall5;
		n2[1]=wall6;
		n2[2]=wall7;
		n2[3]=wall8;
		
		return new Node[] { platform,floor,wall1,wall2,wall3,wall4,wall5,wall6,wall7,wall8,wall9,wall10,wall11,wall12};
	}
	private Node[] prepareLightSource() {
		AmbientLight amLight= new AmbientLight();
		amLight.setColor(Color.valueOf("#424242"));
		PointLight pLight=new PointLight();
		pLight.setColor(Color.WHITE);
		pLight.getTransforms().add(new Translate(0,roomHieght*-1-10,0));
		PointLight pLight1=new PointLight();
		pLight1.setColor(Color.valueOf("#373737"));
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
