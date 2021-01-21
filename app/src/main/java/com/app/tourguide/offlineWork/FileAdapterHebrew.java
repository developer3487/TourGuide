package com.app.tourguide.offlineWork;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tourguide.R;
import com.app.tourguide.callBack.ItemSelectedListener;
import com.app.tourguide.ui.downloadPreview.pojomodel.DataAvailLang;
import com.app.tourguide.ui.mapBox.SpotListAdapter;
import com.app.tourguide.utils.Constants;
import com.bumptech.glide.Glide;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileAdapterHebrew extends RecyclerView.Adapter<FileAdapterHebrew.ViewHolder> {

    @NonNull
    private final List<DownloadData> downloads = new ArrayList<>();
    @NonNull


    ArrayList<DataAvailLang> availLangs;
    Context context;
    ItemSelectedListener listener;


    public FileAdapterHebrew(ArrayList<DataAvailLang> availLangs, Context context) {
        this.availLangs = availLangs;
        this.context = context;
    }

    public void ItemSelectedListener(ItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvLangName.setText(availLangs.get(position).getLanguage());
        Glide.with(context).load(availLangs.get(position).getIcon()).into(holder.ivCountryFlag);
        holder.actionButton.setText(availLangs.get(position).getStatus());
        holder.progressBar.setProgress(availLangs.get(position).getProgress());


        String filePath = getSaveDir() + "/movies/" + getNameFromUrl(availLangs.get(position).getVideoUrl());
        File file = new File(filePath);


        if (file.exists()) {
            try {
                /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(context, Uri.fromFile(file));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                Log.d("DURATION=", String.valueOf(timeInMillisec));
                retriever.release();*/

               /* if (file.delete()) {
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "not deleted", Toast.LENGTH_SHORT).show();
                }
               */

                //set text to view
                //holder.actionButton.setText(context.getString(R.string.txt_view));
                //holder.progressBar.setProgress(100);
            } catch (Exception e) {
                file.delete();
                e.printStackTrace();
            }

        } else {
            //download a file
            holder.actionButton.setText(context.getString(R.string.txt_download));
        }


        holder.actionButton.setOnClickListener(view -> {
            if (holder.actionButton.getText().equals(context.getString(R.string.txt_view))) {
                Uri intentUri = Uri.fromFile(file);
                listener.selectedItem(position, Constants.VIEW_VIDEO, intentUri);
            }

            if (holder.actionButton.getText().equals(context.getString(R.string.txt_download))) {
                listener.selectedItem(position, "", Uri.parse(""));
            }

        });


    }


    private String getSaveDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/fetch";
    }


    String getNameFromUrl(String url) {
        return Uri.parse(url).getLastPathSegment();
    }

    public void addDownload(@NonNull final Download download) {
        boolean found = false;
        DownloadData data = null;
        int dataPosition = -1;
        for (int i = 0; i < downloads.size(); i++) {
            final DownloadData downloadData = downloads.get(i);
            if (downloadData.id == download.getId()) {
                data = downloadData;
                dataPosition = i;
                downloadData.pos = dataPosition;
                found = true;
                break;
            }
        }
        if (!found) {
            final DownloadData downloadData = new DownloadData();
            downloadData.id = download.getId();
            downloadData.download = download;
            downloads.add(downloadData);
            for (int i = 0; i < downloads.size(); i++) {
                DownloadData downloadDataN = downloads.get(i);
                downloadDataN.pos = i;
            }
            notifyItemInserted(downloads.size() - 1);
        } else {
            data.download = download;
            notifyItemChanged(dataPosition);
        }
    }

    @Override
    public int getItemCount() {
        return availLangs.size();
    }

    public void update(@NonNull final Download download, long eta, long downloadedBytesPerSecond) {
        for (int position = 0; position < downloads.size(); position++) {
            final DownloadData downloadData = downloads.get(position);
            downloadData.pos = position;
            if (downloadData.id == download.getId()) {
                switch (download.getStatus()) {
                    case REMOVED:
                    case DELETED: {
                        downloads.remove(position);
                        notifyItemRemoved(position);
                        break;
                    }
                    default: {
                        downloadData.download = download;
                        downloadData.eta = eta;
                        downloadData.downloadedBytesPerSecond = downloadedBytesPerSecond;
                        notifyItemChanged(position);
                    }
                }
                return;
            }
        }
    }

    private String getStatusString(Status status) {
        switch (status) {
            case COMPLETED:
                return "Done";
            case DOWNLOADING:
                return "Downloading";
            case FAILED:
                return "Error";
            case PAUSED:
                return "Paused";
            case QUEUED:
                return "Waiting in Queue";
            case REMOVED:
                return "Removed";
            case NONE:
                return "Not Queued";
            default:
                return "Unknown";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView titleTextView;
        final TextView statusTextView;
        final ProgressBar progressBar;
        TextView progressTextView, tvLangName;
        final Button actionButton;
        final TextView timeRemainingTextView;
        final TextView downloadedBytesPerSecondTextView;
        ImageView ivCountryFlag;

        ViewHolder(View itemView) {
            super(itemView);
            ivCountryFlag = itemView.findViewById(R.id.iv_country_icon);
            tvLangName = itemView.findViewById(R.id.tv_lang_name);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            statusTextView = itemView.findViewById(R.id.status_TextView);
            progressBar = itemView.findViewById(R.id.pbVideoBar);
            actionButton = itemView.findViewById(R.id.actionButton);
            progressTextView = itemView.findViewById(R.id.progress_TextView);
            timeRemainingTextView = itemView.findViewById(R.id.remaining_TextView);
            downloadedBytesPerSecondTextView = itemView.findViewById(R.id.downloadSpeedTextView);
        }

    }

    public static class DownloadData {
        public int id;
        @Nullable
        public Download download;
        public int pos;
        long eta = -1;
        long downloadedBytesPerSecond = 0;

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            if (download == null) {
                return "";
            }
            return download.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this || obj instanceof DownloadData && ((DownloadData) obj).id == id;
        }
    }

}
