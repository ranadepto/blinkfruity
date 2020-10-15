/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 4/16/20, Time: 7:12 PM
*/

package com.concentrationenhancer.supporting;

import com.almasb.fxgl.core.math.FXGLMath;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppHeight;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAppWidth;
import static com.concentrationenhancer.MainApp.TILE_SIZE;

public class LevelWiseFruitPositions
{
	public ArrayList<FruitPositionModel> getLevelWiseFruitPositionList(int level)
	{
		if (level == 1)
		{
			return getLevel1FruitPositionList();
		}
		else if (level == 2)
		{
			return getLevel2FruitPositionList();
		}
		else if (level == 3)
		{
			return getLevel3FruitPositionList();
		}
		else if (level == 4)
		{
			return getLevel4FruitPositionList();
		}
		else
		{
			return getRandomFruitPositionList(level);
		}
	}

	public ArrayList<FruitPositionModel> getLevel1FruitPositionList()
	{
		ArrayList<FruitPositionModel> fruitPositionList = new ArrayList<>();

		fruitPositionList.add(new FruitPositionModel(596.0, 349.0));
		fruitPositionList.add(new FruitPositionModel(832.0, 381.0));
		fruitPositionList.add(new FruitPositionModel(1066.0, 300.0));

		return fruitPositionList;
	}

	public ArrayList<FruitPositionModel> getLevel2FruitPositionList()
	{
		ArrayList<FruitPositionModel> fruitPositionList = new ArrayList<>();

		fruitPositionList.add(new FruitPositionModel(617.0, 311.0));
		fruitPositionList.add(new FruitPositionModel(821.0, 518.0));
		fruitPositionList.add(new FruitPositionModel(961.0, 261.0));
		fruitPositionList.add(new FruitPositionModel(831.0, 121.0));
		fruitPositionList.add(new FruitPositionModel(48.0, 282.0));

		return fruitPositionList;
	}

	public ArrayList<FruitPositionModel> getLevel3FruitPositionList()
	{
		ArrayList<FruitPositionModel> fruitPositionList = new ArrayList<>();

		fruitPositionList.add(new FruitPositionModel(745.0, 276.0));
		fruitPositionList.add(new FruitPositionModel(439.0, 287.0));
		fruitPositionList.add(new FruitPositionModel(1171.0, 125.0));
		fruitPositionList.add(new FruitPositionModel(50.0, 499.0));
		fruitPositionList.add(new FruitPositionModel(473.0, 179.0));
		fruitPositionList.add(new FruitPositionModel(1090.0, 408.0));
		fruitPositionList.add(new FruitPositionModel(616.0, 271.0));

		return fruitPositionList;
	}

	public ArrayList<FruitPositionModel> getLevel4FruitPositionList()
	{
		ArrayList<FruitPositionModel> fruitPositionList = new ArrayList<>();

		fruitPositionList.add(new FruitPositionModel(1091.0, 362.0));
		fruitPositionList.add(new FruitPositionModel(13.0, 316.0));
		fruitPositionList.add(new FruitPositionModel(568.0, 154.0));
		fruitPositionList.add(new FruitPositionModel(730.0, 498.0));
		fruitPositionList.add(new FruitPositionModel(256.0, 397.0));
		fruitPositionList.add(new FruitPositionModel(1110.0, 500.0));
		fruitPositionList.add(new FruitPositionModel(290.0, 269.0));
		fruitPositionList.add(new FruitPositionModel(1120.0, 139.0));
		fruitPositionList.add(new FruitPositionModel(421.0, 126.0));

		return fruitPositionList;
	}

	public ArrayList<FruitPositionModel> getRandomFruitPositionList(int level)
	{
		ArrayList<FruitPositionModel> fruitPositionList = new ArrayList<>();

		for (int i = 0; i < level * 2 + 1; i++)
		{
			fruitPositionList.add(new FruitPositionModel(FXGLMath.random(0, getAppWidth() - TILE_SIZE), FXGLMath.random(TILE_SIZE, getAppHeight() - TILE_SIZE * 2)));
		}

		return fruitPositionList;
	}


}
