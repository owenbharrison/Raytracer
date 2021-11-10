package raytracing.structs;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import io.github.owenbharrison.displib.image.ImageFilters;

import java.awt.image.BufferedImage;

public class Texture {
	public int width, height;
	public Colour[] pixels;

	public Texture(InputStream is){
		try{
			BufferedImage image = ImageIO.read(is);
			this.width = image.getWidth();
			this.height = image.getHeight();
			int[] data = ImageFilters.imageToPixels(image);
			this.pixels = new Colour[this.width*this.height];
			for(int x=0;x<this.width;x++){
				for(int y=0;y<this.height;y++){
					int z = (x+y*width)*3;
					this.pixels[x+y*this.width] = new Colour(data[z], data[z+1], data[z+2]);
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public Colour getColorAtUV(double u, double v){
		int x = (int)Math.min(Math.max(0, u*this.width), this.width-1);
		int y = (int)Math.min(Math.max(0, v*this.height), this.height-1);
		return this.pixels[x+y*this.width];
	}
}
