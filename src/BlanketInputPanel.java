/**
 * This class handles input from the user for the three basic parameters of the random blanket, rows, columns, colors.
 * It extends JPanel for the setup of the GUI and implements KeyListener for responding to inputs to the JTextFields
 * for the three basic parameter values.  
 * 
 * @author Thayer Young 12/2013
 */

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class BlanketInputPanel extends JPanel implements KeyListener
{
	public static final int DEFAULT_ROWS = 12;
	public static final int DEFAULT_COLUMNS = 12;
	public static final int DEFAULT_COLORS = 12;
	public static final Color DEFAULT_THUMB_COLOR = new Color(204, 51, 51);
	
	private int rows, columns, colors;
	private int colorRangeLength;
	
	private JLabel rowsL, columnsL, colorsL;
	private JTextField rowsT, columnsT, colorsT;
	private JCheckBox doNotRandomizeCB;
	private JButton goButton;
	private JPanel sliderPanel;
	private JSlider colorAngleSlider, muteSharpSlider;
	private RangeSlider colorRangeSlider;
	private RangeSliderUI colorRangeSliderUI;
	private Container right;
	private JCheckBox lockRangeCB;
	private JCheckBox reverseColorRangeCB;
	private JButton saveButton, openButton, deleteButton, startOverButton;
	
	/** 
	 * Constructor for the input area of the random blanket program
	 */
	public BlanketInputPanel()
	{
		// set data values
		rows = DEFAULT_ROWS;
		columns = DEFAULT_COLUMNS;
		colors = DEFAULT_COLORS;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentY(TOP_ALIGNMENT);	
		this.add(makeParameterContainer());						// contains the text boxes, goButton and doNotRandomizeCheckBox
		this.add(makeSliderPanel());							// contains the JSliders for controlling color
		this.add(Box.createRigidArea(new Dimension(40,10)));
		this.add(makeButtonContainer());
	}

	public int getRows() 
	{
		return rows;
	}

	public void setRows(int rows) 
	{
		this.rows = rows;
	}

	public int getColumns() 
	{
		return columns;
	}

	public void setColumns(int columns) 
	{
		this.columns = columns;
	}

	public int getColors() 
	{
		return colors;
	}

	public void setColors(int colors) 
	{
		this.colors = colors;
	}
	
	public int getColorRangeLength()
	{
		return colorRangeLength;
	}
	
	public void setColorRangeLength(int colorRangeLengthIn)
	{
		colorRangeLength = colorRangeLengthIn;
	}
	
	public JButton getGoButton() {
		return goButton;
	}

	public void setGoButton(JButton goButton) {
		this.goButton = goButton;
	}
	
	public JCheckBox getDoNotRandomizeCB()
	{
		return doNotRandomizeCB;
	}
	
	public void setDoNotRandomizeCB(boolean doNotRandomizeIn)
	{
		doNotRandomizeCB.setSelected(doNotRandomizeIn);;
	}

	public JPanel getSliderPanel() {
		return sliderPanel;
	}

	public void setSliderPanel(JPanel sliderPanel) {
		this.sliderPanel = sliderPanel;
	}

	public JSlider getColorAngleSlider() {
		return colorAngleSlider;
	}

	public void setColorAngleSlider(JSlider colorAngleSlider) {
		this.colorAngleSlider = colorAngleSlider;
	}

	public JSlider getMuteSharpSlider() {
		return muteSharpSlider;
	}

	public void setMuteSharpSlider(JSlider muteSharpSlider) {
		this.muteSharpSlider = muteSharpSlider;
	}

	public RangeSlider getColorRangeSlider() {
		return colorRangeSlider;
	}

	public void setColorRangeSlider(RangeSlider colorRangeSlider) {
		this.colorRangeSlider = colorRangeSlider;
	}

	public RangeSliderUI getColorRangeSliderUI() {
		return colorRangeSliderUI;
	}

	public void setColorRangeSliderUI(RangeSliderUI colorRangeSliderUI) {
		this.colorRangeSliderUI = colorRangeSliderUI;
	}
	
	public Container getRightContainer()
	{
		return right;
	}
	
	public void setRightContainer(Container rightIn)
	{
		right = rightIn;
	}

	public JCheckBox getReverseColorRangeCB() {
		return reverseColorRangeCB;
	}

	public void setReverseColorRangeCB(JCheckBox reverseColorRangeCB) {
		this.reverseColorRangeCB = reverseColorRangeCB;
	}
	
	public JCheckBox getLockRangeCB() {
		return lockRangeCB;
	}

	public void setLockRangeCB(JCheckBox lockRangeCB) {
		this.lockRangeCB = lockRangeCB;
	}

	public JButton getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(JButton saveButton) {
		this.saveButton = saveButton;
	}

	public JButton getOpenButton() {
		return openButton;
	}

	public void setOpenButton(JButton openButton) {
		this.openButton = openButton;
	}

	public JButton getDeleteButton() {
		return deleteButton;
	}

	public void setDeleteButton(JButton deleteButton) {
		this.deleteButton = deleteButton;
	}

	public JButton getStartOverButton() {
		return startOverButton;
	}

	public void setStartOverButton(JButton startOverButton) {
		this.startOverButton = startOverButton;
	}

	public Component getRowsL()
	{
		return rowsL;
	}
	
	public Component getColumnsL()
	{
		return columnsL;
	}
	
	public Component getColorsL()
	{
		return colorsL;
	}

	public Component getRowsT()
	{
		return rowsT;
	}
	
	public Component getColumnsT()
	{
		return columnsT;
	}
	
	public Component getColorsT()
	{
		return colorsT;
	}
	
	public void setRowsL(boolean reset)
	{	if (reset)
			rowsL.setText("Rows: ");
		else
			rowsL.setText("Rows: " + Integer.toString(rows));
	}
	
	public void setColumnsL(boolean reset)
	{	if (reset)
			columnsL.setText("Columns: ");
		else
			columnsL.setText("Columns: " + Integer.toString(columns));
	}
	
	public void setColorsL(boolean reset)
	{	if (reset)
			colorsL.setText("Colors: ");
		else
			colorsL.setText("Colors: " + Integer.toString(colors));
	}

	public void setRowsT(String RowsTin)
	{
		rowsT.setText(RowsTin);
	}
	
	public void setColumnsT(String ColumnsTin)
	{
		columnsT.setText(ColumnsTin);
	}
	
	public void setColorsT(String ColorsTin)
	{
		colorsT.setText(ColorsTin);
	}	
	
	/**
	 * 
	 * @return
	 */
	public Container makeParameterContainer()
	{
		// set up text entry area
		rowsL= new JLabel(    "Rows:      ");
		columnsL = new JLabel("Columns:   ");
		colorsL = new JLabel( "Colors     ");
		rowsT = new JTextField ();
		rowsT.addKeyListener(this);
		columnsT = new JTextField ();
		columnsT.addKeyListener(this);
		colorsT = new JTextField ();
		colorsT.addKeyListener(this);
		goButton = new JButton("Make a Blanket!");			// These buttons are added to input, but are created here so
		goButton.setPreferredSize(new Dimension(160,30));	//   they can control the output of the GUI 
		goButton.setAlignmentX(CENTER_ALIGNMENT);
		doNotRandomizeCB = new JCheckBox("Do not randomize");// when checked: displays the default (sorted) color order.
		doNotRandomizeCB.setAlignmentX(LEFT_ALIGNMENT);
		
		Container parameterContainer = new Container();
		parameterContainer.setLayout(new BoxLayout(parameterContainer, BoxLayout.Y_AXIS));
		JLabel title = new JLabel("Blanket Parameters");
		title.setPreferredSize(new Dimension(160,30));
		title.setAlignmentX(CENTER_ALIGNMENT);
		Container textEntryContainer= new Container();
		textEntryContainer.setLayout(new GridLayout(3,2));
		textEntryContainer.setPreferredSize(new Dimension(80,80));
		textEntryContainer.add(rowsL);
		textEntryContainer.add(rowsT);
		textEntryContainer.add(columnsL);
		textEntryContainer.add(columnsT);
		textEntryContainer.add(colorsL);
		textEntryContainer.add(colorsT);
		Container randomCBContainer = new Container();
		randomCBContainer.setLayout(new BoxLayout(randomCBContainer, BoxLayout.X_AXIS));
		randomCBContainer.add(doNotRandomizeCB);
		parameterContainer.add(title);
		parameterContainer.add(textEntryContainer);
		parameterContainer.add(goButton);
		parameterContainer.add(randomCBContainer);
		return parameterContainer;
	}
	
	/**
	 * Sets up the panel for the color sliders and the reverse color range check box
	 */
	public JPanel makeSliderPanel()			// TODO add a check box to "move sliders together", to lock colorRangeSliders so they move  
	{										//    together, and/or switches to a single thumb monoChromatic hue slider
		sliderPanel = new JPanel();			// contained by input, contains the JSliders for controlling color				 
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		sliderPanel.setBorder(new TitledBorder("Color Ramp"));
		muteSharpSlider = new JSlider(JSlider.VERTICAL, 0, 100, 0);
		colorAngleSlider = new JSlider(JSlider.VERTICAL, 0, 360, 0);
		colorRangeSlider = new RangeSlider(0, 360);
		colorRangeSliderUI = new RangeSliderUI(colorRangeSlider);
		colorRangeSlider.updateUI(colorRangeSliderUI);
		labelColorRangeSlider();
		lockRangeCB = new JCheckBox("Lock range length");
		reverseColorRangeCB = new JCheckBox("Reverse color range");
				
		Container left = new Container();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		JLabel bright = new JLabel("Bright");
		bright.setAlignmentX(CENTER_ALIGNMENT);
		left.add(bright);
		muteSharpSlider.setAlignmentX(CENTER_ALIGNMENT);
		left.add(muteSharpSlider, left);
		JLabel muted = new JLabel("Muted");
		muted.setAlignmentX(CENTER_ALIGNMENT);
		left.add(muted);
		
		Container center = new Container();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		JLabel shift1 = new JLabel("Shift");
		shift1.setAlignmentX(CENTER_ALIGNMENT);
		center.add(shift1);
		colorAngleSlider.setAlignmentX(CENTER_ALIGNMENT);
		center.add(colorAngleSlider);
		JLabel shift2 = new JLabel("Colors");
		shift2.setAlignmentX(CENTER_ALIGNMENT);
		center.add(shift2);
		
		right = new Container();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		JLabel limit = new JLabel("Limit");
		limit.setAlignmentX(CENTER_ALIGNMENT);
		right.add(limit);
		resetColorRangeSlider();		
		right.add(colorRangeSlider);
		JLabel colors = new JLabel("Colors");
		colors.setAlignmentX(CENTER_ALIGNMENT);
		right.add(colors);
		
		Container sliderContainer = new Container();
		sliderContainer.setLayout(new BoxLayout(sliderContainer, BoxLayout.X_AXIS));
		sliderContainer.add(left);
		sliderContainer.add(Box.createRigidArea(new Dimension(10,40)));
		sliderContainer.add(center);
		sliderContainer.add(Box.createRigidArea(new Dimension(10,40)));
		sliderContainer.add(right);
		
		Container lockCBContainer = new Container();
		lockCBContainer.setLayout(new BoxLayout(lockCBContainer, BoxLayout.X_AXIS));
		lockRangeCB.setHorizontalTextPosition(SwingConstants.LEFT);
		lockRangeCB.setHorizontalAlignment(SwingConstants.RIGHT);
		lockCBContainer.add(lockRangeCB);
		
		Container reverseCBContainer = new Container();
		reverseCBContainer.setLayout(new BoxLayout(reverseCBContainer, BoxLayout.X_AXIS));
		reverseColorRangeCB.setHorizontalTextPosition(SwingConstants.LEFT);
		reverseColorRangeCB.setHorizontalAlignment(SwingConstants.RIGHT);
		reverseCBContainer.add(reverseColorRangeCB);
		
		sliderPanel.add(sliderContainer);
		sliderPanel.add(Box.createRigidArea(new Dimension(10,5)));
		sliderPanel.add(lockCBContainer);
		sliderPanel.add(reverseCBContainer);
		
		return sliderPanel;
	}
	
	/**
	 * Creates the colored labels for the colorRangeSlider 
	 */
	private void labelColorRangeSlider()
	{
		colorRangeSlider.setMajorTickSpacing(30);
		colorRangeSlider.setPaintTicks(true);
		Hashtable<Integer, JComponent> table = new Hashtable<Integer, JComponent>();
		String[] labelText = {"R","O","Y","GY","G","GB","C","AB","B","V","M","RR","R"}; 
		Color[] labelColor = {Color.RED,Color.ORANGE,Color.YELLOW,new Color(127,255,0),Color.GREEN,
						  	  new Color(0,255,127),Color.CYAN,new Color(0,127,255),Color.BLUE,
						  	  new Color(127,0,255),Color.MAGENTA,new Color(255,0,127),Color.RED};
		int number = 0;
		for (int index = 0; index < 13; index++)
		{
			number = 30 * index;
			JLabel label = new JLabel(labelText[index]);
			label.setForeground(labelColor[index]);
			table.put(new Integer(number), label);
		}			
		colorRangeSlider.setLabelTable(table);
		colorRangeSlider.setPaintLabels(true); 
	}
	
	public Container makeButtonContainer()
	{
		Container buttonContainer = new Container();
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
		saveButton = new JButton("Save");
		saveButton.setAlignmentX(CENTER_ALIGNMENT);
		openButton = new JButton("Open");
		openButton.setAlignmentX(CENTER_ALIGNMENT);
        Container saveOpen = new Container();
        saveOpen.setLayout(new BoxLayout(saveOpen, BoxLayout.X_AXIS));
        saveOpen.add(saveButton);
        saveOpen.add(openButton);
        saveOpen.setPreferredSize(new Dimension(160,30));
		deleteButton = new JButton("Delete a Blanket");
		deleteButton.setPreferredSize(new Dimension(160,30));
		deleteButton.setAlignmentX(CENTER_ALIGNMENT);
		startOverButton = new JButton("Start Over");
		startOverButton.setPreferredSize(new Dimension(160,30));
		startOverButton.setAlignmentX(CENTER_ALIGNMENT);
   
		buttonContainer.add(saveOpen);
		buttonContainer.add(Box.createRigidArea(new Dimension(40,10)));
		buttonContainer.add(deleteButton);
		buttonContainer.add(startOverButton);  
		return buttonContainer;
	}
	
	@Override
	public void keyPressed(KeyEvent event) 
	{
		// uses default	
	}
	
	/**
	 * BlanketInputPanel.java implements the KeyListener interface.  This is an override of the default keyReleased
	 *   method.  It checks keyed input into the JTextBoxes for numeric characters, delete or right/left arrow keys.
	 *   It passes the KeyEvent to the keySource method for further handling.
	 *    
	 * @Override
	 */
	@Override
	public void keyReleased(KeyEvent event) // checks keyed input and calls keySource method as appropriate.
	{	
System.out.println("Key released: " + event.getKeyCode());		
		try
		{	// do this if entered key is not numeric or delete or left or right arrow
			if (event.getKeyCode() < 48 		// less than 0
					&& event.getKeyCode() != 8 	// not delete
					&& event.getKeyCode() != 10 	// not enter
					&& event.getKeyCode() != 37 // not left arrow
					&& event.getKeyCode() != 39 // not right arrow
					|| event.getKeyCode() > 57) // greater than 9
				keySource(event, 1);		
			
			// do this if entered key is numeric, but not enter, left or right arrow
			else if (event.getKeyCode() != 37 && event.getKeyCode() != 39 && event.getKeyCode() != 10)  
				keySource(event, 0);
			else if (event.getKeyCode() == 10)
				keySource(event, 2); 
		}
		catch(NumberFormatException nfe)
		{			
		}
	}

	@Override
	public void keyTyped(KeyEvent event) 
	{
		// uses default
	}
	
	/**
	 * Support method for handling KeyEvents from the override of keyReleased. 
	 * @param event a KeyEvent object generated by the keyReleased method override.
	 * @param isNumeric set to true when 0 to 9 is keyed, set to false for other keys, except arrows and delete.  
	 * @return returns the JTextField object that generated the event. 
	 */
	private void keySource(KeyEvent event, int type)
	{	
		if (event.getSource() == rowsT)
		{									
			if (type == 0)
			{	rows = Integer.parseInt(rowsT.getText());
				rowsL.setText("Rows: " + rows);
			}
			else if (type == 1) 
				rowsT.setText(Integer.toString(rows));
			else if (type == 2)
				setFocus(columnsL);
		}
		else if (event.getSource() == columnsT)
		{	
			if (type == 0)
			{	columns = Integer.parseInt(columnsT.getText());
				columnsL.setText("Columns: " + columns);
			}
			else if (type == 1)  
				columnsT.setText(Integer.toString(columns));
			else if (type == 2)
				setFocus(colorsL);
		}
		else 
		{	
			if (type == 0)
			{	colors = Integer.parseInt(colorsT.getText());	
				colorsL.setText("Colors: " + colors);
			}
			else  if (type == 1) 
				colorsT.setText(Integer.toString(colors));
			else if (type == 2)
				setFocus(colorsT);
		}			
	}
	
	public void setFocus(Component componentBefore)
	{
		DefaultKeyboardFocusManager focus = new DefaultKeyboardFocusManager();
		
		focus.focusNextComponent(componentBefore); // switches the focus to the next listened component
	}
	
	/**
	 * Sets and restores the defaults for the colorRangeSlider
	 */
	public void resetColorRangeSlider()	
	{
		colorRangeSlider.setOrientation(JSlider.VERTICAL);
		colorRangeSlider.setValue(0);
		colorRangeSlider.setUpperValue(360);
		colorRangeSliderUI.setLowerThumbColor(DEFAULT_THUMB_COLOR);
		colorRangeSliderUI.setUpperThumbColor(DEFAULT_THUMB_COLOR);
		colorRangeSlider.setAlignmentX(CENTER_ALIGNMENT);
	}
	
	/**
	 * Changes the settings of the colorRangeSlider to reflect the input BlanketOutputPanel 
	 * @param blanketPanel
	 * The BlanketOutputPanel to set the colorRangeSlider for
	 */
	public void setColorRangeSlider(BlanketOutputPanel blanketPanel)
	{			
		int lowAngle = blanketPanel.getLowAngle();
		int highAngle = blanketPanel.getHighAngle();
		float proportionBright = blanketPanel.getProportionAlt();
		colorRangeSlider.setValue(lowAngle);
		colorRangeSliderUI.setLowerThumbColor(BlanketOutputPanel.getFinishedColor(lowAngle, proportionBright));
		colorRangeSlider.setUpperValue(highAngle);
		colorRangeSliderUI.setUpperThumbColor(BlanketOutputPanel.getFinishedColor(highAngle, proportionBright));
	}

}