precision mediump float;

uniform mat4 uMVPMatrix;
uniform float xScale;
uniform float yScale;

attribute vec4 aPosition;
attribute vec2 aTextureCoord;

varying vec4 vColor;
varying vec2 vTextureCoord;

void main() {
	vTextureCoord = aTextureCoord;

	vColor.a = 0.0;
	vColor.r = 0.0;
	vColor.g = 0.0;
	vColor.b = 0.0;

	gl_Position = uMVPMatrix * vec4(aPosition.x*xScale, aPosition.y*yScale, aPosition.z, aPosition.w);
}