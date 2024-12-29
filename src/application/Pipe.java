package application;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Pipe {
	private Stack<PathNode> pnList=new Stack<PathNode>();
	private Queue<PathNode> queue=new LinkedList<PathNode>();
	private LinkedList<PathNode> visited=new LinkedList<PathNode>();
	private PathNode start;
	private PathNode end;
	private Block as;
	int selectedwall=0;
	public Pipe(PathNode start, PathNode end,int selectedwall) {
		this.start=start;
		this.end=end;
		this.selectedwall=selectedwall;
		BFS(this.start);
	}
	
	public void addNeighbor(PathNode a) {
		double adder=100;
		double adderX1=defNxtPnt(a.getX(),adder,end.getX(),0,1);
		double adderX2=defNxtPnt(a.getX(),adder,end.getX(),1,0);
		double adderZ1=defNxtPnt(a.getZ(),adder,end.getZ(),0,0);
		double adderZ2=defNxtPnt(a.getZ(),adder,end.getZ(),1,0);
		double adderY1=defNxtPnt(a.getY(),adder, end.getY(),0,0);
		double adderY2=defNxtPnt(a.getY(),adder,end.getY(),1,0);
		
		
		if(this.selectedwall<2) {
			if(!validate(new PathNode(a.getX()-adderX1,a.getY(),a.getZ()) )) {
				createNode(a,new PathNode(a.getX()-adderX1,a.getY(),a.getZ()));
			}
			if(!validate(new PathNode(a.getX()+adderX2,a.getY(),a.getZ()))) {
				createNode(a,new PathNode(a.getX()+adderX2,a.getY(),a.getZ()));
			}
			if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()-adderZ1)) ) {
				createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()-adderZ1));
			}
			if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()+adderZ2)) ) {
				createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()+adderZ2));
			}
		}else {
			if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()-adderZ1)) ) {
				createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()-adderZ1));
			}
			if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()+adderZ2)) ) {
				createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()+adderZ2));
			}
			if(!validate(new PathNode(a.getX()-adderX1,a.getY(),a.getZ()) )) {
				createNode(a,new PathNode(a.getX()-adderX1,a.getY(),a.getZ()));
			}
			if(!validate(new PathNode(a.getX()+adderX2,a.getY(),a.getZ()))) {
				createNode(a,new PathNode(a.getX()+adderX2,a.getY(),a.getZ()));
			}
		}
		if(!validate(new PathNode(a.getX(),a.getY()-adderY1,a.getZ())) ) {
			createNode(a,new PathNode(a.getX(),a.getY()-adderY1,a.getZ()));
		}
		if(!validate(new PathNode(a.getX(),a.getY()+adderY2,a.getZ()))) {
			createNode(a,new PathNode(a.getX(),a.getY()+adderY2,a.getZ()));
		}
		
	}
	public boolean inBlock(PathNode a) {
		boolean res=false;
		if(a.getX()>=as.x-as.w/2 && a.getX()<=as.x+as.w/2 && a.getY()>=as.y-as.h/2 && a.getY()<=as.y+as.h/2)
		{
			res=true;
			
		}else {
			res=false;
		}
		return res;
	}
	public Block detectBlock(double a, int op) {
		
		 Block res=null;
		
		return res;
	}
	public double defNxtPnt(double a, double b, double c, int op, int f) {
		double adder=0;
		if(op==0) {
			if(a>c) {
				adder=(a-b<c)?a-c:b;
			}else {
				adder=b;
			}
			
		}else {
			if(a<c) {
				adder=(a+b<c)?b:c-a ;
				
			}else {
				adder=b;
			}
		}
		
		return adder;
	}
	
	public void createNode(PathNode p, PathNode ch) {
		ch.setParent(p);
		queue.add(ch);
	}

	public void BFS(PathNode a) {
		visited.add(a);
		addNeighbor(a);
		if(a.getX()==end.getX() && a.getY()==end.getY() && a.getZ()==end.getZ()) {
			trackPath(a);
		}else {
			BFS(queue.poll());
		
		}
	}
	public void trackPath(PathNode a) {
		pnList.push(a);
		if(a.getParent()!=null) {
			trackPath(a.getParent());
		}
	}
	public boolean validate(PathNode a) {
		boolean res=false;
		for(PathNode x:queue)
		{
			if(a.getX()==x.getX() && a.getY()==x.getY() && a.getZ()==x.getZ() )
			{
				res=true;
			}
		}
		
		for(PathNode x:visited)
		{
			if(a.getX()==x.getX() && a.getY()==x.getY() && a.getZ()==x.getZ())
			{
				res=true;
			}
		}
		
		
		return res;
		
	}

	public Stack<PathNode> getPath() {
		
		return this.pnList;
	}
}

