import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class Sudoku extends JFrame {
	
	int[] cellNs = new int[81]; //actual numbers in cells
	
	JButton[] buttons = {new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "),
			new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "),
			new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "),
			new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "),
			new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "),
			new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "),
			new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "),
			new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "),
			new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" "), new JButton(" ")
	};
	
	JButton calculate = new JButton ("Calculate!");
	JButton reset = new JButton ("Reset");
	JButton save = new JButton ("Save");
	JButton load = new JButton ("Load");
	JButton help = new JButton("Help", new ImageIcon("help.png"));
	JLabel itLabel = new JLabel("Iterations: ");
	JLabel iterations = new JLabel("0");
	
	CalculateSolution calculation;	
	Timer timer;
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	JProgressBar progress = new JProgressBar(0, 80);
	
	String rc = "\nRow conflict in row(s) "; boolean[] rcs = {false, false, false, false, false, false, false, false, false};
	String cc = "\nColumn conflict in column(s) "; boolean[] ccs = {false, false, false, false, false, false, false, false, false};
	String gc = "\nGrid conflict in grid(s) "; boolean[] gcs = {false, false, false, false, false, false, false, false, false};
	
	Font boldfont = new Font("Arial Bold", Font.BOLD, 16);
	Font defaultfont = new Font("Arial", Font.PLAIN, 12);

	public void reset() {
		progress.setValue(0);
		iterations.setText("0");
		for(int i = 0; i < 81; i++) {
			buttons[i].setText(" ");
			buttons[i].setFont(defaultfont);
		}
		
	}

	public void save() {

		
		FileWriter fw;
		BufferedWriter bw;
		
		try {
			String filename;
			
			while(true) {
				filename = JOptionPane.showInputDialog("Enter filename below:");
				filename = filename + ".sudoku";
				
				File file = new File(filename);
			
				if(file.exists()) {
					int select = JOptionPane.showConfirmDialog(null, "File name already exists. Overwrite?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(select == JOptionPane.YES_OPTION)
						break;
				} else break;
			
			}
			
			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			
			for(int i = 0; i < 81; i++) {
				if(buttons[i].getText() == " ")
					bw.write("0");
				else bw.write(buttons[i].getText());
			}
			
			bw.close();
			
			JOptionPane.showMessageDialog(null, "File has been saved under the name " + filename);
			
		} catch (IOException e) {
			System.out.println("Failed to save file!!!");
			e.printStackTrace();
		}
	}
	
	public void load() {

		try {
			JFileChooser chooser = new JFileChooser("C:\\Users\\jeff\\Documents\\school\\2011-12\\AP_Computer_Science\\SudokuThread");
			FileFilter filter = new FileNameExtensionFilter("Sudoku file", "sudoku");
			chooser.addChoosableFileFilter(filter);
			
			int returnVal = chooser.showOpenDialog(null);
			
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File filename = chooser.getSelectedFile();
				FileReader fr = new FileReader(filename);
				BufferedReader br = new BufferedReader(fr);
				
				for(int i = 0; i < 81; i++) {
					int invalue = br.read();
					char outvalue = (char)invalue;
					if(outvalue == '0')
						buttons[i].setText(" ");
					else {
						buttons[i].setText("" + outvalue);
						buttons[i].setFont(boldfont);	
					}
				}
				
				br.close();
		    }
				
	
		} catch(FileNotFoundException fnfe) {
			System.out.println("Fatal Error number 208: " + fnfe.getMessage());
		} catch(IOException ioe) {
			System.out.println("Fatal Error number 45784: " + ioe.getMessage());
		}
	    
	}
	
	
	public void initialize() { //this initializes the arrays to be worked with later
		
		for(int i = 0; i < 81; i++) {
				if(buttons[i].getText() == " ") {
					cellNs[i] = 0;
					buttons[i].setFont(defaultfont);
				}
				 else {
					cellNs[i] = Integer.parseInt(buttons[i].getText());
					buttons[i].setFont(boldfont);			
				}
			}
		proofRead();
	}
	
	public void setButtonsEnabled(boolean io) {
		if(io == true) {
			load.setEnabled(true);
			save.setEnabled(true);
			calculate.setEnabled(true);
			reset.setEnabled(true);
			help.setEnabled(true);
			for(int i = 0; i< 81; i++) {
				buttons[i].setEnabled(true);
			}
		} else {
			load.setEnabled(false);
			save.setEnabled(false);
			calculate.setEnabled(false);
			reset.setEnabled(false);
			help.setEnabled(false);
			for(int i = 0; i< 81; i++) {
				buttons[i].setEnabled(false);
			}
		}
	}
	

	public void proofRead() {
		
		int count = 0;
		for(int i = 0; i < 81; i++) {
			if(buttons[i].getText() == " ")
				count++;
		}
		if (count == 81) {
			JOptionPane.showMessageDialog(null, "Must enter at least one number!");
			System.exit(0);
		}

				boolean[] status = checkCell();

				if(status[0] == false) {
					String message = "Please check your input:";
					if(status[1] == true) {
						for(int i = 0; i < 9; i++) {
							if(rcs[i] == true)
								rc = rc + (i+1) + " ";
						}
						message = message + rc;
					}
					if(status[2] == true) {
						for(int i = 0; i < 9; i++) {
							if(ccs[i] == true)
								cc = cc + (i+1) + " ";
						}
						message = message + cc;
					}
					if(status[3] == true) {
						for(int i = 0; i < 9; i++) {
							if(gcs[i] == true)
								gc = gc + (i+1) + " ";
						}
						message = message + gc;
					}
					JOptionPane.showMessageDialog(null, message);
					System.exit(0);
				}
			}

	
	public boolean[] checkCell() {
		int i;
		boolean[] status = {true, false, false, false}; //0: overall status, 1: rowconflict, 2: columnconflict, 3: gridconflict
		
		/*Determine which row, column, (sub)grid the current cell is in */
		
		int row, col, gridrow, gridcol, icell;
		for(int currCell = 0; currCell < 81; currCell++) {
			if(cellNs[currCell] != 0) {
				row = currCell / 9; //1st row is row 0, 2nd row is row 1, etc..
				col = currCell % 9; //similar as above
				
				gridrow = row / 3;
				gridcol = col / 3;
				
				for(i = 0; i < 9 ; i++) {
					icell = row * 9 + i;
					if ((cellNs[icell] == cellNs[currCell]) && (icell != currCell)) {
						status[0] = false;
						status[1] = true;
						rcs[row] = true;
					}
				}
				
				for(i = 0; i < 9; i ++) {
					icell = i * 9 + col;
					if ((cellNs[icell] == cellNs[currCell]) && (icell != currCell)) {
						status[0] = false;
						status[2] = true;
						ccs[col] = true;
					}
				}
				
				/*Check within grid */
				int irow = gridrow * 3; //global cell row (1st row in grid)
				int icol = gridcol * 3; //global cell column (1st column in grid)
				for(int r = 0; r < 3; r++) {
					for (int c = 0; c < 3; c++) {
						icell = ((irow+r) * 9) + (icol + c);
						if ((cellNs[icell] == cellNs[currCell]) && (icell != currCell)) {
							status[0] = false;
							status[3] = true;
							gcs[gridrow*3 + gridcol] = true;
						}
					}
				}
			}
		}
		return status;
	}
	
	public void printSolution(int[] cellN) {
		for(int i = 0 ; i < 81; i ++) {
			if(cellNs[i] == 0) buttons[i].setText(" ");
			else buttons[i].setText(Integer.toString(cellNs[i]));
		}
		if(calculation.finished == true) setButtonsEnabled(true);
	}
	
	public Sudoku() {
		super("Sudoku solver");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);

		GridLayout gl = new GridLayout(9,9,1,1);
		JPanel pane = new JPanel(); //ImagePanel("bg.png");
		JPanel calc = new JPanel();
		JPanel progbarpane = new JPanel();

		Color c = new Color(0xD3D3D3);
		
		pane.setLayout(gl);
      pane.setPreferredSize(new Dimension(500,500));
		progbarpane.setLayout(new GridLayout(1,1,0,0));
		
		MouseWheelListener mwl = new MouseWheelListener() {
			@SuppressWarnings("unused")
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int amount = e.getWheelRotation();
				JButton but = (JButton)e.getSource();
				String txt = (but.getText());
				int intTxt = (txt == " " ? 0 : Integer.parseInt(txt));
				int outNum;
				if(amount > 0 && calculate.isEnabled()) {
					switch(intTxt) {
						case 0: but.setText("1"); break;
						case 1: but.setText("2"); break;
						case 2: but.setText("3"); break;
						case 3: but.setText("4"); break;
						case 4: but.setText("5"); break;
						case 5: but.setText("6"); break;
						case 6: but.setText("7"); break;
						case 7: but.setText("8"); break;
						case 8: but.setText("9"); break;
						case 9: but.setText(" "); break;
                  default: but.setText(" "); break;
					} 
					but.setFont(boldfont);
				}
				if(amount < 0 && calculate.isEnabled()) {
					switch(intTxt) {
						case 0: but.setText("9"); break;
						case 1: but.setText(" "); break;
						case 2: but.setText("1"); break;
						case 3: but.setText("2"); break;
						case 4: but.setText("3"); break;
						case 5: but.setText("4"); break;
						case 6: but.setText("5"); break;
						case 7: but.setText("6"); break;
						case 8: but.setText("7"); break;
						case 9: but.setText("8"); break;
                  default: but.setText(" "); break;
					} 
					but.setFont(boldfont);
				}
			}
		};
		
		KeyListener buttonListener = new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				// empty
			}
			public void keyReleased(KeyEvent arg0) {
				// empty
			}
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() < '0' || e.getKeyChar() > '9')
					return;
				JButton but = (JButton)e.getSource();
				but.setFont(boldfont);
				if(e.getKeyChar() == '0')
					but.setText(" ");
				else but.setText(e.getKeyChar() + "");
			}
		};

			
		final ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(calculation.finished == true) {
					timer.stop();
					progress.setValue(81);
					printSolution(cellNs);
					setEnabled(true);
					toolkit.beep();
				}
				printSolution(cellNs);
				progress.setValue(calculation.currentCell);
				iterations.setText(Integer.toString(calculation.iteration));
			}
		};
		
		calculate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent2) {
				setButtonsEnabled(false);
				
				//calculate the solution:
				initialize();
				calculation = new CalculateSolution(cellNs);
				timer = new Timer(10, taskPerformer);
				timer.start();
			}
		});
		
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent2) {
				reset();
			}
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent savepuzz) {
				save();
			}
		});
		
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent loadpuzz) {
				progress.setValue(0);
				load();
			}
		});
		
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent helping) {
				JOptionPane.showMessageDialog(null, "Welcome to Sudoku 1.0 by Geoffrey Liu" +
						"\n-------------" +
						"\nControls:" +
						"\nUse scrollwheel and left mouse click + number pad" +
						"\nto change button values.");
			}
		});
		
		//add buttons to panel
		for (int i =0; i < 81; i++) {
			pane.add(buttons[i]);
			buttons[i].setBorderPainted(false);
			buttons[i].addKeyListener(buttonListener); 
			buttons[i].addMouseWheelListener(mwl);
			//buttons[i].addActionListener(clickListener);
			buttons[i].setBackground(c);
		}
				
		calc.add(itLabel);
		calc.add(iterations);
		calc.add(calculate);
		calc.add(reset);
		calc.add(save);
		calc.add(load);
		calc.add(help);
		calc.setSize(0,0);
		progbarpane.add(progress);
		
		add(pane, BorderLayout.NORTH);
		add(calc, BorderLayout.CENTER);
		add(progbarpane, BorderLayout.SOUTH);

		pack();
		setVisible(true);
		
	}

	public static void main(String[] args) {
		new Sudoku();
	}
}

@SuppressWarnings("serial")
class ImagePanel extends JPanel {
	  private Image img;
	  
	  public ImagePanel(String img) {
	    this(new ImageIcon(img).getImage());
	  }

	  public ImagePanel(Image img) {
	    this.img = img;
	    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);
	  }

	  public void paintComponent(Graphics g) {
	   g.drawImage(img, 0, 0, null);
	  }
}

class CalculateSolution implements Runnable {	
	int[] cellNums;
	boolean[] predefCells = new boolean[81]; //is predefined? true/false
	private Thread runner;
	boolean finished = false;
	int currentCell;
	int iteration = 0;

	
	public CalculateSolution(int[] inCellNumbers) {
		cellNums = inCellNumbers;

		if(runner == null) {
			runner = new Thread(this);
			runner.start();	
		}
	}
	
	public void run() {
		for(int i = 0; i < 81; i++) 
			predefCells[i] = (cellNums[i] == 0 ? false : true);
		currentCell = getNextFreeCellNumber(-1);
		if(currentCell == -1) {
			finished = true;
			return;
		}
		int firstFreeCell = currentCell;
		int lastFreeCell = getPrevFreeCellNumber(81);
		int avail;

		while(true) {
			avail = findAvailNumber(currentCell);
			if(avail == 0) {
				if (currentCell == firstFreeCell) {
					JOptionPane.showMessageDialog(null, "NO SOLUTION");
					break;
				} else {
					cellNums[currentCell] = 0;
					currentCell = getPrevFreeCellNumber(currentCell);
				}
			} else {
				cellNums[currentCell] = avail;
				if(currentCell == lastFreeCell) {
					break;
				}
				currentCell = getNextFreeCellNumber(currentCell);
			}
		}
		finished = true;
	}
	
	public int findAvailNumber(int currentCell) {
		iteration++;
		/*Determine which row, column, (sub)grid the current cell is in */
		
		int row, col, gridrow, gridcol, icell;
		
		row = currentCell / 9; //1st row is row 0, 2nd row is row 1, etc..
		col = currentCell % 9; //similar as above
		
		gridrow = row / 3;
		gridcol = col / 3;
		 
		/* First, we initialize an array containing unavailable numbers (extracted from predefined values). Then, we look thru that array and find the lowest possible number */
		

		int[] availNumbers = {1,2,3,4,5,6,7,8,9}; //the actual number array containing unavail. values
		
		int i; //loop var
		
		if(cellNums[currentCell] != 0) {
			for(i = 0; i < cellNums[currentCell]; i++)
				availNumbers[i] = 0;
		}
		/* Check across row, take out any predef numbers from availNumbers */
		for(i = 0; i < 9 ; i++) {
			icell = row * 9 + i;
			if ((cellNums[icell] != 0) && (icell != currentCell))
				availNumbers[cellNums[icell] - 1] = 0;
		}
		/* Check across each column, take out any predef numbers from availNumbers */
		for(i = 0; i < 9; i ++) {
			icell = i * 9 + col;
			if ((cellNums[icell] != 0) && (icell != currentCell))
				availNumbers[cellNums[icell] - 1] = 0;
		}
		/*Check within grid */
		int irow = gridrow * 3; //global cell row (1st row in grid)
		int icol = gridcol * 3; //global cell column (1st column in grid)
		for( int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				icell = ((irow+r) * 9) + (icol + c);
				if ((cellNums[icell] != 0) && (icell != currentCell))
					availNumbers[cellNums[icell] - 1] = 0;
			}
		}
		
		//check for lowest available number
		for( i =0; i < 9; i++) {
			if((availNumbers[i] != 0) && (availNumbers[i] != cellNums[currentCell])) {
				return availNumbers[i];
			}
		}
		return 0; //no available numbers!
	}
	
	public int getNextFreeCellNumber(int currentCell) {
		for(int i = currentCell+1; i < 81; i++) {
			if(predefCells[i] == false)
				return i;
		}
		return -1; // -1 = no next free cell!
	}
	
	public int getPrevFreeCellNumber(int currentCell) {
		for(int i = currentCell - 1; i >= 0; i--) {
			if(predefCells[i] == false)
				return i;
		}
		return -1; 
	}
}