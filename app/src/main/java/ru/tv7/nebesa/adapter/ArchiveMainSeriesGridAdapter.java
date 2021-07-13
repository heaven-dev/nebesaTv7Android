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

import java.util.List;

import ru.tv7.nebesa.R;
import ru.tv7.nebesa.helpers.GuideItem;
import ru.tv7.nebesa.helpers.Utils;

import static ru.tv7.nebesa.helpers.Constants.EMPTY;
import static ru.tv7.nebesa.helpers.Constants.ID_NULL;
import static ru.tv7.nebesa.helpers.Constants.NULL_VALUE;

/**
 * Grid adapter for archive main series.
 */
public class ArchiveMainSeriesGridAdapter extends RecyclerView.Adapter<ArchiveMainSeriesGridAdapter.SimpleViewHolder> {

    private FragmentActivity activity = null;
    private Context context = null;
    private List<GuideItem> elements = null;

    public ArchiveMainSeriesGridAdapter(FragmentActivity activity, Context context, List<GuideItem> series) {
        this.activity = activity;
        this.context = context;
        this.elements = series;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout seriesContainer = null;
        public ImageView seriesImage = null;
        public TextView seriesText = null;
        public TextView seriesDateText = null;
        public TextView seriesDurationText = null;

        public SimpleViewHolder(View view) {
            super(view);

            seriesContainer = view.findViewById(R.id.mainArchiveSeriesContainer);
            seriesImage = view.findViewById(R.id.mainArchiveSeriesImage);
            seriesText = view.findViewById(R.id.mainArchiveSeriesText);
            seriesDateText = view.findViewById(R.id.mainArchiveSeriesDate);
            seriesDurationText = view.findViewById(R.id.mainArchiveSeriesDuration);

            // Calculate and set item width
            int itemWidth = Utils.dpToPx(calculateItemWidth());

            if (seriesContainer != null) {
                ViewGroup.LayoutParams params = seriesContainer.getLayoutParams();
                params.width = itemWidth;
                seriesContainer.setLayoutParams(params);
            }
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.archive_main_grid_series_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            GuideItem guideItem = elements.get(position);
            if (guideItem != null) {
                String imagePath = guideItem.getImagePath();
                if (imagePath != null && !imagePath.equals(EMPTY) && !imagePath.equals(NULL_VALUE) && !imagePath.contains(ID_NULL)) {
                    Glide.with(context).asBitmap().load(imagePath).into(holder.seriesImage);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.fallback).into(holder.seriesImage);
                }

                String startDate = guideItem.getStartDate();
                if (startDate != null) {
                    holder.seriesDateText.setText(startDate);
                }

                String duration = guideItem.getDuration();
                if (duration != null) {
                    holder.seriesDurationText.setText(duration);
                }

                String seriesAndName = guideItem.getSeriesAndName();
                if (seriesAndName != null) {
                    holder.seriesText.setText(seriesAndName);
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
        return this.elements.size();
    }

    private static double calculateItemWidth() {
        float width = Utils.getScreenWidthDp() - 82;
        return Math.floor(width / 3.2);
    }
}
