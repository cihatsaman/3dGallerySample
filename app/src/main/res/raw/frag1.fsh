precision mediump float;

uniform float gradientH;
uniform float maxBright;
uniform sampler2D diffuseTex;

uniform float uColorInfluence;
varying vec2 vTextureCoord;

void main() {
	vec4 texColor1 = texture2D(diffuseTex, vTextureCoord);
    texColor1 *= mix(maxBright,0.0,clamp((vTextureCoord.y-1.0+gradientH)/gradientH,0.0,1.0));
	gl_FragColor = texColor1*uColorInfluence;
}
