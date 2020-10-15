/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 2020-04-07, Time: 15:17
*/

package com.concentrationenhancer;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FontType;
import com.almasb.fxgl.ui.ProgressBar;
import com.concentrationenhancer.bci.OpenBCI;
import com.concentrationenhancer.collision.PlayerFruitHandler;
import com.concentrationenhancer.components.PlayerComponent;
import com.concentrationenhancer.supporting.Calculations;
import com.concentrationenhancer.supporting.FruitPositionModel;
import com.concentrationenhancer.supporting.LevelWiseFruitPositions;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;

public class MainApp extends GameApplication
{
	//OpenBCI Parameters
	public static OpenBCI openBCI;
	public static final int OpenBCI_board_id = 0; // BoardIds.SYNTHETIC_BOARD (-1)  |  BoardIds.CYTON_BOARD (0)
	public static final String OpenBCI_serial_port = "/dev/cu.usbserial-DM01N5OD";
	public static int dataReadWindowSizeInMilliSeconds = 500;
	public static String subjectID = "1";
	public static String subjectName = "Rana Depto";
	public static boolean saveRecordedData = false;

	//FXML GUI Parameters
	public static final int TILE_SIZE = 100;
	public static final double PLAYER_SIZE = TILE_SIZE * 1.5;
	public static boolean developerMode = true;
	public static int LEVEL = 1;
	public static int TOTAL_ITEM;
	public static boolean pauseGame = true;
	public static boolean playerAutoMove = false;

	//FXML GUI
	public static LocalDateTime levelStartTimestamp;
	public static LocalDateTime levelEndTimestamp;
	public static Timeline elapsedTimeline;
	public static TextArea consoleTextArea;

	public static Entity player;
	public static Entity selectedFruitMarkEntity;
	public static Entity selectedFruitNameEntity;
	public static Entity arrowDirectionEntity;
	public static Entity setSubjectDetailsTextEntity;

	//Fruit List
	public static File[] fruitFileArray;
	public static ArrayList<String> fruitFileNamelist = new ArrayList<>();
	public static List<FruitModel> fruitModelList = new ArrayList<>();
	public static ArrayList<FruitPositionModel> fruitPositionList = new ArrayList<>();

	//Class
	public static PlayerComponent playerComponent;
	public static Calculations calculations = new Calculations();


	public static void showSubjectDetails()
	{
		final Stage subjectDetailsDialog = new Stage();
		subjectDetailsDialog.initModality(Modality.APPLICATION_MODAL);
		subjectDetailsDialog.initOwner(getPrimaryStage());

		Text subjectIDText = new Text("Subject ID");
		TextField subjectIDTextField = new TextField();
		subjectIDTextField.setText(MainApp.subjectID);
		subjectIDTextField.setAlignment(Pos.CENTER);

		Text subjectNameText = new Text("Subject Name");
		TextField subjectNameTextField = new TextField();
		subjectNameTextField.setText(MainApp.subjectName);
		subjectNameTextField.setAlignment(Pos.CENTER);

		Button setSubjectDetailsAndStartGameBtn = new Button("Set Subject Details & Start Game");
		setSubjectDetailsAndStartGameBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				MainApp.subjectName = subjectNameTextField.getText();
				MainApp.subjectID= subjectIDTextField.getText();
				subjectDetailsDialog.close();
				try
				{
					MainApp.openBCI.startReadingData();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				setSubjectDetailsTextEntity.removeFromWorld();
			}
		});

		VBox dialogVbox = new VBox(10);
		dialogVbox.setAlignment(Pos.CENTER);
		dialogVbox.getChildren().addAll(subjectIDText, subjectIDTextField, subjectNameText, subjectNameTextField, setSubjectDetailsAndStartGameBtn);
		dialogVbox.setPadding(new Insets(10));

		Scene dialogScene = new Scene(dialogVbox, 300, 200);
		subjectDetailsDialog.setScene(dialogScene);
		subjectDetailsDialog.show();
	}

	public static void main(String[] args) throws Exception
	{
		openBCI = new OpenBCI(args);
		launch(args);
	}

	@Override
	protected void initSettings(GameSettings settings)
	{
		settings.setTitle("BlinkFruity");
		settings.setVersion("0.1");
/*
		settings.setWidth(TILE_SIZE*17);
		settings.setHeight(TILE_SIZE*17);
*/
		settings.setWidth(1280);
		settings.setHeight(720);
		settings.setCloseConfirmation(true);
		settings.setFullScreenAllowed(true);
		settings.setPreserveResizeRatio(true);
		settings.setMenuEnabled(!developerMode);
	}

	@Override
	protected void initGame()
	{
		fruitModelList.clear();
		fruitFileNamelist.clear();

		int fruitFileArraySize = -1;
		try
		{
			fruitFileArray = new File("src/main/resources/assets/textures/fruits").listFiles();
			if (fruitFileArray == null)
			{
				fruitFileArray = new File("../src/main/resources/assets/textures/fruits").listFiles();
				fruitFileArraySize = fruitFileArray.length;
			}
			else
			{
				fruitFileArraySize = fruitFileArray.length;
			}
		}
		catch (NullPointerException e1)
		{
			var src = MainApp.class.getProtectionDomain().getCodeSource();
			if (src != null)
			{
				URL jar = src.getLocation();
				ZipInputStream zip = null;
				try
				{
					zip = new ZipInputStream(jar.openStream());

					while (true)
					{
						ZipEntry e = zip.getNextEntry();
						if (e == null)
						{
							break;
						}
						String name = e.getName();
						if (name.startsWith("assets/textures/fruits/") && name.length() > 23)
						{
							fruitFileNamelist.add(name.replaceAll("assets/textures/fruits/", ""));
						}
					}

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				/* Fail... */
			}
		}

		if (fruitFileNamelist.size() < 1)
		{
			for (int i = 0; i < fruitFileArraySize; i++)
			{
				fruitFileNamelist.add(fruitFileArray[i].getName());
			}
		}


		getGameWorld().addEntityFactory(new CEFactory());

		//Level level = getAssetLoader().loadLevel("0.txt", new TextLevelLoader(TILE_SIZE, TILE_SIZE, '0'));
		// getGameWorld().setLevel(level);

		getGameWorld().spawn("BG");

		player = getGameWorld().spawn("Player");
		playerComponent = player.getComponent(PlayerComponent.class);


/*
		//Scrollable window
		getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getAppHeight());
		getGameScene().getViewport().bindToEntity(player, 180, getAppHeight() / 2);
*/


		spawn("f", fruitPositionList.get(0).getFruitPositionX(), fruitPositionList.get(0).getFruitPositionY());
		fruitPositionList.remove(0);

/*
		for (int i = 0; i < TOTAL_ITEM; i++)
		{
			double x=FXGLMath.random(0, getAppWidth()-TILE_SIZE);
			double y=FXGLMath.random(TILE_SIZE, getAppHeight()-TILE_SIZE*2);
			System.out.println(x+", "+y);
			System.out.println();
			spawn("f", x, y);
		}
*/

		new CEFactory().getNextSelectedFruitMarkerAndFruitNameAndArrowDirectionAndCheckIfLevelComplete();
		if (pauseGame)
		{
			setSubjectDetailsTextEntity = new CEFactory().getSetSubjectDetailsTextEntity();
		}
	}

	@Override
	protected void initInput()
	{
		getInput().addAction(new UserAction("Move Up")
		{
			@Override
			protected void onAction()
			{
				LocalDateTime start = LocalDateTime.now();
				playerComponent.moveUp();
				inc("concentrationTime", +java.time.Duration.between(start, LocalDateTime.now()).getNano());
			}
		}, KeyCode.UP);

		getInput().addAction(new UserAction("Move Down")
		{
			@Override
			protected void onAction()
			{
				LocalDateTime start = LocalDateTime.now();
				playerComponent.moveDown();
				inc("concentrationTime", +java.time.Duration.between(start, LocalDateTime.now()).getNano());
			}
		}, KeyCode.DOWN);

		getInput().addAction(new UserAction("Move Left")
		{
			@Override
			protected void onAction()
			{
				LocalDateTime start = LocalDateTime.now();
				playerComponent.moveLeft();
				inc("concentrationTime", +java.time.Duration.between(start, LocalDateTime.now()).getNano());
			}
		}, KeyCode.LEFT);

		getInput().addAction(new UserAction("Move Right")
		{
			@Override
			protected void onAction()
			{
				LocalDateTime start = LocalDateTime.now();
				playerComponent.moveRight();
				inc("concentrationTime", +java.time.Duration.between(start, LocalDateTime.now()).getNano());
			}
		}, KeyCode.RIGHT);
	}

	@Override
	protected void initPhysics()
	{
		getPhysicsWorld().addCollisionHandler(new PlayerFruitHandler());
	}

	int startCountdown = 4;

	@Override
	protected void initUI()
	{
		Texture topbarBorder = texture("topbar.png");
		topbarBorder.setFitWidth(getAppWidth());
		topbarBorder.setFitHeight(TILE_SIZE);
		//topbarBorder.setRotate(180);

		//START Left Part--------------------------------------------------------------
		var itemsToPickLabel = getUIFactoryService().newText("Items Collected", Color.BLACK, FontType.GAME, 12.0);
		ProgressBar itemsRemainingBar = new ProgressBar(false);
		itemsRemainingBar.setHeight(20.0);
		itemsRemainingBar.setLabelVisible(false);
		itemsRemainingBar.setFill(Color.GREEN);
		itemsRemainingBar.setBackgroundFill(Color.DARKGREY);
		itemsRemainingBar.setTraceFill(Color.YELLOW);
		itemsRemainingBar.currentValueProperty().bind(getip("itemsBarValue"));

/*
		Text textShield = getUIFactory().newText("Shield");
		textShield.setFill(Color.BLUE);
		textShield.visibleProperty().bind(getbp("hasShield"));

		Text textDanger = getUIFactory().newText("Danger!");
		textDanger.fillProperty().bind(
				Bindings.when(getbp("overheating")).then(Color.RED).otherwise(Color.DARKGREY)
		);
		textDanger.opacityProperty().bind(
				Bindings.when(getbp("overheating")).then(1.0).otherwise(0.5)
		);

		HBox symbols = new HBox(10, textShield, textDanger);
		//symbols.setTranslateX(35);
		//symbols.setTranslateY(getAppHeight() - 35);
*/

		VBox bars = new VBox(-10, new Text(""), itemsToPickLabel, itemsRemainingBar);
		bars.setPadding(new Insets(10));
		bars.setAlignment(Pos.CENTER_LEFT);

		var timestampLabel = getUIFactoryService().newText("", Color.BLACK, FontType.GAME, 12.0);
		var elapsedTimeLabel = getUIFactoryService().newText("", Color.BLACK, FontType.GAME, 12.0);
		AtomicInteger x = new AtomicInteger();
		elapsedTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
			timestampLabel.setText(LocalDateTime.now().toString());
/*
			long seconds = java.time.Duration.between(levelStartTime,LocalTime.now()).getSeconds();
			long absSeconds = Math.abs(seconds);
			String elapsedTime = String.format("Elapsed Time: %d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
*/
			elapsedTimeLabel.setText("Elapsed: " + x.getAndIncrement() + " Seconds");
		}), new KeyFrame(Duration.seconds(1)));
		elapsedTimeline.setCycleCount(Animation.INDEFINITE);
		if (!pauseGame)
		{
			elapsedTimeline.play();
		}

		var totalFruitsLabel = getUIFactoryService().newText("", Color.BLACK, FontType.GAME, 12.0);
		totalFruitsLabel.textProperty().bind(getip("remainingToCollect").asString("Remaining %d Out of " + TOTAL_ITEM));

		VBox timeVbox = new VBox();
		timeVbox.setPrefHeight(TILE_SIZE);
		timeVbox.setAlignment(Pos.CENTER_LEFT);
		timeVbox.getChildren().addAll(timestampLabel, elapsedTimeLabel, totalFruitsLabel);

		consoleTextArea = new TextArea();
		VBox consoleVbox = new VBox();
		consoleVbox.setPadding(new Insets(10));
		//TODO Remove console width height
		consoleVbox.setMinWidth(40);
		consoleVbox.getChildren().addAll(consoleTextArea);

		HBox leftPartHBox = new HBox();
		leftPartHBox.setPrefWidth(getAppWidth() / 2);
		leftPartHBox.getChildren().addAll(bars, timeVbox);
		//leftPartHBox.getChildren().addAll(bars, timeVbox, consoleVbox);

		//END Left Part--------------------------------------------------------------


		//START Right Part--------------------------------------------------------------

		//Level and Score
		Text textScore = getUIFactoryService().newText("", Color.WHITE, FontType.GAME, 26.0);
		textScore.textProperty().bind(getip("score").asString("Score: %d"));
		textScore.setEffect(new DropShadow(7, Color.BLACK));
		textScore.setOnMouseClicked(new EventHandler()
		{
			@Override
			public void handle(Event event)
			{
				inc("score", +100);
			}
		});

		Text textLevel = getUIFactoryService().newText("", Color.WHITE, FontType.GAME, 26.0);
		textLevel.textProperty().bind(getip("level").asString("Level: %d"));
		textLevel.setEffect(new DropShadow(7, Color.BLACK));
		textLevel.setOnMouseClicked(new EventHandler()
		{
			@Override
			public void handle(Event event)
			{
				inc("level", +1);
			}
		});

		VBox levelAndScoreVBox = new VBox();
		levelAndScoreVBox.setSpacing(5);
		levelAndScoreVBox.setPadding(new Insets(20, 0, 0, 0));
		levelAndScoreVBox.getChildren().addAll(textLevel, textScore);
		//Level and Score

		//START Menu Items
		var gameMenuBtn = getUIFactoryService().newText("Game Menu", Color.BLACK, FontType.GAME, 16.0);
		gameMenuBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				try
				{
					getGameController().gotoGameMenu();
				}
				catch (Exception e)
				{
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setHeaderText("Developer Mode.");
					alert.setContentText("Sorry! Game menu is disabled in developer mood.");
					alert.show();
				}
			}
		});

		var newGameBtn = getUIFactoryService().newText("New Game", Color.BLACK, FontType.GAME, 16.0);
		newGameBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				LEVEL = 1;

				try
				{
					MainApp.openBCI.startReadingData();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				getGameController().startNewGame();


/*
				Timeline threeSecondsCountdown = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						if(startCountdown==4)
						{
							try
							{
								MainApp.openBCI.startReadingData();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
						else if(startCountdown==0)
						{
							getGameController().startNewGame();
						}
						else
						{
							var scorePlus100text = getUIFactoryService().newText(""+startCountdown, Color.LIGHTSEAGREEN, FontType.GAME, 80.0);
							scorePlus100text.setStrokeWidth(2.75);
							var textEntity = entityBuilder().at(getAppWidth() / 2, getAppHeight() / 2).view(scorePlus100text).zIndex(CE_ZIndex.countdownZIndex).buildAndAttach();
							animationBuilder().interpolator(Interpolators.EXPONENTIAL.EASE_OUT()).onFinished(textEntity::removeFromWorld).translate(textEntity).from(textEntity.getPosition()).to(textEntity.getPosition().subtract(0, 130)).buildAndPlay();
						}
						startCountdown--;
					}
				}));
				threeSecondsCountdown.setCycleCount(5);
				threeSecondsCountdown.setOnFinished(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						startCountdown=4;
					}
				});

				threeSecondsCountdown.play();
*/

			}
		});

		var restartLevelBtn = getUIFactoryService().newText("Restart Level", Color.BLACK, FontType.GAME, 16.0);
		restartLevelBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				try
				{
					MainApp.openBCI.startReadingData();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				getGameController().startNewGame();
			}
		});

		var subjectNameBtn = getUIFactoryService().newText("Subject Details", Color.BLACK, FontType.GAME, 16.0);
		subjectNameBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				showSubjectDetails();
			}
		});

		var stopBtn = getUIFactoryService().newText("Stop", Color.BLACK, FontType.GAME, 16.0);
		stopBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				pauseGame = true;
			}
		});

		var exitBtn = getUIFactoryService().newText("Exit", Color.BLACK, FontType.GAME, 16.0);
		exitBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				getGameController().exit();
			}
		});
		//START Menu Items

		HBox rightPartHbox = new HBox();
		rightPartHbox.setPrefWidth(getAppWidth() / 2);
		rightPartHbox.setAlignment(Pos.CENTER_RIGHT);
		rightPartHbox.getChildren().addAll(gameMenuBtn, newGameBtn, restartLevelBtn, subjectNameBtn, stopBtn, exitBtn, levelAndScoreVBox);
		rightPartHbox.setSpacing(20);
		rightPartHbox.setMargin(rightPartHbox, new Insets(0, 20, 0, 0));

		//END Right Part--------------------------------------------------------------

		HBox topbarBox = new HBox();
		topbarBox.setPrefWidth(getAppWidth());
		topbarBox.getChildren().addAll(leftPartHBox, rightPartHbox);

		getGameScene().addUINodes(topbarBorder, topbarBox);
	}

	@Override
	protected void initGameVars(Map<String, Object> vars)
	{
		fruitPositionList = new LevelWiseFruitPositions().getLevelWiseFruitPositionList(LEVEL);
		TOTAL_ITEM = fruitPositionList.size();

		vars.put("score", 0);
		vars.put("level", LEVEL);
		vars.put("remainingToCollect", TOTAL_ITEM);
		vars.put("itemsBarValue", 0);
		vars.put("concentrationLevelValue", 0);
		vars.put("concentrationTime", 0);
	}

	@Override
	protected void onPreInit()
	{
		getGameScene().getRoot().setCursor(Cursor.DEFAULT);
		getSettings().setGlobalMusicVolume(0.1);
		getSettings().setGlobalSoundVolume(0.3);

		if (!developerMode)
		{
			loopBGM("bgm.wav");
		}
	}
}
