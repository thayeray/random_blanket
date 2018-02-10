/**
 * This class represents the highest level of the GUI for the random blanket program.  It uses the two 
 *   lower level containers of the GUI: BlanketInputPanel and BlanketOutputPanel, guiding their use of the 
 *   Blanket_v2 class.
 *
 * @author Thayer Young 12/2013   
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class BlanketFrame extends JFrame
{
	private final static int MAX_BLANKETS = 20;
	
	private JFrame frame;
	private JPanel mainPanel; 
	private BlanketInputPanel input;
	private SliderListener sliderChanged;
	private JPanel output;
	private JScrollPane outputScrollPane;
	private Dimension maxOutputDim = new Dimension(0,0);	
	private int x_margin = 20;								
	private int y_margin = 30;								
	private float marginMultiplier = 0.5f;					
	private BlanketOutputPanel[] blanketsOnDisplay = new BlanketOutputPanel[MAX_BLANKETS];
	private int blanketCount = 0;
	private int editBlanketIndex = -1;// TODO change this to get which blanket to work on or add point & click selection of edit blanket 
	private String directory = "";
	private boolean activelyDeleting = false;
	
	/**
	 * Constructor for the GUI window
	 */
	public BlanketFrame ()
	{
		// set up GUI components
		frame = new JFrame("Random Blanket");				// frame is the outermost container, it contains the mainPanel
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		mainPanel = new JPanel();							// mainPanel contains the input and outputScrollPanel
		input = new BlanketInputPanel();					// input is where all of the buttons and text fields are located
		output = new JPanel();								// output contains the graphical representation of the blankets
		establishActions();								// call the establishActions method to set up the listeners.
        mainPanel.setLayout(new FlowLayout());
        mainPanel.add(input);							// output is in the outputScrollPanel and the later
		frame.add(mainPanel);							//   is added to mainPanel by the pressGoButton() method
		frame.getContentPane();
		frame.pack();
		frame.setVisible (true);
	}	// end constructor

	
	/** This method sets up the check boxes, buttons and sliders.  The buttons are triggered by both clicking the button 
	 *    or pressing the enter key when the button is focused. 
	 */
	public void establishActions()
	{		
		AbstractAction goButtonPressed = new AbstractAction()  
		{	@Override
			public void actionPerformed(ActionEvent event) 
        	{	pressGoButton();
        	}
		};
		input.getGoButton().addActionListener(goButtonPressed);
		makeEnterResponsive(input.getGoButton(), goButtonPressed);
		
		AbstractAction checkDoNotRandomizeCB = new AbstractAction()
		{	@Override
			public void actionPerformed(ActionEvent event)
			{	doNotRandomize();
			}
		};
		input.getDoNotRandomizeCB().addActionListener(checkDoNotRandomizeCB);
				
		AbstractAction saveButtonPressed = new AbstractAction()  
		{	@Override
			public void actionPerformed(ActionEvent event) 
			{
				try
				{	pressSaveButton();
				}
				catch (IOException exception)
				{
					System.out.println(exception);
				}
			}
		};
		input.getSaveButton().addActionListener(saveButtonPressed);
		makeEnterResponsive(input.getSaveButton(), saveButtonPressed);
	
		AbstractAction openButtonPressed = new AbstractAction()  
		{	@Override
			public void actionPerformed(ActionEvent event) 
			{
				try
				{	pressOpenButton();
				}
				catch (IOException exception)
				{
					System.out.println(exception);
				}
			}
		};
		input.getOpenButton().addActionListener(openButtonPressed);
		makeEnterResponsive(input.getOpenButton(), openButtonPressed);
		
		AbstractAction deleteButtonPressed = new AbstractAction()  
		{	@Override
			public void actionPerformed(ActionEvent event) 
        	{	pressDeleteButton();
        	}
		};
		input.getDeleteButton().addActionListener(deleteButtonPressed);
		makeEnterResponsive(input.getDeleteButton(), deleteButtonPressed);
	
		AbstractAction startOverPressed = new AbstractAction()  
		{	@Override
			public void actionPerformed(ActionEvent event) 
        	{	pressStartOver();
        	}
		};
		input.getStartOverButton().addActionListener(startOverPressed);
		makeEnterResponsive(input.getStartOverButton(), startOverPressed);
				
		// Sets up a listener for the sliders: colorAngleSlider and muteSharpSlider
		sliderChanged = new SliderListener();
		input.getMuteSharpSlider().addChangeListener(sliderChanged);
		input.getColorAngleSlider().addChangeListener(sliderChanged);
		input.getColorRangeSlider().addChangeListener(sliderChanged);
		
		@SuppressWarnings("unused")
		AbstractAction checkLockRangeCB = new AbstractAction()
		{	@Override
			public void actionPerformed(ActionEvent event)
			{	lockRangeSlider();
			}
		};
		input.getLockRangeCB().addActionListener(checkDoNotRandomizeCB);
		
		AbstractAction checkReverseColorRangeCB = new AbstractAction()
		{	@Override
			public void actionPerformed(ActionEvent event)
			{	reverseColorRange();
			}
		};
		input.getReverseColorRangeCB().addActionListener(checkReverseColorRangeCB);
	}  // end establishActions()
	
	/**
	 * Used by the establishActions method to add the functionality that the passed JComponent's events are triggered by pressing the enter key.
	 * @param component
	 *   The component to add enter functionality to.
	 * @param action
	 *   The component's associated AbstractAction.
	 * @return
	 */
	private JComponent makeEnterResponsive(JComponent component, AbstractAction action)
	{
		component.getInputMap(JComponent.WHEN_FOCUSED)
				 .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0), "Enter_pressed");
		component.getActionMap().put("Enter_pressed", action);
		return component;
	}
	
	/**
	 * Governs the response to moving the sliders: muteSharpSlider and colorAngleSlider
	 *
	 */
	public class SliderListener implements ChangeListener 
	{
		
		@Override
	    public void stateChanged(ChangeEvent event) 
		{   
			int lowerAngle = input.getColorRangeSlider().getValue();
			int upperAngle = input.getColorRangeSlider().getUpperValue();
			
			if (blanketCount > 0)
			{													
				int colorAngle = input.getColorAngleSlider().getValue();
				BlanketOutputPanel blanketPanel = blanketsOnDisplay[editBlanketIndex];
				blanketPanel.setAutoColor(false);	
				int oldLowAngle = blanketPanel.getLowAngle();
				int oldHighAngle = blanketPanel.getHighAngle();
				if(event.getSource() == input.getMuteSharpSlider())				// response to changing the muteSharpSlider
				{
					blanketPanel.setProportionAlt((float)input.getMuteSharpSlider().getValue() / 100);
					input.setColorRangeSlider(blanketPanel);
				}
				if(event.getSource() == input.getColorAngleSlider())				// response to changing the colorAngleSlider
				{
					blanketPanel.setColorAngle(colorAngle);
					input.setColorRangeSlider(blanketPanel);
				}
				if(event.getSource() == input.getColorRangeSlider() && !activelyDeleting)	// response to changing a thumb in the colorRangeSlider
				{
					
					if (lowerAngle != oldLowAngle)							// response to changing the lower thumb
					{	blanketPanel.setLowAngle(lowerAngle);
/*						if (input.getLockRangeCB().isSelected())
						{	int lockedUpper = upperAngle + (lowerAngle - oldLowAngle);
							if (lockedUpper > 360)
							{	blanketPanel.setLowAngle(lockedUpper - 360);
								lockedUpper = lowerAngle;
							}
							blanketPanel.setHighAngle(lockedUpper);
						}
 */
						input.setColorRangeSlider(blanketPanel);
					}
					if (upperAngle != oldHighAngle)							// response to changing the upper thumb
					{	blanketPanel.setHighAngle(upperAngle);
/*						if (input.getLockRangeCB().isSelected())
						{	int lockedLower = lowerAngle + (upperAngle - oldHighAngle);
							if (lockedLower < 0)
							{	blanketPanel.setHighAngle(360 + lockedLower);
								lockedLower = upperAngle;
							}
							blanketPanel.setHighAngle(lockedLower);
						}
 */						
						input.setColorRangeSlider(blanketPanel);
					}
				}
				blanketPanel.calculateBlanketColors(colorAngle);
				input.repaint();
				output.repaint();
			}
			else
			{
				if(event.getSource() == input.getColorRangeSlider() && !activelyDeleting)	// response to changing a thumb in the colorRangeSlider
				{
					input.getColorRangeSliderUI().setLowerThumbColor(BlanketOutputPanel.getFinishedColor(lowerAngle, (float)input.getMuteSharpSlider().getValue() / 100));
					input.getColorRangeSliderUI().setUpperThumbColor(BlanketOutputPanel.getFinishedColor(upperAngle, (float)input.getMuteSharpSlider().getValue() / 100));
				}
			}
		}
	} // end SliderListener class
	
	private void doNotRandomize()
	{
		if (blanketCount > 0)
		{
			BlanketOutputPanel blanketPanel = blanketsOnDisplay[editBlanketIndex];
			if (input.getDoNotRandomizeCB().isSelected())
			{	blanketPanel.setNotRandom(true);
				blanketPanel.getBlanket().generate();
			}
			if (!input.getDoNotRandomizeCB().isSelected())
			{	blanketsOnDisplay[editBlanketIndex].setNotRandom(false);
				blanketPanel.getBlanket().randomize();
			}
			blanketsOnDisplay[editBlanketIndex].calculateBlanketColors(input.getColorAngleSlider().getValue());
			input.repaint();
			output.repaint();
		}
	}
	
	private void lockRangeSlider()	// TODO fill this stub
	{
/*		if (blanketCount > 0)
		{	Container rightAlt = input.getRightContainer();
			if(input.getLockRangeCB().isSelected())
			{	
				rightAlt.setVisible(false);
				input.setRightContainer(rightAlt);
				input.revalidate();
				input.repaint();
			}
			if(!input.getLockRangeCB().isSelected())
				input.getColorRangeSlider().setVisible(true);
		} 
 */
	}
	
	/**
	 * Switches how the color range is calculated, when true it is: 0 to lowAngle and highAngle to 360, when false: lowAngle to highAngle
	 */
	private void reverseColorRange()
	{
		if (blanketCount > 0)
		{
			if(input.getReverseColorRangeCB().isSelected())
			{	blanketsOnDisplay[editBlanketIndex].setColorRangeReversed(true);
				blanketsOnDisplay[editBlanketIndex].setAutoColor(false);
			}
			if(!input.getReverseColorRangeCB().isSelected())
				blanketsOnDisplay[editBlanketIndex].setColorRangeReversed(false);
			blanketsOnDisplay[editBlanketIndex].calculateBlanketColors(input.getColorAngleSlider().getValue());
			input.repaint();
			output.repaint();
		}
	}
	
	/**
	 * When the "Make a blanket!" button is clicked (or focused and enter pressed), the 3 basic parameters are checked 
	 *   for validity.  If valid the blanket is generated & displayed, and if invalid a corresponding error dialog is 
	 *   displayed.
	 */
	private void pressGoButton()			
	{	
		// if input is valid generate the random blanket
		if(input.getColors() <= input.getRows() * input.getColumns() 
				&& input.getColors() > 0 
				&& input.getRows() * input.getColumns() > 0 
				&& blanketCount < MAX_BLANKETS)
		{
			Blanket_v2 prettyBlanket = new Blanket_v2(input.getRows(), input.getColumns(), input.getColors());
			prettyBlanket.generate();
			if (!input.getDoNotRandomizeCB().isSelected())
				prettyBlanket.randomize();
			System.out.print("Blanket Count pre: " + blanketCount);
			blanketCount++;
			editBlanketIndex++;
			System.out.println("  post: " + blanketCount);
			BlanketOutputPanel newGuy = new BlanketOutputPanel(prettyBlanket);
			newGuy.setBlanketNumber(blanketCount);
			// if the sliders have been adjusted before a blanket is created it assigns the slider values to the new blanket
			if(    input.getMuteSharpSlider().getValue() > 0 
				|| input.getColorAngleSlider().getValue() > 0 
				|| input.getColorRangeSlider().getValue() > 0 
				|| input.getColorRangeSlider().getUpperValue() < 360 
				|| input.getReverseColorRangeCB().isSelected() 
				|| input.getDoNotRandomizeCB().isSelected())  
			{	newGuy.setAutoColor(false);	
			}	
			newGuy.setProportionAlt((float)input.getMuteSharpSlider().getValue() / 100);
			newGuy.setColorAngle(input.getColorAngleSlider().getValue());
			newGuy.setLowAngle(input.getColorRangeSlider().getValue());
			newGuy.setHighAngle(input.getColorRangeSlider().getUpperValue());
			newGuy.setColorRangeReversed(input.getReverseColorRangeCB().isSelected());
			newGuy.setNotRandom(input.getDoNotRandomizeCB().isSelected());
			newGuy.calculateBlanketColors(input.getColorAngleSlider().getValue());
			blanketsOnDisplay[blanketCount - 1] = newGuy;
			output.removeAll();
			for (int panelIndex = blanketCount - 1; panelIndex > -1; panelIndex--)
				output.add(blanketsOnDisplay[panelIndex]);
			output.revalidate();
			if(blanketCount == 1)
			{
				makeScrollPane();
				mainPanel.add(outputScrollPane);
			}
			setScroll();
			frame.getContentPane();
			frame.pack();
			frame.setVisible (true);
			frame.repaint();			
		}				
		// if input is invalid an error dialog is shown
		else errorDialog();
	} // end pressGoButton 

    public void makeScrollPane()
    {
        // instantiate scrollPane
     	outputScrollPane = new JScrollPane(output);
    }
	
	/**
	 * This method checks whether or not the output panel is too large for the default screen, adds JScrollBars when 
	 *   necessary and sets the preferred size of the outputScrollPane accounting for screen insets but not multiple
	 *   monitors.
	 */
	private void setScroll()
	{
     	Dimension screenDimensions = new Dimension();
		
     	frame.pack();
		Insets insets = frame.getToolkit().getScreenInsets(frame.getGraphicsConfiguration());
		int excludeWidth = input.getWidth() + insets.left + insets.right + x_margin;
		float excludeHeight = (float)insets.top + insets.bottom + (float)y_margin * marginMultiplier;
		Dimension desiredOutputDim = setDesiredOutputDim();
     	screenDimensions.setSize(java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width, 
     							java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height);
     	maxOutputDim.setSize(screenDimensions.getWidth() - excludeWidth, screenDimensions.getHeight() - excludeHeight);
		if (desiredOutputDim.width > maxOutputDim.width && desiredOutputDim.height > maxOutputDim.height)
		{
			outputScrollPane.setPreferredSize(maxOutputDim);
			makeHScrollBar();
			makeVScrollBar();
		}
		else if (desiredOutputDim.width > maxOutputDim.width)
		{
			outputScrollPane.setPreferredSize(new Dimension(maxOutputDim.width, desiredOutputDim.height));
			makeHScrollBar();
		}
		else if (desiredOutputDim.height > maxOutputDim.height)
		{
			outputScrollPane.setPreferredSize(new Dimension(desiredOutputDim.width, maxOutputDim.height));
			makeVScrollBar();
		}
		else outputScrollPane.setPreferredSize(desiredOutputDim);
		
		outputScrollPane.revalidate();
	}
	
	/**
	 * Determines the optimal size for the output panel and returns a Dimension object
	 * @return a Dimension object of the optimal size for the outputScrollPane
	 */
	private Dimension setDesiredOutputDim()	// TODO set margins smaller for Windows
	{
		int width = 0, height = 0;
		Dimension desiredOutputDim = new Dimension();
		
		frame.pack();				
		for (int blanketIndex = 0; blanketIndex < blanketCount ; blanketIndex++)
		{	
			width += blanketsOnDisplay[blanketIndex].getWidth();
			if (blanketsOnDisplay[blanketIndex].getHeight() > height)
				height = blanketsOnDisplay[blanketIndex].getHeight();
		}		
		desiredOutputDim.setSize((float)width + x_margin + (float)x_margin * blanketCount * marginMultiplier,
										height + y_margin);		
		return desiredOutputDim;		
	}
	
	/**
	 * Creates a horizontal scroll bar in the outputScrollPane
	 */
	private void makeHScrollBar()
	{
		JScrollBar outputHScroll = new JScrollBar(JScrollBar.HORIZONTAL);
		outputScrollPane.setHorizontalScrollBar(outputHScroll);
		outputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		System.out.println("Horizontal scroll added");
	}

	/**
	 * Creates a vertical scroll bar in the outputScrollPane
	 */
	private void makeVScrollBar()
	{
		JScrollBar outputVScroll = new JScrollBar(JScrollBar.VERTICAL); 
		outputScrollPane.setVerticalScrollBar(outputVScroll);
		outputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		System.out.println("Vertical scroll added");
	}
	
	/**
	 * This support method generates the error dialog for pressGoButton
	 */
	private void errorDialog() 
	{  
		if (blanketCount < MAX_BLANKETS)
		{
			// error dialog: if an input value is 0
			if (input.getRows() * input.getColumns() * input.getColors() <= 0) 
				{	String message = "Parameters can not equal 0.  Please change the number of ";
					int errorCount = 0;
			
					if (input.getRows() <= 0)
					{	errorCount++;
						input.setFocus(input.getRowsL());
						message += "rows";
						if (input.getColumns() <= 0 || input.getColors() <= 0)
							message += ", ";
						else message += ".";
					}
					if (input.getColumns() <= 0)
					{	errorCount++;
						if (errorCount == 1) 
							input.setFocus(input.getColumnsL());
						message += "columns";
						if (input.getColors() <= 0)
							message += ", ";
						else message += ".";
					}
					if (input.getColors() <= 0)
					{	errorCount++;
						if (errorCount == 1) 
							input.setFocus(input.getColorsL());	
						message += "colors.";		
					}
					JOptionPane.showMessageDialog(null, message);
				}			
			// error dialog: if there are more colors than squares	
			else if(input.getColors() > input.getRows() * input.getColumns())
			{	
				input.setFocus(input.getColorsL());
				JOptionPane.showMessageDialog(null, "For " + input.getRows() + " rows by " + input.getColumns() 
						+ " columns, you can have no more than " + input.getRows() * input.getColumns() + " colors.");
			}
		}
		else JOptionPane.showMessageDialog(null, "You have " + MAX_BLANKETS 
											+ " blankets, please delete some or start a new session");
	} // end errorDialog
	
	
	private void pressSaveButton() throws IOException
	{
		String errorMessageTXT;									 
		JFileChooser saveChooser = new JFileChooser();			// save file dialog
		final String DEFAULT_FILE_NAME = "BlanketFile";			// default file name part 1
		final String DEFAULT_FILE_EXTENSION = ".txt";			// default file name part 2
		long timeStamp = System.currentTimeMillis() / 1000;		// default file name part 3
		String userDir;
		if (directory == "")
			userDir = System.getProperty("user.dir");
		else userDir = directory;
		
		File defaultFile = new File(userDir, DEFAULT_FILE_NAME + "_" + timeStamp + DEFAULT_FILE_EXTENSION);	// combine default file name parts
		saveChooser.setSelectedFile(defaultFile);				// set default file name
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Random Blanket TXT files", "txt");
		saveChooser.setFileFilter(filter);
		int status = saveChooser.showSaveDialog(null);
		if (status != JFileChooser.APPROVE_OPTION)
			System.out.println("No File Chosen");
		else
		{
			directory = saveChooser.getSelectedFile().getParent();	// set default directory for the future
			int overwrite;
			File saveFile = saveChooser.getSelectedFile();
			saveFile = checkFileExtension(saveFile);
			if (saveFile.exists())
			{	overwrite = JOptionPane.showConfirmDialog(null, "That file already exists.  Do you want to delete those blankets?");
				if (overwrite == JOptionPane.YES_OPTION)
				{	errorMessageTXT = writeTXT(saveFile);			// calls method to write the TXT file;
				}
				else errorMessageTXT = "Chose not to overwrite";
			}
			else errorMessageTXT = writeTXT(saveFile);			// calls method to write the TXT file;
			
			
			System.out.println(errorMessageTXT);
		}
		// errorMessageSVG = writeSVG();  // calls SVG writing method
	}
	
	private File checkFileExtension(File inFile)
	{
		String name = inFile.getName().toLowerCase();
		int nameLength = name.length();
		String path = inFile.getPath();
		File outFile;
		
		if (nameLength > 4)
			if (!name.endsWith(".txt"))
				outFile = new File(path + ".txt");
			else outFile = inFile;
		else outFile = new File(path + ".txt");
		
		return outFile;
	}
	
	@SuppressWarnings("unused")
	private String writeSVG() throws IOException
	{
		String errorMessage = "SVG: No error";
		for (int blanketIndex = 0; blanketIndex < blanketCount ; blanketIndex++)
			errorMessage = "SVG: " + blanketsOnDisplay[blanketIndex].getBlanket().writeSVG(blanketIndex + 1);
		return errorMessage;
	}
	
	private String writeTXT(File saveFile) throws IOException
	{		
		String errorMessage = "TXT save: No error";
		String encoding = "UTF-8";
		File txtFile = saveFile;  		// new File(fileName)
		boolean test = txtFile.createNewFile();
		if (test == true)
			System.out.println("TXT file created successfully");
		else
			System.out.println("TXT file will be overwritten");
		
	    Writer out = new OutputStreamWriter(new FileOutputStream(txtFile), encoding);
	    try 
	    {	
	    	out.write(blanketCount + "\n\n");
			for (int blanketIndex = 0; blanketIndex < blanketCount; blanketIndex++)
			{
				Blanket_v2 currentBlanket = blanketsOnDisplay[blanketIndex].getBlanket();
				int[][] currentPattern = currentBlanket.getPattern();
				int rows = currentBlanket.getLength();
				int columns = currentBlanket.getWidth();
				int colors = currentBlanket.getColors();
				int vPosition, hPosition;
				
				out.write(blanketIndex + "\n");
				out.write(rows + "\t" + columns + "\t" + colors + "\n");
				
				for (vPosition = 0; vPosition < rows; vPosition++)
				{
					for (hPosition = 0; hPosition < columns; hPosition++)
					{
						out.write(vPosition + "\t" + hPosition + "\t" + currentPattern[vPosition][hPosition] + "\n");
					}
				}
				for (Color color : blanketsOnDisplay[blanketIndex].getBlanketColors())
				{
					out.write(color.getRed() + "\t" + color.getGreen() + "\t" + color.getBlue() + "\n");
				}
				out.write("\n");
			}
	    }    
	    catch (IOException exception)
	    {
	    	errorMessage = "TXT: " + exception.toString();
	    }
	    finally 
	    {
	    	out.close();
	    }	    
	   	return errorMessage;    
	} // end write TXT
	
	private void pressOpenButton() throws IOException
	{
		String errorMessageTXT;
		String userDir;
		
		if (directory == "")										// if no file has been opened or closed yet
			userDir = System.getProperty("user.dir");				// gets user's working directory 
		else userDir = directory;									// session working directory
		JFileChooser openChooser = new JFileChooser(userDir);			// creates the open file dialog, defaulting to userDir
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Random Blanket TXT files", "txt");
		openChooser.setFileFilter(filter);
		int status = openChooser.showOpenDialog(null);					// displays the dialog to the user
		if (status != JFileChooser.APPROVE_OPTION)
			errorMessageTXT = "No File Chosen";
		else
		{	directory = openChooser.getSelectedFile().getParent();	// set default directory for the future
			File openFile = openChooser.getSelectedFile();
			errorMessageTXT = readTXT(openFile);			// calls method to read the TXT file;
		}
		System.out.println(errorMessageTXT);
	}
	
	private String readTXT(File openFile)  throws IOException		// TODO this is not working: loads, but does not display
	{															
		String errorMessage = "TXT open: No error";
		File txtFile = openFile;		
	    Scanner scan = new Scanner(txtFile);
	    
	    try 
	    {	
System.out.println("BlanketFrame in readTXT try");
	    	resetAllAndStartOver();
	    	blanketCount = scan.nextInt();
	    	editBlanketIndex = blanketCount - 1;
			for (int blanketIndex = 0; blanketIndex < blanketCount; blanketIndex++)
			{
				int blanketNumber = scan.nextInt();
				int rows = scan.nextInt();
				int columns = scan.nextInt();
				int colors = scan.nextInt();
				Blanket_v2 currentBlanket = new Blanket_v2(rows, columns, colors);
				for (int square = 0; square < (rows * columns); square++)
				{
					int row = scan.nextInt();
					int column = scan.nextInt();
					int color = scan.nextInt();
					currentBlanket.setPattern(row, column, color);
				}
				BlanketOutputPanel currentBOP = new BlanketOutputPanel (currentBlanket);
				currentBOP.setBlanketNumber(blanketNumber + 1);
				for (int colorObject = 0; colorObject < colors; colorObject++)
				{
					int red = scan.nextInt();
					int green = scan.nextInt();
					int blue = scan.nextInt();
					Color currentColor = new Color(red, green, blue);
					currentBOP.setBlanketColors(currentColor, colorObject);
				}
				blanketsOnDisplay[blanketIndex] = currentBOP;
				output.add(currentBOP);
			}
			output.revalidate();
			frame.getContentPane();
			frame.pack();			
			output.repaint();
	    }
	    catch (InputMismatchException exception)
	    {
	    	JOptionPane.showMessageDialog(null, "The input file has the wrong format: " + exception 
	    			+ "\nThe memory has been cleared");
	    	resetAllAndStartOver();
	    }
	    catch (java.util.NoSuchElementException exception)
	    {
	    	JOptionPane.showMessageDialog(null, "The input file has the wrong length: " + exception 
	    			+ "\nThe memory has been cleared");
	    	resetAllAndStartOver();	    		    	
	    }
	    catch (ArithmeticException exception)
	    {
	    	JOptionPane.showMessageDialog(null, "There was an error reading the input file: " + exception 
	    			+ "\nThe memory has been cleared");
	    	resetAllAndStartOver();
	    }
	    catch (NegativeArraySizeException exception)
	    {
	    	JOptionPane.showMessageDialog(null, "There was an error reading the input file: " + exception 
	    			+ "\nThe memory has been cleared");
	    	resetAllAndStartOver();	    	
	    }
	    catch (ArrayIndexOutOfBoundsException exception)
	    {	
	    	JOptionPane.showMessageDialog(null, "There was an error reading the input file: " + exception 
	    			+ "\nThe memory has been cleared");
	    	resetAllAndStartOver();	    	
	    }
	    catch (IllegalArgumentException exception)
	    {
	    	JOptionPane.showMessageDialog(null, "The input file has the wrong number format: " + exception 
	    			+ "\nThe memory has been cleared");
	    	resetAllAndStartOver();
	    }
	    catch (NullPointerException exception)
	    {
	    	JOptionPane.showMessageDialog(null, "The input file has the wrong length: " + exception 
	    			+ "\nThe memory has been cleared");
	    	resetAllAndStartOver();	    	
	    }
	   	return errorMessage;
	} // end openTXT 
	
	/**
	 * Controls the response to pressing the delete button.  The delete button deletes a single blanket, indicated by 
	 *   the return from the getBlanketToDelete() dialog.  
	 */
	private void pressDeleteButton()	// delete a single blanket
	{
		activelyDeleting = true;
		int blanketToDeleteI = getBlanketToDelete();					// calls the method to get the blanket to delete
		if (blanketToDeleteI > 0 && blanketToDeleteI <= blanketCount)	// checks for a legitimate blanket number 
		{	
			System.out.println("Blanket to delete: " + blanketToDeleteI);
			// copies the blankets to keep, while overwriting the delete blanket
			for(int blanketIndex = blanketToDeleteI - 1; blanketIndex < MAX_BLANKETS; blanketIndex++)
			{	if (blanketIndex + 1 < blanketCount)	// works on all but the last blanket	
					blanketsOnDisplay[blanketIndex] = blanketsOnDisplay[blanketIndex + 1];
				
				else 									// sets the last blanket to null
					blanketsOnDisplay[blanketIndex] = null;
			}
			// if deleting the blanket that is being edited, set the sliders to reflect the blanket before it 
			if (blanketToDeleteI == editBlanketIndex + 1 && blanketCount > 1)
			{
				blanketCount--;		
				editBlanketIndex--;	
				BlanketOutputPanel blanketPanel = blanketsOnDisplay[blanketCount - 1];
				input.getColorAngleSlider().setValue(blanketPanel.getColorAngle());
				float propAlt = BlanketOutputPanel.checkFloat(blanketPanel.getProportionAlt());
				int muteSharp = Math.round(propAlt * 100);
				input.getMuteSharpSlider().setValue(muteSharp);
				input.setColorRangeSlider(blanketPanel);
				input.getReverseColorRangeCB().setSelected(blanketPanel.getColorRangeReversed());
				input.setDoNotRandomizeCB(blanketPanel.isNotRandom());
			}				
			else if (blanketToDeleteI == editBlanketIndex + 1 && blanketCount == 1)	// if only blanket is being deleted, set controls to default
				 {	
				 	 blanketCount--;		
				 	 editBlanketIndex--;
				 	 input.getColorAngleSlider().setValue(0);
				 	 input.getMuteSharpSlider().setValue(0);
				 	 input.resetColorRangeSlider();
				 	 input.getReverseColorRangeCB().setSelected(false);
				 	 input.setDoNotRandomizeCB(false);
				 }
				 else if (blanketToDeleteI < editBlanketIndex + 1)	// delete blanket is less than edit blanket
				 	  {
					 	  blanketCount--;		
					 	  editBlanketIndex--;
				 	  }
				 	  else blanketCount--;	// delete blanket is greater than edit blanket
			
			output.removeAll();	// start of the redrawing process
			for (int panelIndex = blanketCount - 1; panelIndex > -1; panelIndex--)
			{
				blanketsOnDisplay[panelIndex].setBlanketNumber(panelIndex + 1);	// reassigns blanketNumber after deletion
				output.add(blanketsOnDisplay[panelIndex]);
			}
			output.revalidate();
			outputScrollPane.revalidate();
			mainPanel.revalidate();
			setScroll();
			frame.getContentPane();
			frame.pack();			
			output.repaint();
		}
		else System.out.println("Blanket: " + blanketToDeleteI + " will not be deleted");
		
		if (blanketCount < 1)
			mainPanel.remove(outputScrollPane);
		activelyDeleting = false;
	}
	
	private int getBlanketToDelete()
	{
		String blanketToDeleteS;
		int blanketToDeleteI = -1;
		int confirmDelete;
		
		do
		{
			blanketToDeleteS = JOptionPane.showInputDialog("Which blanket would you like to delete? (1 to " + blanketCount 
					+ ") (You can cancel before it is final):");
			try
			{
				if(blanketToDeleteS.length() > 0)
					blanketToDeleteI = Integer.parseInt(blanketToDeleteS);
				if (blanketToDeleteI <= blanketCount && blanketToDeleteI > 0)
					confirmDelete = JOptionPane.showConfirmDialog(null, "Do you want to delete blanket " + blanketToDeleteI);
				else if (blanketCount > 0) 
				{	confirmDelete = JOptionPane.NO_OPTION;
					JOptionPane.showMessageDialog(null, "Please enter a number between 1 and " + blanketCount + ".");
				}
				else 
				{	confirmDelete = JOptionPane.CANCEL_OPTION;
					JOptionPane.showMessageDialog(null, "You do not have any blankets to delete.");
				}
			}
			catch (NumberFormatException nfe)
			{	confirmDelete = JOptionPane.NO_OPTION;
				JOptionPane.showMessageDialog(null, "Please enter a number between 1 and " + blanketCount + ".");
			}
			catch (NullPointerException npe)
			{	confirmDelete = JOptionPane.CANCEL_OPTION;
			}
		} while (confirmDelete == JOptionPane.NO_OPTION);	

		return blanketToDeleteI;
	}
	
	
	private void pressStartOver()
	{
		int confirmStartOver = JOptionPane.showConfirmDialog(null, "Do you want to erase all blankets and start over?");
		if (confirmStartOver == JOptionPane.YES_OPTION)
			resetAllAndStartOver();		
	}
	
	private void resetAllAndStartOver()
	{
		try
		{
		input.setRows(BlanketInputPanel.DEFAULT_ROWS);
		input.setColumns(BlanketInputPanel.DEFAULT_COLUMNS);
		input.setColors(BlanketInputPanel.DEFAULT_COLORS);
		input.setRowsL(true);
		input.setColorsL(true);
		input.setColumnsL(true);
		input.setRowsT(null);
		input.setColumnsT(null);
		input.setColorsT(null);
		input.getColorAngleSlider().setValue(0);
		input.getMuteSharpSlider().setValue(0);
		input.resetColorRangeSlider();
		input.getReverseColorRangeCB().setSelected(false);
		input.setDoNotRandomizeCB(false);
		Blanket_v2.setarrayExcptCount(0);
		Blanket_v2.setdivBy0ExcptCount(0);
		BlanketOutputPanel.setarrayExcptCount(0);
		BlanketOutputPanel.setdivBy0ExcptCount(0);
		for (int eraseIndex = 0; eraseIndex < MAX_BLANKETS; eraseIndex++)
		{	
			blanketsOnDisplay[eraseIndex] = null;
		}
		blanketCount = 0;
		editBlanketIndex = -1;
		output.removeAll();
		output.revalidate();
		input.revalidate();
		mainPanel.remove(outputScrollPane);
		mainPanel.revalidate();
		frame.pack();			
		input.repaint();
		output.repaint();
		input.setFocus(input.getRowsL());
		}
		catch(NullPointerException exception)
		{
			
		}
	}
}