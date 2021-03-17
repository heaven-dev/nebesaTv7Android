package ru.tv7.nebesa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.Utils;

import static ru.tv7.nebesa.helpers.Constants.CAPTION;
import static ru.tv7.nebesa.helpers.Constants.EMPTY;
import static ru.tv7.nebesa.helpers.Constants.IMAGE_PATH;
import static ru.tv7.nebesa.helpers.Constants.NULL_VALUE;
import static ru.tv7.nebesa.helpers.Constants.SERIES;
import static ru.tv7.nebesa.helpers.Constants.SERIES_AND_NAME;
import static ru.tv7.nebesa.helpers.Constants.TYPE;

/**
 * Grid adapter for search result items.
 */
public class SearchResultGridAdapter extends RecyclerView.Adapter<SearchResultGridAdapter.SimpleViewHolder> {

    private FragmentActivity activity = null;
    private Context context = null;
    private JSONArray elements = null;

    public SearchResultGridAdapter(FragmentActivity activity, Context context, JSONArray jsonArray) {
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
        public RelativeLayout searchResultContainer = null;
        public ImageView searchResultImage = null;
        public TextView seriesAndName = null;
        public TextView caption = null;
        public ImageView seriesOrProgram = null;

        public SimpleViewHolder(View view) {
            super(view);

            searchResultContainer = view.findViewById(R.id.searchResultContainer);
            searchResultImage = view.findViewById(R.id.searchResultImage);
            seriesAndName = view.findViewById(R.id.seriesAndName);
            caption = view.findViewById(R.id.caption);
            seriesOrProgram = view.findViewById(R.id.seriesOrProgram);

            // Calculate and set item height
            int itemHeight = Utils.dpToPx(calculateItemHeight());

            if (searchResultContainer != null) {
                ViewGroup.LayoutParams params = searchResultContainer.getLayoutParams();
                params.height = itemHeight;
                searchResultContainer.setLayoutParams(params);
            }

            // Calculate and set image width
            int imageWidth = Utils.dpToPx(calculateImageWidth());

            if (searchResultImage != null) {
                ViewGroup.LayoutParams params = searchResultImage.getLayoutParams();
                params.width = imageWidth;
                searchResultImage.setLayoutParams(params);
            }
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.search_result_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            JSONObject obj = elements.getJSONObject(position);
            if (obj != null) {

                String value = Utils.getValue(obj, IMAGE_PATH);
                if (value != null && !value.equals(EMPTY) && !value.equals(NULL_VALUE)) {
                    Glide.with(context).asBitmap().load(value).into(holder.searchResultImage);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.fallback).into(holder.searchResultImage);
                }

                int imageSrc = 0;
                value = Utils.getValue(obj, TYPE);
                if (value != null) {
                    imageSrc = value.equals(SERIES) ? R.drawable.series : R.drawable.program;
                }

                holder.seriesOrProgram.setImageResource(imageSrc);

                value = Utils.getValue(obj, SERIES_AND_NAME);
                if (value != null) {
                    holder.seriesAndName.setText(value);
                }

                value = Utils.getValue(obj, CAPTION);
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
