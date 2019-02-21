package com.anhnguyen.homeshopping.controller;

import android.content.Context;
import android.os.Environment;

import com.anhnguyen.homeshopping.model.Product;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

public class BaseItemController {
    public void addTo(ArrayList list, Object obj){
        list.add(obj);
    }

    public void addTo(ArrayList list, int position, Object obj){
        list.add(position, obj);
    }

    public void removeFrom(ArrayList list, Object obj){
        if (list.isEmpty() == false) {
            list.remove(obj);
        }
    }

    public void removeFrom(ArrayList list, int position){
        if(list.isEmpty() == false) {
            list.remove(position);
        }
    }

    public void clear(ArrayList list){
        list.clear();
    }

    public void saveTo(Context context, ArrayList list, String file_name) {
        // check if sd card is mounted and available for read & write
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File directory = new File(context.getFilesDir().getAbsolutePath()
                    + File.separator + "serlization");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            ObjectOutput out;

            try {
                out = new ObjectOutputStream(new FileOutputStream(directory
                        + File.separator + file_name));
                out.writeObject(list);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList readFrom(Context context, String file_name) {
        ObjectInputStream input;
        ArrayList ReturnClass = null;
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        try {

            input = new ObjectInputStream(new FileInputStream(directory
                    + File.separator + file_name));
            ReturnClass = (ArrayList<Product>) input.readObject();
            input.close();

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ReturnClass;
    }
}
