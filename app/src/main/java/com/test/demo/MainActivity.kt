package com.test.demo;

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.test.demo.mediafacer.MediaFacer
import com.test.demo.mediafacer.mediaHolders.pictureContent
import kotlinx.coroutines.*
import org.rajawali3d.view.ISurface
import org.rajawali3d.view.SurfaceView


public class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener,  GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener{
	companion object {
		var images: ArrayList<pictureContent> = arrayListOf()
		var image: pictureContent = pictureContent()
	}
	
	private lateinit var renderer: MyRenderer
	private lateinit var mDetector: GestureDetector
	private lateinit var mScaleDetector: ScaleGestureDetector
	private lateinit var rajView : SurfaceView;
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_main)
		rajView = findViewById(R.id.rajView)
		mDetector = GestureDetector(this, this)
		mDetector.setOnDoubleTapListener(this)
		mScaleDetector = ScaleGestureDetector(this, this)

		setRenderer()
	}
	
	private fun setRenderer() {
		renderer = MyRenderer(this)
		renderer.setAntiAliasingMode(ISurface.ANTI_ALIASING_CONFIG.MULTISAMPLING)
		rajView.setSurfaceRenderer(renderer)
		if(PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED || PermissionChecker.checkSelfPermission(
				this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
			) == PermissionChecker.PERMISSION_GRANTED){
			doStart()
		}else{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
			}
		}
	}

	private fun doStart() {
		renderer.currentFolder = -1
		if (renderer.viewController != null) {
			renderer.viewController!!.disappear(PhotoViewController.ACTION_CLEAR)
		}
		GlobalScope.launch(Dispatchers.IO) {
			images = async { getAllPictures(null) }.await()
			withContext(Dispatchers.Main){
				displayImages()
			}
		}
	}

	override fun onResume() {
		super.onResume()
		try {
			if(renderer != null) {
				renderer.onResume()
			}
		}catch (e: Exception){
			e.printStackTrace()
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			1 -> {
				if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
					doStart()
				else {
					finish()
				}
				return
			}
		}
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_OUTSIDE)
			renderer.onTouchRelease()
		return mDetector.onTouchEvent(event) || mScaleDetector.onTouchEvent(event) || super.onTouchEvent(event)
	}
	
	override fun onDown(event: MotionEvent): Boolean {
		renderer.onTapDown()
		return true
	}
	
	override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
		renderer.onFling()
		return true
	}
	
	override fun onLongPress(event: MotionEvent) {
	}
	

	override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
		renderer.onScroll(distanceX.toDouble(), distanceY.toDouble())
		return true
	}
	
	override fun onShowPress(event: MotionEvent) {
	}
	
	override fun onSingleTapUp(event: MotionEvent): Boolean {
		return true
	}

	override fun onDoubleTap(event: MotionEvent): Boolean {
		return true
	}
	
	override fun onDoubleTapEvent(event: MotionEvent): Boolean {
		return true
	}
	
	override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
		return false
	}
	
	override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
		return true
	}
	
	override fun onScaleEnd(detector: ScaleGestureDetector?) {
	}
	
	override fun onScale(detector: ScaleGestureDetector?): Boolean {
		renderer.onScale(detector!!.currentSpan - detector.previousSpan)
		return true
	}

	private suspend fun getAllPictures(image: pictureContent?): ArrayList<pictureContent> {
		var images: ArrayList<pictureContent> = arrayListOf()
		images = MediaFacer.withPictureContex(this).getAllPictureContents(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
		return images
	}


	private fun displayImages(){
		if (renderer.viewController != null)
			renderer.viewController!!.disappear(PhotoViewController.ACTION_CLEAR)
		var photoObjects: Array<PhotoObject?>? = null
		var progress = 0.0
		renderer.viewController = CarouselController(images, renderer)
		renderer.viewController?.let {
			renderer.viewController!!.drawTitle = renderer.currentFolder < 0
			renderer.viewController!!.init(photoObjects, progress)
		}
	}
	
}