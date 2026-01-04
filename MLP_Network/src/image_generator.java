import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.*;


public class image_generator extends JFrame{

	MojKomponent komponent;
	double [][]generated_img=new double [8][8];
	
	public class MojKomponent extends JComponent{

		@Override
		protected void paintComponent(Graphics g) {
			
			for(int i=0;i<8;i++) {
				for(int j=0;j<8;j++) {
					float color=(float)generated_img[i][j]*1000;
					g.setColor(new Color(color, color, color,1));
					g.fillRect(j*getWidth()/8,i*getHeight()/8, getWidth()/8, getHeight()/8);
				}
			}
				
			super.paintComponent(g);
		}
		
	}
	public image_generator(String string) {
		super(string);
		this.gen_img();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension d=kit.getScreenSize();
		setBounds(d.width/4,d.height/4,d.width/2,d.height/2);
		add(komponent=new MojKomponent());
		setVisible(true);
	}

	private void gen_img() {
		String target_loc="Train.txt";
		int ammout=0;
		try {
			File target_val_file=new File(target_loc);
			Scanner Read_val=new Scanner(target_val_file);
			List<String> lines=new ArrayList<String>();

			while(Read_val.hasNextLine()){
				String line = Read_val.nextLine();
				lines.add(line);
			}
		
		for(String line : lines){
			String[] contest=line.trim().split(",");
			if(contest[64].charAt(0)=='P') {
				ammout++;
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				generated_img[i][j]=Double.parseDouble(contest[i*8+j]);
					}
				}
			}
			
		}
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				generated_img[i][j]/=ammout;
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		}
		

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new image_generator("Generator obrazu");
			}
		});
	}

}
