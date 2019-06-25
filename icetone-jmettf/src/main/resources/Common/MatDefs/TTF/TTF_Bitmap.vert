uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
attribute vec3 inPosition;
attribute vec2 inTexCoord;

varying vec2 texCoord;
varying vec4 pos;

void main() {
    texCoord = inTexCoord;
    pos = g_WorldViewMatrix * vec4(inPosition, 1.0);
    
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}
