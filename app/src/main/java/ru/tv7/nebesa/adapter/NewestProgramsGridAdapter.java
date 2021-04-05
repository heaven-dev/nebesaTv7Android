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

import static ru.tv7.nebesa.helpers.Constants.BROADCAST_DATE_TIME;
import static ru.tv7.nebesa.helpers.Constants.DURATION;
import static ru.tv7.nebesa.helpers.Constants.EMPTY;
import static ru.tv7.nebesa.helpers.Constants.ID_NULL;
import static ru.tv7.nebesa.helpers.Constants.IMAGE_PATH;
import static ru.tv7.nebesa.helpers.Constants.NULL_VALUE;
import static ru.tv7.nebesa.helpers.Constants.SERIES_AND_NAME;

/**
 * Grid adapter for archive main programs.
 */
public class NewestProgramsGridAdapter extends RecyclerView.Adapter<NewestProgramsGridAdapter.SimpleViewHolder> {

    private FragmentActivity activity = null;
    private Context context = null;
    private JSONArray elements = null;

    public NewestProgramsGridAdapter(FragmentActivity activity, Context context, JSONArray jsonArray) {
        this.activity = activity;
        this.context = context;
        this.elements = jsonArray;
    }

    public JSONObject getElementByIndex(int index) throws Exception {
        if (index < 0 || index > elements.length() - 1) {
            throw new Exception("Invalid element index provided!");
        }
        return elements.getJSONObject(index);
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout programContainer = null;
        public ImageView programImage = null;
        public TextView programText = null;
        public TextView programDateTimeText = null;
        public TextView programDurationText = null;

        public SimpleViewHolder(View view) {
            super(view);

            programContainer = view.findViewById(R.id.newestProgramsContainer);
            programImage = view.findViewById(R.id.newestProgramsImage);
            programText = view.findViewById(R.id.newestProgramsText);
            programDateTimeText = view.findViewById(R.id.newestProgramsDateTime);
            programDurationText = view.findViewById(R.id.newestProgramsDuration);

            // Calculate and set item width
            int itemWidth = Utils.dpToPx(calculateItemWidth());

            if (programContainer != null) {
                ViewGroup.LayoutParams params = programContainer.getLayoutParams();
                params.width = itemWidth;
                programContainer.setLayoutParams(params);
            }
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.newest_programs_grid_program_element, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            JSONObject obj = elements.getJSONObject(position);
            if (obj != null) {
                String imagePath = Utils.getValue(obj, IMAGE_PATH);
                if (imagePath != null && !imagePath.equals(EMPTY) && !imagePath.equals(NULL_VALUE) && !imagePath.contains(ID_NULL)) {
                    Glide.with(context).asBitmap().load(imagePath).into(holder.programImage);
                }
                else {
                    Glide.with(context).asBitmap().load(R.drawable.fallback).into(holder.programImage);
                }

                String dateTime = Utils.getValue(obj, BROADCAST_DATE_TIME);
                if (dateTime != null) {
                    holder.programDateTimeText.setText(dateTime);
                }

                String duration = Utils.getValue(obj, DURATION);
                if (duration != null) {
                    holder.programDurationText.setText(duration);
                }

                String seriesAndName = Utils.getValue(obj, SERIES_AND_NAME);
                if (seriesAndName != null) {
                    holder.programText.setText(seriesAndName);
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

    private static double calculateItemWidth() {
        float width = Utils.getScreenWidthDp() - 82;
        return Math.floor(width / 3.2);
    }
}
