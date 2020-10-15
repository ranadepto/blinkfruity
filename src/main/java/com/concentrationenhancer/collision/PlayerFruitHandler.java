/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 4/8/20, Time: 4:04 PM
*/

package com.concentrationenhancer.collision;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.ui.FontType;
import com.concentrationenhancer.CEFactory;
import com.concentrationenhancer.CEType;
import com.concentrationenhancer.MainApp;
import com.concentrationenhancer.supporting.CE_ZIndex;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PlayerFruitHandler extends CollisionHandler
{
	public PlayerFruitHandler()
	{
		super(CEType.PLAYER, CEType.FRUIT);
	}

	@Override
	protected void onCollisionBegin(Entity player, Entity fruit)
	{
		inc("score", +100);
		inc("concentrationLevelValue", +(100 / MainApp.TOTAL_ITEM));
		inc("itemsBarValue", (100 / MainApp.TOTAL_ITEM));
		inc("remainingToCollect", -1);

		if (MainApp.fruitPositionList.size() > 0)
		{
			spawn("f", MainApp.fruitPositionList.get(0).getFruitPositionX(), MainApp.fruitPositionList.get(0).getFruitPositionY());
			MainApp.fruitPositionList.remove(0);
		}

		showScorePlus100Text(fruit);

		removeSelectedFruitMarkerAndFruitNameAndArrowDirection(fruit);

		collectFruitIntoTheBasketAndRemoveCollectedFruit(player, fruit);

		new CEFactory().getNextSelectedFruitMarkerAndFruitNameAndArrowDirectionAndCheckIfLevelComplete();

		//new CEFactory().arrowDirectionFromPlayerToFruit();

	}

	private void collectFruitIntoTheBasketAndRemoveCollectedFruit(Entity player, Entity fruit)
	{
		fruit.removeComponent(CollidableComponent.class);

		Node fruitItem = fruit.getViewComponent().getChildren().get(0);

/*
		animationBuilder()
				.duration(Duration.seconds(0.65))
				.interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT())
				.onFinished(new Runnable()
				{
					@Override
					public void run()
					{
						play("jump.wav");


						//System.out.println(player.getViewComponent().getChildren().get(0));

					}
				})
				.translate(player)
				.from(player.getPosition())
				.to(fruit.getPosition().subtract(0, 10))
				.buildAndPlay();
*/

		animationBuilder().duration(Duration.seconds(0.52)).interpolator(Interpolators.EXPONENTIAL.EASE_IN()).onFinished(new Runnable()
		{
			@Override
			public void run()
			{
				play("jump.wav");
				player.getViewComponent().addChild(fruitItem);
				int playerChildrenSize = player.getViewComponent().getChildren().size() - 1;

				double x = ((playerChildrenSize - 1) * (MainApp.TILE_SIZE / MainApp.TOTAL_ITEM)) - MainApp.TILE_SIZE / 5;
				player.getViewComponent().getChildren().get(playerChildrenSize).setTranslateX(x);
				player.getViewComponent().getChildren().get(playerChildrenSize).setTranslateY(15);

/*
						Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
							player.getViewComponent().getChildren().get(playerChildrenSize).setTranslateX(player.getViewComponent().getChildren().get(playerChildrenSize).getTranslateX()+1);
							//System.out.println(player.getViewComponent().getChildren().get(playerChildrenSize).getTranslateY());
						}), new KeyFrame(Duration.seconds(.01)));
						clock.setCycleCount((int) x);
						clock.play();

						Timeline clockY = new Timeline(new KeyFrame(Duration.ZERO, e -> {
							player.getViewComponent().getChildren().get(playerChildrenSize).setTranslateY(player.getViewComponent().getChildren().get(playerChildrenSize).getTranslateY()+.5);
						}), new KeyFrame(Duration.seconds(.01)));
						clockY.setCycleCount(30);
						clockY.play();
*/

			}
		})
/*
				.scale(fruit)
				.from(new Point2D(1, 1))
				.to(new Point2D(0.5, 0.5))
*/.translate(fruit).from(fruit.getPosition()).to(new Point2D(player.getX(), player.getY()).subtract(0, 10)).buildAndPlay();
	}

	private void showScorePlus100Text(Entity fruit)
	{
		var scorePlus100text = getUIFactoryService().newText("+100", Color.RED, FontType.GAME, 26.0);
		scorePlus100text.setStrokeWidth(2.75);
		var textEntity = entityBuilder().at(fruit.getPosition()).view(scorePlus100text).zIndex(CE_ZIndex.scoreZIndex).buildAndAttach();
		animationBuilder().interpolator(Interpolators.EXPONENTIAL.EASE_OUT()).onFinished(textEntity::removeFromWorld).translate(textEntity).from(textEntity.getPosition()).to(textEntity.getPosition().subtract(0, 100)).buildAndPlay();

	}

	private void removeSelectedFruitMarkerAndFruitNameAndArrowDirection(Entity fruit)
	{
		if (MainApp.arrowDirectionEntity != null)
		{
			MainApp.arrowDirectionEntity.removeFromWorld();
		}

		animationBuilder().duration(Duration.seconds(0.52)).interpolator(Interpolators.EXPONENTIAL.EASE_IN()).onFinished(MainApp.selectedFruitMarkEntity::removeFromWorld).scale(MainApp.selectedFruitMarkEntity).from(new Point2D(1, 1)).to(new Point2D(0, 0)).buildAndPlay();
		animationBuilder().duration(Duration.seconds(0.52)).interpolator(Interpolators.EXPONENTIAL.EASE_IN()).onFinished(MainApp.selectedFruitNameEntity::removeFromWorld).scale(MainApp.selectedFruitNameEntity).from(new Point2D(1, 1)).to(new Point2D(0, 0)).buildAndPlay();


		try
		{
			int fruitIndex = -1;
			for (int i = 0; i < MainApp.fruitModelList.size(); i++)
			{
				if (MainApp.fruitModelList.get(i).getFruit().getX() == fruit.getAnchoredPosition().getX() && MainApp.fruitModelList.get(i).getFruit().getY() == fruit.getAnchoredPosition().getY())
				{
					fruitIndex = i;
					break;
				}
			}
			if (fruitIndex != -1)
			{
				MainApp.fruitModelList.remove(fruitIndex);
			}
		}
		catch (Exception e)
		{

		}
	}


}
