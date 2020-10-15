/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 4/25/20, Time: 10:13 PM
*/

package com.concentrationenhancer.bci;

import brainflow.AggOperations;
import brainflow.BrainFlowError;
import brainflow.DataFilter;
import brainflow.FilterTypes;
import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.Arrays;

public class SignalProcessing
{
	public double[][] getNotchFilteredData(double[][] data, int notchValue) throws BrainFlowError
	{
		DataFilter dataFilter = new DataFilter();
		for (int j = 1; j < 9; j++)
		{
			// try different functions and different decomposition levels here
			dataFilter.perform_bandpass(data[j], 250, notchValue, 4, 4, FilterTypes.CHEBYSHEV_TYPE_1.get_code(), 1);
			//dataFilter.perform_wavelet_denoising (data[j], "db4", 3);
		}

		return data;
	}

	public double[][] getBandpassFilteredData(double[][] data, double lowerRange, double upperRange) throws BrainFlowError
	{
		DataFilter dataFilter = new DataFilter();
		for (int j = 1; j < 9; j++)
		{
			dataFilter.perform_bandpass(data[j], 250, (upperRange + lowerRange) / 2, (upperRange - lowerRange) / 2, 4, FilterTypes.CHEBYSHEV_TYPE_1.get_code(), 1.0);
		}

		return data;
	}

	public ArrayList<double[][]> getBandpassFilteredDataOffline(ArrayList<double[][]> dataList, double lowerRange, double upperRange) throws BrainFlowError
	{
		DataFilter dataFilter = new DataFilter();
		ArrayList<double[][]> filteredDataList = new ArrayList<>();
		for (int i = 0; i < dataList.size(); i++)
		{
			double data[][] = dataList.get(i);
			for (int j = 1; j < 9; j++)
			{
				dataFilter.perform_bandpass(data[j], 250, (upperRange + lowerRange) / 2, (upperRange - lowerRange) / 2, 4, FilterTypes.CHEBYSHEV_TYPE_1.get_code(), 1.0);
			}
			filteredDataList.add(data);
		}

		return filteredDataList;
	}

	public double[][] getDenoisedData(double[][] data) throws BrainFlowError
	{
		DataFilter dataFilter = new DataFilter();
		for (int j = 1; j < 9; j++)
		{
			// try different functions and different decomposition levels here
			dataFilter.perform_rolling_filter(data[j], 6, AggOperations.MEDIAN.get_code());
			//dataFilter.perform_wavelet_denoising (data[j], "db4", 3);
		}

		return data;
	}

	public ArrayList<double[][]> getDenoisedDataOffline(ArrayList<double[][]> dataList) throws BrainFlowError
	{
		DataFilter dataFilter = new DataFilter();
		ArrayList<double[][]> denoisedDataList = new ArrayList<>();
		for (int i = 0; i < dataList.size(); i++)
		{
			double data[][] = dataList.get(i);
			for (int j = 1; j < 9; j++)
			{
				// try different functions and different decomposition levels here
				dataFilter.perform_rolling_filter(data[j], 3, AggOperations.MEDIAN.get_code());
				//dataFilter.perform_wavelet_denoising (data[j], "db4", 3);
			}
			denoisedDataList.add(data);
		}

		return denoisedDataList;
	}

	public ArrayList<double[][]> getWaveletDenoisedData(ArrayList<double[][]> dataList) throws BrainFlowError
	{
		DataFilter dataFilter = new DataFilter();
		ArrayList<double[][]> waveletDenoisedDataList = new ArrayList<>();
		for (int i = 0; i < dataList.size(); i++)
		{
			double data[][] = dataList.get(i);
			for (int j = 1; j < 9; j++)
			{
				// try different functions and different decomposition levels here
				dataFilter.perform_wavelet_denoising(data[j], "db4", 3);
			}
			waveletDenoisedDataList.add(data);
		}

		return waveletDenoisedDataList;
	}

	public ArrayList<ArrayList<Complex>> getFFTData(double[][] data) throws BrainFlowError
	{
		int numberOfChannel = 8;
		DataFilter dataFilter = new DataFilter();

		ArrayList<ArrayList<Complex>> channelWiseFFTList = new ArrayList<>();

		//going through every channel
		for (int j = 1; j <= numberOfChannel; j++)
		{
			//getting number of samples of j-th channel data
			int totalDataLength = data[j].length;
			int numberOfDataToReadForWaveletAndFFT = (int) Math.pow(2, Math.floor(Math.log(totalDataLength) * 1.4426950408889634073599246810019));
			int startIndex = 0, endIndex = numberOfDataToReadForWaveletAndFFT;
			channelWiseFFTList.add(new ArrayList<>());

			//performing fft on all samples
			while (numberOfDataToReadForWaveletAndFFT != 0)
			{
				//copying power of 2 number of data in separate variable to process (fft works only for power of 2)
				double tempData[] = new double[numberOfDataToReadForWaveletAndFFT];
				for (int k = startIndex, l = 0; k < endIndex; k++, l++)
				{
					tempData[l] = data[j][k];
				}

				//perform fft (fft works only for power of 2)
				Complex[] tempFFTData = dataFilter.perform_fft(tempData, 0, numberOfDataToReadForWaveletAndFFT, numberOfDataToReadForWaveletAndFFT);

				//saving fft data channel-wise (len of fft_data is N / 2 + 1)
				channelWiseFFTList.get(j - 1).addAll(new ArrayList<>(Arrays.asList(tempFFTData)));

				//update next window size
				totalDataLength -= numberOfDataToReadForWaveletAndFFT;
				numberOfDataToReadForWaveletAndFFT = (int) Math.pow(2, Math.floor(Math.log(totalDataLength) * 1.4426950408889634073599246810019));
				startIndex = endIndex;
				endIndex += numberOfDataToReadForWaveletAndFFT;
			}
		}

		return channelWiseFFTList;
	}

	public ArrayList<ArrayList<Complex>> getFFTDataOffline(ArrayList<double[][]> dataList) throws BrainFlowError
	{
		ArrayList<ArrayList<Complex>> offlineFFTList = getFFTData(dataList.get(0));

		for (int i = 1; i < dataList.size(); i++)
		{
			ArrayList<ArrayList<Complex>> tempList = getFFTData(dataList.get(i));
			for (int j = 0; j < tempList.size(); j++)
			{
				offlineFFTList.get(j).addAll(tempList.get(j));
			}
		}

/*
		for (int i=0;i<offlineFFTList.size();i++)
		{
			System.out.println("\n\nChannel "+(i+1)+" Data: "+offlineFFTList.get(i).size());
			for(int j=0;j<offlineFFTList.get(i).size();j++)
			{
				System.out.println(offlineFFTList.get(i).get(j));
			}
		}
*/

		return offlineFFTList;
	}

	public ArrayList<ArrayList<Complex>> getFFTDataWithPadding(double[][] data) throws BrainFlowError
	{
		int numberOfChannel = 8;
		DataFilter dataFilter = new DataFilter();

		ArrayList<ArrayList<Complex>> channelWiseFFTList = new ArrayList<>();

		int totalDataLength = data[0].length;
		int numberOfDataToReadForWaveletAndFFT = (int) Math.pow(2, Math.floor(Math.log(totalDataLength) * 1.4426950408889634073599246810019) + 1);

		//going through every channel
		for (int j = 1; j <= numberOfChannel; j++)
		{
			channelWiseFFTList.add(new ArrayList<>());

			double tempData[] = new double[numberOfDataToReadForWaveletAndFFT];
			for (int k = 0, l = 0; k < totalDataLength; k++, l++)
			{
				tempData[l] = data[j][k];
			}
			for (int k = totalDataLength, l = 0; k < numberOfDataToReadForWaveletAndFFT; k++, l++)
			{
				tempData[l] = 0;
			}

			try
			{
				//perform fft (fft works only for power of 2)
				Complex[] tempFFTData = DataFilter.perform_fft(tempData, 0, numberOfDataToReadForWaveletAndFFT, numberOfDataToReadForWaveletAndFFT);
				//saving fft data channel-wise (len of fft_data is N / 2 + 1)
				channelWiseFFTList.get(j - 1).addAll(new ArrayList<>(Arrays.asList(tempFFTData)));
			}
			catch (Exception e)
			{
				if (totalDataLength > 0)
				{
					e.printStackTrace();
				}
			}
		}


		return channelWiseFFTList;
	}

	public ArrayList<ArrayList<Complex>> getFFTDataWithPaddingOffline(ArrayList<double[][]> dataList) throws BrainFlowError
	{
		ArrayList<ArrayList<Complex>> offlineFFTList = getFFTDataWithPadding(dataList.get(0));

		for (int i = 1; i < dataList.size(); i++)
		{
			ArrayList<ArrayList<Complex>> tempList = getFFTDataWithPadding(dataList.get(i));
			for (int j = 0; j < tempList.size(); j++)
			{
				offlineFFTList.get(j).addAll(tempList.get(j));
			}
		}

/*
		for (int i=0;i<offlineFFTList.size();i++)
		{
			System.out.println("\n\nChannel "+(i+1)+" Data: "+offlineFFTList.get(i).size());
			for(int j=0;j<offlineFFTList.get(i).size();j++)
			{
				System.out.println(offlineFFTList.get(i).get(j));
			}
		}
*/

		return offlineFFTList;
	}


	public ArrayList<String> getFFTDataForFile(ArrayList<ArrayList<Complex>> dataList) throws BrainFlowError
	{
		ArrayList<String> fileDataList = new ArrayList<>();

		for (int i = 0; i < dataList.get(0).size(); i++)
		{
			String line = dataList.get(0).get(i).toString();
			for (int j = 1; j < dataList.size(); j++)
			{
				line += ", " + dataList.get(j).get(i);
			}
			fileDataList.add(line);
		}

		return fileDataList;
	}


}
