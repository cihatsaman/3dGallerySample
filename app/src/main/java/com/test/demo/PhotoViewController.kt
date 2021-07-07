package com.test.demo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.ThumbnailUtils
import android.provider.MediaStore.Images.Thumbnails.MINI_KIND
import android.text.TextUtils
import android.util.SizeF
import com.test.demo.mediafacer.mediaHolders.pictureContent
import kotlinx.coroutines.*
import org.rajawali3d.Object3D
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.math.MathUtil
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Plane

data class PhotoObject(
	var index: Int = 0,
	var photo: Object3D? = null,
	var photoPosOrg: Vector3? = null,
	var shadow: Object3D? = null
)

abstract class PhotoViewController(var mImages: ArrayList<pictureContent>, var mRenderer: MyRenderer) {
	companion object {
		val KEEP_ASPECT = 0
		val CLIP_IN = 2
		val ACTION_CLEAR = 0
	}
	
	var planeWidth = 5.0
	var planeHeight = 3.0
	var progress = 0.0
	var maxProgress = 0.0
	var mouseSensitivity = 0.08
	var slideShowSpeed = 0.0
	var photoShow = false
	var drawTitle = true
	protected lateinit var photoObjects: Array<PhotoObject?>
	protected var sceneManager: Job? = null
	protected var isPortrait = false
	
	@Volatile
	protected var sceneManagerRunning = false
	
	
	open fun init(priorPhotoObjects: Array<PhotoObject?>?, priorProgress: Double) {
		mRenderer.currentCamera.fieldOfView = 45.0
		isPortrait = mRenderer.viewportWidth < mRenderer.viewportHeight
		fitPlanSize()
		mRenderer.currentScene.backgroundColor = 0x0
		this.photoObjects = arrayOfNulls(mImages.size)
		priorPhotoObjects?.forEachIndexed { index, photoObject -> photoObject?.let { photoObjects[index] = photoObject } }
	}
	
	abstract fun update(deltaTime: Double)
	abstract fun onScroll(distanceX: Double, distanceY: Double)
	open fun onScale(distance: Float) {}
	open fun onTapDown() {}
	open fun onFling() {}
	open fun onTouchRelease() {}
	open fun leftPage() {}
	open fun rightPage() {}
	open fun reIndex(index: Int): Int {
		return index
	}
	
	open fun disappear(reason: Int, except: Int = -1): Triple<Array<PhotoObject?>?, ArrayList<pictureContent>?, Double> {
		stopSceneManager()
		photoObjects.forEachIndexed { index, _ -> removePhotoObject(index) }
		return Triple(null, null, progress)
	}
	
	private fun fitPlanSize() {
		planeWidth = 5.0
		planeHeight = mRenderer.viewportHeight.toDouble() / mRenderer.viewportWidth.toDouble() * planeWidth
	}

	fun zToFit(w: Double): Double {
		val h = mRenderer.viewportHeight.toDouble() / mRenderer.viewportWidth.toDouble() * w
		return h / MathUtil.tan(MathUtil.degreesToRadians(mRenderer.currentCamera.fieldOfView))
	}
	
	fun surroundFit(size: SizeF): SizeF {
		val screenRatio = mRenderer.viewportWidth.toFloat() / mRenderer.viewportHeight.toFloat()
		val objectRatio = size.width / size.height
		return if (screenRatio > objectRatio)
			SizeF(screenRatio * size.height, size.height)
		else
			SizeF(size.width, 1F / screenRatio * size.width)
	}
	
	private fun fitAspectRatio(fitWidth: Double, fitHeight: Double, orgWidth: Double, orgHeight: Double): SizeF {
		val orgRatio = orgWidth / orgHeight
		val fitRatio = fitWidth / fitHeight
		return if (orgRatio > fitRatio)
			SizeF(fitWidth.toFloat(), (fitRatio / orgRatio * fitHeight).toFloat())
		else
			try {
				SizeF((orgRatio / fitRatio * fitWidth).toFloat(), fitHeight.toFloat())
			}catch (e:IllegalArgumentException){
			
			} as SizeF
	}
	
	@SuppressLint("NewApi")
	private fun thumbnailImage(path: String, cropAspectRatio: Float = 0F): Bitmap {
		return ThumbnailUtils.createImageThumbnail(path, MINI_KIND)!!
	}
	
	private fun drawImageTitleOrFrame(
		bitmap: Bitmap,
		name: String,
		preferredHeight: Double,
		realSize: SizeF,
		drawFrame: Boolean = false,
		drawTitle: Boolean = false
	): Bitmap {
		if (!drawFrame && !drawTitle)
			return bitmap
		val mutableBitmap: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
		val paint = Paint()
		paint.textSize = (preferredHeight / realSize.height * 0.1 * bitmap.height).toFloat()
		val canvas = Canvas(mutableBitmap)
		if (drawTitle) {
			paint.color = Color.BLACK
			canvas.drawText(name, (bitmap.width / 20.0).toFloat() + 1F, (bitmap.height * 0.9).toFloat() + 1F, paint)
			paint.color = Color.WHITE
			canvas.drawText(name, (bitmap.width / 20.0).toFloat(), (bitmap.height * 0.9).toFloat(), paint)
		}
		if (drawFrame) {
			paint.color = Color.WHITE
			paint.style = Paint.Style.STROKE
			paint.strokeWidth = paint.textSize / 2F
			canvas.drawRect(0F, 0F, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
		}
		return mutableBitmap
	}
	
	private fun clipBitmap(bitmap: Bitmap, ratio: Float): Bitmap {
		val w = bitmap.width.toFloat()
		val h = bitmap.height.toFloat()
		return if (w / h > ratio) {
			val gap = ((w / h - ratio) * h / 2).toInt()
			Bitmap.createBitmap(bitmap, gap, 0, bitmap.width - gap * 2, bitmap.height)
		} else {
			val gap = ((h / w - 1 / ratio) * w / 2).toInt()
			Bitmap.createBitmap(bitmap, 0, gap, bitmap.width, bitmap.height - gap * 2)
		}
	}
	
	fun loadPhoto(
            index: Int,
            preferredWidth: Double = 200.0,
            preferredHeight: Double = 200.0,
            withShadow: Boolean = true,
            fitMode: Int = KEEP_ASPECT,
            drawTitle: Boolean = false,
            drawFrame: Boolean = false
	): PhotoObject {
		// Make thumbnail and Clip needed.
			
			var bitmap = thumbnailImage(mImages[index].picturePath)/*BitmapFactory.decodeFile(mImages[index].path)*/
			mImages[index].objectSize = fitAspectRatio(preferredWidth, preferredHeight, bitmap.width.toDouble(), bitmap.height.toDouble())
			if (fitMode == CLIP_IN)
				bitmap = clipBitmap(bitmap, (preferredWidth / preferredHeight).toFloat())
			val realSize = when (fitMode) {
				KEEP_ASPECT -> mImages[index].objectSize
				else -> SizeF(preferredWidth.toFloat(), preferredHeight.toFloat())
			}
			mImages[index].frameSize = realSize
			var pname:String = ""
			if(mImages[index].picturName  != null && !TextUtils.isEmpty(mImages[index].picturName)){
				pname = mImages[index].picturName;
			}
			bitmap = drawImageTitleOrFrame(bitmap, pname, preferredHeight, realSize, drawTitle = drawTitle, drawFrame = drawFrame)
			
			val tex = Texture("tex$index", bitmap)
			tex.filterType = ATexture.FilterType.LINEAR
			// real photo
			val material =
				Material(
					CustomVertexShader(R.raw.vert1, mImages[index].vertexShaderData),
					CustomFragmentShader(R.raw.frag1, if (withShadow) 0.2F else 0.0F, 1.0F, mImages[index].fragShaderData)
				)
			material.colorInfluence = 1F
			material.addTexture(tex)
			val photo = Plane(realSize.width, realSize.height,1,1)
			photo.material = material
			photo.isDoubleSided = true
			
			// photo shadow object
			var photoShadow: Object3D? = null
			if (withShadow) {
				val materialShadow = Material(
					CustomVertexShader(R.raw.vert1, mImages[index].vertexShaderData),
					CustomFragmentShader(R.raw.frag1, 0.2F, 0.2F, mImages[index].fragShaderData)
				)
				materialShadow.colorInfluence = 1F
				materialShadow.addTexture(tex)
				photoShadow = Plane(realSize.width, realSize.height, 1, 1)
				photoShadow.material = materialShadow
				photoShadow.rotate(Vector3.Axis.X, 180.0)
				photoShadow.isDoubleSided = true
			}
			return PhotoObject(photo = photo, shadow = photoShadow, index = index)
	}
	
	fun getPickObjects(): ArrayList<Object3D> {
		val r: ArrayList<Object3D> = arrayListOf()
		photoObjects.forEach {
			it?.let { r.add(it.photo!!) }
		}
		return r
	}
	
	protected fun removeObject(obj: Object3D) {
		val material = obj.material
		val texture = material?.textureList?.get(0)
		if (obj.parent != null)
			obj.parent.removeChild(obj)
		else
			mRenderer.currentScene.removeChild(obj)
		mRenderer.removeMaterial(material)
		mRenderer.removeTexture(texture)
	}
	
	protected fun removePhotoObject(index: Int) {
		if (photoObjects[index] == null)
			return
		photoObjects[index]?.photo?.let { removeObject(photoObjects[index]?.photo!!) }
		photoObjects[index]?.shadow?.let { removeObject(photoObjects[index]?.shadow!!) }
		photoObjects[index] = null
	}
	
	fun stopSceneManager() {
		sceneManagerRunning = false
		sceneManager?.let {
			while (!sceneManager!!.isCancelled && !sceneManager!!.isCompleted) {
			}
		}
	}

	fun getImageInfo(index: Int): pictureContent {
		return mImages[index]
	}

	
	
}