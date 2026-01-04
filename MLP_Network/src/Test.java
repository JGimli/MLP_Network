import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Test extends JFrame{
	MojKomponent komponent;
	Siec siec;
	double MLP_network_acc=0;
	
	public class MojKomponent extends JPanel{
		private List<Point> points=new ArrayList<Point>();
		boolean draw=false;
		
		//w kostruktorze umieszczamy metody pozwalające rysować 
		//dowaolne kształty(literyw naszym przypadku)
		public MojKomponent(){	
			setBackground(Color.white);
			
			addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				draw=false;
				if(e.getButton()!=MouseEvent.BUTTON3) {
					points.add(e.getPoint());
					points.add(e.getPoint());
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1) {
				points.add(e.getPoint());
				draw=true;
				repaint();
				}
				if(e.getButton()==MouseEvent.BUTTON3) {
					clear();
				}
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				draw=true;
				
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(draw) {
					points.add(e.getPoint());
					repaint();
				}
			}
		});
		}
		
		@Override
		protected void paintComponent(Graphics g) {	
			super.paintComponent(g);
			g.setColor(Color.black);
			for(int i=1;i<points.size();i++) {
				Point p=points.get(i);
				Point pp=points.get(i-1);
				if(p.x==pp.x&&p.y==pp.y) {
					i+=2;
					continue;
				}
				g.drawLine(pp.x, pp.y, p.x, p.y);
			}
			
			
		}
		
		//metoda konwertująca obraz na tablice double [64] 
		//przy użycziu średniej wartoś w "komurkach"
		
		public double[] getDrawingPixelValues() {
		    if (points.isEmpty()) 
		    	return new double[64]; 

		    int minX = points.stream().mapToInt(p -> p.x).min().orElse(0);
		    int minY = points.stream().mapToInt(p -> p.y).min().orElse(0);
		    int maxX = points.stream().mapToInt(p -> p.x).max().orElse(getWidth());
		    int maxY = points.stream().mapToInt(p -> p.y).max().orElse(getHeight());

		    int croppedWidth = maxX - minX + 1;
		    int croppedHeight = maxY - minY + 1;


		    BufferedImage croppedImage = new BufferedImage(croppedWidth, croppedHeight, BufferedImage.TYPE_INT_RGB);
		    Graphics2D g2d = croppedImage.createGraphics();
		    g2d.translate(-minX, -minY);
		    paint(g2d);
		    g2d.dispose();


		    int newSize = 8;
		    double[] pixelValues = new double[newSize * newSize];
		    int cellWidth = croppedWidth / newSize;
		    int cellHeight = croppedHeight / newSize;

		    for (int row = 0; row < newSize; row++) {
		        for (int col = 0; col < newSize; col++) {
		            int sum = 0;
		            int count = 0;

		            for (int y = row * cellHeight; y < (row + 1) * cellHeight; y++) {
		                for (int x = col * cellWidth; x < (col + 1) * cellWidth; x++) {
		                    if (x < croppedWidth && y < croppedHeight) {
		                        int color = croppedImage.getRGB(x, y);
		                        int grayscale = (color >> 16) & 0xff;
		                        sum += grayscale;
		                        count++;
		                    }
		                }
		            }

		            double avgValue = count > 0 ? (double) sum / count : 0;
		            pixelValues[row * newSize + col] = Math.abs(avgValue / 255.0 - 1);
		        }
		    }

		    return pixelValues;
		}
		
		public void clear() {
			points.clear();
			repaint();
		}
		
		
	}
	
		
	public Test(String string) {
		
		super(string);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension d=kit.getScreenSize();
		setBounds(d.width/4,d.height/4,d.width/2,d.height/2);
		//setResizable(false);
		komponent=new MojKomponent();
		add(komponent);
		
		// sieć

		int [] tab=new int [3];
		tab[0]=12; tab[1]=6;tab[2]=3;
		siec=new Siec(64,3,tab);
		
		//uczenie i testowanie sieci
		do {
			for(int i=0;i<30;i++)
				learn();
		}while(!train("Train.txt",0.75));
		train("Test.txt",0.8);
		//
		JPanel panel=new JPanel(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
		Font out_font=new Font("Times New Roman", Font.PLAIN, 16);
		JTextArea out=new JTextArea();
		out.setFont(out_font);
		out.setEditable(false);
		out.setText(" NETWORK_ACC:\n"+String.format("%3.2f%c", MLP_network_acc*100,'%')+"\n");
		
		//przycisk pobierający wartości z rysunku i analizujący wyniki z sieci
		
		JButton get_drawing_values=new JButton("Sprawdz Rysunek");
		get_drawing_values.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				double[] picturePixelValues = komponent.getDrawingPixelValues();
				
				double[]result=siec.oblicz_wyjscie(picturePixelValues);
				double max=result[0];
				String str_out="";
				for(int i=1;i<result.length;i++) {
					max=Math.max(max, result[i]);
				}
				if(max<0.6) {
					str_out+="Wynik jest niejednoznaczny\n";
				}
				else {
				if(max==result[0]) {
					str_out+="to jest P\n";
				}else if(max==result[1]) {
					str_out+="to jest R\n";
				} else {
					str_out+="to jest S\n";
				}
				}
				str_out+=" P\t: "+String.format("%3.2f%c", result[0]*100,'%')+"\n";
				str_out+=" R\t: "+String.format("%3.2f%c", result[1]*100,'%')+"\n";
				str_out+=" S\t: "+String.format("%3.2f%c", result[2]*100,'%')+"\n";
				out.setText(str_out);
				
			}
		});
		
		JButton Clear_button=new JButton("Wyczyść");
		Clear_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				komponent.clear();
				
			}
		});
		
		
		
		JRadioButton radioB1 = new JRadioButton("P");
	    JRadioButton radioB2 = new JRadioButton("R");
	    JRadioButton radioB3 = new JRadioButton("S");
	    ButtonGroup radio_group = new ButtonGroup();
	    radio_group.add(radioB1);
	    radio_group.add(radioB2);
	    radio_group.add(radioB3);
	    
	    JButton Train_button=new JButton("Dodaj do ciągu uczącego");
		Train_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(radio_group.getSelection()!=null) {
					double[] picturePixelValues = komponent.getDrawingPixelValues();
					try {
						FileWriter file_writer=new FileWriter("Train.txt",true);
						BufferedWriter buff_writer=new BufferedWriter(file_writer);
						for(int i=0;i<picturePixelValues.length;i++) {
							buff_writer.write(Double.toString(picturePixelValues[i]));
							buff_writer.write(",");
							
						}						
						if(radioB1.isSelected()) {
							buff_writer.write("P");
						}else if(radioB2.isSelected()) {
							buff_writer.write("R");
						}else {
							buff_writer.write("S");
						}
						buff_writer.newLine();
						buff_writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else {
					out.setText("Proszę wybrać 1 z radio buttonów");
				}
			}
		});
		
		JButton Test_button=new JButton("Dodaj do ciągu testowego");
		Test_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(radio_group.getSelection()!=null) {
					double[] picturePixelValues = komponent.getDrawingPixelValues();
					try {
						FileWriter file_writer=new FileWriter("Test.txt",true);
						BufferedWriter buff_writer=new BufferedWriter(file_writer);
						for(int i=0;i<picturePixelValues.length;i++) {
							buff_writer.write(Double.toString(picturePixelValues[i]));
							buff_writer.write(",");
						
						}
						
						if(radioB1.isSelected()) {
							buff_writer.write("P");
						}else if(radioB2.isSelected()) {
							buff_writer.write("R");
						}else {
							buff_writer.write("S");
						}
						buff_writer.newLine();
						buff_writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else {
					out.setText("Proszę wybrać 1 z radio buttonów");
				}
			}
		});
		c.ipadx=0;
		c.fill=GridBagConstraints.HORIZONTAL;
		
		
		c.gridx = 1;
		c.gridy = 1;
		c.ipady=40;
		panel.add(out,c);
		
		//get img value button
		c.gridy = 2;
		c.ipady=20;
		
		//get drawing value button
		c.gridy = 3;
		panel.add(get_drawing_values,c);
		
		//clear button
		c.gridy = 4;
		panel.add(Clear_button,c);
		
		//radio buttons
		c.gridy = 5;
		panel.add(radioB1,c);
		c.gridy = 6;
		panel.add(radioB2,c);
		c.gridy = 7;
		panel.add(radioB3,c);
		
		c.gridy = 8;
		panel.add(Train_button,c);
		c.gridy = 9;
		panel.add(Test_button,c);
		
		add(panel,BorderLayout.EAST);
		setVisible(true);
	}
	
	

	Set<String> File_list(String dir){
		return Stream.of(new File(dir).listFiles())
				.filter(file -> !file.isDirectory())
				.map(File::getName)
				.collect(Collectors.toSet());
	}
	
	// uczenie sieci MLP
	void learn() {
		String target_loc="Train.txt";
		try {
			File target_val_file=new File(target_loc);
			Scanner Read_val=new Scanner(target_val_file);
			List<String> lines=new ArrayList<String>();

			while(Read_val.hasNextLine()){
				String line = Read_val.nextLine();
				lines.add(line);
			}
			Collections.shuffle(lines);
			for(String line : lines){
				String[] contest=line.trim().split(",");
				
				double Learn_sample_values[]=new double[64];
				
				for(int i=0;i<64;i++) {
					Learn_sample_values[i]=Double.parseDouble(contest[i]);
				}
				
				double[] Sample_target_values=new double [3];
				
				char temp_lab=contest[64].charAt(0);
				
				if(temp_lab=='P') {
					Sample_target_values[0]= 1d;
					Sample_target_values[1]= 0d;
					Sample_target_values[2]= 0d;
				}else if(temp_lab=='R') {
					Sample_target_values[0]= 0d;
					Sample_target_values[1]= 1d;
					Sample_target_values[2]= 0d;
				}else if(temp_lab=='S') {
					Sample_target_values[0]= 0d;
					Sample_target_values[1]= 0d;
					Sample_target_values[2]= 1d;
				}
				siec.oblicz_wyjscie(Learn_sample_values);
				siec.back_popag(Sample_target_values,Learn_sample_values,0.9d);
			
			}
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}
	//testowanie sieci MLP
	
	boolean train(String dir,double target) {
		String target_loc=dir;
		
		double acc=0;
		int amount=0;
		try {
			File target_val_file=new File(target_loc);
			Scanner Read_val=new Scanner(target_val_file);
			
			
			
			while(Read_val.hasNextLine()){
				String line = Read_val.nextLine();
				String[] contest=line.trim().split(",");
				
				double Learn_sample_values[]=new double[64];
				
				for(int i=0;i<64;i++) {
					Learn_sample_values[i]=Double.parseDouble(contest[i]);
				}
				
				double[] Sample_target_values=new double [3];
				
				char temp_lab=contest[64].charAt(0);
				
				if(temp_lab=='P') {
					Sample_target_values[0]= 1d;
					Sample_target_values[1]= 0d;
					Sample_target_values[2]= 0d;
				}else if(temp_lab=='R') {
					Sample_target_values[0]= 0d;
					Sample_target_values[1]= 1d;
					Sample_target_values[2]= 0d;
				}else if(temp_lab=='S') {
					Sample_target_values[0]= 0d;
					Sample_target_values[1]= 0d;
					Sample_target_values[2]= 1d;
				}
				double []result= siec.oblicz_wyjscie(Learn_sample_values);
				double max=-1;
				int index=0;
				int j;
				for(j=0;j<result.length;j++) {
					if(max<result[j]) {
						max=result[j];
						index=j;
					}
				}
				if(Sample_target_values[index]==1d&&result[index]>=0.6) {
					acc++;			
				}
				amount++;
			}
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		MLP_network_acc=acc/amount;
		return target<(acc/amount);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new Test("neurony");
			}
		});
	}

}
