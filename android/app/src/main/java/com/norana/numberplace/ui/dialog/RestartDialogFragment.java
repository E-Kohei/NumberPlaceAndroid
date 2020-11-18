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

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.norana.numberplace.R;
import com.norana.numberplace.ui.activity.PlayActivity;
import com.norana.numberplace.ui.activity.SavedActivity;

public class RestartDialogFragment extends DialogFragment {

    public RestartDialogFragment(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Activity activity = requireActivity();
        int style = R.style.Theme_AppCompat_Dialog;
        if (activity instanceof PlayActivity)
            style = R.style.AlertStyleRed;
        else if (activity instanceof SavedActivity)
            style = R.style.AlertStyleGreen;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), style);
        builder.setMessage(R.string.dialog_restart_puzzle)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (activity instanceof PlayActivity){
                                    ((PlayActivity) activity).restart();
                                }
                                else if (activity instanceof SavedActivity){
                                    ((SavedActivity) activity).restart();
                                }
                            }
                        })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
        return builder.create();
    }
}
