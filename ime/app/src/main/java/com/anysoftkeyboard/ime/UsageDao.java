package com.anysoftkeyboard.ime;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UsageDao {
    @Insert
    void insert(UsageStat stat);

    @Query("SELECT chosenAppPackage FROM usage_stats WHERE textCategory = :category GROUP BY chosenAppPackage ORDER BY COUNT(chosenAppPackage) DESC")
    List<String> getRankedAppsForCategory(String category);
}