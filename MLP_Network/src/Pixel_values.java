import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Pixel_values {
	public float[] get_value(String file_path) 
	throws IOException
	{
		BufferedImage img=null;
		File file=null;
		float  img_values[]= new float[64];
		try {
			file=new File(file_path);
			img = ImageIO.read(file);
		}
		catch (IOException e) {
			System.out.println(e);
		}
		int width=img.getWidth();
		int height=img.getHeight();
		for(int i=0;i<height;i++) {
			for(int j=0;j<width;j++) {
				int p=img.getRGB(i, j);
				int val=p&0xff;
				float val_double=Math.abs((float)val/255-1);
				img_values[height*i+j]=val_double;
			}
		}
		return img_values;
	}
}
