/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 4/28/20, Time: 2:48 PM
*/

package com.concentrationenhancer.bci;

import com.concentrationenhancer.MainApp;

public class Algorithm
{
	private double threshold_uV = 100;
	private double ch1_mean = 0;
	private double ch2_mean = 0;
	private boolean BLINKED = false;

	public boolean detectEyeBlink(double[][] data_newest_uV)
	{
		int fp1Index = 1;

		double ch1_sum = 0;

		int totalSamples=data_newest_uV[fp1Index].length;

		String msg = "\nNewest uV: " + Math.abs(data_newest_uV[fp1Index][0]);
		msg += "\nChannel1 Mean: " + ch1_mean;
		msg += "\nResult1: " + (Math.abs(data_newest_uV[fp1Index][0]) - ch1_mean);

		int blinkCount1=0,blinkCount2=0;
		for(int i=0;i<totalSamples;i++)
		{
			if (((Math.abs(data_newest_uV[fp1Index][i]) - ch1_mean) > threshold_uV) && ((Math.abs(data_newest_uV[fp1Index][i])) > threshold_uV))
			{
				blinkCount1++;
			}
			else
			{
/*
				System.out.println("NO BLINK");
				BLINKED = false;
*/
			}
		}

		for (int i = 0; i < totalSamples; i++)
		{
			ch1_sum = ch1_sum + Math.abs(data_newest_uV[fp1Index][i]);
		}
		ch1_mean = ch1_sum / totalSamples;

		//MainApp.consoleTextArea.setText("BLINKED "+blinkCount1);
		if(blinkCount1>10)
		{
			return true;
		}
		return false;
	}
}
