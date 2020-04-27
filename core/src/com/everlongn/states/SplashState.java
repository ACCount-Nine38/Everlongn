package com.everlongn.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class SplashState extends State {
    float count = 0f;
    private Texture background;

    public SplashState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void tick(float delta) {
        count += delta;
        if(count >= 2) {
            stateManager.setState(StateManager.CurrentState.GAMESTATE);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void dispose() {

    }
}
