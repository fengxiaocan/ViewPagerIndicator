/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.indicator;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Implementation of {@link androidx.viewpager.widget.PagerAdapter} that uses a
 * {@link androidx.fragment.app.Fragment} to manage each page. This class also handles saving and
 * restoring of fragment's state.
 * This version of the pager is more useful when there are a large number of
 * pages, working more like a list view. When pages are not visible to the user,
 * their entire fragment may be destroyed, only keeping the saved state of that
 * fragment. This allows the pager to hold on to much less memory associated
 * with each visited page as compared to {@link androidx.fragment.app.FragmentPagerAdapter} at the
 * cost of potentially more overhead when switching between pages.
 * When using FragmentPagerAdapter the host ViewPager must have a valid ID set.
 * Subclasses only need to implement {@link #getItem(int)} and
 * {@link androidx.viewpager.widget.PagerAdapter#getCount()} to have a working adapter.
 * Here is an example implementation of a pager containing fragments of lists:
 * {
 * development/samples/Support13Demos/src/com/example/android/supportv13/app/
 * FragmentStatePagerSupport.java complete}
 * The <code>R.layout.fragment_pager</code> resource of the top-level fragment
 * is:
 * { development/samples/Support13Demos/res/layout/fragment_pager.xml
 * complete}
 * The <code>R.layout.fragment_pager_list</code> resource containing each
 * individual fragment's layout is:
 * {
 * development/samples/Support13Demos/res/layout/fragment_pager_list.xml
 * complete}
 */
public abstract class FragmentListPageAdapter extends BaseFragmentPageAdapter{

    protected final FragmentManager mFragmentManager;
    protected FragmentTransaction mCurTransaction = null;

//    protected SparseArray<Fragment.SavedState> mSavedState = new SparseArray<>();
    protected SparseArray<Fragment> mFragments = new SparseArray<>();
    protected Fragment mCurrentPrimaryItem = null;

    public FragmentListPageAdapter(FragmentManager fm){
        mFragmentManager = fm;
    }

    @Override
    public void startUpdate(ViewGroup container){
    }

    @Override
    public Fragment getExitFragment(int position){
        return mFragments.get(position);
    }

    @Override
    public Fragment getCurrentFragment(){
        return mCurrentPrimaryItem;
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position){
        Fragment f = mFragments.get(position);
        if(f != null){
            return f;
        }

        if(mCurTransaction == null){
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        Fragment fragment = getItem(position);
//        Fragment.SavedState fss = mSavedState.get(position);
//        if(fss != null){
//            fragment.setInitialSavedState(fss);
//        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.put(position,fragment);
        mCurTransaction.add(container.getId(),fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container,int position,Object object){
        Fragment fragment = (Fragment)object;

        if(mCurTransaction == null){
            mCurTransaction = mFragmentManager.beginTransaction();
        }

//        try{
//            mSavedState.put(position,fragment.isAdded() ? mFragmentManager.saveFragmentInstanceState(fragment) : null);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
        mFragments.remove(position);

        mCurTransaction.remove(fragment);
    }

    @Override
    public void setPrimaryItem(ViewGroup container,int position,Object object){
        Fragment fragment = (Fragment)object;
        if(fragment != mCurrentPrimaryItem){
            if(mCurrentPrimaryItem != null){
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if(fragment != null){
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container){
        if(mCurTransaction != null){
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view,Object object){
        return ((Fragment)object).getView() == view;
    }

//    @Override
//    public Parcelable saveState(){
//        Bundle state = null;
//        if(mSavedState.size() > 0){
//            state = new Bundle();
//            state.putSparseParcelableArray("states",mSavedState.clone());
//        }
//        int size = mFragments.size();
//        for(int i = 0;i < size;i++){
//            int index = mFragments.keyAt(i);
//            Fragment f = mFragments.valueAt(i);
//            if(f != null && f.isAdded()){
//                if(state == null){
//                    state = new Bundle();
//                }
//                String key = "f" + index;
//                mFragmentManager.putFragment(state,key,f);
//            }
//        }
//        return state;
//    }
//
//    @Override
//    public void restoreState(Parcelable state,ClassLoader loader){
//        if(state != null){
//            Bundle bundle = (Bundle)state;
//            bundle.setClassLoader(loader);
//            mSavedState.clear();
//            mFragments.clear();
//            if(bundle.containsKey("states")){
//                mSavedState = bundle.getSparseParcelableArray("states");
//            }
//            Iterable<String> keys = bundle.keySet();
//            for(String key: keys){
//                if(key.startsWith("f")){
//                    int index = Integer.parseInt(key.substring(1));
//                    Fragment f = mFragmentManager.getFragment(bundle,key);
//                    if(f != null){
//                        f.setMenuVisibility(false);
//                        mFragments.put(index,f);
//                    }
//                }
//            }
//        }
//    }
}
