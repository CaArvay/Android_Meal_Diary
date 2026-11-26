package com.example.colearvay_project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Meal {
    public long id;
    public String mealName, resName, resAddr, date;
    public int rating;
    public double price;

    /**
     * Constructor for making a Meal. id is set to -1 and changed later when passed to the database
     * @param sName
     * @param sRest
     * @param sAddr
     * @param nRating
     * @param dPrice
     * @param sDate
     */
    public Meal(String sName, String sRest, String sAddr, int nRating, double dPrice, String sDate)
    {
        this.id = -1;
        this.mealName = sName;
        this.resName = sRest;
        this.resAddr = sAddr;
        this.rating = nRating;
        this.price = dPrice;
        this.date = sDate;
    }

    /**
     * Constructor for making a Meal that already has an id. Used for a Meal alreeady in the
     * database that needs to be updated rather than added
     * @param id
     * @param sName
     * @param sRest
     * @param sAddr
     * @param nRating
     * @param dPrice
     * @param sDate
     */
    public Meal(long id, String sName, String sRest, String sAddr, int nRating, double dPrice, String sDate)
    {
        this(sName, sRest, sAddr, nRating, dPrice, sDate);
        this.id = id;
    }

    @Override
    public String toString()
    {
        return this.resName + " - " + this.date;
    }
}
