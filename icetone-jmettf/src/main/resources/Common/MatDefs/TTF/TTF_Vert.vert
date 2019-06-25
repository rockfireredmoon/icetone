uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;
varying vec4 pos;

varying vec2 texCoord;
varying vec2 texCoord2;

void main() {
    texCoord = inTexCoord;
    texCoord2 = inTexCoord2;

    pos = g_WorldViewMatrix * vec4(inPosition, 1.0);
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}
