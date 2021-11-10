package raytracing.shapes;

import raytracing.structs.*;

public abstract class Shape {
	protected Vec3D pos;
	protected Colour col;
	protected boolean reflective;
	protected boolean shaded;

	public abstract double intersectRay(Ray r);
	public abstract Intersection getIntersection(Ray r);

	public Vec3D getPos(){
		return this.pos;
	}

	public Colour getCol(){
		return this.col;
	}

	public boolean isReflective(){
		return this.reflective;
	}

	public boolean isShaded(){
		return this.shaded;
	}
}
