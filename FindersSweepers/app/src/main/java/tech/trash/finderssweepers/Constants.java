package tech.trash.finderssweepers;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Aaron on 3/24/2018.
 */

public class Constants {
    public final static String URL = "http://172.24.13.142";
    public static ArrayList<String> dummyCategories = new ArrayList<>();
    public static int dummyScore = 0;
    public static ArrayList<Coordinate> dummyCoordinates = new ArrayList<>();
    public static ArrayList<Trash> dummyTrash = new ArrayList<>();
    public final static String[] CATEGORIES = {
            "Soft Plastic",
            "Hard Plastic",
            "Paper",
            "Cigarettes",
            "Cans/Bottles",
            "Needles",
            "Pile",
            "Human",
            "Other"
    };
}
