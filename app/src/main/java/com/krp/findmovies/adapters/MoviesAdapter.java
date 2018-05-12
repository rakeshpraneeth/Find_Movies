package com.krp.findmovies.adapters;

import com.krp.findmovies.viewModels.RowViewModel;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends BindableAdapter {

    private List<RowViewModel> entertainmentList;

    public MoviesAdapter(){
        this.entertainmentList = new ArrayList<>();
    }

    @Override
    protected Object getObjForPosition(int position) {
        return entertainmentList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return entertainmentList.get(position).getLayout();
    }

    @Override
    public int getItemCount() {
        return entertainmentList.size();
    }

    public void setList(List<RowViewModel> list){
        if(entertainmentList !=null){
            entertainmentList.clear();
        }
        entertainmentList.addAll(list);
        notifyDataSetChanged();
    }
}
