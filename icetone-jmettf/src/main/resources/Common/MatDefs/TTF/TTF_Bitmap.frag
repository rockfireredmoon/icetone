#ifdef GL_ES
    precision mediump float;
#endif

uniform sampler2D m_Texture;
uniform vec4 m_Color;

varying vec2 texCoord;
#if defined(USE_CLIPPING)
	uniform vec4 m_Clipping;
#endif
varying vec4 pos;

void main() {
	#if defined(USE_CLIPPING)
		if (pos.x < m_Clipping.x || pos.x > m_Clipping.z || 
			pos.y < m_Clipping.y || pos.y > m_Clipping.w) {
			discard;
			return;
		}
	#endif
    vec4 col = texture2D(m_Texture, texCoord);
    if (col.r <= 0.01) {
        discard;
    } else {
        col.a = m_Color.a * col.r;
        col.rgb = m_Color.rgb;
        
        gl_FragColor = col;
    }
}

