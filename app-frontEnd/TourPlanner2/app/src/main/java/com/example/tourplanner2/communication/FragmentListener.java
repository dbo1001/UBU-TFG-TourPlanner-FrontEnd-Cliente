package com.example.tourplanner2.communication;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public interface FragmentListener<T extends AppCompatActivity> extends Serializable {

	void onArticleSelected(T AppCompatActivity, Intent intent);
}
