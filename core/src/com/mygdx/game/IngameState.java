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
    int numLayers = 5; //Number of layers
    int numSlices = 5; //Number of slices per layer
    int sliceSize = 30; //Thickness of slices
    int gapSize = 6; //Size of gaps between layers and between slices
    int sliceResolution = 15; //Number of vertices per arc (2 arcs per slice)
    Color[][] colorSlices;
    Vector3 touchPosition = new Vector3();

    Mesh circleSlices[]; //Slice meshes (one for every layer) (index 0 is the innermost layer)
    ShaderProgram shader; //ShaderProgram for rendering

    IngameState(StateManager sm) {
        super(sm);
        camera = new OrthographicCamera(480,
            480 * Gdx.app.getGraphics().getHeight() / Gdx.app.getGraphics().getWidth());

        //Color setup
        colorSlices = new Color[numLayers][numSlices];
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

    void colorSetup(){
        colorSlices[0][0] = Color.BLUE; colorSlices[0][1] = Color.RED; colorSlices[0][2] = Color.YELLOW;
        colorSlices[0][3] = Color.GREEN; colorSlices[0][4] = Color.PURPLE;

        colorSlices[1][0] = Color.BLUE; colorSlices[1][1] = Color.RED; colorSlices[1][2] = Color.YELLOW;
        colorSlices[1][3] = Color.GREEN; colorSlices[1][4] = Color.PURPLE;

        colorSlices[2][0] = Color.BLUE; colorSlices[2][1] = Color.RED; colorSlices[2][2] = Color.YELLOW;
        colorSlices[2][3] = Color.GREEN; colorSlices[2][4] = Color.PURPLE;

        colorSlices[3][0] = Color.BLUE; colorSlices[3][1] = Color.RED; colorSlices[3][2] = Color.YELLOW;
        colorSlices[3][3] = Color.GREEN; colorSlices[3][4] = Color.PURPLE;

        colorSlices[4][0] = Color.BLUE; colorSlices[4][1] = Color.RED; colorSlices[4][2] = Color.YELLOW;
        colorSlices[4][3] = Color.GREEN; colorSlices[4][4] = Color.PURPLE;

    }

    void inputHandler(){
        if (Gdx.input.isTouched()) {
            touchPosition.x = Gdx.input.getX();
            touchPosition.y = Gdx.input.getY();
            camera.unproject(touchPosition);

            //System.out.println(touchPosition.x);
            //System.out.println(touchPosition.y);

            //Checking which layer was touched
            boolean layerTouch = false;
            for (int i = 1; i <= numLayers; i++){
                int r = i * gapSize + i * sliceSize;
                if (Math.pow(touchPosition.x, 2) + Math.pow(touchPosition.y, 2) < r * r){
                    System.out.println(i);
                    layerTouch = true;
                    break;
                }
            }

            //Implementation with Math.atan2(y, x) is much simpler (look it up)
            //Checking which slice touched
            if(layerTouch) {
                double degree = MathUtils.atan2(touchPosition.y, touchPosition.x)
                        * MathUtils.radiansToDegrees;
            }

            //Implementation with atan2(y, x) is MUCH simpler (look it up)
            /*{
                double degree;
                if (touchPosition.y >= 0) {
                    double c = Math.sqrt(Math.pow(touchPosition.x, 2) + Math.pow(touchPosition.y, 2));
                    degree = Math.asin((double)touchPosition.x / c) * 180 / Math.PI;
                    System.out.println(degree);
                }
                else {
                    double c = Math.sqrt(Math.pow(touchPosition.x, 2) + Math.pow(touchPosition.y, 2));
                    degree = 180 - (Math.asin((double)touchPosition.x / c) * 180 / Math.PI);
                    System.out.println(degree);
                }

            }*/


        }
    }

    @Override
    public void update() {
        inputHandler();
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        //TODO Make sensible rendering code!
        shader.begin();
        shader.setUniformMatrix("u_combined", camera.combined);
        for(int slice = 0; slice < numSlices; slice++) {
            Matrix4 mat = new Matrix4(new Vector3(MathUtils.cosDeg(360f / numSlices * (slice + .5f)),
                MathUtils.sinDeg(360f / numSlices * (slice + .5f)), 0).scl(gapSize),
                new Quaternion(Vector3.Z, 360f / numSlices * slice), new Vector3(1, 1, 1));
            shader.setUniformMatrix("u_world", mat);

            for (int layer = 0; layer < numLayers; layer++) {
                shader.setUniformf("u_color", colorSlices[layer][slice]);
                circleSlices[layer].render(shader, GL20.GL_TRIANGLES);
            }
        }
        shader.end();
    }
}
