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
	int pid=0;
	double adderX,adderY,adderZ;
	public Pipe(PathNode start, PathNode end) {
		this.start=start;
		this.end=end;
		adderX=(start.getX()>end.getX())?(start.getX()-end.getX()):(end.getX()-start.getX());
		adderY=(start.getY()>end.getY())?(start.getY()-end.getY()):(end.getY()-start.getY());
		adderZ=(start.getZ()>end.getZ())?(start.getZ()-end.getZ()):(end.getZ()-start.getZ());
		BFS(start);
	}
	public void addNeighbor(PathNode a) {
		if(!validate(new PathNode(a.getX()-adderX,a.getY(),a.getZ()))) {
			createNode(a,new PathNode(a.getX()-adderX,a.getY(),a.getZ()));
		}
		if(!validate(new PathNode(a.getX()+adderX,a.getY(),a.getZ()))) {
			createNode(a,new PathNode(a.getX()+adderX,a.getY(),a.getZ()));
		}
		if(!validate(new PathNode(a.getX(),a.getY()-adderY,a.getZ()))) {
			createNode(a,new PathNode(a.getX(),a.getY()-adderY,a.getZ()));
		}
		if(!validate(new PathNode(a.getX(),a.getY()+adderY,a.getZ()))) {
			createNode(a,new PathNode(a.getX(),a.getY()+adderY,a.getZ()));
		}
		if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()-adderZ))) {
			createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()-adderZ));
		}
		if(!validate(new PathNode(a.getX(),a.getY(),a.getZ()+adderZ))) {
			createNode(a,new PathNode(a.getX(),a.getY(),a.getZ()+adderZ));
		}
		pid++;
	}
	public void createNode(PathNode p, PathNode ch) {
		ch.setParent(p);
		p.id=pid;
		//System.out.println(ch.getX() +" "+ch.getY() + " " +ch.getZ());
		queue.add(ch);
	}
	public void BFS(PathNode a) {
		//System.out.println("q l  "+queue.size());
		visited.add(a);
		addNeighbor(a);
		if(a.getX()==end.getX() && a.getY()==end.getY() && a.getZ()==end.getZ()) {
			System.out.println("a "+a.getX()+" - "+a.getY()+" - "+a.getZ());
			System.out.println("e "+end.getX()+" - "+end.getY()+" - "+end.getZ());
			trackPath(a);
		}else {
			//System.out.println("eeeeeeeeeeeeee " + a.getParent().id);
			
			BFS(queue.poll());
		
		}
	}
	public void trackPath(PathNode a) {
		pnList.push(a);
		if(a.getX()==start.getX() && a.getY()==start.getY() && a.getZ()==start.getZ()) {
			//System.out.println("----------");
			//System.out.println("dsfddsf fff "+pnList.size());
		}else {
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

