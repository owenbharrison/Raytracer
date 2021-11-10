package raytracing.structs;

public class Vec3D{
	public double x, y, z;
	
	public Vec3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3D add(Vec3D v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	public Vec3D sub(Vec3D v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}
	
	public Vec3D mult(double d) {
		this.x *= d;
		this.y *= d;
		this.z *= d;
		return this;
	}
	
	public Vec3D div(double d) {
		this.x /= d;
		this.y /= d;
		this.z /= d;
		return this;
	}
	
	public double mag() {
		return (double)Math.sqrt(Vec3D.dot(this, this));
	}
	
	public Vec3D normalize() {
		this.div(this.mag());
		return this;
	}
	
	public Vec3D copy() {
		return new Vec3D(this.x, this.y, this.z);
	}
	
	public static Vec3D add(Vec3D a, Vec3D b) {
		return a.copy().add(b);
	}
	
	public static Vec3D sub(Vec3D a, Vec3D b) {
		return a.copy().sub(b);
	}
	
	public static Vec3D mult(Vec3D a, double d) {
		return a.copy().mult(d);
	}
	
	public static Vec3D div(Vec3D a, double d) {
		return a.copy().div(d);
	}
	
	public static double dot(Vec3D a, Vec3D b) {
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}
	
	public static double[] dirToUV(Vec3D a) {
		Vec3D b = a.copy().normalize();
		return new double[] {
				0.5f + (double)Math.atan2(b.x, b.z)/(2f*(double)Math.PI),
				0.5f - (double)Math.asin(b.y)/(double)Math.PI
		};
	}
	
	public static Vec3D cross(Vec3D a, Vec3D b) {
		return new Vec3D(
			a.y*b.z - a.z*b.y,
			a.z*b.x - a.x*b.z,
			a.x*b.y - a.y*b.x
		);
	}
	
	public static Vec3D normalize(Vec3D a) {
		return a.copy().normalize();
	}
	
	public static Vec3D computeReflect(Vec3D dir, Vec3D norm) {
		return Vec3D.sub(dir, Vec3D.mult(norm, 2f*Vec3D.dot(dir, norm))).normalize();
	}
	
	public String toString() {
		return "[x: "+this.x+", y: "+this.y+", z: "+this.z+"]";
	}
}
