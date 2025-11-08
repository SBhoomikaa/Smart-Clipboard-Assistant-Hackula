package com.anysoftkeyboard.ime;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usage_stats")
public class UsageStat {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String textCategory;
    public String chosenAppPackage;
}
