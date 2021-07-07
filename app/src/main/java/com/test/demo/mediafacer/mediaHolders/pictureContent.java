package com.test.demo.mediafacer.mediaHolders;

import android.util.SizeF;

public class pictureContent {

    private String picturName;
    private String picturePath;
    private  Long pictureSize;
    private  String assertFileStringUri;
    private  int pictureId;
    private String bucketID;
    private SizeF objectSize = new SizeF(0F, 0F);
    private SizeF frameSize = new SizeF(0F, 0F);
    private float[] vertexShaderData = new float[]{1f,1f};
    private float[] fragShaderData = new float[]{0f, 1f, 0f, 1f};
    
    public pictureContent() {
    }

    public pictureContent(String picturName, String picturePath, long pictureSize, String assertFileStringUri) {
        this.picturName = picturName;
        this.picturePath = picturePath;
        this.pictureSize = pictureSize;
        this.assertFileStringUri = assertFileStringUri;
    }


    public String getPicturName() {
        return picturName;
    }

    public void setPicturName(String picturName) {
        this.picturName = picturName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public long getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(long pictureSize) {
        this.pictureSize = pictureSize;
    }

    public String getAssertFileStringUri() {
        return assertFileStringUri;
    }

    public void setAssertFileStringUri(String assertFileStringUri) {
        this.assertFileStringUri = assertFileStringUri;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }
    
    public String getBucketID() {
        return bucketID;
    }
    
    public void setBucketID(String bucketID) {
        this.bucketID = bucketID;
    }
    
    public SizeF getObjectSize() {
        return objectSize;
    }
    
    public void setObjectSize(SizeF objectSize) {
        this.objectSize = objectSize;
    }
    
    public SizeF getFrameSize() {
        return frameSize;
    }
    
    public void setFrameSize(SizeF frameSize) {
        this.frameSize = frameSize;
    }
    
    public float[] getVertexShaderData() {
        return vertexShaderData;
    }
    
    public void setVertexShaderData(float[] vertexShaderData) {
        this.vertexShaderData = vertexShaderData;
    }
    
    public float[] getFragShaderData() {
        return fragShaderData;
    }
    
    public void setFragShaderData(float[] fragShaderData) {
        this.fragShaderData = fragShaderData;
    }
    
    
}
