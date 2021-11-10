package raytracing.shapes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import raytracing.structs.Colour;
import raytracing.structs.Intersection;
import raytracing.structs.Ray;
import raytracing.structs.Vec3D;

public class Mesh extends Shape{
	public ArrayList<Triangle> tris;
	
	public Mesh(InputStream is) {
		try {
			this.tris = new ArrayList<>();
			ArrayList<Vec3D> vtxs = new ArrayList<>();
			Pattern vrx = Pattern.compile("v (-?[.0-9]+) (-?[.0-9]+) (-?[.0-9]+)");
			Pattern frx = Pattern.compile("f ([0-9]+).+? ([0-9]+).+? ([0-9]+).+?");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			for (String line; (line = reader.readLine()) != null; ) {
				Matcher vmt = vrx.matcher(line);
				if(vmt.find()) {
					double x = Double.parseDouble(vmt.group(1));
					double y = Double.parseDouble(vmt.group(2));
					double z = Double.parseDouble(vmt.group(3));
					vtxs.add(new Vec3D(x, y, z));
				}
				Matcher fmt = frx.matcher(line);
				if(fmt.find()) {
					Vec3D a = vtxs.get(Integer.parseInt(fmt.group(1))-1);
					Vec3D b = vtxs.get(Integer.parseInt(fmt.group(2))-1);
					Vec3D c = vtxs.get(Integer.parseInt(fmt.group(3))-1);
					this.tris.add(new Triangle(a, b, c, new Colour(255, 0, 0), false));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double intersectRay(Ray r) {
		double intersectDist = Double.POSITIVE_INFINITY;
		for(Triangle t:this.tris) {
			double dst = t.intersectRay(r);
			if(dst<intersectDist) {
				intersectDist = dst;
			}
		}
		if(intersectDist==Double.POSITIVE_INFINITY)return -1.0;
		return intersectDist;
	}

	@Override
	public Intersection getIntersection(Ray r) {
		double intersectDist = Double.POSITIVE_INFINITY;
		Triangle bestTri = null;
		for(Triangle t:this.tris) {
			double dst = t.intersectRay(r);
			if(dst<intersectDist) {
				intersectDist = dst;
				bestTri = t;
			}
		}
		if(intersectDist==Double.POSITIVE_INFINITY)return null;
		else {
			Vec3D intersectPt = Vec3D.add(r.origin, Vec3D.mult(r.dir, intersectDist));
			return new Intersection(bestTri, r, intersectDist, intersectPt, bestTri.getNormal());
		}
	}
}
