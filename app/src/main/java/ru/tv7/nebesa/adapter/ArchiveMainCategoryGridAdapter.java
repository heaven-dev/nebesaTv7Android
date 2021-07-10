package ru.tv7.nebesa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Utils;

import static ru.tv7.nebesa.helpers.Constants.BACK_TEXT;
import static ru.tv7.nebesa.helpers.Constants.CATEGORY_IMAGE_SIZE_IN_PERCENT;
import static ru.tv7.nebesa.helpers.Constants.CATEGORY_NAME;
import static ru.tv7.nebesa.helpers.Constants.NAME;

/**
 * Grid adapter for archive main categories.
 */
public class ArchiveMainCategoryGridAdapter extends RecyclerView.Adapter<ArchiveMainCategoryGridAdapter.SimpleViewHolder> {

    private FragmentActivity activity = null;
    private Context context = null;
    private JSONArray elements = null;
    private double contentHeight = 0.0;

    public ArchiveMainCategoryGridAdapter(FragmentActivity activity, Context context, JSONArray jsonArray, double contentHeight) {
        this.activity = activity;
        this.context = context;
        this.elements = jsonArray;
        this.contentHeight = contentHeight;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout categoryContainer = null;
        public LinearLayout categoryItem = null;
        public LinearLayout backItem = null;
        public ImageView categoryImage = null;
        public TextView categoryText = null;
        public TextView backText = null;

        public SimpleViewHolder(View view) {
            super(view);

            categoryContainer = view.findViewById(R.id.categoryContainer);
            categoryItem = view.findViewById(R.id.categoryItem);
            backItem = view.findViewById(R.id.backItem);

            categoryImage = view.findViewById(R.id.categoryImage);
            categoryText = view.findViewById(R.id.categoryText);
            backText = view.findViewById(R.id.backText);

            // Calculate and set element width
            int elementWidth = Utils.dpToPx(calculateItemWidth());

            setElementWidth(this, elementWidth);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.archive_main_grid_category_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            JSONObject obj = elements.getJSONObject(position);
            if (obj != null) {
                String categoryText = Utils.getJsonStringValue(obj, NAME);
                if (categoryText == null) {
                    categoryText = Utils.getJsonStringValue(obj, CATEGORY_NAME);
                }

                boolean isBackElement = false;
                if (categoryText == null) {
                    categoryText = Utils.getJsonStringValue(obj, BACK_TEXT);
                    isBackElement = true;
                }


                if (!isBackElement) {
                    holder.categoryText.setText(categoryText);

                    holder.categoryItem.setVisibility(View.VISIBLE);
                    holder.backItem.setVisibility(View.GONE);

                    int elementWidth = Utils.dpToPx(calculateItemWidth());
                    setElementWidth(holder, elementWidth);

                    int imageWidth = Math.round(CATEGORY_IMAGE_SIZE_IN_PERCENT * elementWidth);
                    setImageWidth(holder, imageWidth);
                }
                else {
                    holder.backText.setText(categoryText);

                    holder.categoryItem.setVisibility(View.GONE);
                    holder.backItem.setVisibility(View.VISIBLE);

                    int backElementWidth = Utils.dpToPx(contentHeight) + 20;
                    setElementWidth(holder, backElementWidth);
                }
            }
        }
        catch (Exception e) {
            Utils.toErrorPage(activity);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.elements.length();
    }

    private static void setElementWidth(final SimpleViewHolder holder, int width) {
        if (holder.categoryContainer != null) {
            ViewGroup.LayoutParams params = holder.categoryContainer.getLayoutParams();
            params.width = width;
            holder.categoryContainer.setLayoutParams(params);
        }
    }

    private static void setImageWidth(final SimpleViewHolder holder, int width) {
        if (holder.categoryImage != null) {
            ViewGroup.LayoutParams params = holder.categoryImage.getLayoutParams();
            params.width = width;
            holder.categoryImage.setLayoutParams(params);
        }
    }

    private static double calculateItemWidth() {
        float width = Utils.getScreenWidthDp() - 82;
        return Math.floor(width / 3.2);
    }
}
