/*
 * Copyright 2020, E-Kohei
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

package com.norana.numberplace.ui.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.norana.numberplace.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ManualFragment extends Fragment {

    private int manual_id;

    private ManualFragment() {}

    public static ManualFragment createInstance(int manual_string_id){
        ManualFragment fragment = new ManualFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("manual_string_id", manual_string_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("manual_string_id")){
            this.manual_id = bundle.getInt("manual_string_id");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View layout = inflater.inflate(R.layout.manual_fragment, container, false);

        // get id-s of manual text and images
        int title_id = R.string.menu_basic_operation;
        Integer manual_intro_id = null;
        Integer manual_file_id_1 = R.raw.manual_none;
        Integer manual_file_id_2 = null;
        Integer manual_drawable_1 = null;
        Integer manual_drawable_2 = null;
        switch (this.manual_id){
            case R.string.menu_basic_operation:
                title_id = this.manual_id;
                manual_file_id_1 = R.raw.manual_basic_operation_1;
                manual_file_id_2 = R.raw.manual_basic_operation_2;
                manual_drawable_1 = R.drawable.manual_basic_1;
                manual_drawable_2 = R.drawable.manual_basic_2;
                break;
            case R.string.menu_play_manual:
                title_id = this.manual_id;
                manual_intro_id = R.raw.manual_intro_play;
                manual_file_id_1 = R.raw.manual_play_puzzle_1;
                manual_file_id_2 = R.raw.manual_play_puzzle_2;
                manual_drawable_1 = R.drawable.manual_play_1;
                manual_drawable_2 = R.drawable.manual_play_2;
                break;
            case R.string.menu_make_manual:
                title_id = this.manual_id;
                manual_intro_id = R.raw.manual_intro_make;
                manual_file_id_1 = R.raw.manual_make_puzzle;
                manual_drawable_1 = R.drawable.manual_make_1;
                break;
            case R.string.menu_saved_manual:
                title_id = this.manual_id;
                manual_intro_id = R.raw.manual_intro_collection;
                manual_file_id_1 = R.raw.manual_collection_1;
                manual_file_id_2 = R.raw.manual_collection_2;
                manual_drawable_1 = R.drawable.manual_save_1;
                manual_drawable_2 = R.drawable.manual_save_2;
                break;
        }

        // read strings from raw files
        Activity activity = requireActivity();
        CharSequence introduction = readFile(activity, manual_intro_id);
        CharSequence document1 = readFile(activity, manual_file_id_1);
        CharSequence document2 = readFile(activity, manual_file_id_2);

        // set text and images
        ((TextView) layout.findViewById(R.id.manual_title)).setText(title_id);
        ((TextView) layout.findViewById(R.id.manual_introduction)).setText(introduction);
        ((TextView) layout.findViewById(R.id.manual_document_1)).setText(document1);
        ((TextView) layout.findViewById(R.id.manual_document_2)).setText(document2);
        if (manual_drawable_1 != null){
            ((ImageView) layout.findViewById(R.id.manual_image_1)).setImageResource(manual_drawable_1);
        }
        if (manual_drawable_2 != null){
            ((ImageView) layout.findViewById(R.id.manual_image_2)).setImageResource(manual_drawable_2);
        }
        return layout;

    }

    private static CharSequence readFile(Activity activity, Integer fileId){
        if (fileId == null)
            return "";
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
