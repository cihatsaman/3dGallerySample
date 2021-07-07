package com.test.demo;

import android.content.Context
import android.view.MotionEvent
import org.rajawali3d.materials.Material
import org.rajawali3d.postprocessing.PostProcessingManager
import org.rajawali3d.postprocessing.passes.FXAAPass
import org.rajawali3d.postprocessing.passes.RenderPass


class MyRenderer(mActivity: Context?) : org.rajawali3d.renderer.Renderer(mActivity) {
	
	companion object {

	}
	
	var viewController: PhotoViewController? = null
	var currentMode = -1
	var emptyMaterial: Material? = null
	var currentFolder = -1
	var mEffects: PostProcessingManager? = null
	
	override fun initScene() {
		emptyMaterial = Material()
		emptyMaterial!!.colorInfluence = 1F
		emptyMaterial!!.color = 0xffffff
		setFrameRate(30)
		reset()
		
		mEffects = PostProcessingManager(this, 1.3333).apply {
			addPass(RenderPass(currentScene, currentCamera, 0xffffff))
			addPass(FXAAPass().apply { renderToScreen = true })
		}
	}
	
	private fun reset() {
		currentScene.clearChildren()
		if (mTextureManager.textureCount > 0)
			mTextureManager.reset()
		viewController?.stopSceneManager()
		viewController = null
		currentMode = -1
	}

	
	override fun onTouchEvent(event: MotionEvent?) {
	}
	
	override fun onOffsetsChanged(x: Float, y: Float, z: Float, w: Float, i: Int, j: Int) {
	}
	
	override fun onRender(elapsedTime: Long, deltaTime: Double) {
		try {
			super.onRender(elapsedTime, deltaTime)
		} catch (e: Exception) {
		}
		viewController?.update(deltaTime)
	}
	
	fun onFling() {
		viewController?.onFling()
	}
	
	fun onTouchRelease() {
		viewController?.onTouchRelease()
	}
	
	fun onTapDown() {
		viewController?.onTapDown()
	}
	
	fun onScroll(distanceX: Double, distanceY: Double) {
		if (viewController == null || viewController?.photoShow == null)
			return
		
		viewController?.slideShowSpeed = 0.0
		viewController?.onScroll(distanceX, distanceY)
	}
	
	fun onScale(distance: Float) {
		if (viewController!!.photoShow)
			return
		viewController?.slideShowSpeed = 0.0
		viewController?.onScale(distance)
	}
}