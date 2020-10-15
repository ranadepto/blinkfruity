/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 4/23/20, Time: 1:57 AM
*/

package com.concentrationenhancer.bci;

import brainflow.BoardShim;
import brainflow.BrainFlowError;
import brainflow.BrainFlowInputParams;
import brainflow.DataFilter;
import com.concentrationenhancer.MainApp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class OpenBCI
{
	int board_id;
	BoardShim board_shim;
	Timer timer;
	ArrayList<double[][]> recordedDataList = new ArrayList<>();
	ArrayList<String> detectedBlinkTimestampList = new ArrayList<>();
	String[] args;
	SignalProcessing signalProcessing = new SignalProcessing();
	Algorithm algorithm = new Algorithm();

	public OpenBCI(String[] args) throws Exception
	{
		this.args = args;
	}

	public void startReadingData() throws Exception
	{
		BoardShim.enable_board_logger();
		BrainFlowInputParams params = new BrainFlowInputParams();
		board_id = parse_args(args, params);
		board_shim = new BoardShim(board_id, params);

		try
		{
			stopReadingData();
		}
		catch (Exception e)
		{

		}

		MainApp.pauseGame = false;
		MainApp.levelStartTimestamp = LocalDateTime.now();
		MainApp.elapsedTimeline.play();
		recordedDataList.clear();
		detectedBlinkTimestampList.clear();


		board_shim.prepare_session();
		board_shim.start_stream(); // use this for default options
		// board_shim.start_stream (450000, "file://file_stream.csv:w");

		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				int num_rows = 0;
				try
				{
					num_rows = BoardShim.get_num_rows(board_id);
					// double[][] data = board_shim.get_current_board_data (30); // doesnt flush it from ring buffer
					double[][] data = board_shim.get_board_data(); // get all data and flush from ring buffer
/*
					for (int i = 0; i < 9; i++)
					{
						System.out.println (Arrays.toString (data[i]));
						System.out.println();
					}
					System.out.println("\n"+board_shim.get_board_data().length+"------------------------------------------------------------\n\n");
*/
					recordedDataList.add(data);
					//##System.out.println("\n" + Arrays.toString(recordedDataList.get(recordedDataList.size() - 1)[0]) + "------------------------------------------------------------\n\n");

					try
					{
						//Checking Focus state
						//algorithmConcentration.getFocusState(data);

						//Checking eye blinks
						MainApp.playerAutoMove = algorithm.detectEyeBlink(signalProcessing.getDenoisedData(data));

						if (MainApp.playerAutoMove)
						{
							//Adding timestamp of eye blink
							detectedBlinkTimestampList.add(LocalDateTime.now().toString());

							double x = MainApp.selectedFruitMarkEntity.getX() - MainApp.player.getX();
							double y = MainApp.selectedFruitMarkEntity.getY() - MainApp.player.getY();

							if (x > MainApp.TILE_SIZE)
							{
								MainApp.playerComponent.moveRight();
							}
							else if (x < -MainApp.TILE_SIZE)
							{
								MainApp.playerComponent.moveLeft();
							}
							if (y > MainApp.TILE_SIZE)
							{
								MainApp.playerComponent.moveDown();
							}
							else if (y < 0)
							{
								MainApp.playerComponent.moveUp();
							}

							MainApp.playerAutoMove = false;
						}

					}
					catch (Exception e)
					{

					}

					if (MainApp.pauseGame && MainApp.saveRecordedData)
					{
						stopReadingData();
						saveData(recordedDataList, "1_RAW", "RAW data with noise");

						saveDataFFT(detectedBlinkTimestampList, "2_DetectedEyeBlinkTimestamp", "");

/*
						ArrayList<double[][]> filteredDataList = signalProcessing.getBandpassFilteredDataOffline(recordedDataList, 6, 32);
						saveData(filteredDataList, "2_FILTERED", "Filtered data, CHEBYSHEV_TYPE_1 Bandpass 6Hz-32Hz 4th Order Ripple 1.0");

						ArrayList<double[][]> denoisedDataList = signalProcessing.getDenoisedDataOffline(filteredDataList);
						saveData(denoisedDataList, "3_FILTERED+DENOISED", "Filtered data, CHEBYSHEV_TYPE_1 Bandpass 6Hz-32Hz 4th Order Ripple 1.0 | MEDIAN denoised data with period of 3");

						//ArrayList<double[][]> waveletDenoisedDataList=signalProcessing.getWaveletDenoisedData(filteredDataList);
						//saveData(waveletDenoisedDataList, "3_FILTERED+DENOISED", "Filtered data, CHEBYSHEV_TYPE_1 Bandpass 6Hz-32Hz 4th Order Ripple 1.0 | Wavelet denoised data with db4 with decomposition level 3");

						ArrayList<ArrayList<Complex>> fftDataList = signalProcessing.getFFTDataOffline(denoisedDataList);
						saveDataFFT(signalProcessing.getFFTDataForFile(fftDataList), "4_FILTERED+DENOISED+FFT", "Filtered data, CHEBYSHEV_TYPE_1 Bandpass 6Hz-32Hz 4th Order Ripple 1.0 | MEDIAN denoised data with period of 3 | FFT data");

						ArrayList<ArrayList<Complex>> fftDataWithPaddingList = signalProcessing.getFFTDataWithPaddingOffline(denoisedDataList);
						saveDataFFT(signalProcessing.getFFTDataForFile(fftDataWithPaddingList), "5_FILTERED+DENOISED+FFTWithPadding", "Filtered data, CHEBYSHEV_TYPE_1 Bandpass 6Hz-32Hz 4th Order Ripple 1.0 | MEDIAN denoised data with period of 3 | FFT data");
*/

						MainApp.saveRecordedData = false;
					}
					else if (MainApp.pauseGame)
					{
						stopReadingData();
					}

				}
				catch (BrainFlowError brainFlowError)
				{
					brainFlowError.printStackTrace();
				}
			}
		}, 3000, MainApp.dataReadWindowSizeInMilliSeconds);

	}

	private void stopReadingData() throws BrainFlowError
	{
		MainApp.pauseGame = true;
		MainApp.levelEndTimestamp = LocalDateTime.now();
		MainApp.elapsedTimeline.stop();
		timer.cancel();
		board_shim.stop_stream();

		board_shim.release_session();
	}

	private void saveData(ArrayList<double[][]> dataList, String dataType, String dataDescription) throws BrainFlowError
	{
		String fileName = "S"+MainApp.subjectID+"_"+MainApp.subjectName + "_Level" + MainApp.LEVEL + "_" + MainApp.levelEndTimestamp + "_" + dataType;
		List<String> lines = Arrays.asList("%Data recorded at AIMS Lab (http://www.aimsl.uiu.ac.bd) by Rana Depto (mail@ranadepto.com)", "%Brainwaves were obtained via the Ultracortex Mark IV Headset using the OpenBCI Cyton Board", "%Recording Application = BlinkFruity (https://www.concentrate.today/blinkfruity)", "%Data type = " + dataDescription, "%Record starting timestamp = " + MainApp.levelStartTimestamp, "%Record ending timestamp   = " + MainApp.levelEndTimestamp, "%Recorded durations = ~" + java.time.Duration.between(MainApp.levelStartTimestamp.toLocalTime(), MainApp.levelEndTimestamp.toLocalTime()).getSeconds() + " seconds", "%Number of channels = 8", "%Sample Rate = 250.0 Hz", "%First Column = SampleIndex", "%Other Columns = EEG data in microvolts followed by Accel Data (in G) interleaved with Aux Data");
		Path file = Paths.get(fileName + ".csv");
		try
		{
			//Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
			Files.write(file, lines, StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		DataFilter dataFilter = new DataFilter();
		for (int i = 0; i < dataList.size(); i++)
		{
			dataFilter.write_file(dataList.get(i), fileName + ".csv", "a");
		}

		System.out.println("Data saved with the name " + fileName + ".csv");
	}

	private void saveDataFFT(ArrayList<String> dataList, String dataType, String dataDescription) throws BrainFlowError
	{
		String fileName = "S"+MainApp.subjectID+"_"+MainApp.subjectName + "_Level" + MainApp.LEVEL + "_" + MainApp.levelEndTimestamp + "_" + dataType;
		List<String> lines = new ArrayList<>(Arrays.asList("%Data recorded at AIMS Lab (http://www.aimsl.uiu.ac.bd) by Rana Depto (mail@ranadepto.com)", "%Brainwaves were obtained via the Ultracortex Mark IV Headset using the OpenBCI Cyton Board", "%Recording Application = BlinkFruity (https://www.concentrate.today/blinkfruity)", "%Data type = " + dataDescription, "%Record starting timestamp = " + MainApp.levelStartTimestamp, "%Record ending timestamp   = " + MainApp.levelEndTimestamp, "%Recorded durations = ~" + java.time.Duration.between(MainApp.levelStartTimestamp.toLocalTime(), MainApp.levelEndTimestamp.toLocalTime()).getSeconds() + " seconds", "%Number of channels = 8"));
		lines.addAll(dataList);

		Path file = Paths.get(fileName + ".csv");
		try
		{
			Files.write(file, lines, StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Data saved with the name " + fileName + ".csv");
	}

	private int parse_args(String[] args, BrainFlowInputParams params)
	{
		int board_id = MainApp.OpenBCI_board_id;
		params.serial_port = MainApp.OpenBCI_serial_port;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("--ip-address"))
			{
				params.ip_address = args[i + 1];
			}
			if (args[i].equals("--serial-port"))
			{
				params.serial_port = args[i + 1];
			}
			if (args[i].equals("--ip-port"))
			{
				params.ip_port = Integer.parseInt(args[i + 1]);
			}
			if (args[i].equals("--ip-protocol"))
			{
				params.ip_protocol = Integer.parseInt(args[i + 1]);
			}
			if (args[i].equals("--other-info"))
			{
				params.other_info = args[i + 1];
			}
			if (args[i].equals("--board-id"))
			{
				board_id = Integer.parseInt(args[i + 1]);
			}
			if (args[i].equals("--timeout"))
			{
				params.timeout = Integer.parseInt(args[i + 1]);
			}
		}
		return board_id;
	}

}
