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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import com.norana.numberplace.R;

public class EulaDialogFragment extends DialogFragment{

	private final static String EULA_PREFERENCES = "eula_pref";
	private final static String IS_EULA_ACCEPTED = "eula_pref.accepted";
	
	public EulaDialogFragment(){}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
		CharSequence eula_text = readFile(requireActivity());
		builder.setTitle(R.string.dialog_eula_title)
			.setMessage(eula_text)
			.setPositiveButton(R.string.agree,
				new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					Activity activity = requireActivity();
					final SharedPreferences preferences = activity
						.getSharedPreferences(EULA_PREFERENCES,
							Context.MODE_PRIVATE);
					preferences.edit()
						.putBoolean(IS_EULA_ACCEPTED, true)
						.apply();
				}
			})
			.setNegativeButton(R.string.dont_agree,
				new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					requireActivity().finish();
				}
			});
		return builder.create();
	}

	private CharSequence readFile(Activity activity){
		InputStream in = null;
		BufferedReader din = null;
		final Resources res = requireActivity().getResources();
		try {
			in = res.openRawResource(R.raw.eula);
			din = new BufferedReader(
					new InputStreamReader(in, "utf-8"));
			StringBuilder buffer = new StringBuilder();
			String s;
			while ((s = din.readLine()) != null){
				// copy sentences to buffer
				buffer.append(s).append('\n');
			}
			return buffer;
		}
		catch (IOException e){
			return activity.getString(R.string.io_error);
		}
		finally{
			if (din != null){
				try{ din.close(); } catch (IOException e){ //ignore
				}
			}
			if (in != null){
				try{ in.close(); } catch (IOException e){ //ignore
				}
			}
		}
	}
}
