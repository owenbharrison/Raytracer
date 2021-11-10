package raytracing.shapes;

import raytracing.structs.*;

public class Sphere extends Shape{
	public double radius;

	public Sphere(Vec3D pos, double radius, Colour col, boolean reflective) {
		this.pos = pos.copy();
		this.radius = radius;
		this.col = col;
		this.reflective = reflective;
		this.shaded = true;
	}
	
	public double intersectRay(Ray r){
		double EPSILON = 0.0001f;
		Vec3D oc = Vec3D.sub(r.origin, this.pos);
		double a = Vec3D.dot(r.dir, r.dir);
		double b = 2f * Vec3D.dot(oc, r.dir);
		double c = Vec3D.dot(oc,oc) - this.radius*this.radius;
		double disc = b*b - 4f*a*c;
		if(disc < EPSILON)return -1;
		else{
			double sqrt = (double)Math.sqrt(disc);
			double num = -b - sqrt;
			if(num > EPSILON)return num/(2f*a);

			num = -b + sqrt;
			return num>EPSILON?num/(2f*a):-1f;
		}
	}
	
	public Intersection getIntersection(Ray r) {
		double intersectDist = this.intersectRay(r);
		if(intersectDist<0) return null;
		else {
		  Vec3D intersectPt = Vec3D.add(r.origin, Vec3D.mult(r.dir, intersectDist));
		  Vec3D intersectNormal = Vec3D.normalize(Vec3D.sub(intersectPt, this.pos));
		  return new Intersection(this, r, intersectDist, intersectPt, intersectNormal);
		}
	}
}
