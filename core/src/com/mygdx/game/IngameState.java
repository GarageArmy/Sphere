package com.mygdx.game;

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
    int numLayers = 6; //Number of layers
    int numSlices = 6; //Number of slices per layer
    int sliceSize = 30; //Thickness of slices
    int gapSize = 6; //Size of gaps between layers and between slices
    int sliceResolution = 15; //Number of vertices per arc (2 arcs per slice)
    Color[][] sliceColor; //Color of each slices

    Mesh circleSlices[]; //Slice meshes (one for every layer) (index 0 is the innermost layer)
    ShaderProgram shader; //ShaderProgram for rendering

    boolean dragging = false;
    Vector3 touchPosition = new Vector3(), currentPosition = new Vector3();
    int dragLayer, dragSlice;
    float dragAngle;

    IngameState(StateManager sm) {
        super(sm);
        camera = new OrthographicCamera(480,
                480 * Gdx.app.getGraphics().getHeight() / Gdx.app.getGraphics().getWidth());

        //Color setup
        sliceColor = new Color[numLayers][numSlices];
        colorSetup();

        circleSlices = new Mesh[numLayers];
        shader = new ShaderProgram(Gdx.files.internal("Slices.vert"),
                Gdx.files.internal("Slices.frag"));

        makeSliceMeshes();
    }

    void makeSliceMeshes() {
        float vertices[] = new float[sliceResolution * 2 * 2]; //Vertex array for mesh generation
        short indices[] = new short[sliceResolution * 6 - 6]; //Vertex array for mesh generation
        Vector2 vec = new Vector2();
        //Translate slices outwards to create radial gaps
        //FIXME fix input handling for larger slice counts, when this translation is bigger
        Vector2 translation = new Vector2(gapSize / 2 / MathUtils.sin(MathUtils.PI / numSlices), 0);
        translation.rotateRad(MathUtils.PI / numSlices);
        float sliceAngle = 360f / numSlices;

        for (int i = 0; i < numLayers; i++) {
            //Vertex generation
            vec.x = (sliceSize + gapSize) * i;
            vec.y = 0;

            short j = 0, k = 0; //j: counter for the while loop; k: index counter
            while (j < sliceResolution * 2) {
                vertices[k++] = vec.x + translation.x;
                vertices[k++] = vec.y + translation.y;

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
            while (j < sliceResolution - 1) {
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

    void colorSetup() {
        for (int slice = 0; slice < numSlices; slice++) {
            Color c;
            switch (slice) {
                case 0:
                    c = Color.BLUE;
                    break;
                case 1:
                    c = Color.RED;
                    break;
                case 2:
                    c = Color.YELLOW;
                    break;
                case 3:
                    c = Color.GREEN;
                    break;
                case 4:
                    c = Color.PURPLE;
                    break;
                default:
                    c = Color.WHITE;
                    break;
            }

            for (int layer = 0; layer < numLayers; layer++)
                sliceColor[layer][slice] = c;
        }
    }

    void rotateColorArray(float angle, int layer){
        // FIX more realistic rotation range

        Color[] prev = new Color[numSlices];
        int multiplier = (int) angle / (360 / numSlices);
        if (multiplier < 0) multiplier = numSlices - -1 * multiplier;

        for (int i = 0; i < numSlices; i++){
            prev[i] = sliceColor[layer][i];
        }

            for (int i = numSlices - 1; i >= 0; i--){
                if (i - multiplier >= 0)
                    sliceColor[dragLayer][i] = prev[i - multiplier];
                else sliceColor[dragLayer][i] = prev[numSlices - (multiplier - i)];
            }
    }

    void inputHandler() {
        if (!Gdx.input.isTouched()) {
            if(dragging) {
                //TODO snap rotation by shifting array elements
                rotateColorArray(dragAngle, dragLayer);
                dragLayer = 0;
                dragSlice = 0;
                dragAngle = 0;
                dragging = false;
            }
            return;
        }

        currentPosition.x = Gdx.input.getX();
        currentPosition.y = Gdx.input.getY();
        camera.unproject(currentPosition);
        if(!dragging)
        {
            dragging = true;
            touchPosition.set(currentPosition);

            dragLayer = MathUtils.floor(touchPosition.len() / (gapSize + sliceSize));
            if(dragLayer >= numLayers)
            {
                dragLayer = 0;
                dragging = false;
                return;
            }

            //Note that atan2 returns in the range of [-PI; PI]
            dragSlice = MathUtils.floor(
                MathUtils.atan2(touchPosition.y, touchPosition.x) / (MathUtils.PI2 / numSlices));
            if(dragSlice < 0) dragSlice += numSlices;
        }

        dragAngle = MathUtils.atan2(currentPosition.y, currentPosition.x)
                - MathUtils.atan2(touchPosition.y, touchPosition.x);
        dragAngle *= MathUtils.radiansToDegrees;
        System.out.println(dragAngle);
        System.out.println(MathUtils.floor(((int) dragAngle) / (360 / numSlices)));

    }

    @Override
    public void update() {
        inputHandler();

        /* Dragging data
        Gdx.app.log("Drag information",
            "\ndragging: " + (dragging ? "true" : "false")
          + "\ntouchPosition: " + touchPosition.toString()
          + "\ncurrentPosition: " + currentPosition.toString()
          + "\ndragAngle: " + dragAngle + "°"
          + "\ndragLayer: " + dragLayer
          + "\ndragSlice: " + dragSlice);
        */
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        shader.begin();
        shader.setUniformMatrix("u_combined", camera.combined);

        Matrix4 mat = new Matrix4(Vector3.Zero, new Quaternion(), new Vector3(1, 1, 1));

        for (int slice = 0; slice < numSlices; slice++) {
            shader.setUniformMatrix("u_world", mat);

            for (int layer = 0; layer < numLayers; layer++) {
                shader.setUniformf("u_color", sliceColor[layer][slice]);

                if(dragging && layer == dragLayer) {
                    mat.rotate(Vector3.Z, dragAngle);
                    shader.setUniformMatrix("u_world", mat);
                }

                circleSlices[layer].render(shader, GL20.GL_TRIANGLES);

                if(dragging && layer == dragLayer) {
                    mat.rotate(Vector3.Z, -dragAngle);
                    shader.setUniformMatrix("u_world", mat);
                }
            }

            mat.rotate(Vector3.Z, 360f / numSlices);
        }
        shader.end();
    }
}
