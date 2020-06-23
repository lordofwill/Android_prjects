package com.example.bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookmarkAdapter extends ArrayAdapter {
    private ArrayList<StationVO> bookmarks;
    Context context;
    int resource;

    public BookmarkAdapter(Context context, int resource, ArrayList<StationVO> bookmarks) {
        super(context, resource, bookmarks);
        this.context = context;
        this.resource = resource;
        this.bookmarks = bookmarks;
    }

    public void addBookmark(StationVO bookmark) {
        bookmarks.add(bookmark);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return bookmarks.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, null);
            StationViewHolder holder = new StationViewHolder(convertView);
            convertView.setTag(holder);
        }

        StationViewHolder holder = (StationViewHolder) convertView.getTag();
        TextView text1 = holder.text1;
        TextView text2 = holder.text2;
        final StationVO vo = bookmarks.get(position);

        text1.setText(vo.name);
        text2.setText(vo.id+"("+vo.next+" 방향)");


        return convertView;
    }
}
