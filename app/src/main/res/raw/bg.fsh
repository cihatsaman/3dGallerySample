precision mediump float;

uniform sampler2D diffuseTex;
uniform float uTime;
uniform float uProgress;
varying vec2 vTextureCoord;

vec3 desaturate(vec3 color, float amount)
{
    vec3 gray = vec3(dot(vec3(0.2126,0.7152,0.0722), color));
    return vec3(mix(color, gray, amount));
}

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
	float t = mod(uTime*0.2, 2.0);
	if(t>1.0)
	    t = 2.0-t;
	t = 0.3 + t*0.5;
	/*
	float i = 1.0 - (pow(abs(vTextureCoord.x), 4.0) + pow(abs(vTextureCoord.y), 4.0));
	i = smoothstep(t - 0.8, t + 0.8, i);
	i = floor(i * 20.0) / 20.0;
	vec4 tex = vec4(desaturate(texture2D(diffuseTex, vec2(vTextureCoord.x*0.1+uProgress,vTextureCoord.y*0.1+0.5)).rgb, 0.5).rgb, 0.0);
	//vec4 tex = texture2D(diffuseTex, vTextureCoord);
	vec4 color = vec4(desaturate(vec3(vTextureCoord * 0.5 + 0.5, i),0.5),1.0);
	gl_FragColor = tex*0.5+color*0.5;
	*/

    vec4 textureColor = texture2D(diffuseTex, vec2(vTextureCoord.x*0.1+uProgress,vTextureCoord.y*0.05+0.9));
    vec3 fragRGB = textureColor.rgb;
    vec3 fragHSV = rgb2hsv(fragRGB).xyz;
    //fragHSV.x += vHSV.x / 360.0;
    //fragHSV.yz *= vHSV.yz;
    fragHSV.z = t;
    fragHSV.xyz = mod(fragHSV.xyz, 1.0);
    fragRGB = hsv2rgb(fragHSV);
    gl_FragColor = vec4(fragRGB, textureColor.w);
}
