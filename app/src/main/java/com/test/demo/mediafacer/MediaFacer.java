package com.test.demo.mediafacer;

import android.content.Context;


public class MediaFacer {

    public static com.test.demo.mediafacer.VideoGet withVideoContex(Context contx){
        return com.test.demo.mediafacer.VideoGet.getInstance(contx);
    }

    /**Returns a static instance of {@link PictureGet} */
    public static com.test.demo.mediafacer.PictureGet withPictureContex(Context contx){
        return com.test.demo.mediafacer.PictureGet.getInstance(contx);
    }

    public static AudioGet withAudioContex(Context contx){
        return AudioGet.getInstance(contx);
    }

    public static void Initialize(){

    }

    /** scans all media content on device */
    private void ScanAllMedia(){



    }

    /** save general information about all media on de vice in  */
    private void UpdateGeneralMediaInfo(){



    }

}
