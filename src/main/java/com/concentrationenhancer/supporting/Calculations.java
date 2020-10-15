/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 4/9/20, Time: 12:17 PM
*/

package com.concentrationenhancer.supporting;

import com.almasb.fxgl.entity.Entity;
import com.concentrationenhancer.MainApp;

public class Calculations
{
	public double getEuclideanDistance(double x, double y, double a, double b)
	{
		return Math.sqrt(Math.pow(x - a, 2) + Math.pow(y - b, 2));
	}

	public int getNearestFruitIndex(Entity player)
	{
		int index = 0;
		double minDistance = Double.MAX_VALUE, distance = 0;
		for (int i = 0; i < MainApp.fruitModelList.size(); i++)
		{
			distance = getEuclideanDistance(player.getX(), player.getY(), MainApp.fruitModelList.get(i).getFruit().getX(), MainApp.fruitModelList.get(i).getFruit().getY());
			if (distance < minDistance)
			{
				minDistance = distance;
				index = i;
			}
		}
		return index;
	}
}
