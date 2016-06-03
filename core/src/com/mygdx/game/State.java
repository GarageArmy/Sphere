package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Adam on 2016. 05. 15.
 */
public abstract class State {

    protected StateManager stateManager;
    protected Camera camera;

    public State (StateManager sm) {
        stateManager = sm;
    }

    public void render (SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }
    public abstract void update ();
}
