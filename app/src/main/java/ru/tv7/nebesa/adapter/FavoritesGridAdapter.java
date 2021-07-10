package ru.tv7.nebesa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Utils;

import static ru.tv7.nebesa.helpers.Constants.CAPTION;
import static ru.tv7.nebesa.helpers.Constants.EMPTY;
import static ru.tv7.nebesa.helpers.Constants.ID_NULL;
import static ru.tv7.nebesa.helpers.Constants.IMAGE_PATH;
import static ru.tv7.nebesa.helpers.Constants.IS_SERIES;
import static ru.tv7.nebesa.helpers.Constants.NAME;
import static ru.tv7.nebesa.helpers.Constants.NULL_VALUE;
import static ru.tv7.nebesa.helpers.Constants.ONE_STR;
import static ru.tv7.nebesa.helpers.Constants.SERIES_AND_NAME;

/**
 * Grid adapter for favorite items.
 */
public class FavoritesGridAdapter extends RecyclerView.Adapter<FavoritesGridAdapter.SimpleViewHolder> {

    private FragmentActivity activity = null;
    private Context context = null;
    private JSONArray elements = null;

    public FavoritesGridAdapter(FragmentActivity activity, Context context, JSONArray jsonArray) {
        this.activity = activity;
        this.context = context;
        this.elements = jsonArray;
    }

    public JSONObject getElementByIndex(int index) throws Exception {
        if (elements != null && elements.length() > index) {
            return elements.getJSONObject(index);
        }
        return null;
    }

    public JSONArray getElements() {
        return elements;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout favoriteContainer = null;
        public ImageView favoriteImage = null;
        public ImageView itemIcon = null;
        public TextView seriesAndName = null;
        public TextView caption = null;

        public SimpleViewHolder(View view) {
            super(view);

            favoriteContainer = view.findViewById(R.id.favoriteContainer);
            favoriteImage = view.findViewById(R.id.favoriteImage);
            itemIcon = view.findViewById(R.id.itemIcon);
            seriesAndName = view.findViewById(R.id.seriesAndName);
            caption = view.findViewById(R.id.caption);

            // Calculate and set item height
            int itemHeight = Utils.dpToPx(calculateItemHeight());

            if (favoriteContainer != null) {
                ViewGroup.LayoutParams params = favoriteContainer.getLayoutParams();
                params.height = itemHeight;
                favoriteContainer.setLayoutParams(params);
            }

            // Calculate and set image width
            int imageWidth = Utils.dpToPx(calculateImageWidth());

            if (favoriteImage != null) {
                ViewGroup.LayoutParams params = favoriteImage.getLayoutParams();
                params.width = imageWidth;
                favoriteImage.setLayoutParams(params);
            }
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.favorite_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            JSONObject obj = elements.getJSONObject(position);
            if (obj != null) {

                String value = Utils.getJsonStringValue(obj, IMAGE_PATH);
                if (value != null && !value.equals(EMPTY) && !value.equals(NULL_VALUE) && !value.contains(ID_NULL)) {
                    Glide.with(context).asBitmap().load(value).into(holder.favoriteImage);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.fallback).into(holder.favoriteImage);
                }

                int drawableId = 0;

                value = Utils.getJsonStringValue(obj, IS_SERIES);
                if (value != null && value.equals(ONE_STR)) {
                    // series
                    drawableId = R.drawable.series;
                    value = Utils.getJsonStringValue(obj, NAME);
                }
                else {
                    // program
                    drawableId = R.drawable.program;
                    value = Utils.getJsonStringValue(obj, SERIES_AND_NAME);
                }

                holder.itemIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemIcon.getResources(), drawableId, null));

                if (value != null) {
                    holder.seriesAndName.setText(value);
                }

                value = Utils.getJsonStringValue(obj, CAPTION);
                if (value != null && value.length() > 0) {
                    holder.caption.setText(value);
                }
                else {
                    holder.caption.setText(EMPTY);
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

    private static double calculateItemHeight() {
        float width = Utils.getScreenHeightDp() - 124;
        return Math.floor(width / 3.5);
    }

    private static double calculateImageWidth() {
        double height = calculateItemHeight();
        return Math.round(height / 0.56);
    }
}
