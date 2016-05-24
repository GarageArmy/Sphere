attribute vec2 a_position;

uniform mat4 u_projTrans;
uniform vec4 color; //Uniform color (set for every slice)

varying vec4 vColor;

void main() {
    vColor = a_color;
    gl_Position = u_projTrans * vec4(a_position.xy, 0.0, 1.0);
}