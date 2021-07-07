package com.test.demo.mediafacer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.test.demo.mediafacer.mediaHolders.pictureContent;
import com.test.demo.mediafacer.mediaHolders.pictureFolderContent;

import java.io.File;
import java.util.ArrayList;

public class PictureGet {

    private static  PictureGet pictureGet;
    private final Context pictureContex;
    public static final Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri internalContentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    public static final Uri fileContentUri = MediaStore.Files.getContentUri("external");
    private static Cursor cursor;

    private PictureGet(Context context){
        pictureContex = context.getApplicationContext();
    }

    static PictureGet getInstance(Context context){
        if(pictureGet == null){
            pictureGet = new PictureGet(context);
        }
        return pictureGet;
    }

    @SuppressLint("InlinedApi")
    private final String[] Projections = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN};

    /**Returns an ArrayList of {@link pictureContent}  */
    @SuppressLint("InlinedApi")
    public ArrayList<pictureContent> getAllPictureContents(Uri contentLocation){
        ArrayList<pictureContent> images = new ArrayList<>();
        cursor = pictureContex.getContentResolver().query( contentLocation, Projections, null, null,
                "LOWER ("+MediaStore.Images.Media.DATE_MODIFIED+") DESC");
        try {
            cursor.moveToFirst();
            do{
                pictureContent pictureContent = new pictureContent();
    
                pictureContent.setBucketID(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)));

                pictureContent.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pictureContent.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pictureContent.setPictureSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                pictureContent.setPictureId(id);

                Uri contentUri = Uri.withAppendedPath(contentLocation, String.valueOf(id));
                pictureContent.setAssertFileStringUri(contentUri.toString());

                images.add(pictureContent);
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }

    /**Returns an ArrayList of {@link pictureContent} in a specific folder*/
    @SuppressLint("InlinedApi")
    public ArrayList<pictureContent> getAllPictureContentByBucket_id(int bucket_id){
        ArrayList<pictureContent> images = new ArrayList<>();
        cursor = pictureContex.getContentResolver().query( externalContentUri, Projections, MediaStore.Images.Media.BUCKET_ID + " like ? ", new String[] {"%"+bucket_id+"%"},
                MediaStore.Images.Media.DATE_MODIFIED);
        try {
            cursor.moveToFirst();
            do{
                pictureContent pictureContent = new pictureContent();
                pictureContent.setBucketID(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)));
                
                pictureContent.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pictureContent.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pictureContent.setPictureSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                pictureContent.setPictureId(id);

                Uri contentUri = Uri.withAppendedPath(externalContentUri, String.valueOf(id));
                pictureContent.setAssertFileStringUri(contentUri.toString());

                images.add(pictureContent);
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }
    
    @SuppressLint("InlinedApi")
    public ArrayList<pictureContent> getAllPictureContentByFile_id(int bucket_id){
        ArrayList<pictureContent> images = new ArrayList<>();
        cursor = pictureContex.getContentResolver().query( fileContentUri, Projections, MediaStore.Files.FileColumns.BUCKET_ID + " like ? ", new String[] {"%"+bucket_id+"%"},
            "LOWER ("+MediaStore.Files.FileColumns.DATE_TAKEN+") DESC");
        try {
            cursor.moveToFirst();
            do{
                pictureContent pictureContent = new pictureContent();
                pictureContent.setBucketID(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)));
                
                pictureContent.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)));
    
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                pictureContent.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
                
                pictureContent.setPictureSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)));
                
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                pictureContent.setPictureId(id);
                
                Uri contentUri = Uri.withAppendedPath(externalContentUri, String.valueOf(id));
                pictureContent.setAssertFileStringUri(contentUri.toString());
    
                File file = new File(datapath);
                if(file != null && file.getName().startsWith(".") && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".jpg"))) {
                    images.add(pictureContent);
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }
    
    /**Returns an ArrayList of {@link pictureFolderContent}  */
    @SuppressLint("InlinedApi")
    public ArrayList<pictureFolderContent> getAllPictureFolders(){
        ArrayList<pictureFolderContent> absolutePictureFolders = new ArrayList<>();
        ArrayList<Integer> picturePaths = new ArrayList<>();
        cursor = pictureContex.getContentResolver().query( externalContentUri, Projections, null, null,
                MediaStore.Images.Media.DATE_MODIFIED);
        try{
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    pictureFolderContent photoFolder = new pictureFolderContent();
                    pictureContent pictureContent = new pictureContent();
                    pictureContent.setBucketID(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)));
                    pictureContent.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
        
                    pictureContent.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
        
                    pictureContent.setPictureSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
        
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    pictureContent.setPictureId(id);
        
                    pictureContent.setAssertFileStringUri(Uri.withAppendedPath(externalContentUri, String.valueOf(id)).toString());
        
                    String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        
                    int bucket_id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
        
                    String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
                    folderpaths = folderpaths + folder + "/";
                    if (!picturePaths.contains(bucket_id)) {
                        picturePaths.add(bucket_id);
                        photoFolder.setBucket_id(bucket_id);
                        photoFolder.setFolderPath(folderpaths);
                        photoFolder.setFolderName(folder);
                        photoFolder.getPhotos().add(pictureContent);
                        absolutePictureFolders.add(photoFolder);
                    }
//                else {
//                    for (pictureFolderContent folderX : absolutePictureFolders){
//                        if(folderX.getBucket_id() == bucket_id){
//                            folderX.getPhotos().add(pictureContent);
//                        }
//                    }
//                }
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return absolutePictureFolders;
    }
    
    /**Returns an ArrayList of {@link pictureFolderContent}  */
    @SuppressLint("InlinedApi")
    public ArrayList<pictureContent> getAllPictureFoldersSingle(){
        ArrayList<pictureContent> absolutePictureFolders = new ArrayList<>();
        ArrayList<Integer> picturePaths = new ArrayList<>();
        cursor = pictureContex.getContentResolver().query( externalContentUri, Projections, null, null,
            MediaStore.Images.Media.DATE_MODIFIED+" DESC");
        try{
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    //pictureFolderContent photoFolder = new pictureFolderContent();
                    pictureContent pictureContent = new pictureContent();
                    pictureContent.setBucketID(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)));
        
                    pictureContent.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
        
                    pictureContent.setPictureSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
        
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    pictureContent.setPictureId(id);
        
                    pictureContent.setAssertFileStringUri(Uri.withAppendedPath(externalContentUri, String.valueOf(id)).toString());
        
                    String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        
                    int bucket_id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                    File file = new File(datapath);
                    //if(file.getParentFile() != null) {
                        pictureContent.setPicturName(folder);
                    //}
                    if (!picturePaths.contains(bucket_id)) {
                        picturePaths.add(bucket_id);
                        absolutePictureFolders.add(pictureContent);
//                    photoFolder.setBucket_id(bucket_id);
//                    photoFolder.setFolderPath(folderpaths);
//                    photoFolder.setFolderName(folder);
//                    absolutePictureFolders.getPhotos().add(pictureContent);
                    }
//                else {
//                    for (pictureFolderContent folderX : absolutePictureFolders){
//                        if(folderX.getBucket_id() == bucket_id){
//                            folderX.getPhotos().add(pictureContent);
//                        }
//                    }
//                }
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return absolutePictureFolders;
    }
    
    /**Returns an ArrayList of {@link pictureFolderContent}  */
    @SuppressLint("InlinedApi")
    public ArrayList<pictureContent> getAllPictureFoldersHiddenSingle(){
        ArrayList<pictureContent> absolutePictureFolders = new ArrayList<>();
        ArrayList<String> picturePaths = new ArrayList<>();
        cursor = pictureContex.getContentResolver().query( fileContentUri, null, null, null,
            "LOWER ("+MediaStore.Files.FileColumns.DATE_MODIFIED+") DESC");
        try{
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    //pictureFolderContent photoFolder = new pictureFolderContent();
                    pictureContent pictureContent = new pictureContent();
                    pictureContent.setBucketID(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)));
                    
                    pictureContent.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
                    
                    pictureContent.setPictureSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)));
                    
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                    pictureContent.setPictureId(id);
                    
                    pictureContent.setAssertFileStringUri(Uri.withAppendedPath(externalContentUri, String.valueOf(id)).toString());
                    
                    String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME));
                    String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                    
                    //int bucket_id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                    //if(file.getParentFile() != null) {
                    pictureContent.setPicturName(folder);
                    //}
                    File file = new File(datapath);
                    if(file != null && file.getName().startsWith(".") && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".jpg"))) {
                        if (!picturePaths.contains(file.getParentFile().getAbsolutePath())) {
                            picturePaths.add(file.getParentFile().getAbsolutePath());
                            absolutePictureFolders.add(pictureContent);
//                    photoFolder.setBucket_id(bucket_id);
//                    photoFolder.setFolderPath(folderpaths);
//                    photoFolder.setFolderName(folder);
//                    absolutePictureFolders.getPhotos().add(pictureContent);
                        }
                    }
//                else {
//                    for (pictureFolderContent folderX : absolutePictureFolders){
//                        if(folderX.getBucket_id() == bucket_id){
//                            folderX.getPhotos().add(pictureContent);
//                        }
//                    }
//                }
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return absolutePictureFolders;
    }
    
}
