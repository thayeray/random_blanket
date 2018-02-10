/**
 * Defines the Blanket object attributes, constructors and methods
 * The Blanket object is used to create random blanket patterns
 * @author Thayer Young  Last Edited: 9/29/2013
 */

import java.util.Arrays;
import java.util.Random;
import java.io.*;

import javax.swing.JOptionPane;


public class Blanket_v2
{	
	private static int arrayExcptCount = 0;
	private static int divBy0ExcptCount = 0;
	
	//********************
	// Data Declarations
	//********************
	private int length;
	private int width;
	private int colors;
	private int[][] pattern;
	private Random randomGenerator = new Random();

	//********************
	// Constructors
	//********************
	public Blanket_v2()
	{
		length = 6;
		width = 6;
		colors = 6;
		pattern = new int[length][width];
	}

	public Blanket_v2(int len, int wid, int col) 
	{
		length = len;
		width = wid;
		colors = col;
		try
		{	pattern = new int[len][wid];
		}													
		catch (ArrayIndexOutOfBoundsException exception)
		{	System.out.println("Blanket_v2 line 45:" + exception);
			arrayExcptCount++;
			checkExceptionMessages();
		}
		catch (NullPointerException exception)
		{	
		}
	}
	
	//********************
	// Methods
	//********************
	public void generate() 	// Fills the 2D array "pattern" with integers corresponding to the number of "colors"
	{
		int v_count, h_count;
		for (v_count = 0; v_count < length; v_count++)
		{
			for (h_count = 0; h_count < width; h_count++)
			{
				int location = v_count*width + (h_count+1);
				try
				{
					pattern[v_count][h_count] = initialColor(location);
				}															
				catch (ArrayIndexOutOfBoundsException exception)
				{	System.out.println("Blanket_v2 line 60:" + exception);
					arrayExcptCount++;
					checkExceptionMessages();
				}
				catch (NullPointerException exception)
				{	
				}
			}
		}
	}
	
	private int initialColor(int vh_location)
	{
		int rightColor = 0;
		
		int lengthWidth = length * width;
		
		if (colors == 0)
		{	colors = 1;
			divBy0ExcptCount++;
			checkExceptionMessages();
		}
		int modulus = lengthWidth % colors;				
		int set1count = (lengthWidth - modulus) / colors;	
		int set1multiplier = colors - modulus;
		
		if (set1count == 0 || set1count + 1 == 0)
		{	set1count = 1;
			divBy0ExcptCount++;
			checkExceptionMessages();
		}
		int versionA = (vh_location - 1) / set1count;
		int versionB = set1multiplier + ((vh_location-1) - set1count*set1multiplier) / (set1count+1);
		
		if (versionA < set1multiplier)	
			rightColor = versionA;
		else
			rightColor = versionB;
				
		return rightColor;
	}
	
	public void randomize()
	{
		int iterations = 4 * length * width;
		
		for (int iter = 0; iter < iterations; iter++)
		{
			int rowFrom = randomGenerator.nextInt(length);
			int columnFrom = randomGenerator.nextInt(width);
			int rowTo = randomGenerator.nextInt(length);
			int columnTo = randomGenerator.nextInt(width);
			try
			{	int moveFrom = pattern[rowFrom][columnFrom];
				int moveTo = pattern[rowTo][columnTo];
		
				pattern[rowTo][columnTo] = moveFrom;
				pattern[rowFrom][columnFrom] = moveTo;
			}															
			catch (ArrayIndexOutOfBoundsException exception)
			{	System.out.println("Blanket_v2 line 105-109:" + exception);
				arrayExcptCount++;
				checkExceptionMessages();
			}
			catch (NullPointerException exception)
			{	
			}
		}
	}
	
	public int[][] getPattern()
	{
		return pattern;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getColors()
	{
		return colors;
	}
	
	public void setPattern(int row, int column, int color)
	{	try
		{
			pattern[row][column] = color;
		}															
		catch (ArrayIndexOutOfBoundsException exception)
		{	System.out.println("Blanket_v2 line 140:" + exception);
			arrayExcptCount++;
			checkExceptionMessages();
		}
		catch (NullPointerException exception)
		{	
		}
	}
	
	public static int getArrayExcptCount()
	{
		return arrayExcptCount;
	}
	
	public static void setarrayExcptCount(int setValue)
	{
		arrayExcptCount = setValue;
	}

	public static int getDivBy0ExcptCount()
	{
		return divBy0ExcptCount;
	}
	
	public static void setdivBy0ExcptCount(int setValue)
	{
		divBy0ExcptCount = setValue;
	}
	
	public String writeSVG(int blanketNumber) throws IOException
	{
		final String DEFAULT_FILE_NAME = "Blanket";
		final String DEFAULT_FILE_EXTENSION = ".svg";
		
		String errorMessage = "No Error";
		String encoding = "UTF-8";
		String rgb;
		String fileName;
		long timeStamp = System.currentTimeMillis() / 1000;
		
		fileName = DEFAULT_FILE_NAME + "_" + timeStamp +"_" + blanketNumber + "_"+ DEFAULT_FILE_EXTENSION;
		File svgFile = new File(fileName);
		boolean test = svgFile.createNewFile();
		if (test == true)
			System.out.println("SVG file created successfully");
		else
			System.out.println("SVG file will be overwritten");
		
	    Writer out = new OutputStreamWriter(new FileOutputStream(svgFile), encoding);
	    try 
	    {	    
	    	out.write("<?xml version=\"1.0\" standalone=\"no\"?>" + "\r\n");
	    	out.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">" + "\r\n");
	    	out.write("<svg width=\"800\" height=\"800\" xmlns=\"http://www.w3.org/2000/svg\">" + "\r\n");

			int vPosition, hPosition;
			for (vPosition = 0; vPosition < length; vPosition++)
			{
				for (hPosition = 0; hPosition < width; hPosition++)
				{
					int value = pattern[vPosition][hPosition];			
					
					if (colors == 0)
					{	colors = 1;
						divBy0ExcptCount++;
						checkExceptionMessages();
					}
    		    	int angle = value * 360 / colors;					
    		    	int x = hPosition * 40;
					int y = vPosition * 40;
    		    	double red = Math.cos(Math.toRadians(angle)) * 255 / 2 + 255 / 2;
    		    	double	green = Math.cos(Math.toRadians(angle-120)) * 255 / 2 + 255 / 2;
    		    	double	blue = Math.cos(Math.toRadians(angle-240)) * 255 / 2 + 255 / 2;
    		    	rgb = (int)red + "," + (int)green + "," + (int)blue;
    		    	out.write("<rect x=\"" + x +"\" y=\""+ y + "\" width=\"38\" height=\"38\" style=\"fill:rgb(" + rgb + ");\"/>" + "\r\n");
				}
			}	    	
	    	out.write("</svg>" + "\r\n");
	    }    
	    catch (IOException exception)
	    {
	    	errorMessage = exception.toString();
	    }															
	    catch (ArrayIndexOutOfBoundsException exception)
	    {	errorMessage = exception.toString();
	    	arrayExcptCount++;
			checkExceptionMessages();
	    }
		catch (NullPointerException exception)
		{	
		}

	    finally 
	    {
	    	out.close();
	    }	    
	    return errorMessage;
	}

	
	public String writeCSV() throws IOException
	{
		final String DEFAULT_FILE_NAME = "javaBlanket_v2";
		final String DEFAULT_FILE_EXTENSION = ".csv";
		
		String errorMessage = "No error";
		String encoding = "UTF-8";
		String fileName;
		long timeStamp = System.currentTimeMillis() / 1000;
		
		fileName = DEFAULT_FILE_NAME + "_" + timeStamp + DEFAULT_FILE_EXTENSION;
		
		File csvFile = new File(fileName);
		boolean test = csvFile.createNewFile();
		if (test == true)
			System.out.println("CSV file created successfully");
		else
			System.out.println("CSV file will be overwritten");
		
	    Writer out = new OutputStreamWriter(new FileOutputStream(csvFile), encoding);
	    try 
	    {	
	    	out.write("hPosition" + ", " + "vPosition" + ", " + "Color" + "\n");
			int vPosition, hPosition;
			for (vPosition = 0; vPosition < length; vPosition++)
			{
				for (hPosition = 0; hPosition < width; hPosition++)
				{
    		    	out.write(hPosition + ", " + vPosition + ", " + pattern[vPosition][hPosition] + "\n");
				}
			}	    	
	    	out.write("\n");
	    }    
	    catch (IOException exception)
	    {
	    	errorMessage = exception.toString();
	    }
	    catch (ArrayIndexOutOfBoundsException exception)
	    {	errorMessage = exception.toString();
	    }
	    
	    finally 
	    {
	    	out.close();
	    }	    
	    return errorMessage;	    
	}

	
	
	public boolean check()		// Checks that 2D array "pattern" meets the requirements of being a pretty blanket
	{
		boolean goodToGo = false;
		int numberCorrect = 0;
		
		for (int colorToCheck = 0; colorToCheck < colors; colorToCheck++)
		{
			int desired = desiredCount(colorToCheck);
			int currentCount = count(colorToCheck+1);		
			
			if (currentCount == desired)
				numberCorrect++;
		}
		if (numberCorrect == colors)
			goodToGo = true;
		return goodToGo;
	}
	
	private int desiredCount(int checkColor)
	{
		int desiredCount = 0;
		int lengthWidth = length * width;
		
		if (colors == 0)
		{	colors = 1;
			divBy0ExcptCount++;
			checkExceptionMessages();
		}
		int modulus = lengthWidth % colors;							
				
		if(checkColor < (colors - modulus))
			desiredCount = (lengthWidth - modulus) / colors;		
		else
			desiredCount = 1 + (lengthWidth - modulus) / colors;	
		return desiredCount;
	}
	
	private int count(int colorToCount)
	{
		int colorCount = 0;
		int vCount, hCount;
		for (vCount = 0; vCount < length; vCount++)
		{
			for (hCount = 0; hCount < width; hCount++)
			{	try
				{
					if (pattern[vCount][hCount] == colorToCount)
						colorCount++;
				}
				catch (ArrayIndexOutOfBoundsException exception)
			    {	System.out.println("Blanket_v2 line 303:" + exception);
			    	arrayExcptCount++;
					checkExceptionMessages();
			    }
				catch (NullPointerException exception)
				{	
				}
			}
		}
		return colorCount;
	}
	
	@Override
	public String toString()	// Converts 2D array "pattern" to a string so it can be printed to the screen 
	{
		String[] partialResult;
		String result;
		try
		{
			partialResult = new String[length];
			int vcount;		
			for (vcount = 0; vcount < length; vcount++)
			{
				partialResult[vcount] = Arrays.toString(pattern[vcount]) + "\n";
			}
			result = Arrays.toString(partialResult);
		}
		catch (ArrayIndexOutOfBoundsException exception)	
	    {	System.out.println("Blanket_v2 line 320:" + exception);
	    	result = "Array Index Error";
	    	arrayExcptCount++;
			checkExceptionMessages();
	    }
		catch (NullPointerException exception)
		{	result = "Null Pointer Error";
		}
			
		return result;		
	}
	
	private static void checkExceptionMessages()
	{
		if (BlanketOutputPanel.getArrayExcptCount() == 1 && BlanketOutputPanel.getDivBy0ExcptCount() == 1)
		{	
			JOptionPane.showMessageDialog(null, "There was an error reading the blanket: Array Index Out of Bounds and Division by Zero Exceptions" 
    			+ "\nIt is recommended you Start Over, but the portion of the file that could be read is presented\n");
			System.out.println("AIOoBE is true");
		}
		else if (BlanketOutputPanel.getArrayExcptCount() == 1)
		{	
			JOptionPane.showMessageDialog(null, "There was an error reading the blanket: Array Index Out of Bounds Exception" 
    			+ "\nIt is recommended you Start Over, but the portion of the file that could be read is presented\n");
			System.out.println("AIOoBE is true");
		}	
		else if (BlanketOutputPanel.getDivBy0ExcptCount() == 1)
		{	
			JOptionPane.showMessageDialog(null, "There was an error reading the blanket: Arithmetic Exception: Division by zero Exception" 
    			+ "\nIt is recommended you Start Over, but the portion of the file that could be read is presented\n");
			System.out.println("DivBy0E is true");
		}
	}
}