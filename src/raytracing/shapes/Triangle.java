package raytracing.shapes;

import raytracing.structs.Colour;
import raytracing.structs.Intersection;
import raytracing.structs.Ray;
import raytracing.structs.Vec3D;

public class Triangle extends Shape{
	public Vec3D p[];//3 vertexes
	
	public Triangle(Vec3D a, Vec3D b, Vec3D c, Colour col, boolean reflective) {
		this.col = col;
		this.p = new Vec3D[] {a, b, c};
		this.reflective = reflective;
		this.shaded = false;
		this.pos = Vec3D.div(Vec3D.add(this.p[0], Vec3D.add(this.p[1], this.p[2])), 3.0);
	}
	
	public double intersectRay(final Ray r){
		double EPSILON = 0.0001;
		Vec3D v0 = this.p[0];
		Vec3D v1 = this.p[1];	
		Vec3D v2 = this.p[2];
		Vec3D e1 = Vec3D.sub(v1, v0);
		Vec3D e2 = Vec3D.sub(v2, v0);
		Vec3D h = Vec3D.cross(r.dir, e2);
		double a = Vec3D.dot(e1, h);
		if(a>-EPSILON&&a<EPSILON)return -1.0;
		double f = 1.0/a;
		Vec3D s = Vec3D.sub(r.origin, v0);
		double u = f * Vec3D.dot(s, h);
		if(u<0.0||u>1.0)return -1.0;
		Vec3D q = Vec3D.cross(s, e1);
		double v = f * Vec3D.dot(r.dir, q);
		if(v<0.0||u+v>1.0)return -1.0;
		double t = f * Vec3D.dot(e2, q);
		if(t>EPSILON)return t;
		else return -1.0;
	}
	
	public Vec3D getNormal() {
		Vec3D e1 = Vec3D.sub(this.p[1], this.p[0]);
		Vec3D e2 = Vec3D.sub(this.p[2], this.p[0]);
		return Vec3D.cross(e1, e2).normalize();
	}
	
	public Intersection getIntersection(final Ray r) {
		double intersectDist = this.intersectRay(r);
		if(intersectDist<=0.0) return null;
		else {
			Vec3D intersectPt = Vec3D.add(r.origin, Vec3D.mult(r.dir, intersectDist));
			Vec3D e1 = Vec3D.sub(this.p[1], this.p[0]);
			Vec3D e2 = Vec3D.sub(this.p[2], this.p[0]);
			Vec3D intersectNormal = Vec3D.normalize(Vec3D.cross(e1, e2));
			return new Intersection(this, r, intersectDist, intersectPt, intersectNormal);
		}
	}
	
	public String toString() {
		return "["+this.p[0].toString()+", "+this.p[1].toString()+", "+this.p[2].toString()+"]";
	}
}

