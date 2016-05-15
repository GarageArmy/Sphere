package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Adam on 2016. 05. 15..
 */
public abstract class State {

    protected StateManager stm;
    protected OrthographicCamera cam;
    protected Vector3 touchPosition;
    public  State (StateManager sm){
        stm = sm;
        touchPosition = new Vector3();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Main.WIDTH, Main.HEIGHT);
    }

    public abstract void render (SpriteBatch sprite);
    public abstract void update (float t);
}
