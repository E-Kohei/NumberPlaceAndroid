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

package com.norana.numberplace.ui.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.cardview.widget.CardView;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;

import java.util.List;

import com.norana.numberplace.R;
import com.norana.numberplace.database.SudokuItem;
import com.norana.numberplace.ui.activity.MainActivity;
import com.norana.numberplace.ui.adapter.SudokuListAdapter;
import com.norana.numberplace.ui.callback.CopyClickCallback;
import com.norana.numberplace.ui.callback.DeleteClickCallback;
import com.norana.numberplace.ui.callback.SudokuClickCallback;
import com.norana.numberplace.view.SudokuView;
import com.norana.numberplace.viewmodel.SudokuListViewModel;
import com.norana.numberplace.databinding.SudokuListFragmentBinding;
import com.norana.numberplace.databinding.SudokuItemBinding;

public class SudokuListFragment extends Fragment{

	private SudokuListAdapter mAdapter;

	private SudokuListFragmentBinding mBinding;

	private SudokuListViewModel viewModel;

	// if the user is flicking an item, this is false to avoid clicking
	private boolean mIsTouchable = true;

	private LinearLayout selectedContainer = null;

	// callback used when sudoku item is clicked
	private final SudokuClickCallback mSudokuClickCallback =
		new SudokuClickCallback(){
		@Override
		public void onClick(SudokuItem item){
			if (getLifecycle().getCurrentState()
					.isAtLeast(Lifecycle.State.STARTED) && mIsTouchable){
				long id = item.getId();
				SudokuListAdapter.SudokuItemViewHolder viewHolder =
					(SudokuListAdapter.SudokuItemViewHolder) mBinding
					.sudokuList.findViewHolderForItemId(id);
				SudokuItemBinding itemBinding = (viewHolder == null) ? null :
					viewHolder.getBinding();
				SudokuView sudokuview = (itemBinding == null) ? null :
					itemBinding.sudokuItemView;
				((MainActivity) getActivity())
					.startSavedSudokuActivity(id, sudokuview);
			}
		}
	};

	// callback for clicking delete
	private final DeleteClickCallback mDeleteClickCallback =
		new DeleteClickCallback(){
		@Override
		public void onClick(SudokuItem item){
			if (getLifecycle().getCurrentState()
					.isAtLeast(Lifecycle.State.STARTED) && mIsTouchable){
				long id = item.getId();
				viewModel.delete(id);
			}
		}
	};

	// callback for clicking copy
	private final CopyClickCallback mCopyClickCallback =
		new CopyClickCallback(){
		@Override
		public void onClick(SudokuItem item){
			if (getLifecycle().getCurrentState()
					.isAtLeast(Lifecycle.State.STARTED) && mIsTouchable){
				SudokuItem copy = item.copy();
				viewModel.insert(copy);
			}
		}
	};

	public SudokuListFragment(){
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		mBinding = DataBindingUtil.inflate(inflater,
				R.layout.sudoku_list_fragment, container, false);
		
		mAdapter = new SudokuListAdapter(mSudokuClickCallback,
										 mDeleteClickCallback,
										 mCopyClickCallback);
		mBinding.sudokuList.setAdapter(mAdapter);
		mBinding.makeFab.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				((MainActivity) requireActivity()).startMakeSudokuActivity();
			}
		});

		GestureDetector gestureDetector = getMyDetector(mBinding.sudokuList);
		mBinding.sudokuList.addOnItemTouchListener(
				new SimpleOnItemTouchListener(){
			@Override
			public void onTouchEvent(RecyclerView rv, MotionEvent event){
			}
			
			@Override
			public boolean onInterceptTouchEvent(RecyclerView rv,
					MotionEvent event){
				if (!rv.isAttachedToWindow() || rv.getAdapter() == null){
					return false;
				}
				// handle touch event to the recycler view
				gestureDetector.onTouchEvent(event);
				return false;
			}
		});

		return mBinding.getRoot();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		viewModel = 
			(new ViewModelProvider(this)).get(SudokuListViewModel.class);

		viewModel.getAllSudokus().observe(getViewLifecycleOwner(),
				new Observer<List<SudokuItem>>(){
			@Override
			public void onChanged(@Nullable final List<SudokuItem> items){
				if (items != null && !items.isEmpty()){
					mBinding.setIsLoading(false);
					mBinding.setIsEmpty(false);
					mAdapter.setSudokus(items);
				}
				else if (items != null && items.isEmpty()){
					// show text that tells the dataset is empty
					mBinding.setIsLoading(false);
					mBinding.setIsEmpty(true);
					mAdapter.setSudokus(items);
				}
				else{
					// show text that tells loading until finishing loading
					mBinding.setIsLoading(true);
					mBinding.setIsEmpty(false);
				}
				// espresso does not know how to wait for data binding's loop
				// so execute changes
				// sync
				mBinding.executePendingBindings();
			}
		});
	}


	// get GestureDetector to listen fling event of the recycler view
	private GestureDetector getMyDetector(RecyclerView rv){
		GestureDetector gd = new GestureDetector(requireActivity(),
				new SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY){
				float flingTangent = getFlingTangent(velocityX, velocityY);
				View v = rv.findChildViewUnder(e1.getX(), e1.getY());
				LinearLayout container = (v == null) ? null : 
					(LinearLayout) rv.findContainingItemView(v);
				int flingPos = rv.getChildAdapterPosition(container);
				if (selectedContainer == null){
					if (flingTangent < Math.PI/6 && velocityX < 0
							&& container != null && mIsTouchable){
						// show the delete button
						mIsTouchable = false;
						getSlideLeftAnimation(container, velocityX)
						.start();
						return true;
					}
				}
				else{   // selectedContainer != null
					int pos2 = rv.getChildAdapterPosition(selectedContainer);
					if (flingTangent < Math.PI/6 && velocityX > 0
							&& flingPos == pos2 && mIsTouchable){
						// hide the delete button with the speed vx
						mIsTouchable = false;
						getSlideRightAnimation(selectedContainer, velocityX)
						.start();
					}
					else{
						// hide the delete button slowly (0.5sec)
						mIsTouchable = false;
						getSlideRightAnimation(selectedContainer, 1500)
						.start();
					}
					selectedContainer = null;
					return true;
				}
				return false;
			}
		});
		return gd;
	}

	// get fling vector's tangent
	private float getFlingTangent(float vx, float vy){
		return (float) Math.atan2(Math.abs(vy), Math.abs(vx));
	}

	// get animation that slides an item to the right
	private ValueAnimator getSlideRightAnimation(LinearLayout container,
			float velocityX){
		ValueAnimator anim = ValueAnimator.ofFloat(1f, 0f);
		AnimatorUpdateListener updateListener = new AnimatorUpdateListener(){
			@Override
			public void onAnimationUpdate(ValueAnimator updateAnim){
				float animatedValue =
					(float) updateAnim.getAnimatedValue();
				// change the layout weight of the delete cardview
				CardView menuV = (CardView) container.getChildAt(1);
				LinearLayout.LayoutParams params =
					(LinearLayout.LayoutParams) menuV.getLayoutParams();
				params.weight = animatedValue;
				menuV.setLayoutParams(params);
				menuV.setAlpha(animatedValue);
			}
		};
		AnimatorListener startEndListener = new AnimatorListener(){
			@Override public void onAnimationEnd(Animator animation){
				// set touchable
				mIsTouchable = true;
			}

			@Override public void onAnimationStart(Animator animation){}
			@Override public void onAnimationCancel(Animator animation){}
			@Override public void onAnimationRepeat(Animator animation){}
		};
		anim.addUpdateListener(updateListener);
		anim.addListener(startEndListener);
		anim.setInterpolator(new LinearOutSlowInInterpolator());
		anim.setDuration((long)Math.abs(1000000/velocityX));
		return anim;
	}

	// get animation that slides an item to the left
	private ValueAnimator getSlideLeftAnimation(LinearLayout container,
			float velocityX){
		ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
		AnimatorUpdateListener mListener = new AnimatorUpdateListener(){
			@Override
			public void onAnimationUpdate(ValueAnimator updateAnim){
				float animatedValue =
					(float) updateAnim.getAnimatedValue();
				// change the layout weight of the delete cardview
				CardView menuV = (CardView) container.getChildAt(1);
				LinearLayout.LayoutParams params =
					(LinearLayout.LayoutParams) menuV.getLayoutParams();
				params.weight = animatedValue;
				menuV.setLayoutParams(params);
				menuV.setAlpha(animatedValue);
			}
		};
		AnimatorListener endListener = new AnimatorListener(){
			@Override public void onAnimationEnd(Animator animation){
				// set touchable
				mIsTouchable = true;
				selectedContainer = container;
			}

			@Override public void onAnimationStart(Animator animation){}
			@Override public void onAnimationCancel(Animator animation){}
			@Override public void onAnimationRepeat(Animator animation){}
		};
		anim.addUpdateListener(mListener);
		anim.addListener(endListener);
		anim.setInterpolator(new OvershootInterpolator());
		anim.setDuration((long)Math.abs(1000000/velocityX));
		return anim;
	}
}
