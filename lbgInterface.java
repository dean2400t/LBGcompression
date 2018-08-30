
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.EventListener;
import java.awt.event.ActionEvent;

import java.awt.Dimension;
import javax.swing.JLabel;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;


public class lbgInterface extends JFrame {

	private JLabel lblImage;
	private JLabel label;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private String path;
	private String saveDirectory;
	private JLabel[] labels;
	private StatusUpdate statuesUp;


	JRadioButton rdbtnGif;
	
	JRadioButton rdbtnJpg;
	
	JRadioButton rdbtnPng;
	
	JRadioButton rdbtnRgba;
	
	JRadioButton rdbtnNewRadioButton;
	
	private static JTextArea tx;
	public static EventListener ev;
	private final Action action = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	private final Action action_2 = new SwingAction_2();
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup2 = new ButtonGroup();

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					lbgInterface frame = new lbgInterface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	private String getMethodName(int method)
	{
		if (method==1)
			return "Voronoi";
		else
			return "GreedySphere";
		
	}
	
	private static String getFileName(String path)
	{
		char[] chArray=path.toCharArray();
		int startIndex;
		if (chArray[chArray.length-4]=='.')
			startIndex=chArray.length-5;
		else
			startIndex=chArray.length-6;
		int endIndex=startIndex;
		String name="";
		while (startIndex>=0)
			if (chArray[startIndex]!='\\')
				startIndex--;
			else
				break;
		startIndex++;
		
		for (;startIndex<=endIndex;startIndex++)
			name+=chArray[startIndex];
		return name;
	}
	
	private String getConvertedFormat()
	{
		if (rdbtnJpg.isSelected())
			return "jpg";
		if (rdbtnPng.isSelected())
			return "png";
		if (rdbtnGif.isSelected())
			return "gif";
		return null;
	}
	
	private String getColorSpace()
	{
		if (rdbtnRgba.isSelected())
			return "RGBA";
		if (rdbtnNewRadioButton.isSelected())
			return "RGB";
		return null;
	}
	
	private int getNumOfClusters()
	{
		String num=textField.getText();
		return Integer.parseInt(num);
	}
	
	private double getDistortion()
	{
		String num=textField_1.getText();
		return Double.parseDouble(num);
	}
	
	public lbgInterface() {
		setResizable(true);
		setSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 0, 0, 0));
		path="";
		JPanel panel = new JPanel();
		contentPane.add(panel);
		
		statuesUp=new StatusUpdate();
		
		labels=new JLabel[10];
		JButton sourceBTN = new JButton("1. Source");
		sourceBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser file = new JFileChooser();
				file.setCurrentDirectory(
						new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Pictures"));
				String convertTo=getConvertedFormat();
				FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
					    "Image files", ImageIO.getReaderFileSuffixes());
				
				file.setAcceptAllFileFilterUsed(false);
				file.addChoosableFileFilter(imageFilter);
				int result = file.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					file.getSelectedFile().getAbsolutePath();
					ImageIcon sourceImage;
					path=file.getSelectedFile().getPath();
					sourceImage	= new ImageIcon(path);
					
					Image image=sourceImage.getImage();
					Image resizedImage=image.getScaledInstance(lblImage.getWidth(), lblImage.getHeight(), Image.SCALE_SMOOTH);
					sourceImage=new ImageIcon(resizedImage);
					lblImage.setIcon(sourceImage);
					tx.append(path+"\n");
				}

			}
		});
		sourceBTN.setBounds(10, 9, 90, 23);
		
		lblImage = new JLabel("Source Image");
		lblImage.setBounds(10, 52, 141, 141);
		
		JButton btnChooseDestenation = new JButton("6. Destenation");
		btnChooseDestenation.setBounds(297, 9, 120, 23);
		btnChooseDestenation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser;
				chooser = new JFileChooser(); 
			    chooser.setCurrentDirectory(new java.io.File("."));
			    chooser.setDialogTitle("Choose folder");
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    //
			    // disable the "All files" option.
			    //
			    chooser.setAcceptAllFileFilterUsed(false);
			    //    
			    if (chooser.showOpenDialog(lblImage) == JFileChooser.APPROVE_OPTION) { 
			    	saveDirectory=chooser.getSelectedFile().toString();
			    	tx.append("Destination folder:" + saveDirectory+"\n");
			      }
			     }

			}
		);
		
		label = new JLabel("Compressed Image");
		label.setBounds(288, 52, 141, 141);
		
		textField = new JTextField();
		textField.setText("64");
		textField.setBounds(157, 210, 76, 20);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setText("0.1");
		textField_1.setBounds(157, 266, 76, 20);
		textField_1.setColumns(10);
		
		JLabel lblCluster = new JLabel("4. Num of Clusters");
		lblCluster.setBounds(157, 185, 115, 14);
		
		JLabel lblDistortion = new JLabel("5. Distortion (0.000001-100%)");
		lblDistortion.setBounds(157, 241, 180, 14);
		
		 rdbtnRgba = new JRadioButton("RGBA");
		rdbtnRgba.setBounds(157, 54, 57, 21);
		rdbtnRgba.setFont(new Font("Tahoma", Font.PLAIN, 9));
		buttonGroup.add(rdbtnRgba);
		
		 rdbtnNewRadioButton = new JRadioButton("RGB");
		rdbtnNewRadioButton.setBounds(157, 30, 45, 21);
		rdbtnNewRadioButton.setFont(new Font("Tahoma", Font.PLAIN, 9));
		rdbtnNewRadioButton.setSelected(true);
		buttonGroup.add(rdbtnNewRadioButton);
		panel.setLayout(null);
		panel.add(sourceBTN);
		panel.add(lblImage);
		panel.add(textField_1);
		panel.add(lblDistortion);
		panel.add(textField);
		panel.add(rdbtnNewRadioButton);
		panel.add(lblCluster);
		panel.add(label);
		panel.add(rdbtnRgba);
		panel.add(btnChooseDestenation);
		
		JButton btnConvert = new JButton("7a. Convert");
		btnConvert.setBounds(317, 265, 105, 23);
		panel.add(btnConvert);
		
		
		//regular convert button pressed
		btnConvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				int index=0;
				String methodName=getMethodName(1);
				String convertTo=getConvertedFormat();
				String colorSpace=getColorSpace();
				String name=getFileName(path);
				BufferedImage bf;
				int numOfClusters=getNumOfClusters();
				double distortion=getDistortion();
				String update="";
				
				try {
					if (colorSpace.compareTo("RGB")==0)
					{
						statuesUp=new StatusUpdate();
						Operation oporation=new Operation(path, 1, 1, numOfClusters, distortion, statuesUp);
						new Thread(oporation).start();
						String fin="Finish";
						while (update.compareTo(fin)!=0)
						{
							update=statuesUp.getUpdate();
							tx.append(update);
						
							statuesUp.finishedUpdate();
						}
						tx.append("\n\n");
						update="";
						statuesUp.waitSetBF(null, 1);
						bf=statuesUp.getBF();
					}
					else
					{
						statuesUp=new StatusUpdate();
						Operation oporation=new Operation(path, 1, 2, numOfClusters, distortion, statuesUp);
						new Thread(oporation).start();
						String fin="Finish";
						while (update.compareTo(fin)!=0)
						{
							update=statuesUp.getUpdate();
							tx.append(update);
							statuesUp.finishedUpdate();
						}
						tx.append("\n\n");
						update="";
						statuesUp.waitSetBF(null, 1);
						bf=statuesUp.getBF();
					}
					File outputfile;
					if (saveDirectory==null)
						outputfile = new File(name+numOfClusters+methodName+"Dis"+distortion+"."+convertTo);
					else
						outputfile = new File(saveDirectory+"\\"+name+numOfClusters+methodName+"Dis"+distortion+"."+convertTo);
					String p=outputfile.getPath();
					ImageIO.write(bf, convertTo, outputfile);
					
					ImageIcon sourceImage;
					p=outputfile.getPath();
					
					sourceImage	= new ImageIcon(p);
					Image image=sourceImage.getImage();
					Image resizedImage=image.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
					sourceImage=new ImageIcon(resizedImage);
					label.setIcon(sourceImage);
					
					if (saveDirectory==null)
						p=name+"OriginalAfterJava."+convertTo;
					else
						p=saveDirectory+"\\"+name+"OriginalAfterJava."+convertTo;
					
					BufferedImage originalImage = ImageIO.read(new File(path));
					
					outputfile=new File(p);
					
					ImageIO.write(originalImage, convertTo, outputfile);
					}
				
				 catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				      
				      
				   
				}
			
		});
		
		JButton btnConvert_1 = new JButton("7b. Convert+");
		btnConvert_1.setBounds(317, 492, 115, 23);
		btnConvert_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//oporation.encodeWAVfile(imagePath, outputName, 256, 100);
				//for (int clusters=2; clusters<=1024; clusters*=2)
				
				
				int index=0;
				String methodName=getMethodName(1);
				String name=getFileName(path);
				BufferedImage bf = null;
				String convertTo=getConvertedFormat();
				String colorSpace=getColorSpace();	
				double distortion=getDistortion();
				String update="";
				try {
				for (int i=2; i<=1024; i*=2)
				{
					//tx.append(i+": \n");
					
					if (colorSpace.compareTo("RGB")==0)
					{
						statuesUp=new StatusUpdate();
						Operation oporation=new Operation(path, 1, 1, i, distortion, statuesUp);
						new Thread(oporation).start();
						String fin="Finish";
						while (update.compareTo(fin)!=0)
						{
							update=statuesUp.getUpdate();
							tx.append(update);
							statuesUp.finishedUpdate();
						}
						tx.append("\n\n");
						update="";
						statuesUp.waitSetBF(null, 1);
						bf=statuesUp.getBF();
					}
					else
					{
						statuesUp=new StatusUpdate();
						Operation oporation=new Operation(path, 1, 2, i, distortion, statuesUp);
						new Thread(oporation).start();
						String fin="Finish";
						while (update.compareTo(fin)!=0)
						{
							update=statuesUp.getUpdate();
							tx.append(update);
							statuesUp.finishedUpdate();
						}
						tx.append("\n\n");
						update="";
						statuesUp.waitSetBF(null, 1);
						bf=statuesUp.getBF();
					}
						
					File outputfile;
						if (saveDirectory==null)
							outputfile = new File(name+i+methodName+"Dis"+distortion+"."+convertTo);
						else
							outputfile = new File(saveDirectory+"\\"+name+i+methodName+"Dis"+distortion+"."+convertTo);
						String p=outputfile.getPath();
						ImageIO.write(bf, convertTo, outputfile);
						ImageIcon sourceImage;
						p=outputfile.getPath();
						sourceImage	= new ImageIcon(p);
						
						Image image=sourceImage.getImage();
						Image resizedImage=image.getScaledInstance(labels[index].getWidth(), labels[index].getHeight(), Image.SCALE_SMOOTH);
						sourceImage=new ImageIcon(resizedImage);
						labels[index].setIcon(sourceImage);
						index++;
						if (i==1024)
						{
							sourceImage	= new ImageIcon(p);
							
							image=sourceImage.getImage();
							resizedImage=image.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
							sourceImage=new ImageIcon(resizedImage);
							label.setIcon(sourceImage);
							
							if (saveDirectory==null)
								p=name+"OriginalAfterJava."+convertTo;
							else
								p=saveDirectory+"\\"+name+"OriginalAfterJava."+convertTo;
							
							BufferedImage originalImage = ImageIO.read(new File(path));
							outputfile=new File(p);
							ImageIO.write(originalImage, convertTo, outputfile);
						}
				}
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				      
				      
				   
				}
			
		});
		panel.add(btnConvert_1);
		
		labels[0] = new JLabel("2");
		labels[0].setBounds(10, 318, 70, 70);
		panel.add(labels[0]);
		
		labels[1] = new JLabel("4");
		labels[1].setBounds(92, 318, 70, 70);
		panel.add(labels[1]);
		
		labels[2] = new JLabel("8");
		labels[2].setBounds(173, 318, 70, 70);
		panel.add(labels[2]);
		
		labels[3] = new JLabel("16");
		labels[3].setBounds(253, 318, 70, 70);
		panel.add(labels[3]);
		
		labels[4] = new JLabel("32");
		labels[4].setBounds(336, 318, 70, 70);
		panel.add(labels[4]);
		
		labels[5] = new JLabel("64");
		labels[5].setBounds(10, 411, 70, 70);
		panel.add(labels[5]);
		
		labels[6] = new JLabel("128");
		labels[6].setBounds(92, 411, 70, 70);
		panel.add(labels[6]);
		
		labels[7] = new JLabel("256");
		labels[7].setBounds(173, 411, 70, 70);
		panel.add(labels[7]);
		
		labels[8] = new JLabel("512");
		labels[8].setBounds(253, 411, 70, 70);
		panel.add(labels[8]);
		
		labels[9] = new JLabel("1024");
		labels[9].setBounds(336, 411, 70, 70);
		panel.add(labels[9]);
		
		JLabel lblConvertTo = new JLabel("3. Convert to:");
		lblConvertTo.setBounds(157, 82, 80, 14);
		panel.add(lblConvertTo);
		
		 rdbtnGif = new JRadioButton("GIF");
		rdbtnGif.setFont(new Font("Tahoma", Font.PLAIN, 9));
		rdbtnGif.setBounds(157, 103, 45, 21);
		buttonGroup2.add(rdbtnGif);
		panel.add(rdbtnGif);
		
		 rdbtnJpg = new JRadioButton("JPG");
		rdbtnJpg.setFont(new Font("Tahoma", Font.PLAIN, 9));
		rdbtnJpg.setBounds(157, 127, 45, 21);
		buttonGroup2.add(rdbtnJpg);
		panel.add(rdbtnJpg);
		
		 rdbtnPng = new JRadioButton("PNG");
		rdbtnPng.setFont(new Font("Tahoma", Font.PLAIN, 9));
		rdbtnPng.setBounds(157, 151, 45, 21);
		rdbtnPng.setSelected(true);
		buttonGroup2.add(rdbtnPng);
		panel.add(rdbtnPng);
		
		
	
		
		tx=new JTextArea();
		
		
		JScrollPane scroll=new JScrollPane(tx);
		scroll.setMaximumSize(new Dimension(200, 400));
		scroll.setLocation(456, 9);
		scroll.setSize(304, 531);
		scroll.setVerticalScrollBarPolicy(scroll.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scroll);
		
		JLabel lblPowerOfTwo = new JLabel("Power of two samples: 2,4,8,16,...,1024.");
		lblPowerOfTwo.setFont(new Font("Arial", Font.BOLD, 12));
		lblPowerOfTwo.setBounds(10, 297, 243, 23);
		panel.add(lblPowerOfTwo);
		
		JLabel label_12 = new JLabel("2. Color Space");
		label_12.setBounds(157, 9, 100, 23);
		panel.add(label_12);
		
		
		
		
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class SwingAction_1 extends AbstractAction {
		public SwingAction_1() {
			putValue(NAME, "SwingAction_1");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class SwingAction_2 extends AbstractAction {
		public SwingAction_2() {
			putValue(NAME, "SwingAction_2");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
