package com.codexter.botguise;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class PackAdaptor extends RecyclerView.Adapter<PackAdaptor.MyViewHolder> {

    private Context mContext;
    private List<Pack> packList;

    public PackAdaptor(Context mContext, List<Pack> packList) {
        this.mContext = mContext;
        this.packList = packList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pack_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Pack pack = packList.get(position);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Raleway-Regular.ttf");
        holder.title.setText(pack.getName());
        holder.title.setTypeface(typeface);

        // loading pack cover using Glide library
        Glide.with(mContext).load(pack.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChooseMode.class);
                switch (pack.getThumbnail()) {
                    case R.drawable.pack01:
                        intent.putExtra("PACK", mContext.getString(R.string.pack01));
                        mContext.startActivity(intent);
                        break;
                    case R.drawable.pack02:
                        intent.putExtra("PACK", mContext.getString(R.string.pack02));
                        mContext.startActivity(intent);
                        break;
                    case R.drawable.pack03:
                        intent.putExtra("PACK", mContext.getString(R.string.pack03));
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_pack, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    @Override
    public int getItemCount() {
        return packList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.download_pack:
                    //TODO Download pack
                    Toast.makeText(mContext, "Pack is downloading.", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }
}