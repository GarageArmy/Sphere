package com.mygdx.game;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Gabriel025 on 2016.05.16.
 */
public class IngameState extends State {
    int numLayers; //Number of layers
    int numSlices; //Number of slices per layer
    int sliceSize; //Thickness of slices
    int gapSize; //Size of gaps between layers and between slices
    int sliceResolution; //Number of vertices per arc (2 arcs per slice)
    Mesh circleSlices[]; //Meshes of the slices (one for every layer)

    IngameState(StateManager sm) {
        super(sm);
        camera = new OrthographicCamera(800, 480);

        numLayers = 6;
        numSlices = 6;
        sliceSize = 30;
        gapSize = 10;
        sliceResolution = 5;
        circleSlices = new Mesh[numLayers];

        float verts[] = new float[sliceResolution * 2];
        float inds[] = new float[sliceResolution * 6 - 6];
        for (int i = 0; i < numLayers; i++) {

            circleSlices[i] = new Mesh(Mesh.VertexDataType.VertexBufferObject, true,
                    sliceResolution * 2, sliceResolution * 6 - 6,
                    VertexAttribute.Position(), VertexAttribute.ColorPacked());
        }


    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }
}
