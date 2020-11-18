/*
 * Copyright 2019, E-Kohei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.norana.numberplace.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.norana.numberplace.R;
import com.norana.numberplace.ui.dialog.AboutAppDialogFragment;
import com.norana.numberplace.ui.dialog.EulaDialogFragment;
import com.norana.numberplace.ui.fragment.LevelSelectFragment;
import com.norana.numberplace.ui.fragment.MainMenuFragment;
import com.norana.numberplace.ui.fragment.SudokuListFragment;

public class MainActivity extends AppCompatActivity {

	// keys for shared preferences
	private final static String EULA_PREFERENCES = "eula_pref";
	private final static String IS_EULA_ACCEPTED = "eula_pref.accepted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/* show Eula before opening app if the user have not agreed yet */
		//showEula();

        setContentView(R.layout.activity_main);

		// set fragment if this is first creation
		if (savedInstanceState == null){
			MainMenuFragment fragment = new MainMenuFragment();
			getSupportFragmentManager().beginTransaction()
				.add(R.id.main_fragment_container, fragment,
						MainMenuFragment.TAG)
				.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		switch (id){
			case R.id.action_manual:
				// start manual activity
				startManualActivity();
				return true;
			case R.id.action_about:
				// show fragment about this app
				showAbout();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Show levels of sudokus the user can play */
	public void showLevels(){
		LevelSelectFragment fragment = new LevelSelectFragment();
		getSupportFragmentManager().beginTransaction()
			// set fade in/out animation 
			.setCustomAnimations(
				R.animator.animation_fade_in,
				R.animator.animation_fade_out,
				R.animator.animation_fade_in,
				R.animator.animation_fade_out)
			.addToBackStack(null)
			.replace(R.id.main_fragment_container, fragment)
			.commit();
	}

	/** Show the list of saved sudokus */
	public void showSavedSudokus(){
		SudokuListFragment fragment = new SudokuListFragment();
		getSupportFragmentManager().beginTransaction()
			// set fade in/out animation
			.setCustomAnimations(
				R.animator.animation_fade_in,
				R.animator.animation_fade_out,
				R.animator.animation_fade_in,
				R.animator.animation_fade_out)
			.addToBackStack(null)
			.replace(R.id.main_fragment_container, fragment)
			.commit();
	}

	/** Starts Activity to play a sudoku */
	public void startPlaySudokuActivity(String level){
		Intent data = new Intent(MainActivity.this, PlayActivity.class);
		data.putExtra("level", level);
		startActivity(data);
	}

	/** Starts Activity to make a sudoku */
	public void startMakeSudokuActivity(){
		Intent data = new Intent(MainActivity.this, MakeActivity.class);
		startActivity(data);
	}

	/** Starts Activity to play or edit a saved sudoku */
	public void startSavedSudokuActivity(long sudokuId, View transView){
		Intent data = new Intent(MainActivity.this, SavedActivity.class);
		data.putExtra("sudokuId", sudokuId);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
				&& transView != null){
			// apply activity transition
			ActivityOptions options = ActivityOptions
				.makeSceneTransitionAnimation(MainActivity.this,
						transView, "sudokuTransition");
			startActivity(data, options.toBundle());
		}
		else{
			// start activity without transition
			startActivity(data);
		}
	}

	/** Starts Activity to see how to use */
	public void startManualActivity(){
		Intent data = new Intent(MainActivity.this, ManualActivity.class);
		startActivity(data);
	}

	// show dialog fragment about this app
	private void showAbout(){
		AboutAppDialogFragment fragment = new AboutAppDialogFragment();
		fragment.show(getSupportFragmentManager(), "aboutAppDialog");
	}

	// show Eula
	private void showEula(){
		final SharedPreferences preferences = getSharedPreferences(
				EULA_PREFERENCES, MODE_PRIVATE);
		if (!preferences.getBoolean(IS_EULA_ACCEPTED, false)){
			EulaDialogFragment fragment = new EulaDialogFragment();
			fragment.show(getSupportFragmentManager(), "Eula");
		}
	}
}
