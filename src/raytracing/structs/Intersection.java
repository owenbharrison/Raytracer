package raytracing.structs;

import raytracing.shapes.Shape;

public class Intersection {
  public Shape shape;
  public Ray ray;
  public double dist;
  public Vec3D pos;
  public Vec3D normal;
  
  public Intersection(final Shape shape, final Ray ray, final double dist, final Vec3D pos, final Vec3D normal) {
  	this.shape = shape;
  	this.ray = ray;
  	this.dist = dist;
  	this.pos = pos;
  	this.normal = normal;
  }
}
