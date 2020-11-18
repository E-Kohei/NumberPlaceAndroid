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

package com.norana.numberplace.view;

import android.content.Context;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.transition.ChangeBounds;
import android.view.View;
import android.view.ViewGroup;
import android.util.AttributeSet;

public class SudokuTransition extends ChangeBounds{

	public SudokuTransition(){}

	public SudokuTransition(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	@Override
	public void captureStartValues(TransitionValues transitionValues){
		super.captureStartValues(transitionValues);
	}

	@Override
	public void captureEndValues(TransitionValues transitionValues){
		super.captureEndValues(transitionValues);
	}

	@Override
	public Animator createAnimator(ViewGroup sceneRoot,
			TransitionValues startValues, TransitionValues endValues){
		if (startValues == null || endValues == null){
			return null;
		}

		final View startView = startValues.view;
		final View endView = endValues.view;

		Animator animFadeOut = null;
		Animator anim = super.createAnimator(sceneRoot, startValues, endValues);
		Animator animFadeIn = null;

		if (startView instanceof ViewGroup){
			View child1 = ((ViewGroup) startView).getChildAt(0);
			if (child1 != null){
				animFadeOut = ObjectAnimator
					.ofFloat(child1, "alpha", 1.0f, 0.0f);
				animFadeOut.setDuration(500);
			}
		}
		if (endView instanceof ViewGroup){
			View child2 = ((ViewGroup) endView).getChildAt(0);
			if (child2 != null){
				animFadeIn = ObjectAnimator
					.ofFloat(child2, "alpha", 0.0f, 1.0f);
				animFadeIn.setDuration(500);
			}
		}

		if (animFadeOut != null && anim != null && animFadeIn != null){
			AnimatorSet set = new AnimatorSet();
			set.playSequentially(animFadeOut, anim, animFadeIn);
			return set;
		}
		else{
			return null;
		}
	}
}

