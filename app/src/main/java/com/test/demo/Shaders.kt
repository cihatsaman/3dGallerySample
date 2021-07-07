package com.test.demo

import android.opengl.GLES20
import org.rajawali3d.materials.shaders.FragmentShader
import org.rajawali3d.materials.shaders.VertexShader
import org.rajawali3d.util.RawShaderLoader


class CustomVertexShader(var shaderResId: Int, var shaderData: FloatArray) : VertexShader() {
	private var xScaleLoc = 0
	private var yScaleLoc = 0
	
	override fun initialize() {
		mShaderString = RawShaderLoader.fetch(shaderResId)
	}
	
	override fun main() {}
	override fun setLocations(programHandle: Int) {
		super.setLocations(programHandle)
		xScaleLoc = getUniformLocation(programHandle, "xScale")
		yScaleLoc = getUniformLocation(programHandle, "yScale")
	}
	
	override fun applyParams() {
		super.applyParams()
		GLES20.glUniform1f(xScaleLoc, shaderData[0])
		GLES20.glUniform1f(yScaleLoc, shaderData[1])
	}
	
	init {
		mNeedsBuild = false
		initialize()
	}
}

class CustomFragmentShader(var shaderResId: Int, var gradH: Float, var maxBright: Float, var shaderData: FloatArray) : FragmentShader() {
	private var mGradientH = 0
	private var mMaxBright = 0
	override fun initialize() {
		mShaderString = RawShaderLoader.fetch(shaderResId)
	}

	override fun main() {}
	override fun setLocations(programHandle: Int) {
		super.setLocations(programHandle)
		mGradientH = getUniformLocation(programHandle, "gradientH")
		mMaxBright = getUniformLocation(programHandle, "maxBright")
	}
	
	override fun applyParams() {
		super.applyParams()
		GLES20.glUniform1f(mGradientH, gradH)
		GLES20.glUniform1f(mMaxBright, maxBright)
	}
	
	init {
		mNeedsBuild = false
		initialize()
	}
}


