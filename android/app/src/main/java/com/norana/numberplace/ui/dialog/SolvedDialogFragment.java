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

import android.app.Dialog;
import android.os.Bundle;
import android.content.DialogInterface;

import com.norana.numberplace.R;

public class SolvedDialogFragment extends DialogFragment{
	
	public SolvedDialogFragment(){}

	public static SolvedDialogFragment createInstance(String timeText){
		SolvedDialogFragment fragment = new SolvedDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("timeText", timeText);
		fragment.setArguments(bundle);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder 
			= new AlertDialog.Builder(requireActivity());

		Bundle bundle = getArguments();
		String timeText = "";
		if (bundle != null && bundle.containsKey("timeText")){
			timeText = bundle.getString("timeText");
		}

		String dialogText = 
			requireActivity().getString(R.string.dialog_solved_text);
		builder.setTitle(R.string.dialog_solved_title)
			.setMessage(String.format(dialogText, timeText))
			.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){}
			});
		return builder.create();
	}
}
