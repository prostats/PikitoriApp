package piki.example.com.loginpikiapp.pikitori.core;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.domain.ImageModel;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ImageUtils {

    public final static int DISPLAYWIDHT = 1024;
    public final static int DISPLAYHEIGHT = 768;
    private static final String TAG = "ImageUtils";

    public static Map<String,ArrayList<ImageModel>> getImagesFromExternalByDay(Context context){

        TreeMap<String,ArrayList<ImageModel>> folders = new TreeMap<>();

        String pictureCols[] = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.DATE_ADDED
        };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, pictureCols, null, null,  MediaStore.Images.Media.DATE_ADDED+" ASC");

        if(cursor!=null && cursor.getCount()>0 ){
            cursor.moveToFirst();
        }
        ArrayList<ImageModel> imageslist =null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            for (int index = 0; index < cursor.getCount(); index++) {

                String id = cursor.getString(cursor.getColumnIndex(pictureCols[0]));
                String imageUri = cursor.getString(cursor.getColumnIndex(pictureCols[1]));
                String bucket = cursor.getString(cursor.getColumnIndex(pictureCols[2]));
                String latitude = cursor.getString(cursor.getColumnIndex(pictureCols[3]));
                String longitude = cursor.getString(cursor.getColumnIndex(pictureCols[4]));
                Long date = cursor.getLong(cursor.getColumnIndex(pictureCols[5]));
                Timestamp timestamp = new Timestamp(date * 1000);
                String day = sdf.format(new Date(timestamp.getTime()));

                Log.d(TAG, "date: "+ day + " id: "+ id + "image:" + imageUri + "latitude: "+ latitude + " longitude: " + longitude  );

                if(folders.containsKey(day)){
                    ImageModel tmp = new ImageModel(id,imageUri,bucket,false);
                    tmp.setLat(latitude);
                    tmp.setLng(longitude);
                    tmp.setDay(day);
                    folders.get(day).add(tmp);
                }else{
                    imageslist =  new ArrayList<ImageModel>();
                    ImageModel tmp = new ImageModel(id,imageUri,bucket,false);
                    tmp.setLat(latitude);
                    tmp.setLng(longitude);
                    tmp.setDay(day);
                    imageslist.add(tmp);
                    Log.d(TAG, "day added : " + day);
                    folders.put(day,imageslist);
                }
                cursor.moveToPosition(index+1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor !=null) {
                cursor.close();
            }
        }

        return folders;
    }

    public static ArrayList<ImageModel> getImagesFromExternal(Context context){

        ArrayList<ImageModel> imageslist = new ArrayList<ImageModel>();

        String pictureCols[] = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE
        };
        Cursor cursor = context.getContentResolver() .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, pictureCols, null, null,  MediaStore.Images.Media.DATE_MODIFIED+" DESC");

        if(cursor!=null && cursor.getCount()>0 ){
            cursor.moveToFirst();
        }

        try {
            for (int index = 0; index < cursor.getCount(); index++) {

                String id = cursor.getString(cursor.getColumnIndex(pictureCols[0]));
                String imageUri = cursor.getString(cursor.getColumnIndex(pictureCols[1]));
                String bucket = cursor.getString(cursor.getColumnIndex(pictureCols[2]));
                String latitude = cursor.getString(cursor.getColumnIndex(pictureCols[3]));
                String longitude = cursor.getString(cursor.getColumnIndex(pictureCols[4]));

                ImageModel tmp = new ImageModel(id,imageUri,bucket,false);
                tmp.setLat(latitude);
                tmp.setLng(longitude);
                imageslist.add(tmp);

                cursor.moveToPosition(index+1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor !=null) {
                cursor.close();
            }
        }

        return imageslist;
    }

    public static  String getRealPathFromURI(Context context, Uri uri){
        if(uri.toString().startsWith("file://")){
            return uri.toString().replaceAll("file://","");
        }
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(columnIndex);
    }

    public static DisplayImageOptions universalImageConfiguration(final int width, final int height) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_default_profile)
                .showImageOnFail( R.drawable.ic_default_profile )
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap( bmp, width , height, false);
                    }
                })
                .build();
        return options;
    }

    public List<Bitmap> renderImage(Context context, List<String> selectedImage) {
        int size = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 4;
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = false;

        for (String uri : selectedImage) {
            Bitmap org = BitmapFactory.decodeFile(uri, bmpFactoryOptions);

            //캔버스 생성
//            Bitmap mbitmap = Bitmap.createBitmap(4 * size, 3 * size, Bitmap.Config.ARGB_8888);
//            Canvas mcanvas = new Canvas(mbitmap);
//            mcanvas.drawColor(Color.BLACK);

            //mcanvas.drawBitmap();

            Bitmap msized = null;
            //비율 만들기
            float widthRatio = org.getWidth() / (float) DISPLAYWIDHT;
            float heightRatio = org.getHeight() / (float) DISPLAYHEIGHT;

            if (widthRatio > 1 || heightRatio > 1) {
                if (widthRatio >= heightRatio) {

                    msized = Bitmap.createScaledBitmap(org, (int) ((float) org.getWidth() / widthRatio), (int) ((float) org.getHeight() / widthRatio), false);
                    System.out.println("너비가 큰 사진수정: " + msized.getWidth() + ":" + msized.getHeight());
//                    mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
                } else {

                    msized = Bitmap.createScaledBitmap(org, (int) (org.getWidth() / heightRatio), (int) (org.getHeight() / heightRatio), false);
                    System.out.println("높이가 큰 사진수정: " + msized.getWidth() + ":" + msized.getHeight());
//                    mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
                }
            } else {
                System.out.println("그냥 작은사진");
                msized = org;
//                mcanvas.drawBitmap(msized, (((4 * size) / 2) - (msized.getWidth() / 2)), (((3 * size) / 2) - (msized.getHeight() / 2)), null);
            }
            System.out.println("ratio " + widthRatio + ":" + heightRatio);
            System.out.println("image size: w: " + org.getWidth() + " h: " + org.getHeight() + " rew: " + msized.getWidth() + " reh: " + msized.getHeight());
            bitmapList.add(msized);
        }
        return bitmapList;
    }

    public List<File>  saveFiles(List<Bitmap> bitmapList, List<String> selectedImage){
        List<File> fileList = new ArrayList<File>();
        for(int i = 0; i< bitmapList.size() ; i++) {
            String filepath = selectedImage.get(i);
            String filename = filepath.substring(filepath.lastIndexOf("/"),filepath.indexOf("."));
            fileList.add(i, new File(saveBitmaptoPNG(getApplicationContext(),bitmapList.get(i), filename )));
        }
        return fileList;
    }

    public  String saveBitmaptoPNG(Context context,Bitmap bitmap, String name){
        String ex_storage = context.getCacheDir().getAbsolutePath();
//        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        System.out.println(ex_storage);
        String file_name = name+".png";
        String folder_path = ex_storage+BasicInfo.tmpfolder;
        String path = folder_path+ File.separator + file_name;

        File file_path = null;
        try{
            file_path = new File(folder_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
        return path;
    }

    public  boolean deleteBitmapCache(Context context){
        String ex_storage = context.getCacheDir().getAbsolutePath();
        String pikitmp = ex_storage+File.separator + BasicInfo.tmpfolder;
        File tempFile = new File(pikitmp);
        if(tempFile.exists()) {
            tempFile.delete();
        }
        return  true;
    }

    public static  Uri makePath(Context context, String filename) {

        String path = context.getCacheDir().getAbsolutePath();
        System.out.println(path);
        String folderPath = path + File.separator + "pikitmp";

        //1. pikitmp 폴더 생성.
        File fileFolderPath = new File(folderPath);
        fileFolderPath.mkdir();

        //2. 파일 생성.
        String filePath = folderPath + File.separator +  filename +".png";
        File file = new  File(filePath);

        return Uri.fromFile(file);
    }


}
