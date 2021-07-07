package com.test.demo;

import com.test.demo.mediafacer.mediaHolders.pictureContent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.rajawali3d.math.MathUtil
import org.rajawali3d.math.vector.Vector3
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CarouselController(mImages: ArrayList<pictureContent>, mScene: MyRenderer) : PhotoViewController(mImages, mScene) {
	private var xDiff = 0.0
	private var zDiff = 0.0
	private var sign = 1
	private var clipRight = 5
	private var clipLeft = 5
	private var distanceX:Double = 0.0
	private var progressOld:Double = 0.0
	
	override fun init(priorPhotoObjects: Array<PhotoObject?>?, priorProgress: Double) {
		super.init(priorPhotoObjects, priorProgress)
		mouseSensitivity = 0.08
		xDiff = planeWidth * 1.2
		zDiff = planeWidth * 1.4
		
		progress = 0.0
		maxProgress = xDiff * (mImages.size - 1)
		
		mRenderer.currentCamera.x = 0.0
		mRenderer.currentCamera.y = 0.0
		mRenderer.currentCamera.z = zToFit(planeWidth * 2)
		mRenderer.currentCamera.lookAt = Vector3()
		
		sceneManager = GlobalScope.launch {
			sceneManagerRunning = true
			while (sceneManagerRunning) {
				for (i in 0 until mImages.size) {
					if (!sceneManagerRunning)
						break
					val basePos = (progress / xDiff).toInt()
					val posMin = max(basePos - clipLeft, 0)
					val posMax = min(basePos + clipRight, mImages.size - 1)
					if (i in posMin..posMax) { // load
						loadPhotoObject(i)
					} else {                     // remove
						removePhotoObject(i)
					}
				}
			}
		}
		updatePositions()
	}
	
	private fun loadPhotoObject(index: Int) {
		if (photoObjects[index] != null)
			return
		val po = loadPhoto(index, planeWidth, planeHeight, drawTitle = drawTitle)
		po.photo!!.x = index * xDiff
		po.photo!!.z = -index * zDiff
		po.photo!!.name = "$index"
		po.shadow!!.position = po.photo!!.position
		po.shadow!!.y -= po.photo!!.boundingBox.max.y - po.photo!!.boundingBox.min.y
		po.shadow!!.name = "shadow$index"
		mRenderer.currentScene.addChild(po.photo)
		mRenderer.currentScene.addChild(po.shadow)
		photoObjects[index] = po
	}
	
	override fun update(deltaTime: Double) {
		if (progress <= 0.0)
			sign = 1
		if (progress >= maxProgress)
			sign = -1
		progress = MathUtil.clamp(progress + sign * deltaTime * slideShowSpeed, 0.0, maxProgress)
		if (!photoShow)
			updatePositions()
	}
	
	private fun updatePositions() {
		photoObjects.forEach {
			it?.let {
				val vec = Vector3()
				val rot = posOnPath(it.index, vec)
				it.photo!!.position = vec
				it.photo!!.setRotation(Vector3.Axis.Y, rot)
				it.shadow!!.x = vec.x
				it.shadow!!.z = vec.z
				it.shadow!!.setRotation(Vector3.Axis.Y, rot)
			}
		}
	}
	
	override fun onScroll(distanceX: Double, distanceY: Double) {
		this.progress = MathUtil.clamp(this.progress + distanceX * mouseSensitivity, 0.0, maxProgress)
		this.progressOld = progress
		this.distanceX = distanceX
		updatePositions()
	}
	
	private fun posOnPath(index: Int, outPos: Vector3): Double {
		val rotateThreshold = planeWidth
		val angle = Vector3(1.0, 0.0, 0.0).angle(Vector3(xDiff, zDiff, 0.0))
		outPos.x = index * xDiff - progress
		outPos.y = 0.0
		outPos.z = -abs(zDiff / xDiff * outPos.x)
		
		return MathUtil.clamp(outPos.x / rotateThreshold * angle, -angle, angle)
	}
	
	override fun onTouchRelease() {
		super.onTouchRelease()
		var diff = distanceX
		if(diff.toString().contains("-")){
			diff -= 30
			progressOld -= 24
		}else{
			diff += 30
			progressOld += 24
		}
		this.progress = MathUtil.clamp(progressOld + diff * mouseSensitivity, 0.0, maxProgress)
		updatePositions()
	}
}