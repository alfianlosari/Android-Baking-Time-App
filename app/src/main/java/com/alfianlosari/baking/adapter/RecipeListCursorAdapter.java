package com.alfianlosari.baking.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfianlosari.baking.ui.RecipeListActivity;
import com.alfianlosari.baking.R;
import com.squareup.picasso.Picasso;

/**
 * Created by alfianlosari on 29/12/17.
 */

public final class RecipeListCursorAdapter extends RecyclerView.Adapter<RecipeListCursorAdapter.RecipeViewHolder> {

    private Cursor mCursor;
    private RecipeClickItemListener clickListener;

    public RecipeListCursorAdapter(RecipeClickItemListener listener) {
        this.clickListener = listener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.recipe_card_list_item, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String name = mCursor.getString(RecipeListActivity.INDEX_RECIPE_NAME);
        holder.mTextView.setText(name);
        holder.itemView.setTag(mCursor.getLong(RecipeListActivity.INDEX_RECIPE_ID));
        String image = mCursor.getString(RecipeListActivity.INDEX_RECIPE_IMAGE);
        if (!image.isEmpty()) {
            Picasso.with(holder.itemView.getContext()).load(mCursor.getString(RecipeListActivity.INDEX_RECIPE_IMAGE)).into(holder.mImageView);

        }
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null && cursor == null) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public interface RecipeClickItemListener {
        public void onClick(long id);
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public ImageView mImageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textview_recipe);
            mImageView = itemView.findViewById(R.id.image_recipe);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long id = (long) view.getTag();
                    clickListener.onClick(id);
                }
            });
        }
    }


}
