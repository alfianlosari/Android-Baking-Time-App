package com.alfianlosari.baking.adapter;

import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alfianlosari.baking.R;
import com.alfianlosari.baking.provider.IngredientContract;
import com.alfianlosari.baking.provider.StepContract;

import org.w3c.dom.Text;

/**
 * Created by alfianlosari on 01/01/18.
 */

public final class RecipeDetailCursorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Cursor mIngredientCursor;
    private Cursor mStepsCursor;
    private RecipeDetailStepClickListener mStepClickListener;

    private static final int VIEW_TYPE_INGREDIENTS = 0;
    private static final int VIEW_TYPE_STEPS = 1;

    public RecipeDetailCursorAdapter(RecipeDetailStepClickListener listener) {
        this.mStepClickListener = listener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_INGREDIENTS:
                layoutId = R.layout.ingredient_card_list_item;
                break;
            case VIEW_TYPE_STEPS:
                layoutId = R.layout.step_card_list_item;
                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setFocusable(true);

        switch (viewType) {
            case VIEW_TYPE_INGREDIENTS:
                return new RecipeIngredientViewHolder(view);
            case VIEW_TYPE_STEPS:
                return new RecipeStepsViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_INGREDIENTS:
                RecipeIngredientViewHolder ingredientViewHolderViewHolder = (RecipeIngredientViewHolder) holder;
                String text = "";
                mIngredientCursor.moveToPosition(-1);
                while (mIngredientCursor.moveToNext()) {

                    text = text
                            + formatNumber(mIngredientCursor.getFloat(mIngredientCursor.getColumnIndex(IngredientContract.COLUMN_QUANTITY))) + " "
                            + mIngredientCursor.getString(mIngredientCursor.getColumnIndex(IngredientContract.COLUMN_MEASURE)) + " "
                            + mIngredientCursor.getString(mIngredientCursor.getColumnIndex(IngredientContract.COLUMN_INGREDIENT));
                    if (!mIngredientCursor.isLast()) {
                        text += "\n";
                    }

                }
                ingredientViewHolderViewHolder.mTextView.setText(text);
                return;

            case VIEW_TYPE_STEPS:
                RecipeStepsViewHolder stepViewHolder = (RecipeStepsViewHolder) holder;
                int cursorOffset = 0;
                if (mIngredientCursor != null && mIngredientCursor.getCount() > 0) {
                    cursorOffset = -1;
                }

                Resources res = holder.itemView.getContext().getResources();
                mStepsCursor.moveToPosition(position + cursorOffset);
                int order = mStepsCursor.getInt(mStepsCursor.getColumnIndex(StepContract.COLUMN_STEP_ORDER));
                String headerText = "";
                if (order > 0) {
                    headerText = res.getString(R.string.step_header, 0, order);
                } else {
                    headerText = res.getString(R.string.step_intro);
                }

                String shortDescription = mStepsCursor.getString(mStepsCursor.getColumnIndex(StepContract.COLUMN_SHORT_DESCRIPTION));
                stepViewHolder.mTextView.setText(shortDescription);
                stepViewHolder.mHeaderTextView.setText(headerText);
                stepViewHolder.itemView.setTag(mStepsCursor.getLong(mStepsCursor.getColumnIndex(StepContract.COLUMN_ID)));
                return;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + holder.getItemViewType());
        }


    }

    @Override
    public int getItemCount() {
        int ingredientsCount = 0;
        if (mIngredientCursor != null && mIngredientCursor.getCount() > 0) {
            ingredientsCount = 1;
        }

        int stepsCount = 0;
        if (mStepsCursor != null && mStepsCursor.getCount() > 0) {
            stepsCount = mStepsCursor.getCount();
        }

        return ingredientsCount + stepsCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mIngredientCursor != null && mIngredientCursor.getCount() > 0 && position == 0) {
            return VIEW_TYPE_INGREDIENTS;
        } else {
            return VIEW_TYPE_STEPS;
        }
    }

    public void swapStepsCursor(Cursor cursor) {
        if (mStepsCursor != null && cursor == null) {
            mStepsCursor.close();
        }
        mStepsCursor = cursor;
        notifyDataSetChanged();
    }

    public void swapIngredientsCursor(Cursor cursor) {
        if (mIngredientCursor != null && cursor == null) {
            mIngredientCursor.close();
        }
        mIngredientCursor = cursor;
        notifyDataSetChanged();
    }

    public interface RecipeDetailStepClickListener {
        void onClickRecipe(long id);
    }

    public String formatNumber(float d) {
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return String.format("%.1f", d);
        }
    }

    public class RecipeIngredientViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public RecipeIngredientViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textview_ingredient);
        }

    }

    public class RecipeStepsViewHolder extends RecyclerView.ViewHolder {

        public TextView mHeaderTextView;
        public TextView mTextView;

        public RecipeStepsViewHolder(final View itemView) {
            super(itemView);
            mHeaderTextView = itemView.findViewById(R.id.textview_header);
            mTextView = itemView.findViewById(R.id.textview_step);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mStepClickListener.onClickRecipe((long) itemView.getTag());
                }
            });
        }
    }

}
