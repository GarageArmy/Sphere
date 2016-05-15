package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Stack;

/**
 * Created by Adam on 2016. 05. 15..
 */
public class StateManager {

    private Stack<State> states;

    public StateManager(){
        states = new Stack<State>();
    }

    public void pushStack (State state){
        states.push(state);
    }

    public void popStack(){
        states.pop();
    }

    public  void setState(State state){
        states.pop();
        states.push(state);
    }

    public void render (SpriteBatch sprite){
        states.peek().render(sprite);
    }

    public void update (float t){
        states.peek().update(t);
    }
}
