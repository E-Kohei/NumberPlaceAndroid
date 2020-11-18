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
import android.content.res.Resources;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import com.norana.numberplace.R;

public class AboutAppDialogFragment extends DialogFragment{
	
	public AboutAppDialogFragment(){}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
		// get the layout inflater
		LayoutInflater inflater = requireActivity().getLayoutInflater();


		View layout = inflater.inflate(R.layout.dialog_about, null);
		final TextView message = layout.findViewById(R.id.message);
		final TextView notice = layout.findViewById(R.id.notice);

		CharSequence about_message 
			= readFile(requireActivity(), R.raw.about_app);
		message.setText(about_message);
		CharSequence notice_message
			= readFile(requireActivity(), R.raw.notice);
		notice.setText(notice_message);

		builder.setTitle(R.string.dialog_about_title)
			.setView(layout)
			.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){}
			});
		return builder.create();
	}

	private static CharSequence readFile(Activity activity, int fileId){
		InputStream in = null;
		BufferedReader din = null;
		final Resources res = activity.getResources();
		try {
			in = res.openRawResource(fileId);
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
				try{ din.close(); }catch (IOException e){ // ignore
				}
			}
			if (in != null){
				try{ in.close(); } catch (IOException e){ //ignore
				}
			}
		}
	}
}