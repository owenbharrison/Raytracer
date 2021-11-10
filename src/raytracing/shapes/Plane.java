package raytracing.shapes;

import raytracing.structs.*;

public class Plane extends Shape{
	public Vec3D normal;

	public Plane(Vec3D pos, Vec3D normal, Colour col, boolean reflective){
		this.pos = pos;
		this.normal = Vec3D.normalize(normal);
		this.col = col;
		this.reflective = reflective;
		this.shaded = false;
	}
	
	public double intersectRay(Ray r){
		double EPSILON = 0.0001f;
		double denom = Vec3D.dot(this.normal, r.dir);
		if (Math.abs(denom) > EPSILON){
			double t = Vec3D.dot(Vec3D.sub(this.pos, r.origin), this.normal) / denom;
			if (t > EPSILON) return t;
		}
		return -1f;
	}

	public Intersection getIntersection(Ray r){
		double intersectDist = this.intersectRay(r);
		if(intersectDist<0) return null;
		else {
			Vec3D intersectPt = Vec3D.add(r.origin, Vec3D.mult(r.dir, intersectDist));return new Intersection(this, r, intersectDist, intersectPt, this.normal);
		}
	}
}

