/**
 * This class draws the output of the random blanket in the output panel of the BlanketFrame.  The blanket of interest
 * is passed to the constructor.  The constructor is used by BlanketFrame. 
 * 
 * @author Thayer Young 12/2013
 */

import java.awt.*;

import javax.swing.*;


@SuppressWarnings("serial")
public class BlanketOutputPanel extends JPanel
{
	/** For a color difference below this threshold the monochrome ramp will be increasingly displayed */
	public static final int COLOR_DIFFERENCE_THRESHOLD = 50;
	/** For a color difference below this threshold the monochrome ramp will be increasingly displayed */
	public static final float MUTED_BRIGHTNESS = 0.7f;
	/** factor that ensures muting of the getForcedMutedColor method */
	public static final float MUTING_MULTIPLIER = 0.8f;						
	
	private static int arrayExcptCount = 0;
	private static int divBy0ExcptCount = 0;
	
	private Blanket_v2 blanketToPlot;
	private int blanketNumber;
	private JLabel blanketLabel;
	private Color[] blanketColors;		// array of all colors in the blanket, use floats
	private Color[][] squareColors;		// color of each square in the blanket, use floats
	private boolean notRandom = false;	// when true the default sorted pattern is displayed, when false the random pattern is shown 
	private boolean autoColor = true; 	// will calculate a color ramp when true
	private float proportionAlt = 0.0f;	// the proportion of the color that is Alt (sharp), 0 is muted
	private int colorAngle = 0;			// the offset used for calculating custom color ramps
	private int lowAngle = 0, highAngle = 360;	// the limits (min and max) of the colorAngle as set by the colorRangeSlider
	private boolean colorRangeReversed = false; // when true color range calculated as 0 to lowAngle and highAngle to 360
	private boolean colorsDifferent = true;		// when false the monochromatic color ramp is used
	private int colorDifference = 400;	// The sum of the difference between the min and max blanketColors for R, G and B. 
										//  Calculated by the areColorsDifferent method
	
	/**
	 * Constructor
	 * @param blanketIn the Blanket_v2 object to be drawn
	 */
	public BlanketOutputPanel (Blanket_v2 blanketIn)
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(blanketLabel = new JLabel());
		blanketToPlot = blanketIn;
		setPreferredSize (new Dimension(40*blanketToPlot.getWidth()+4, 40*blanketToPlot.getLength()+18));
		setBackground (Color.white);
		try
		{	blanketColors = new Color[blanketToPlot.getColors()];
			squareColors = new Color[blanketToPlot.getLength()][blanketToPlot.getWidth()];
		}
		catch (ArrayIndexOutOfBoundsException exception)						
		{	System.out.println( "BlanketOutputPanel line 37-38: " + exception);
			arrayExcptCount++;
			checkExceptionMessages();
		}
		catch (NullPointerException exception)
		{	
		}
		finally
		{
			
		}
	}
	
	public void setBlanketNumber(int blanketNumberIn)
	{
		blanketNumber = blanketNumberIn;
		blanketLabel.setText("Blanket " + Integer.toString(blanketNumber));
	}
	
	public int getBlanketNumber()
	{
		return blanketNumber;
	}
	
	public Color[] getBlanketColors()
	{
		return blanketColors;
	}
	
	public void setBlanketColors(Color colorIn, int arrayPosition)
	{	try
		{
			blanketColors[arrayPosition] = colorIn;
		}
		catch (ArrayIndexOutOfBoundsException exception)						
		{	System.out.println( "BlanketOutputPanel line 65: " + exception);
			arrayExcptCount++;
			checkExceptionMessages();
		}
		catch (NullPointerException exception)
		{	
		}
		finally
		{
			
		}
	}
	
	public Blanket_v2 getBlanket()
	{
		return blanketToPlot;
	}
	
	public boolean getAutoColor()
	{
		return autoColor;
	}
	
	public void setAutoColor(boolean autoColorIn)
	{
		autoColor = autoColorIn;
	}
	
	public Color getSquareColor(int row, int column)
	{
		return squareColors[row][column];
	}
	
	public void setSquareColor(Color color, int row, int column)
	{
		squareColors[row][column] = color;
	}
	
	public float getProportionAlt()
	{
		return proportionAlt;
	}
	
	public void setProportionAlt(float sharperLessMuted)
	{
		proportionAlt = sharperLessMuted;
	}
	
	public int getColorAngle()
	{
		return colorAngle;
	}
	
	public void setColorAngle(int colorAngleIn)
	{
		colorAngle = colorAngleIn;
	}
	
	public int getLowAngle()
	{
		return lowAngle;
	}
	
	public void setLowAngle(int lowAngleIn)
	{
		lowAngle = lowAngleIn;
	}
	
	public int getHighAngle()
	{
		return highAngle;
	}
	
	public void setHighAngle(int highAngleIn)
	{
		highAngle = highAngleIn;
	}
	
	public boolean getColorRangeReversed()
	{
		return colorRangeReversed;
	}
	
	public void setColorRangeReversed(boolean rangeReversedIn)
	{
		colorRangeReversed = rangeReversedIn;
	}
	
	public boolean getColorsDifferent() 
	{
		return colorsDifferent;
	}

	public boolean isNotRandom()
	{
		return notRandom;
	}
	
	public void setNotRandom(boolean doNotRandomize)
	{
		notRandom = doNotRandomize;
	}
	
	public static int getArrayExcptCount ()
	{
		return arrayExcptCount;
	}
	
	public static void setarrayExcptCount (int setValue)
	{
		arrayExcptCount = setValue;
	}

	public static int getDivBy0ExcptCount ()
	{
		return divBy0ExcptCount;
	}
	
	public static void setdivBy0ExcptCount (int setValue)
	{
		divBy0ExcptCount = setValue;
	}
		
	/**
	 * 
	 * @Override
	 */
	@Override
	public void paintComponent (Graphics page)
	{		
		try
		{	
			int[][] patternToPlot = blanketToPlot.getPattern();
			super.paintComponent(page);
		
			for (int i=0; i < blanketToPlot.getLength(); i++)
			{
				for(int j=0; j < blanketToPlot.getWidth(); j++)
				{	
					Color setCol = new Color(0.5f, 0.5f, 0.5f);
					int value = patternToPlot[i][j];
					if (autoColor)
					{	setCol = getSetColorRampColor(value, 0);
						squareColors[i][j] = setCol;
					}
					else
						setCol = squareColors[i][j];
					page.setColor(setCol);
					page.fillRect((j*40+2),(i*40+16), 38, 38);	

				}
			}
		}
		catch (ArrayIndexOutOfBoundsException exception)						
		{	System.out.println( "BlanketOutputPanel line 115,118,121: " + exception);
			arrayExcptCount++;
			checkExceptionMessages();
		}
		catch (NullPointerException exception)
		{	
		}
	}
	
	/**
	 * Recalculates all of the BlanketOutputPanel's colors based on a colorAngle offset to the hue angle.  
	 * @param colorAngle
	 *   The offset to the hue angle, should be in the range 0 - 360.
	 */
	public void calculateBlanketColors(int colorAngle)
	{	
		Color multiColor = Color.BLACK, monoColor = Color.BLACK;

		// calculate Color from the combined color ramp (mute/bright) and set to blanketColors and squareColors arrays.
		for (int colorIndex = 0; colorIndex < blanketToPlot.getColors(); colorIndex++)
		{
			multiColor = getSetColorRampColor(colorIndex, colorAngle);
			setBlanketColors(multiColor, colorIndex);
		}
		setSquareColors();
		
		// if color range is too small, overwrite using the getMonochromaticColor method, 
		//   this is then repeated to incorporate the colorAngle offset.
		areColorsDifferent();
		if (!colorsDifferent)	
		{
			int numColors = blanketToPlot.getColors();
			float proportionMono = 1f - (float)colorDifference / COLOR_DIFFERENCE_THRESHOLD;
			for (int colorIndex = 0; colorIndex < numColors; colorIndex++)
			{
				monoColor = getMonochromeTone(numColors, colorAngle, proportionAlt, colorIndex, multiColor);
				Color setColor = getCombinedColor(monoColor, multiColor, proportionMono);
				setBlanketColors(setColor, colorIndex);
			}
			// enables the colorAngleSlider to shift the monochromatic tones
			Color[] blanketColorsCopy = new Color[numColors];
			int shift = Math.round(numColors * colorAngle / 360); 
			for (int fromIndex = 0; fromIndex < numColors; fromIndex++)
			{
				int toIndex = fromIndex + shift;
				if (toIndex > numColors - 1)
					toIndex -= numColors;
				blanketColorsCopy[toIndex] = blanketColors[fromIndex];
			}
			for (int index = 0; index < numColors; index++)
			{
				blanketColors[index] = blanketColorsCopy[index];
			}
			setSquareColors();
		}
	}
	
	/** 
	 *  Calls the getColorRampColor method, assigns the Color to the blanketColors array and returns that Color to whatever
	 *     callled getSetColorRampColor
	 * @param 
	 *   value the integer corresponding to the i'th color. 
	 * @param 
	 *   offset an integer between 0 and 360, which corresponds to an offset in degrees to the cosine function used
	 *   				for determining the color ramp. 
	 * @return 
	 *   a Color object for the corresponding value in the color ramp.
	 */
	private Color getSetColorRampColor(int value, int offset)
	{	
    	Color setCol = getColorRampColor(value, offset);	
    	
    	// assign Color object to array of Color objects
    	try
    	{	blanketColors[value] = setCol;
    	}
    	catch (ArrayIndexOutOfBoundsException exception)						
    	{	System.out.println( "BlanketOutputPanel line 146: ");
    		arrayExcptCount++;
    		checkExceptionMessages();
    	}
    	catch (NullPointerException exception)
    	{	
    	} 
    	return setCol;
	}
	
	/** 
	 *  Calculates and returns a color in a color ramp, for the specified color value and angle offset
	 * @param 
	 *   value the integer corresponding to the i'th color. 
	 * @param 
	 *   offset an integer between 0 and 360, which corresponds to an offset in degrees 
	 *    to the function used for determining the color ramp. 
	 * @return 
	 *   a Color object for the corresponding value in the color ramp.
	 */
	public Color getColorRampColor(int value, int offset) 
	{
		Color setCol = Color.BLACK;
		double angle = 1.0;
		
		if (value <= blanketToPlot.getColors())
			angle = getOffsetAngle(value, offset);
		setCol = getFinishedColor(angle, proportionAlt);
    	return setCol;	
	}
	
	/**
	 * Calculates the angle, incorporating any offset, that is used to find the color ramp color
	 * @param value
	 *   value the integer corresponding to the i'th color.
	 * @param offset
	 *   offset an integer between 0 and 360, which corresponds to an offset in degrees to the 
	 *   function used for determining the color ramp.
	 * @return
	 *   The corresponding angle for the combination of specific value and offset  
	 */
	private double getOffsetAngle(int value, int offset)
	{
		int numColors = blanketToPlot.getColors();
		double angle = 1.0;
		double calcValue = value;
		 	
		if (value <= numColors)
		{	
			if (numColors == 0)							
			{	angle = value * 360;
				divBy0ExcptCount++;
				checkExceptionMessages();
			}
			// the color range is limited by the lowValue and highValue from the colorRangeSlider, and shifted by offset (offsetValue)
			else
			{		
				calcValue = getCalcValue(value, offset);
				angle = calcValue * 360 / numColors;	// converts the calcValue to an angle relative to its position in numColors
			}				// TODO add check box to move sliders together. Or add option to turn strict limiting off, (add back checking of bright color ramp)
		}
		return angle;
	}
	
	/**
	 * Combines the value and offset into calcValue, taking the color range limitations into account. 
	 * @param value
	 *   The position of the desired value in the blanket's number of colors
	 * @param offset
	 *   The offset to the hue angle. 
	 * @return
	 *   The offset value.
	 */
	private double getCalcValue(int value, int offset)
	{
		int numColors = blanketToPlot.getColors();
		double lowValue = 0; 
		double highValue = numColors;
		double calcValue = value;
		 	
		if (value <= numColors)
		{	
			// the color range is limited by the lowValue and highValue from the colorRangeSlider, and shifted by offset (offsetValue)
			lowValue = (double) lowAngle * numColors / 360;
			highValue = (double) highAngle * numColors / 360;
			double offsetValue = (double) offset * numColors / 360;
			double sum = offsetValue + value;
			if (sum > numColors)		// Keeps black from happening, maintains color restriction during shifting
				sum = sum - numColors;	//   This causes an uneven color shift when colors reach their maximum.  An even alternative is: sum - (sum - numColors) which reverses the color ramp at max.
			// restricts the color ramp to colorRangeSlider values
			if(colorRangeReversed)
			{
				double rangeLength = lowValue + (numColors - highValue);
				calcValue = sum / numColors * rangeLength;
				if(calcValue > lowValue)
					calcValue = highValue + (calcValue - lowValue);
			}
			else 
				calcValue = sum / numColors * (highValue - lowValue) + lowValue;
		}
		return calcValue;
	}
	
	/**
	 * Calculates and returns a Color object of a tone in the monochrome ramp for the given Color.
	 * @param numColors
	 *   The number of tones in the ramp that the desired color is a part of.
	 * @param offsetAngle
	 *   The colorAngle from the colorAngleSlider.  
	 * @param proportionAlt
	 *   The proportion of the color that is from the bright color ramp
	 * @param value
	 *   The value, representing the position within numColors, for which the tone is calculated.
	 * @param colorIn
	 *   The template color from which the tone is to be calculated
	 * @return
	 *   The Color object of the desired tone.
	 */
	public static Color getMonochromeTone(int numColors, int offsetAngle, float proportionAlt, double value, Color colorIn)
	{			// make this work with colorAngle, shift tones from value to value.
				//   adding the offset to value only slightly increases the brightness, it does not shift tones between squares
		double position = 1.0 - value / numColors;
		float[] HSBArray= Color.RGBtoHSB(colorIn.getRed(), colorIn.getGreen(), colorIn.getBlue(), new float[3]);
		HSBArray[1] = (float)position;
		Color setCol = Color.getHSBColor(HSBArray[0], HSBArray[1], HSBArray[2]);
		return setCol; 
	}
	
	/**
	 * This method returns the finished Color that is displayed by BlanketFrame.  It incorporates values from 3 methods:
	 *  getMutedColor, getForcedMutedColor and getBrightColor.       
	 * @param angle
	 *   The hue angle of the desired color
	 * @param proportionBright
	 *   The relative contribution of bright to muted color ramps.  1 is bright, 0 is muted.
	 * @return
	 *   The finished Color object based on the input parameters.
	 */
	public static Color getFinishedColor(double angle, float proportionBright)
	{
		Color col = Color.BLACK;
		if (angle >= 41 && angle <= 79 || angle >= 161 && angle <= 199 || angle >= 281 && angle <= 319)
			col = getMutedColor(angle);				// in these ranges (c, m, y) getMutedColor is brighter than getForcedMutedColor
		else col = getForcedMutedColor(angle);
		Color altCol = getBrightColor(angle);
		Color setCol = getCombinedColor(altCol, col, proportionBright);
    	// Bright (Alt) and Mute color ramps combined, by multiplying by proportionAlt.  Convert to float by dividing by 255
   	return setCol;
	}
	
	/**
	 * This method combines two colors based on the given proportion of the first color.
	 * @param color1
	 *   The first color to be combined.
	 * @param color2
	 *   The second color to be combined.
	 * @param proportion1
	 *   The proportion of the first color that the returned color will have.  1.0 = 100%, 0.0 = 0%.
	 * @return
	 *   The combined Color object.
	 */
	public static Color getCombinedColor(Color color1, Color color2, float proportion1)
	{
    	float proportion2 = 1f - proportion1;
		float setRed = (proportion2 * color2.getRed() + proportion1 * color1.getRed()) / 255f;
		float setGreen = (proportion2 * color2.getGreen() + proportion1 * color1.getGreen()) / 255f;
		float setBlue = (proportion2 * color2.getBlue() + proportion1 * color1.getBlue()) / 255f;
    	Color setCol = new Color(checkFloat(setRed), checkFloat(setGreen), checkFloat(setBlue)); 
    	return setCol;
	}
	
	/**
	 * For a given input angle the corresponding muted color ramp Color object is returned.
	 *   This method uses an HSB conversion and forces the brightness to be MUTED_BRIGHTNESS.
	 *   This method gives brighter secondary colors and more muted primary colors than getForcedMutedColor.
	 * @param angle
	 *   The angle from 0 to 360 degrees for which the muted color ramp color is desired
	 * @return
	 *   The Color object that corresponds to the input angle
	 */
	public static Color getMutedColor(double angle)
	{
		// Muted Color Ramp:  this ramp is based on shifted cosine curves
		//   This ramp is calculated by first converting value to an angle in degrees, scaled to its position in numColors.
		//   The cosine is found for the angle (plus user offset) and converted from min -1 max 1 to min 0 max 1.
		//   An additional offset of 120 or 240 degrees is added to green and blue so that you get color not gray.
		//   The calculated value is then reduced by the forcedMuting factor to ensure that the ramp is always muted.
		double red, green, blue;
    	  red = Math.cos(Math.toRadians(angle + 0.0)) * 255 / 2 + 255 / 2;		// shifts the range to between 0 and 255
    	green = Math.cos(Math.toRadians(angle + 240.0)) * 255 / 2 + 255 / 2;
    	 blue = Math.cos(Math.toRadians(angle + 120.0)) * 255 / 2 + 255 / 2;
    	float[] HSBcol= Color.RGBtoHSB((int)Math.round(red), (int)Math.round(green), (int)Math.round(blue), new float[3]);
    	Color col = Color.getHSBColor(HSBcol[0], HSBcol[1], MUTED_BRIGHTNESS);
    	return col;
	}

	/**
	 * For a given input angle the corresponding muted color ramp Color object is returned.
	 *   This method multiplies each red, green, blue value by MUTING_MULTIPLIER before assigning to the Color object.
	 *   This method gives brighter primary colors and more muted secondary colors than getMutedColor.
	 * @param angle
	 *   The angle from 0 to 360 degrees for which the muted color ramp color is desired
	 * @return
	 *   The Color object that corresponds to the input angle
	 */
	public static Color getForcedMutedColor(double angle)
	{
		// Muted Color Ramp:  this ramp is based on shifted cosine curves
		//   This ramp is calculated by first converting value to an angle in degrees, scaled to its position in numColors.
		//   The cosine is found for the angle (plus user offset) and converted from min -1 max 1 to min 0 max 1.
		//   An additional offset of 120 or 240 degrees is added to green and blue so that you get color not gray.
		//   The calculated value is then reduced by the MUTING_MULTIPLIER to ensure that the ramp is always muted.
		double red, green, blue;
    	  red = Math.cos(Math.toRadians(angle + 0.0)) / 2.0 + 0.5;	 // shifts the range to between 0 and 1
    	green = Math.cos(Math.toRadians(angle + 240.0)) / 2.0 + 0.5;
    	 blue = Math.cos(Math.toRadians(angle + 120.0)) / 2.0 + 0.5;
    	Color col = new Color((float)red * MUTING_MULTIPLIER, (float)green * MUTING_MULTIPLIER, (float)blue * MUTING_MULTIPLIER);
    	return col;
	}
	
	/**
	 * For a given input angle the corresponding bright (alt) color ramp Color object is returned
	 * @param angle
	 *   The angle from 0 to 360 degrees for which the bright (alt) color ramp color is desired
	 * @return
	 *   The Color object that corresponds to the input angle
	 */
	public static Color getBrightColor(double angle) 		
	{		
    	// Bright Color Ramp (Alt):  this ramp maximizes bright colors by using a function composed of 
    	//   4 lines that coarsely approximate a cosine function.
    	//   The 4 lines are : two horizontal (y = 0 and y = 255) and two diagonal that connect the horizontal lines.
    	//   The calculation begins by scaling value to the range 0 to 12 based on its position in numColors.
    	//   The same is done to the offset based on its position in degrees, and it is added to the scaled valueAlt.
    	//   valueAlt is then fit to the function for each color.  As with Muted, the functions are shifted to avoid gray.
    	double valueAlt = 12.0 * angle / 360.0;  // double valueAlt = 12.0 * calcValue / numColors;
    	double redAlt = 0.0, greenAlt = 0.0, blueAlt = 0.0;
    	if(valueAlt >= 0 && valueAlt <= 2 || valueAlt >= 10 && valueAlt <= 12)  redAlt = 255;
    		else if(valueAlt >= 4 && valueAlt <= 8)  redAlt = 0;
    			else if(valueAlt > 2 && valueAlt < 4)  redAlt = valueAlt * -127.5 + 510;
    				else if(valueAlt > 8 && valueAlt < 10)  redAlt = valueAlt * 127.5 - 1020;
    	if(valueAlt >= 2 && valueAlt <= 6)  greenAlt = 255;
    		else if(valueAlt == 0 || valueAlt >= 8 && valueAlt <= 12)  greenAlt = 0;
    			else if(valueAlt > 0 && valueAlt < 2)  greenAlt = valueAlt * 127.5 + 0;
    				else if(valueAlt > 6 && valueAlt < 8)  greenAlt = valueAlt * -127.5 + 1020;
    	if(valueAlt >= 6 && valueAlt <= 10)  blueAlt = 255;
    		else if(valueAlt >=0 && valueAlt <= 4)  blueAlt = 0;
    			else if(valueAlt > 4 && valueAlt < 6)  blueAlt = valueAlt * 127.5 - 510;
    				else if(valueAlt > 10 && valueAlt <= 12)  blueAlt = valueAlt * -127.5 + 1530;
    	Color altCol = new Color((int)Math.round(redAlt), (int)Math.round(greenAlt), (int)Math.round(blueAlt));
    	return altCol;
	}

	/**
	 * Used to determine if the monochrome ramp should be used.  If the colorDifference falls below the COLOR_DIFFERENCE_THRESHOLD
	 *   colorsDifferent is set to false, and the monochrome ramp is phased in.  colorDifference is calculated as the sum of the
	 *   differences between the minimum and maximum values for each RGB color in blanketColors. 
	 * @return
	 */
	public boolean areColorsDifferent()
	{
		int numColors = blanketToPlot.getColors();
		int[] red = new int[numColors];
		int[] green = new int[numColors];
		int[] blue = new int[numColors];	
		
		for (int colorIndex = 0; colorIndex < numColors; colorIndex++)
		{	
			Color color = blanketColors[colorIndex];
			red[colorIndex] = color.getRed();
			green[colorIndex] = color.getGreen();
			blue[colorIndex] = color.getBlue();
		}
		red = sortMaxMin(red);
		green = sortMaxMin(green);
		blue = sortMaxMin(blue);
		
		colorDifference = (red[numColors - 1] - red[0]) + (green[numColors - 1] - green[0]) + (blue[numColors - 1] - blue[0]);
		// if the sum of the differences between max and min for each color (red, green, blue) is less than the COLOR_DIFFERENCE_THRESHOLD
		//    then colors are NOT different: (tells getColorRampColor to use the monochromatic ramp).
		if (colorDifference < COLOR_DIFFERENCE_THRESHOLD)
			 colorsDifferent = false;
		else colorsDifferent = true;
		return colorsDifferent;
	}
	
	/**
	 * Returns the given array with the minimum value in the 0 position and the maximum value in the last position.
	 * @param intArray
	 * @return
	 */
	private int[] sortMaxMin(int[] intArray)
	{
		int max = 0;
		int min = 255;
		
		for (int current : intArray)
		{
			if (current > max)
				max = current;
			if (current < min)
				min = current;
		}
		intArray[intArray.length - 1] = max;
		intArray[0] = min;
		
		return intArray;
	}
	
	/**
	 * Verifies that an input float value is between 0 and 1, if not it returns 0 or 1. 
	 * @param floatIn
	 * @return
	 */
	public static float checkFloat(float floatIn)
	{
		if (floatIn > 1f)
			floatIn = 1f;
		if (floatIn < 0f)
			floatIn = 0f;
		return floatIn;
	}
	
	/**
	 * Fills the squareColors 2d-array with the appropriate Color objects. For each cell in the Blanket_v2 pattern it matches
	 *   the color value in the pattern, to the corresponding Color object in the blanketColors array, and copies that Color 
	 *   object into the squareColors array's cell.
	 */
	private void setSquareColors() 
	{		
		for (int row = 0; row < blanketToPlot.getLength(); row++)
		{
			for (int column = 0; column < blanketToPlot.getWidth(); column++)
			{
				int cellValue = blanketToPlot.getPattern()[row][column];
				Color color = blanketColors[cellValue];
				squareColors[row][column] = color;
			}
		}
	}
	
	private static void checkExceptionMessages()
	{
		if (BlanketOutputPanel.getArrayExcptCount() == 1 && BlanketOutputPanel.getDivBy0ExcptCount() == 1)
		{	
			JOptionPane.showMessageDialog(null, "There was an error reading the input file: Array Index Out of Bounds and Division by Zero Exceptions" 
    			+ "\nIt is recommended you Start Over, but the portion of the file that could be read is presented\n");
			System.out.println("AIOoBE is true");
		}
		else if (BlanketOutputPanel.getArrayExcptCount() == 1)
		{	
			JOptionPane.showMessageDialog(null, "There was an error reading the input file: Array Index Out of Bounds Exception" 
    			+ "\nIt is recommended you Start Over, but the portion of the file that could be read is presented\n");
			System.out.println("AIOoBE is true");
		}	
		else if (BlanketOutputPanel.getDivBy0ExcptCount() == 1)
		{	
			JOptionPane.showMessageDialog(null, "There was an error reading the input file: Arithmetic Exception: Division by zero Exception" 
    			+ "\nIt is recommended you Start Over, but the portion of the file that could be read is presented\n");
			System.out.println("DivBy0E is true");
		}
	}
}