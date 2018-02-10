import java.awt.Color;


public class MutedColorComparison
{
	public static void main(String[] args)
	{
		for (double angle = 0; angle < 360; angle++)
		{
			Color HSB = BlanketOutputPanel.getMutedColor(angle);
			Color mult = BlanketOutputPanel.getForcedMutedColor(angle);
			System.out.println("angle:\t" + angle + "\tHSB: red:\t" + HSB.getRed() + "\tgreen:\t" + HSB.getGreen() + "\tblue\t" + HSB.getBlue()
					+ "\tmult: red\t"+mult.getRed()+"\tgreen:\t"+mult.getGreen()+"\tblue:\t"+mult.getBlue());
		}
	}
}