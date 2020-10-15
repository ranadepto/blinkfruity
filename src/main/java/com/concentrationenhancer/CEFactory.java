/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 2020-04-07, Time: 15:22
*/

package com.concentrationenhancer;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FontType;
import com.concentrationenhancer.components.PlayerComponent;
import com.concentrationenhancer.supporting.CE_ZIndex;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.time.LocalTime;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class CEFactory implements EntityFactory
{
	@Spawns("BG")
	public Entity getBackground(SpawnData data)
	{
		Texture t = texture("background/bg_1.png", getAppWidth(), getAppHeight());

		return entityBuilder().view(new ScrollingBackgroundView(t.superTexture(t, HorizontalDirection.RIGHT), Orientation.HORIZONTAL)).onClick(new Runnable()
		{
			@Override
			public void run()
			{

			}
		}).zIndex(CE_ZIndex.backgroundZIndex).build();
	}

	@Spawns("Player")
	public Entity getPlayer(SpawnData data)
	{
		return FXGL.entityBuilder().type(CEType.PLAYER).from(data).at((getAppWidth() / 2) - MainApp.PLAYER_SIZE / 2, getAppHeight() - MainApp.PLAYER_SIZE)
				//.viewWithBBox(new Rectangle(MainApp.TILE_SIZE,MainApp.TILE_SIZE, Color.DARKRED))
				//.view(texture("player.png").toAnimatedTexture(4, Duration.seconds(0.8)).loop())
				.viewWithBBox(texture("basket.png", MainApp.PLAYER_SIZE, MainApp.PLAYER_SIZE)).bbox(new HitBox(BoundingShape.box(MainApp.PLAYER_SIZE, MainApp.PLAYER_SIZE))).with(new CollidableComponent(true)).with(new PlayerComponent()).zIndex(CE_ZIndex.playerZIndex).build();
	}

	@Spawns("f")
	public Entity getFruits(SpawnData data)
	{
		Random random = new Random();

		if (MainApp.fruitFileNamelist.size() > 0)
		{
			int x = random.nextInt(MainApp.fruitFileNamelist.size());
			String name = MainApp.fruitFileNamelist.get(x);
			MainApp.fruitFileNamelist.remove(name);
			Entity fruit = FXGL.entityBuilder().type(CEType.FRUIT).from(data).viewWithBBox(FXGL.getAssetLoader().loadTexture("fruits/" + name, MainApp.TILE_SIZE, MainApp.TILE_SIZE)).with(new CollidableComponent(true)).zIndex(CE_ZIndex.selectedFruitNameZIndex).build();
			MainApp.fruitModelList.add(new FruitModel(MainApp.fruitModelList.size(), name, fruit));

			animationBuilder().interpolator(Interpolators.EXPONENTIAL.EASE_OUT()).duration(Duration.seconds(0.95)).scale(fruit).from(new Point2D(0, 0)).to(new Point2D(1, 1)).buildAndPlay();

			return fruit;
		}
		else
		{
			try
			{
				String name = MainApp.fruitFileArray[random.nextInt(MainApp.fruitFileArray.length)].getName();
				Entity fruit = entityBuilder().type(CEType.FRUIT).from(data)
						//.view(new Rectangle(MainApp.TILE_SIZE, MainApp.TILE_SIZE, Color.GRAY.saturate()))
						.viewWithBBox(getAssetLoader().loadTexture("fruits/" + name, MainApp.TILE_SIZE, MainApp.TILE_SIZE)).with(new CollidableComponent(true)).zIndex(CE_ZIndex.fruitZIndex).build();
				MainApp.fruitModelList.add(new FruitModel(MainApp.fruitModelList.size(), name, fruit));

				animationBuilder().interpolator(Interpolators.EXPONENTIAL.EASE_OUT()).duration(Duration.seconds(0.95)).scale(fruit).from(new Point2D(0, 0)).to(new Point2D(1, 1)).buildAndPlay();

				return fruit;
			}
			catch (Exception e)
			{
				Entity fruit = entityBuilder().type(CEType.FRUIT).from(data).viewWithBBox(getAssetLoader().loadTexture("Fruit.png", MainApp.TILE_SIZE, MainApp.TILE_SIZE)).with(new CollidableComponent(true)).zIndex(CE_ZIndex.fruitZIndex).build();
				MainApp.fruitModelList.add(new FruitModel(MainApp.fruitModelList.size(), "Fruit.png", fruit));

				animationBuilder().interpolator(Interpolators.EXPONENTIAL.EASE_OUT()).duration(Duration.seconds(0.95)).scale(fruit).from(new Point2D(0, 0)).to(new Point2D(1, 1)).buildAndPlay();

				return fruit;
			}
		}
	}

	@Spawns("w")
	public Entity getWall(SpawnData data)
	{
		return FXGL.entityBuilder().from(data).view(new Rectangle(MainApp.TILE_SIZE, MainApp.TILE_SIZE, Color.GRAY.saturate())).build();
	}

/*
	private Text createText(String string) {
		Text text = new Text(string);
		text.setBoundsType(TextBoundsType.VISUAL);
		text.setStyle(
				"-fx-font-family: \"Times New Roman\";" +
						"-fx-font-style: italic;" +
						"-fx-font-size: 48px;"
		);

		return text;
	}

	private Circle encircle(Text text) {
		Circle circle = new Circle();
		circle.setFill(Color.DEEPSKYBLUE);
		final double PADDING = 10;
		circle.setRadius(getWidth(text) / 2 + PADDING);

		return circle;
	}

	private Rectangle getRectangle(Text text) {
		Rectangle rectangle = new Rectangle();
		rectangle.setFill(Color.DEEPSKYBLUE);
		final double PADDING = 20;
		rectangle.setWidth(getWidth(text) + PADDING);
		rectangle.setHeight(50);

		return rectangle;
	}

	private double getWidth(Text text) {
		new Scene(new Group(text));
		text.applyCss();

		return text.getLayoutBounds().getWidth();
	}
*/

	public Entity getSetSubjectDetailsTextEntity()
	{
		var setSubjectDetailsText = getUIFactoryService().newText("Click here to set subject details and start the game.", Color.RED, FontType.GAME, 35.0);
		setSubjectDetailsText.setStrokeWidth(2.84);

/*
		Rectangle rectangle = getRectangle(setSubjectDetailsText);

		StackPane layout = new StackPane();
		layout.getChildren().addAll(
				rectangle,
				setSubjectDetailsText
		);
		layout.setPadding(new Insets(20));
*/


		var textEntity = entityBuilder().at(getAppWidth() / 4, getAppHeight() / 2).view(setSubjectDetailsText).onClick(new Runnable()
		{
			@Override
			public void run()
			{
				MainApp.showSubjectDetails();
			}
		}).zIndex(CE_ZIndex.scoreZIndex).buildAndAttach();
		animationBuilder().interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
				//.onFinished(textEntity::removeFromWorld)
				.translate(textEntity).from(textEntity.getPosition()).to(textEntity.getPosition().subtract(0, 100)).buildAndPlay();

		return textEntity;
	}

	public void getNextSelectedFruitMarkerAndFruitNameAndArrowDirectionAndCheckIfLevelComplete()
	{
		if (MainApp.fruitModelList.size() < 1)
		{
			levelComplete();

			return;
		}

		int index = MainApp.calculations.getNearestFruitIndex(MainApp.player);
		Circle circle = new Circle(MainApp.TILE_SIZE / 2, Color.SKYBLUE.saturate());
		circle.setStroke(Color.GRAY);
		MainApp.selectedFruitMarkEntity = entityBuilder().type(CEType.FRUIT).at(MainApp.fruitModelList.get(index).getFruit().getX() + MainApp.TILE_SIZE / 2, MainApp.fruitModelList.get(index).getFruit().getY() + MainApp.TILE_SIZE / 2).view(circle).with(new CollidableComponent(true)).opacity(.4).zIndex(CE_ZIndex.selectedFruitMarkerZIndex).buildAndAttach();

		var selectedFruitNameText = getUIFactoryService().newText(MainApp.fruitModelList.get(index).getName().replace(".png", ""), Color.BLACK, FontType.GAME, 16.0);
		MainApp.selectedFruitNameEntity = entityBuilder().at(MainApp.fruitModelList.get(index).getFruit().getX() + ((MainApp.TILE_SIZE - selectedFruitNameText.getLayoutBounds().getWidth()) / 2), MainApp.fruitModelList.get(index).getFruit().getY() - 10).view(selectedFruitNameText).zIndex(CE_ZIndex.selectedFruitNameZIndex).buildAndAttach();

		animationBuilder().repeat(5).interpolator(Interpolators.EXPONENTIAL.EASE_OUT()).autoReverse(true).duration(Duration.seconds(0.25)).scale(MainApp.selectedFruitMarkEntity).from(new Point2D(1.0 / 1.3, 1.0 / 1.3)).to(new Point2D(1.1, 1.1)).buildAndPlay();

/*
		animationBuilder()
				.duration(Duration.seconds(0.35))
				.interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT())
				.onFinished(new Runnable()
				{
					@Override
					public void run()
					{
						arrowDirectionFromPlayerToFruit();
					}
				})
				.scale(MainApp.player)
				.from(new Point2D(1,1))
				.to(new Point2D(1,1))
				.buildAndPlay();
*/

		arrowDirectionFromPlayerToFruit();

	}


	public void arrowDirectionFromPlayerToFruit()
	{
		if (MainApp.fruitModelList.size() == 0)
		{
			return;
		}

		Color arrowColor = Color.GRAY.brighter();
		var rect = new Rectangle(150, 10, null);
		rect.setStroke(arrowColor);
		rect.setStrokeWidth(2);

		var arrow1 = new Rectangle(35, 2.5, arrowColor);
		arrow1.setTranslateX(128);
		arrow1.setTranslateY(-5);
		arrow1.setRotate(30);

		var arrow2 = new Rectangle(35, 2.5, arrowColor);
		arrow2.setRotate(-30);
		arrow2.setTranslateX(128);
		arrow2.setTranslateY(12);

		Entity arrowEntity = entityBuilder().at(MainApp.player.getX() + MainApp.TILE_SIZE / 2, MainApp.player.getY() + MainApp.TILE_SIZE / 2).view(new Group(rect, arrow1, arrow2))
				//.with(new IrremovableComponent())
				//.with(new CatapultLineIndicatorComponent())
				.zIndex(CE_ZIndex.arrowDirectionZIndex).buildAndAttach();

		//Rotate the arrow to the next fruit direction
		Point2D sourcePoint = new Point2D(MainApp.player.getX(), MainApp.player.getY());
		Point2D targetDirection = new Point2D(MainApp.selectedFruitNameEntity.getX(), MainApp.selectedFruitNameEntity.getY());
		float angle = (float) Math.toDegrees(Math.atan2(targetDirection.getY() - sourcePoint.getY(), targetDirection.getX() - sourcePoint.getX()));
		if (angle < 0)
		{
			angle += 360;
		}
		arrowEntity.rotateBy(angle);

		animationBuilder().duration(Duration.seconds(0.40)).interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT()).scale(arrowEntity).from(new Point2D(0, 0)).to(new Point2D(1, 1)).buildAndPlay();


		if (MainApp.arrowDirectionEntity != null)
		{
			MainApp.arrowDirectionEntity.removeFromWorld();
		}
		MainApp.arrowDirectionEntity = arrowEntity;
	}


	private void levelComplete()
	{
		set("concentrationLevelValue", +100);
		set("itemsBarValue", 0);
		MainApp.elapsedTimeline.stop();


		double x = 0, y = 0;
		try
		{
			x = java.time.Duration.between(MainApp.levelStartTimestamp.toLocalTime(), LocalTime.now()).getSeconds();
			y = Integer.valueOf(String.valueOf(getip("concentrationTime").divide(5000000).get()));
		}
		catch (Exception e)
		{
		}
		int level = (int) ((y / x) * 10);
		String concentrationLevelLabel = "";
		if (level > 9)
		{
			concentrationLevelLabel = "  Superb! Great Concentration Level!";
		}
		else if (level > 7)
		{
			concentrationLevelLabel = "You have a very good concentration level!";
		}
		else if (level > 5)
		{
			concentrationLevelLabel = "You have a good concentration level!";
		}
		else if (level > 3)
		{
			concentrationLevelLabel = "You have an average concentration level!";
		}
		else
		{
			concentrationLevelLabel = "You have a poor concentration level!";
		}

		var text = getUIFactoryService().newText(getip("score").asString("Congratulations! Your Score is  %d!!").get(), Color.RED, FontType.GAME, 26.0);
		text.setStrokeWidth(10.84);

		Entity textEntity = entityBuilder().at(getAppWidth() / 2 - 180, getAppHeight() / 1.5).view(text).onClick(new Runnable()
		{
			@Override
			public void run()
			{
				startNewLevel();
			}
		}).zIndex(CE_ZIndex.scoreZIndex).buildAndAttach();

		animationBuilder().duration(Duration.seconds(1.52)).interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
				//.onFinished(textEntity::removeFromWorld)
				.translate(textEntity).from(textEntity.getPosition()).to(textEntity.getPosition().subtract(0, 100)).buildAndPlay();


		animationBuilder().duration(Duration.seconds(3.52)).autoReverse(false).interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT()).translate(MainApp.player).from(MainApp.player.getPosition()).to(new Point2D(getAppWidth() / 2 - MainApp.PLAYER_SIZE / 2, getAppHeight() / 4)).buildAndPlay();

		//getGameScene().getViewport().shake(8,2);

		MainApp.pauseGame = true;
		MainApp.saveRecordedData = true;

	}


	private void startNewLevel()
	{
		MainApp.LEVEL++;
		MainApp.TOTAL_ITEM += 2;
		getGameController().startNewGame();
		try
		{
			MainApp.openBCI.startReadingData();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
