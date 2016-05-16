package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

import java.util.Stack;

/**
 * Created by Adam on 2016. 05. 15.
 */
public class StateManager extends Stack<State> {

    public StateManager() {

    }

    public void update () {
        peek().update();
    }

    public void render (SpriteBatch batch) {
        peek().render(batch);
    }
}
