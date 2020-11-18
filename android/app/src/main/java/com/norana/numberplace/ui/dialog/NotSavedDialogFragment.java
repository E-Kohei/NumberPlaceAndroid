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

package com.norana.numberplace.ui.dialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.content.DialogInterface;

import com.norana.numberplace.R;
import com.norana.numberplace.ui.activity.PlayActivity;
import com.norana.numberplace.ui.activity.SavedActivity;

public class NotSavedDialogFragment extends DialogFragment{
	
	public NotSavedDialogFragment(){}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		Activity activity = requireActivity();
		int style = R.style.Theme_AppCompat_Light_Dialog_Alert;
		int text = R.string.dialog_play_notsaved_message;
		if (activity instanceof PlayActivity) {
			style = R.style.AlertStyleRed;
			text = R.string.dialog_play_notsaved_message;
		}
		else if (activity instanceof SavedActivity) {
			style = R.style.AlertStyleGreen;
			text = R.string.dialog_saved_notsaved_message;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(activity, style);
		builder.setMessage(text)
			.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int id){
					if (activity instanceof SavedActivity){
						((SavedActivity) activity).saveSudoku();
						((SavedActivity) activity)
							.supportFinishAfterTransition();
					}
					else if (activity instanceof PlayActivity){
						((PlayActivity) activity).saveSudoku();
						activity.finish();
					}
				}
			})
			.setNegativeButton(R.string.no,
					new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int id){
						if (activity instanceof SavedActivity){
							((SavedActivity) activity)
									.supportFinishAfterTransition();
						}
						else if (activity instanceof PlayActivity){
							activity.finish();
						}
					}
			});

		return builder.create();
	}
}
