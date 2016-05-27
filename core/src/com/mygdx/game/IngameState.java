package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Gabriel025 on 2016.05.16.
 */
public class IngameState extends State {
    int numLayers; //Number of layers
    int numSlices; //Number of slices per layer
    int sliceSize; //Thickness of slices
    int gapSize; //Size of gaps between layers and between slices
    int sliceResolution; //Number of vertices per arc (2 arcs per slice)
    Mesh circleSlices[]; //Slice meshes (one for every layer) (index 0 is the innermost layer)
    ShaderProgram shader; //ShaderProgram for rendering

    IngameState(StateManager sm) {
        super(sm);
        camera = new OrthographicCamera(800, 480);

        numLayers = 6;
        numSlices = 6;
        sliceSize = 35;
        gapSize = 5;
        sliceResolution = 15;
        circleSlices = new Mesh[numLayers];
        shader = new ShaderProgram(Gdx.files.internal("assets/Slices.vert"),
                Gdx.files.internal("assets/Slices.frag"));

        makeSliceMeshes();
    }

    void makeSliceMeshes() {
        float vertices[] = new float[sliceResolution * 2 * 2]; //Vertex array for mesh generation
        short indices[] = new short[sliceResolution * 6 - 6]; //Vertex array for mesh generation
        Vector2 vec = new Vector2();
        float sliceAngle = 360f / numSlices;

        for (int i = 0; i < numLayers; i++) {
            //Vertex generation
            vec.x = (sliceSize + gapSize) * i;
            vec.y = 0;

            short j = 0, k = 0; //j: counter for the while loop; k: index counter
            while (j < sliceResolution * 2) {
                vertices[k++] = vec.x;
                vertices[k++] = vec.y;

                j++;
                if (j == sliceResolution) {
                    vec.x = sliceSize * (i + 1) + gapSize * i;
                    vec.y = 0;
                } else
                    vec.rotate(sliceAngle / (sliceResolution - 1));
            }

            //Index generation
            j = 0;
            k = 0;
            while(j < sliceResolution - 1) {
                indices[k++] = j;
                indices[k++] = (short) (j + 1);
                indices[k++] = (short) (j + sliceResolution);
                indices[k++] = (short) (j + 1);
                indices[k++] = (short) (j + sliceResolution + 1);
                indices[k++] = (short) (j + sliceResolution);
                j++;
            }

            circleSlices[i] = new Mesh(Mesh.VertexDataType.VertexBufferObject, true,
                    sliceResolution * 2, sliceResolution * 6 - 6,
                    new VertexAttribute(VertexAttributes.Usage.Position,
                            2, ShaderProgram.POSITION_ATTRIBUTE));
            circleSlices[i].setVertices(vertices);
            circleSlices[i].setIndices(indices);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        //TODO Make sensible rendering code!
        shader.begin();
        shader.setUniformMatrix("u_combined", camera.combined);
        for(int i = 0; i < numSlices; i++)
            for (Mesh circleSlice : circleSlices) {
                Matrix4 mat = new Matrix4(
                        new Vector3(MathUtils.cosDeg(360f / numSlices * (i + .5f)),
                                MathUtils.sinDeg(360f / numSlices * (i + .5f)), 0).scl(gapSize),
                        new Quaternion(Vector3.Z, 360f / numSlices * i), new Vector3(1, 1, 1));

                shader.setUniformMatrix("u_world", mat);

                shader.setUniformf("u_color", MathUtils.random(1f), MathUtils.random(1f), MathUtils.random(1f), 1f);
                circleSlice.render(shader, GL20.GL_TRIANGLES);
            }
        shader.end();
    }
}
