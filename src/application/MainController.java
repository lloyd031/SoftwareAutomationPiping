package application;

import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Stack;

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
import javafx.scene.control.TitledPane;
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
	private double prevPointY=0;
	private double prevPointXZ=0;
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
    private PathNode start;
    private PathNode end;
    private Group flowline;
    private double width=50;
    private double height=20;
    private double length=70;
    private int selectedwall=2;
    private Box[] n;
    private Box[] n2;
    private PointLight acuL;
    private double acuw=10.5;
    private double acuh=3.2;
    private double acud=2.5;
    private double compw=8.7;
    private double comph=6.5 ;
    private double compd=3.3;
    private double wall=2.032/2;
    private double selectedcomp=0;
	@FXML
    private SubScene threeDModel;
	
	@FXML 
    private BorderPane borderPane;
	@FXML 
    private TextField txtlength,txtwidth,txtheight,txtacul,txtacut,txtacur,txtacub,txtcompl,txtcompw,txtcompd,txtcompt,txtcompr,txtcomph,txtcompb;
	@FXML 
    private TextField txtevapw,txtevaph,txtevapd;
	@FXML
    private Button btndim,btnevap,btncomp;
	@FXML
	private TitledPane acrdncomploc,acrdnaculoc;
	@FXML
	private Label lblcap,lblcap2,runbtn;
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
		camera.translateXProperty().set(-10);
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
        setAcuLoc();
        setCompLoc();
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
            System.out.println("acu : "+acu.getTranslateZ());
            addAcul(root3D);
            initializeLocationAcu();
            initAcuDragged();
            root3D.getChildren().addAll(acu,cmp);
        });
    	acrdncomploc.setOnMouseClicked(e->{
    		selectedcomp=1;
    	});
    	acrdnaculoc.setOnMouseClicked(e->{
    		selectedcomp=0;
    	});
    
        
    	initKeyPressed(root3D);
      
        runbtn.setOnMouseClicked(e->{
        	Run r=new Run();
        	double cap=r.capacityIntons(this.length/10, this.width/10);
        	lblcap.setText(String.format("%.5f",cap));
        	lblcap2.setText(String.format("%.5f",r.capacityInKW(cap)));
        });
        
    	
    }
    
    private void prepdragproperty(double x, double y, int sc, double tempx, double tempy) {
    	compdragged=true;
		selectedcomp=sc;
		anchorX=x;
		anchorY=y;
		this.prevPointY=y;
		this.prevPointXZ=x;
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
        	if(selectedcomp==0) {
        		
            	double newX=tempX-(anchorX-e.getSceneX())/10*i;
            	double newY=tempY-(anchorY-e.getSceneY())/10;
            	double leftBound=(selectedwall>1)?n[1].getTranslateX()+wall/2 + acuw/2:n[2].getTranslateZ()-wall/2 - acuw/2;
            	double rightBound=(selectedwall>1)?n[0].getTranslateX()-wall/2 - acuw/2:n[3].getTranslateZ()+wall/2 + acuw/2;
            	boolean conA=(selectedwall==2)?newX<rightBound:(selectedwall==3)?newX>leftBound:(selectedwall==0)?newX>rightBound:newX<leftBound;
            	boolean conB=(selectedwall==2)?acu.getTranslateX()>leftBound:(selectedwall==3)?acu.getTranslateX()<rightBound:(selectedwall==0)?acu.getTranslateZ()<leftBound:acu.getTranslateZ()>rightBound;
            	boolean conC=(selectedwall==2)?newX>leftBound:(selectedwall==3)?newX<rightBound:(selectedwall==0)?newX<leftBound:newX>rightBound;
            	boolean conD=(selectedwall==2)?acu.getTranslateX()<rightBound:(selectedwall==3)?acu.getTranslateX()>leftBound:(selectedwall==0)?acu.getTranslateZ()>rightBound:acu.getTranslateZ()<leftBound;
            		if(this.prevPointXZ>e.getSceneX() &&conA) {
            			if(conB) {
               			acuX.set(newX);
               		}else {
               			acuX.set((selectedwall==2||selectedwall==0)?leftBound:rightBound);
               		}
            		}else if(this.prevPointXZ<e.getSceneX() && conC) {
            			if(conD) {
               			acuX.set(newX);
               		}else {
               			acuX.set((selectedwall==2||selectedwall==0)?rightBound:leftBound);
               		}
            		}
            		
            	
            	
            	acu.translateYProperty().bind(acuY);
            	//removing the second condition sa main ifs will result a wierd snapping effect sa acu
            			if(this.prevPointY<e.getSceneY() && newY>n[selectedwall].getTranslateY()-height/2+acuh) {
            				if(acu.getTranslateY()<0) {
                        		acuY.set(newY);
            					}
                			else {
                				acuY.set(0);
                			}
            			}else if(this.prevPointY>e.getSceneY() && newY<0) {
            				if(acu.getTranslateY()>n[selectedwall].getTranslateY()-height/2+acuh) {
                        		acuY.set(newY);
            					}else {
            						acuY.set(n[selectedwall].getTranslateY()-height/2+acuh);
            					}
                			
            			}
            			
            	}
        	this.prevPointY=e.getSceneY();	
        	this.prevPointXZ=e.getSceneX();	
        	setAcuLoc();
        	});
    	cmp.setOnMouseDragged(e->{
    		//double leftBound=(selectedwall>1)?width/2+wall*2:length/-2-wall*2;
    		//double rightBound=(selectedwall>1)?width/-2 - wall*2:length/-2 - wall*2;
        	if(selectedcomp==1) {
        		
            	double newX=tempX+(anchorX-e.getSceneX())/10*i;
            	double newY=tempY-(anchorY-e.getSceneY())/10;
            	cmp.translateYProperty().bind(compY);
            	compX.set(newX);
            	compY.set(newY);
            	
            	
            	setCompLoc();
        	}
        	
        	});
    	
    	
    	acu.setOnMouseReleased(e->{
    		compdragged=false;
    		this.prevPointXZ=0;
    	});
    	cmp.setOnMouseReleased(e->{
    		compdragged=false;
    		this.prevPointXZ=0;
    	});
    }
   
    private void setAcuLoc() {
    	
		
			if(selectedwall > 1) {
    			txtacul.setText(String.format("%.3f",(selectedwall==2)?(width-acuw)/20 + acu.getTranslateX()/10:(width-acuw)/20 - acu.getTranslateX()/10));
    			txtacur.setText(String.format("%.3f",(selectedwall==3)?(width-acuw)/20 + acu.getTranslateX()/10:(width-acuw)/20 - acu.getTranslateX()/10 ));
    		}else {
    			txtacul.setText(String.format("%.3f",(selectedwall==1)?(length-acuw)/20 + acu.getTranslateZ()/10:(length-acuw)/20 - acu.getTranslateZ()/10));
    			txtacur.setText(String.format("%.3f",(selectedwall==0)?(length-acuw)/20 + acu.getTranslateZ()/10:(length-acuw)/20 - acu.getTranslateZ()/10 ));
    		}
    		txtacut.setText(String.format("%.2f",height/10 - Math.abs((acu.getTranslateY()-acuh)/10)));
    		txtacub.setText(String.format("%.2f",Math.abs(acu.getTranslateY()/10)));
    		
		
		/*
		 * double cux=(selectedwall==2 || selectedwall==3)?(acu.getTranslateX()-width/-2-acuw/2)/10:(acu.getTranslateZ()-length/-2-acuw/2)/10;
 	    txtevapx.setText(String.format("%.2f",cux));
 	    double cuy=(((Math.round((acu.getTranslateY()-3.2/2)*10.0)/10.0)-(Math.round(((height+0.75)/-2)*10.0)/10.0)))/10;
 	    
 	    txtevapy.setText(String.format("%.2f", cuy));*/
	
		
	
}
    private void setCompLoc() {
    		if(selectedwall > 1) {
    			txtcompr.setText(String.format("%.3f",(selectedwall==2)?(width+wall*4-compw)/20+cmp.getTranslateX()/10:(width+wall*4-compw)/20-cmp.getTranslateX()/10));
    			txtcompl.setText(String.format("%.3f",(selectedwall==3)?(width+wall*4-compw)/20+cmp.getTranslateX()/10:(width+wall*4-compw)/20-cmp.getTranslateX()/10));
    		}else {
    			txtcompr.setText(String.format("%.3f",(selectedwall==1)?(length+wall*4-compw)/20+cmp.getTranslateZ()/10:(length+wall*4-compw)/20-cmp.getTranslateZ()/10));
    			txtcompl.setText(String.format("%.3f",(selectedwall==0)?(length+wall*4-compw)/20+cmp.getTranslateZ()/10:(length+wall*4-compw)/20-cmp.getTranslateZ()/10));
    		}
			txtcompt.setText(String.format("%.2f",height/10 - Math.abs((cmp.getTranslateY()-comph)/10)));
    		txtcompb.setText(String.format("%.2f",Math.abs(cmp.getTranslateY()/10)));
}
    private void initializeLocationAcu()
    {
    	this.acuX=new SimpleDoubleProperty(0);
    	this.acuY=new SimpleDoubleProperty((height)/-2+acuh/2);
    	this.compX=new SimpleDoubleProperty(0);
    	this.compY=new SimpleDoubleProperty((height)/-2+comph/2);
    	setAcuLoc();
    	setCompLoc();
    }
    private void initlocation(DoubleProperty a,double bound, String val, int mul) {
    	try{a.set( bound + Double.parseDouble(val)*mul);
    	updateLoc();
    	}catch(Exception x) {
    		updateLoc();
		   }
    	}
    private void updateLoc() {
		if(selectedcomp==0) {
		    		setAcuLoc();
		   }else {
		    		setCompLoc();
		   }
    }
    private void loadModel(Group root3D) {
    	root3D.getChildren().addAll(drawRoom(width,height,length));
        root3D.getChildren().addAll(prepareLightSource());
        acu=setAcu();
        cmp=setCompressor();
        flowline=new Group();
        addAcul(root3D);
        root3D.getChildren().addAll(acu,cmp,flowline);
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
 		//this.floor.getTranslateY()-0.75-(acuh+1)/2
        material.setDiffuseColor(Color.valueOf("#b0b2b4"));
        Box box=drawBox(0,acuw,acuh-1,1,0,-(acuh/2),-(acud)/2 + 0.5);
	    Cylinder c=drawCylinder(0,acuw,box.getTranslateY()-box.getHeight()/2,box.getTranslateZ(),90);
		Cylinder c1=drawCylinder(0,acuw,box.getTranslateY()+box.getHeight()/2,box.getTranslateZ(),90);
		Box box1=drawBox(0,acuw,0.3,acud-0.5,0,(box.getTranslateY()-box.getHeight()/2-0.5)+0.3/2,box.getTranslateZ()+(acud-0.5)/2);
		Box box2=drawBox(0,acuw,0.3,acud-0.5,0,(box.getTranslateY()+box.getHeight()/2+0.5)-0.3/2,box.getTranslateZ()+(acud-0.5)/2);
		Box box3=drawBox(0,0.3,acuh,acud-0.5,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+(acud-0.5)/2);
		Box box4=drawBox(0,0.3,acuh,acud-0.5,(box.getTranslateX()+box.getWidth()/2)-0.3/2,box.getTranslateY(),box.getTranslateZ()+(acud-0.5)/2);
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
	    	acu.translateZProperty().set(wall.getTranslateZ()+((acud+this.wall)/2)*z);
	    }else if(i==0 || i ==1) {
	    	acu.translateXProperty().set(wall.getTranslateX()+((acud+this.wall)/2)*z);
	    }
	    acu.translateYProperty().set((height)/-2 + (acuh)/2);
	    box3.setOnMouseClicked(e->{
	    	if(this.start==null) {
	    		
	    		Pipe p=new Pipe(this.start, this.end);
	    	}
	    });
	    return  acu;
    }
   	private Group setCompressor() {
    	int i=selectedwall;
    	Node wall=this.n2[i];
    	int z=(i==2 || i==0)?-1:1;
    	int deg=(i==2)?180:(i==3)?0:(i==0)?270:90;
        Box box=drawBox(1,compw,comph-1,1,0,-(comph/2),-compd/2 + 0.5);
        Cylinder c=drawCylinder(1,compw,box.getTranslateY()-box.getHeight()/2,box.getTranslateZ(),90);
		Cylinder c1=drawCylinder(1,compw,box.getTranslateY()+box.getHeight()/2,box.getTranslateZ(),90);
		Box box1=drawBox(1,compw,0.3,compd-0.5,0,(box.getTranslateY()-box.getHeight()/2-0.5)+0.3/2,box.getTranslateZ()+(compd-0.5)/2);
		Box box2=drawBox(1,compw,0.3,compd-0.5,0,(box.getTranslateY()+box.getHeight()/2+0.5)-0.3/2,box.getTranslateZ()+(compd-0.5)/2);
		Box box3=drawBox(0,0.3,comph-0.5,compd-0.5,(box.getTranslateX()-box.getWidth()/2)+0.3,box.getTranslateY(),box.getTranslateZ()+(compd-0.5)/2);
		Box box4=drawBox(1,0.3,comph,compd-0.5,(box.getTranslateX()+box.getWidth()/2)-0.3/2,box.getTranslateY(),box.getTranslateZ()+(compd-0.5)/2);
		Box box5=drawBox(1,0.3,comph-0.5,0.5,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+(compd-0.5)-0.25);
		Box box6=drawBox(1,0.3,comph-0.5,0.5,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+(compd-0.5)/2);
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
	    	compressor.translateZProperty().set(wall.getTranslateZ()-(compd+this.wall)/2*z);
	    	
	    }else if(i==0 || i ==1) {
	    	compressor.translateXProperty().set(wall.getTranslateX()-(compd+this.wall)/2*z);
	    }
	    compressor.translateYProperty().set((height)/-2 + acuh);
	    box4.setOnMouseClicked(e->{
	    	if(this.start==null) {
	    		int mul1=(selectedwall==2|| selectedwall==0)?1:-1;
	    		double x1=(selectedwall>1)?compressor.getTranslateX()+(Double.parseDouble(txtcompw.getText())*10)/2*-mul1:n2[selectedwall].getTranslateX()+this.wall*mul1;
	    		double z1=(selectedwall>1)?n2[selectedwall].getTranslateZ()+this.wall*mul1:compressor.getTranslateZ()+(Double.parseDouble(txtcompw.getText())*10)/2*mul1;
	    		double x2=(selectedwall>1)?acu.getTranslateX()+(Double.parseDouble(txtevapw.getText())*10)/2*-mul1:n[selectedwall].getTranslateX()+this.wall*-mul1;
	    		double z2=(selectedwall>1)?n[selectedwall].getTranslateZ()+this.wall*-mul1:acu.getTranslateZ()+(Double.parseDouble(txtevapw.getText())*10)/2*mul1;
	    		this.start=new PathNode(x1,compressor.getTranslateY()-1,z1);
	    		this.end=new PathNode(x2,acu.getTranslateY()-1,z2);
	    		
	    		/**
	    		 * Cylinder a=new Cylinder(0.25,1);
	    		a.translateZProperty().set(compressor.getTranslateZ());
	    		a.translateYProperty().set(compressor.getTranslateY()-1);
	    		a.translateXProperty().set(compressor.getTranslateX()-(Double.parseDouble(txtcompw.getText())*10)/2-0.5);
	    		a.setRotationAxis(Rotate.Z_AXIS); 
	    		a.setRotate(90);
	    		this.flowline.getChildren().add(a);
	    		Cylinder a=new Cylinder(0.25,1);
	    		a.translateZProperty().set(acu.getTranslateZ());
	    		a.translateYProperty().set(acu.getTranslateY()-1);
	    		a.translateXProperty().set(acu.getTranslateX()-(Double.parseDouble(txtevapw.getText())*10)/2-0.5);
	    		a.setRotationAxis(Rotate.Z_AXIS); 
	    		a.setRotate(90);
	    		this.flowline.getChildren().add(a);
	    		 */
	    		
	    		Pipe p=new Pipe(this.start, this.end);
	    		Stack<PathNode> k=p.getPath();
	    		PathNode path[]=new PathNode[p.getPath().size()];
	    		for(int j=0; j<path.length; j++) {
	    				path[j]=k.pop();
	    		}
	    		for(int j=path.length-1; j>=0; j--) {
	    			if(j!=0) {
	    				if(path[j].getZ()<path[j-1].getZ() || path[j].getZ()>path[j-1].getZ()){
	    					double w=path[j-1].getZ()-path[j].getZ();
	    					Cylinder pathpoint=createpn(Math.abs(w),path[j].getX(),path[j].getY(), path[j].getZ()+w/2,90);
			    			pathpoint.setRotationAxis(Rotate.X_AXIS);
			    			this.flowline.getChildren().add(pathpoint);// System.out.println(path[j].getX()+"------ "+path[j].getY()+"------ "+path[j].getZ());
	    				}
	    				if(path[j].getY()>path[j-1].getY() || path[j].getY()<path[j-1].getY()){
	    					double w=path[j].getY()-path[j-1].getY();
	    					Cylinder pathpoint=createpn(Math.abs(w),path[j].getX(),path[j].getY()-w/2, path[j].getZ(),0);
			    			this.flowline.getChildren().add(pathpoint);// System.out.println(path[j].getX()+"------ "+path[j].getY()+"------ "+path[j].getZ());
	    				}
	    				if(path[j].getX()>path[j-1].getX() || path[j].getX()<path[j-1].getX()){
	    					double w=path[j].getX()-path[j-1].getX();
	    					Cylinder pathpoint=createpn(Math.abs(w),path[j].getX()-w/2,path[j].getY(), path[j].getZ(),90);
	    					pathpoint.setRotationAxis(Rotate.Z_AXIS);
			    			this.flowline.getChildren().add(pathpoint);// System.out.println(path[j].getX()+"------ "+path[j].getY()+"------ "+path[j].getZ());
	    				}
	    			}else {
	    				if(path[0].getX()>path[1].getX() || path[0].getX()>path[1].getX()){
	    					double w=path[0].getX()-path[1].getX();
	    					Cylinder pathpoint=createpn(Math.abs(w),path[j].getX()-w/2,path[j].getY(), path[j].getZ(),90);
			    			pathpoint.setRotationAxis(Rotate.Z_AXIS);
			    			this.flowline.getChildren().add(pathpoint);// System.out.println(path[j].getX()+"------ "+path[j].getY()+"------ "+path[j].getZ());
	    				}
	    			}
    				
	    		}
	    		
	    	}
	    });
	    return  compressor;
    }
   	public Cylinder createpn(double w, double x, double y, double z, int deg) {
   		Cylinder pathpoint=new Cylinder(0.25,w);
		pathpoint.translateXProperty().set(x);
		pathpoint.translateYProperty().set(y);
		pathpoint.translateZProperty().set(z);
		pathpoint.setRotate(deg);
   		return pathpoint;
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
    	
    	
    	Box platform= prepareBox(roomWidth+10,roomLength+10,0.5,0,0.76,0,0);
		Box floor= prepareBox(roomWidth,roomLength,1.5,0,0.75,0,1);
		Box wall1= prepareBox(wall,roomLength+wall*2,roomHieght,(roomWidth/2)+wall/2,roomHieght/2*-1,0,0);
		Box wall2= prepareBox(wall,roomLength+wall*2,roomHieght,(roomWidth/2*-1)-wall/2,roomHieght/2*-1,0,0);
		Box wall3= prepareBox(roomWidth,wall,roomHieght,0,roomHieght/2*-1,roomLength/2+wall/2,0);
		Box wall4= prepareBox(roomWidth,wall,roomHieght,0,roomHieght/2*-1,roomLength/-2-wall/2,0);
		Box wall5= prepareBox(wall,roomLength+wall*4,roomHieght,wall1.getTranslateX()+wall,roomHieght/2*-1,0,2);
		Box wall6= prepareBox(wall,roomLength+wall*4,roomHieght,wall2.getTranslateX()-wall,roomHieght/2*-1,0,2);
		Box wall7= prepareBox(roomWidth+wall*4,wall,roomHieght,0,roomHieght/2*-1,wall3.getTranslateZ()+wall,2);
		Box wall8= prepareBox(roomWidth+wall*4,wall,roomHieght,0,roomHieght/2*-1,wall4.getTranslateZ()-wall,2);
		Box wall9= prepareBox(wall,roomLength+wall*2,0.1,(roomWidth/2)+wall/2,roomHieght*-1,0,2);
		Box wall10= prepareBox(wall,roomLength+wall*2,0.1,(roomWidth/2*-1)-wall/2,roomHieght*-1,0,2);
		Box wall11=prepareBox(roomWidth,wall,0.1,0,roomHieght*-1,roomLength/2+wall/2,2);
		Box wall12= prepareBox(roomWidth,wall,0.1,0,roomHieght*-1,roomLength/-2-wall/2,2);
		
		Box[] wall= { wall1,wall2,wall3,wall4,wall5,wall6,wall7,wall8,wall9,wall10,wall11,wall12,platform,floor};
		for(int i=0; i<4; i++) {
			n[i]=wall[i];
			n2[i]=wall[i+4];
		}

		return wall;
	}
	private Node[] prepareLightSource() {
		AmbientLight amLight= new AmbientLight();
		amLight.setColor(Color.valueOf("#424242"));
		PointLight pLight=new PointLight();
		pLight.setColor(Color.WHITE);
		pLight.getTransforms().add(new Translate(0,roomHieght*-1-7,0));
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
	private void initKeyPressed(Group g) {
		txtacul.setOnKeyPressed(e->{
	    	   double boundL=(selectedwall==2)?(width-acuw)/-2:(selectedwall==3)?(width-acuw)/2:(selectedwall==0)?(length-acuw)/2:(length-acuw)/-2;
	    	   int mul=(selectedwall==2 || selectedwall==1)?10:-10;
	    	   switch(e.getCode()) {
	     	   case ENTER:
	     		   if(txtacul.getText().equals("center")) {
	     			  acuX.set(0);
	     			  setAcuLoc();
	     		   }else {
	     			initlocation(acuX, boundL,txtacul.getText(),mul);
	     			 }
	     		   break;
	     	    default:
	     	    	break;
	     	   }
	        });
	     txtacur.setOnKeyPressed(e->{
	    	 	double boundR=(selectedwall==2)?(width-acuw)/2:(selectedwall==3)?(width-acuw)/-2:(selectedwall==0)?(length-acuw)/-2:(length-acuw)/2;
	    	 	int mul=(selectedwall==2 || selectedwall==1)?-10:10;
	    	 	switch(e.getCode()) {
	     	   case ENTER:
	     		  if(txtacur.getText().equals("center"))  {
	     			  acuX.set(0);
	     			  setAcuLoc();
	     		  }else {
	     				initlocation(acuX, boundR,txtacur.getText(),mul);
	     			}
	     		   
	     		   break;
	     	    default:
	     	    	break;
	     	   }
	        });
	     txtacub.setOnKeyPressed(e->{
	    		switch(e.getCode()) {
	        	   case ENTER:
	        		   
	        		   if(txtacub.getText().equals("center"))  {
	        			   acu.translateYProperty().bind(acuY);
	        			   acuY.set((height)/-2+acuh/2);
	        			   setAcuLoc();
	        		   }else {
	        			   acu.translateYProperty().bind(acuY);
	        			   initlocation(acuY, 0,txtacub.getText(),-10);
	        				}
	        		 break;
	        	    default:
	        	    	break;
	        	   }
	      	   
	         });
	    	
	    	txtacut.setOnKeyPressed(e->{
		      	 switch(e.getCode()) {
	      	   case ENTER:
	      		 if(txtacut.getText().equals("center")) {
	      			acu.translateYProperty().bind(acuY);
	         		 acuY.set((height)/-2+acuh/2);
	         		 setAcuLoc();
	      		 }else {
	      			 	 acu.translateYProperty().bind(acuY);
	        		     initlocation(acuY, -height + acuh,txtacut.getText(),10);
	      				 }
	      		 break;
	      	    default:
	      	    	break;
	      	   
	      	   }
	         });
	    	
	    	txtevapw.setOnKeyPressed(e->{
			      switch(e.getCode()) {
		      	   case ENTER:
		      		 
		      			 try {
		      				 
		      				 this.acuw=Double.parseDouble(txtevapw.getText())*10;
		      				 g.getChildren().remove(this.acu);
		      				 this.acu=setAcu();
		      				 g.getChildren().add(this.acu);
		      				initAcuDragged();
		      			 }
		      			     
		      			  catch(Exception x) {
		        		    	setAcuLoc();
		        		    };
		      			break;
		      	    default:
		      	    	break;
		      	   }
		         });
		    	txtevaph.setOnKeyPressed(e->{
			      	 switch(e.getCode()) {
		      	   case ENTER:
		      		 try {
		      				 this.acuh=Double.parseDouble(txtevaph.getText())*10;
		      				 g.getChildren().remove(this.acu);
		      				 this.acu=setAcu();
		      				 g.getChildren().add(this.acu);
		      				initAcuDragged();
		      			 }
		      			catch(Exception x) {
		        		    	setAcuLoc();
		        		    };
		      		break;
		      	    default:
		      	    	break;
		      	   
		      	   }
		         });
		    	
		    	txtevapd.setOnKeyPressed(e->{
		      		 switch(e.getCode()) {
		      	   case ENTER:
		      		 try {
		      				 this.acud=Double.parseDouble(txtevapd.getText())*10;
		      				 g.getChildren().remove(this.acu);
		      				 this.acu=setAcu();
		      				 g.getChildren().add(this.acu);
		      				initAcuDragged();
		      			 }
		      			catch(Exception x) {
		        		    	setAcuLoc();
		        		    };
		      		   break;
		      	    default:
		      	    	break;
		      	   }
		         });
		    	
		    	
		    	txtcompw.setOnKeyPressed(e->{
				      switch(e.getCode()) {
			      	   case ENTER:
			      		 
			      			 try {
			      				 this.compw=Double.parseDouble(txtcompw.getText())*10;
			      				 g.getChildren().remove(this.cmp);
			      				 this.cmp=setCompressor();
			      				 g.getChildren().add(this.cmp);
			      				initAcuDragged();
			      			 }
			      			  catch(Exception x) {
			      				setCompLoc();
			        		    };
			      			break;
			      	    default:
			      	    	break;
			      	   }
			         });
			    	txtcomph.setOnKeyPressed(e->{
				      	 switch(e.getCode()) {
			      	   case ENTER:
			      		 try {
			      				 this.comph=Double.parseDouble(txtcomph.getText())*10;
			      				 g.getChildren().remove(this.cmp);
			      				 this.cmp=setCompressor();
			      				 g.getChildren().add(this.cmp);
			      				initAcuDragged();
			      			 }
			      			catch(Exception x) {
			      				setCompLoc();
			        		    };
			      		break;
			      	    default:
			      	    	break;
			      	   }
			         });
			    	
			    	txtcompd.setOnKeyPressed(e->{
			      		 switch(e.getCode()) {
			      	   case ENTER:
			      		 try {
			      				 this.compd=Double.parseDouble(txtcompd.getText())*10;
			      				 g.getChildren().remove(this.cmp);
			      				 this.cmp=setCompressor();
			      				 g.getChildren().add(this.cmp);
			      				initAcuDragged();
			      			 }
			      			catch(Exception x) {
			      				setCompLoc();
			        		    };
			      		   break;
			      	    default:
			      	    	break;
			      	   }
			         });
			    	txtcompb.setOnKeyPressed(e->{
			    		switch(e.getCode()) {
			        	   case ENTER:
			        		   
			        		   if(txtcompb.getText().equals("center"))  {
			        			   cmp.translateYProperty().bind(compY);
			        			   compY.set((height)/-2+comph/2);
			        			   setCompLoc();
			        		   }else {
			        			   cmp.translateYProperty().bind(compY);
			        			   initlocation(compY, 0,txtcompb.getText(),-10);
			        			}
			        		 break;
			        	    default:
			        	    	break;
			        	   }
			      	   
			         });
			    	
			    	txtcompt.setOnKeyPressed(e->{
				      	 switch(e.getCode()) {
			      	   case ENTER:
			      		 if(txtcompt.getText().equals("center")) {
			      			cmp.translateYProperty().bind(compY);
			      			compY.set((height)/-2+comph/2);
			         		 setCompLoc();
			      		 }else {
			      			 cmp.translateYProperty().bind(compY);
			        		 initlocation(compY, -height + comph,txtcompt.getText(),10);
			      			 }
			      		 break;
			      	    default:
			      	    	break;
			      	   }
			         });
			    	txtcompl.setOnKeyPressed(e->{
				    	   double boundL=(selectedwall==2)?(width+wall*4-compw)/2:(selectedwall==3)?(width+wall*4-compw)/-2:(selectedwall==0)?(length+wall*4-compw)/-2:(length+wall*4-compw)/2;
				     	   int mul=(selectedwall==2 || selectedwall==1)?-10:10;
				    	   switch(e.getCode()) {
				     	   case ENTER:
				     		   if(txtcompl.getText().equals("center")) {
				     			  compX.set(0);
				     			  setCompLoc();
				     		   }else {
				     			  initlocation(compX, boundL,txtcompl.getText(),mul);
				     		   }
				     		   break;
				     	    default:
				     	    	break;
				     	   }
				        });
			    	
			    	txtcompr.setOnKeyPressed(e->{
				    	   double boundR=(selectedwall==2)?(width+wall*4-compw)/-2:(selectedwall==3)?(width+wall*4-compw)/2:(selectedwall==0)?(length+wall*4-compw)/2:(length+wall*4-compw)/-2;
				    	   int mul=(selectedwall==2 || selectedwall==1)?10:-10;
				    	   switch(e.getCode()) {
				     	   case ENTER:
				     		   if(txtcompr.getText().equals("center")) {
				     			  compX.set(0);
				     			  setCompLoc();
				     		   }else {
				     			   initlocation(compX, boundR,txtcompr.getText(),mul);
				     			}
				     		   break;
				     	    default:
				     	    	break;
				     	   }
				        });
			    	
			    	
	}
}
