package com.gosproj.gosproject.Structures;

import java.util.ArrayList;
import java.util.Date;

public class MainCategory
{
    public String date;
    public String name;
    public ArrayList<SecondaryCategory> secondaryCategories;

    public MainCategory(String name, String date, ArrayList<SecondaryCategory> secondaryCategories)
    {
        this.date = date;
        this.name = name;
        this.secondaryCategories = secondaryCategories;
    }
}
