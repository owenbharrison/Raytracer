package raytracing.structs;

import raytracing.shapes.Plane;

public class Ray {
	public Vec3D origin;
	public Vec3D dir;
	
	public Ray(Vec3D origin, Vec3D dir) {
		this.origin = origin.copy();
		this.dir = dir.copy();
	}
	
	public static double intersectPlane(Ray r, Plane p) {
		double denom = Vec3D.dot(r.dir, p.normal);
		double t = Vec3D.dot(Vec3D.sub(p.getPos(), r.origin), p.normal);
		return denom==0d ? (t==0d ? 0d : -1d) : t/denom;
	}
	
	public static Ray reflectPlane(Ray r, Plane p) {
		double intersectDist = Ray.intersectPlane(r, p);
		if(intersectDist>0d) {
			Vec3D intersectPt = Vec3D.mult(r.dir, intersectDist);
			intersectPt.add(r.origin);
			Vec3D reflectDir = Vec3D.computeReflect(r.dir, p.normal); 
			reflectDir.normalize();
			return new Ray(intersectPt, reflectDir);
		} else {
			return null;
		}
	}
}