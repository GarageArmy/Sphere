attribute vec2 a_position;

uniform mat4 u_world;
uniform mat4 u_combined;
uniform vec4 u_color; //Uniform color (set for every slice)

varying vec4 v_color;

void main() {
    v_color = u_color;
    gl_Position = u_combined * u_world * vec4(a_position.xy, 0.0, 1.0);

}