/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 2020-04-07, Time: 15:40
*/

package com.concentrationenhancer.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.entity.components.TypeComponent;
import com.concentrationenhancer.CEType;
import com.concentrationenhancer.MainApp;
import javafx.geometry.Point2D;

public class PlayerComponent extends Component
{
	private TransformComponent position;


	private double speed;

	@Override
	public void onUpdate(double tpf)
	{
		speed = tpf * 300;


/*
		if(LocalTime.now().getSecond()%5==0)
		{
			moveRight();
		}
*/
	}


	public void moveRight()
	{
		if (canMove(new Point2D(MainApp.PLAYER_SIZE, 0)))
		{
			entity.translateX(speed);
		}
	}

	public void moveLeft()
	{
		if (canMove(new Point2D(-5, 0)))
		{
			entity.translateX(-speed);
		}
	}

	public void moveUp()
	{
		if (canMove(new Point2D(0, -MainApp.TILE_SIZE)))
		{
			entity.translateY(-speed);
		}
	}

	public void moveDown()
	{
		if (canMove(new Point2D(0, MainApp.PLAYER_SIZE + 5)))
		{
			entity.translateY(speed);
		}
	}


	private boolean canMove(Point2D direction)
	{
		Point2D newPosition = position.getPosition().add(direction);

		return FXGL.getGameScene().getViewport().getVisibleArea().contains(newPosition)

				&& !MainApp.pauseGame

				&&

				FXGL.getGameWorld().getEntitiesAt(newPosition).stream().filter(e -> e.hasComponent(TypeComponent.class)).map(e -> e.getComponent(TypeComponent.class)).noneMatch(type -> type.isType(CEType.GROUND) || type.isType(CEType.WALL));
	}


}
