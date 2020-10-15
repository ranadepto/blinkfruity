/*
		Created by:Rana Depto

		Email:mail@ranadepto.com
	
        Date: 4/9/20, Time: 10:21 AM
*/

package com.concentrationenhancer;

import com.almasb.fxgl.entity.Entity;

public class FruitModel
{
	int id;
	String name;
	Entity fruit;

	public FruitModel(int id, String name, Entity fruit)
	{
		this.id = id;
		this.name = name;
		this.fruit = fruit;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Entity getFruit()
	{
		return fruit;
	}

	public void setFruit(Entity fruit)
	{
		this.fruit = fruit;
	}
}
