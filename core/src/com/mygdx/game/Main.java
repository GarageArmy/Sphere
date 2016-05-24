package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {

	/*
	public static final int HEIGHT = 800;
	public static final int WIDTH = 480;
	*/

	SpriteBatch batch;
    StateManager statemgr;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        statemgr = new StateManager();

        statemgr.push(new IngameState(statemgr));
    }

	@Override
	public void render () {
        statemgr.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        statemgr.render(batch);
	}
}
