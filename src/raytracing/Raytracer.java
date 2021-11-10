package raytracing;

import java.awt.Dimension;
import java.awt.Graphics2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import raytracing.structs.*;
import raytracing.shapes.*;

import io.github.owenbharrison.displib.Display;
import io.github.owenbharrison.displib.maths.Maths;

public class Raytracer extends Display{
	private static final long serialVersionUID = -5815685366474883535L;
	
	public static final int MAX_REFLECTIONS = 3;
	public int resolution = 4;
	public int cols, rows;
	
	public double fov;
	public double camYaw;//left-right
	public double camPitch;//up-down
	public Vec3D sunPos, camPos;
	
	public Texture skyboxTexture;
	public double mouseSensitivity = 0.5;
	
	public ArrayList<Shape> shapes;
	
	public static void main(String[] args){
		Raytracer raytracer = new Raytracer();
		raytracer.setPreferredSize(new Dimension(1000, 500));
		raytracer.start();
	}
	
	@Override
	public void setup() {
		cols = width/resolution;
		rows = height/resolution;
		
		Arrays.fill(KEYS, false);
		
		fov = 45.0;
		camYaw = Math.PI/4.0;
		camPitch = Math.PI / 1.5;
		camPos = new Vec3D(10.0, 10.0, 10.0);
		sunPos = new Vec3D(0.0, 40.0, 0.0);
		
		shapes = new ArrayList<Shape>();
		
		double d = 10.0;
		for(int i=0;i<14;i++){
			Vec3D randomPos = new Vec3D(
					Maths.map(Math.random(), 0.0, 1.0, -d, d),
				Maths.map(Math.random(), 0.0, 1.0, -d, d),
				Maths.map(Math.random(), 0.0, 1.0, -d, d)
			);
			double radius = Maths.map(Math.random(), 0.0, 1.0, 0.5, 4.0);
			boolean reflective = Math.random() > 0.25;
			shapes.add(new Sphere(randomPos, radius, makeRandomColor(), reflective));
		}
//		Mesh cube = new Mesh(Raytracer.class.getResourceAsStream("resources/teapot2.obj"));
//		shapes.add(new Triangle(new Vec3D(1.0, 0.0, 0.0), new Vec3D(0.0, 1.0, 0.0), new Vec3D(0.0, 0.0, 1.0), Color.WHITE, true));
//		shapes.add(cube);
//		for(Triangle t:cube.tris) {
//			System.out.println(t.toString());
//		}
		
		//shapes.add(new Plane(new Vec3D(0.0, 0.0, -15.0), new Vec3D(0.0, 1.0, 0.0), new Color(0.0, 0.0, 255.0), true));
		skyboxTexture = new Texture(Raytracer.class.getResourceAsStream("resources/skybox.jpg"));
	}
	
	@Override
	public void update(double dt) {
		if(mouseScroll!=0) {
			resolution-=mouseScroll;
			resolution = (int)Maths.clamp(resolution, 1, 20);
		}
		
		double upDownSign = (KEYS[32]?1.0:KEYS[16]?-1.0:0.0);
		camPos.y += upDownSign*8.0*dt;

		Vec3D forwardBackDir = new Vec3D(Math.cos(camYaw),0.0,Math.sin(camYaw));
		Vec3D forwardBackVec = Vec3D.mult(forwardBackDir,14.0*dt);
		double forwardBackSign = (KEYS[87]?1.0:KEYS[83]?-1.0:0.0);
		camPos.add(Vec3D.mult(forwardBackVec, forwardBackSign));
		
		Vec3D leftRightDir = new Vec3D(Math.cos(camYaw+Math.PI/2.0),0.0,Math.sin(camYaw+Math.PI/2.0));
		Vec3D leftRightVec = Vec3D.mult(leftRightDir, 14.0*dt);
		double leftRightSign = (KEYS[65]?1.0:KEYS[68]?-1.0:0.0);
		camPos.add(Vec3D.mult(leftRightVec, leftRightSign));
		
		//change view direction
		if (leftMouseButton) {//if holding mouse down, update camera directionals
			camYaw += (double)(prevMouseX-mouseX)*mouseSensitivity*dt;
			camPitch -= (double)(prevMouseY-mouseY)*mouseSensitivity*dt;
		}

		sunPos = KEYS[13]?camPos.copy():sunPos;

		double bfr = 0.000001;
		camPitch = Maths.clamp(camPitch, bfr, Math.PI - bfr);
		camYaw = camYaw % (Math.PI * 2.0);//make it wrap back to always be between 0.0 and 2.0*PI
		
		setTitle("Raytracer @ "+getFps()+"fps");
	}
	
	public Colour solveRay(Ray eyeRay, int numBounces){
		ArrayList<Intersection> hits = new ArrayList<Intersection>();
		for(Shape s:this.shapes) {
			Intersection hit = s.getIntersection(eyeRay);
			if (hit != null) hits.add(hit);
		}

		Collections.sort(hits, new Comparator<Intersection>(){
			public int compare(Intersection a, Intersection b) {
				if(a.dist>b.dist)return 1;
				else if(a.dist<b.dist)return -1;
				else return (int)b.dist-(int)a.dist;
			}
		});
		
		if (hits.size() > 0.0) {
			Intersection hit = hits.get(0);
			Vec3D sunDir = Vec3D.sub(sunPos, hit.pos).normalize();
			Ray shadowRay = new Ray(hit.pos, sunDir);
			boolean castShadow = false;
			for(Shape s:shapes) {
				if (s!=hit.shape&&s.intersectRay(shadowRay)>0.0) {
					castShadow = true;
				}
			}

			Colour col = hit.shape.getCol();
			if (hit.shape.isReflective() && numBounces < Raytracer.MAX_REFLECTIONS) {//allowed to reflect
				Vec3D reflDir = Vec3D.computeReflect(eyeRay.dir, hit.normal);//get dir
				Colour reflCol = solveRay(new Ray(hit.pos, reflDir), numBounces+1);//get col of refection
				col = new Colour(//80% of reflection and 20% of original color
					(int)(col.r*0.2+reflCol.r*0.8),
					(int)(col.g*0.2+reflCol.g*0.8),
					(int)(col.b*0.2+reflCol.b*0.8)
				);
			}
			double amt = hit.shape.isShaded()?Vec3D.dot(hit.normal,sunDir):1.0;//if no shading make it full bright
			return multiplyColor(col, amt*(castShadow?0.5:1.0));//shade by normal and shadow
		}
		else {
			double[] uv = Vec3D.dirToUV(eyeRay.dir);
			return skyboxTexture.getColorAtUV(uv[0], uv[1]);//get color of skybox
		}
	}
	
	@Override
	public void draw(Graphics2D g) {
		background(g, 0);
		
		cols = width/resolution;
		rows = height/resolution;
		
		//made using https://en.wikipedia.org/wiki/Ray_tracing_(graphics)#Algorithm_overview
		Vec3D target = new Vec3D(
			Math.cos(camYaw)*Math.sin(camPitch),
			Math.cos(camPitch),
			Math.sin(camYaw)*Math.sin(camPitch)
		).add(camPos);//target
		double d = 1.0;//eye to viewport dist
		Vec3D v = new Vec3D(0.0, 1.0, 0.0);//up dir
		
		Vec3D t = Vec3D.sub(target, camPos);//dir
		Vec3D b = Vec3D.cross(v, t);//pointing right
		Vec3D tn = Vec3D.normalize(t);//dir normalized
		Vec3D bn = Vec3D.normalize(b);//pointing right normalized
		Vec3D vn = Vec3D.cross(tn, bn);//up dir normalized
		
		double gx = d*Math.tan(fov/2.0);//calc viewport width
		double gy = gx*((rows-1.0)/cols-1.0);//calc viewport height
		Vec3D qx = Vec3D.mult(bn, 2.0*gx/(cols-1.0));//step width vec
		Vec3D qy = Vec3D.mult(vn, 2.0*gy/(rows-1.0));//step height vec
		
		Vec3D p1m = Vec3D.sub(Vec3D.sub(Vec3D.mult(tn, d), Vec3D.mult(bn, gx)), Vec3D.mult(vn, gy));	 //left bottom
		for (int i=0;i<cols;i++) {
			for (int j=0;j<rows;j++) {
				Vec3D pij = Vec3D.mult(qx,i).add(Vec3D.mult(qy,j)).add(p1m).normalize();
				Ray r = new Ray(camPos, pij);
				Colour col = solveRay(r, 0);
				stroke(g, col.r, col.g, col.b);
				g.fillRect(i*resolution, j*resolution, resolution, resolution);
			}
		}
	}
	
	public Colour multiplyColor(Colour c, double d) {
		return new Colour(
				(int)Maths.clamp(c.r*d, 0.0, 255.0),
				(int)Maths.clamp(c.g*d, 0.0, 255.0),
				(int)Maths.clamp(c.b*d, 0.0, 255.0)
		);
	}

	public Colour makeRandomColor() {
		int b1 = (int)Maths.map(Math.random(), 0.0, 1.0, 0.0, 255.0);
		int s1 = (int)Maths.map(Math.random(), 0.0, 1.0, 0.0, 80.0);
		int s2 = (int)Maths.map(Math.random(), 0.0, 1.0, 0.0, 80.0);
		double randChoice = Math.random();
		if (randChoice < 0.3333) {
			return new Colour(b1, s1, s2);
		}else if (randChoice < 0.6667) {
			return new Colour(s2, b1, s1);
		}else {
			return new Colour(s1, s2, b1);
		}
	}
}
