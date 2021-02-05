package com.saqcess.qicpic.app.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


import com.saqcess.qicpic.model.ImageModel;

import java.util.ArrayList;

public class Gallery {
    private boolean boolean_folder;
    public static ArrayList<ImageModel> al_images = new ArrayList<>();

    public ArrayList<ImageModel> getimage_old(Context context) {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                ImageModel obj_model = new ImageModel();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAl_imagepath(al_path);

                al_images.add(obj_model);


            }


        }


        for (int i = 0; i < al_images.size(); i++) {
            Log.e("FOLDER", al_images.get(i).getStr_folder());
            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
            }
        }

        return al_images;
    }

    public ArrayList<ImageModel> getImages(Context context) {
       al_images.clear();
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_id, thum;

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        //cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        final String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        cursor = new MergeCursor(new Cursor[]{context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null),
                context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, null),
                context.getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null, null, null),
                context.getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, columns, null, null, null)});


        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        // column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);

        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            // Log.e("column_id", cursor.getString(column_id));
            Log.e("thum", cursor.getString(thum));


            for (int i = 0; i < al_images.size(); i++) {
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setStr_thumb(cursor.getString(thum));
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                if (absolutePathOfImage.endsWith(".jpg") || absolutePathOfImage.endsWith(".png")) {
                    Log.e("type", "0");
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.add(absolutePathOfImage);
                    ImageModel obj_model = new ImageModel();
                    obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                    obj_model.setAl_imagepath(al_path);
                    obj_model.setType(0);
                    al_images.add(obj_model);

                } else if (absolutePathOfImage.endsWith(".mp4")) {
                    ArrayList<String> al_path = new ArrayList<>();
                    al_path.add(absolutePathOfImage);
                    ImageModel obj_model = new ImageModel();
                    obj_model.setBoolean_selected(false);
                    obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                    //  obj_model.setStr_path(absolutePathOfImage);
                    obj_model.setAl_imagepath(al_path);
                    obj_model.setStr_thumb(cursor.getString(thum));
                    obj_model.setType(1);
                    al_images.add(obj_model);

                    Log.e("type", "1");
                } else if (absolutePathOfImage.endsWith("gif")) {
                    Log.e("type", "2");
                }


            }

        }

//        for (int i = 0; i < al_images.size(); i++) {
//            Log.e("FOLDER",""+ al_images.get(i).getAl_imagepath().size());
////            for (int j = 0; j < al_images.; j++) {
////                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
////            }
//        }
//        Log.e("FOLDER", "total folder"+al_images.size());

        return al_images;

    }
}