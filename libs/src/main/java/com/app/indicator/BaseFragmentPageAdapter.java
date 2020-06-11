package com.app.indicator;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

public abstract class BaseFragmentPageAdapter extends PagerAdapter{
    protected int mChildCount;

    public abstract Fragment getItem(int position);

    public abstract Fragment getExitFragment(int position);

    public abstract Fragment getCurrentFragment();


    @Override
    public int getItemPosition(Object object){
        if(mChildCount>0){
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged(){
        mChildCount=getCount();
        super.notifyDataSetChanged();
    }
}
