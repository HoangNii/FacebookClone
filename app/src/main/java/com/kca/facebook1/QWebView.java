package com.kca.facebook1;


import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;
import java.util.Objects;

public class QWebView extends WebView {

    private final String TAG =  QWebView.class.getSimpleName();
    private final int SAVE_IMAGE = 0;
    private final int SHARE_IMAGE = 1;
    private final int COPY_IMAGE = 2;
    private final int SHARE_LINK = 3;
    private final int COPY_LINK = 4;

    private AppCompatActivity activity;

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public QWebView(Context context) {
        super(context);
    }

    public QWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
        final HitTestResult result = getHitTestResult();
        MenuItem.OnMenuItemClickListener onClick = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if(i==SAVE_IMAGE){
                    saveImage(result);
                }else if(i==SHARE_IMAGE){
                    shareImage(result);
                }else if(i==COPY_IMAGE){
                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(
                                ClipData.newUri(activity.getContentResolver(),"URI", Uri.parse(result.getExtra()))
                        );
                    }
                    Toast.makeText(activity, R.string.wv_menu_copy_link_to_clip, Toast.LENGTH_SHORT).show();

                }else if(i==COPY_LINK){
                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(
                                ClipData.newUri(activity.getContentResolver(),"URI", Uri.parse(result.getExtra()))
                        );
                    }
                    Toast.makeText(activity, R.string.wv_menu_copy_link_to_clip, Toast.LENGTH_SHORT).show();

                }else if(i==SHARE_LINK){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
                    activity.startActivity(Intent.createChooser(intent,
                            activity.getString(R.string.wv_menu_share_link)));

                }
                return true;
            }
        };

        if(result.getType()== HitTestResult.IMAGE_TYPE||result.getType()== HitTestResult.SRC_IMAGE_ANCHOR_TYPE){
            menu.add(0,SAVE_IMAGE,0, R.string.wv_menu_save_image).setOnMenuItemClickListener(onClick);
            menu.add(0,SHARE_IMAGE,0, R.string.wv_menu_share_image).setOnMenuItemClickListener(onClick);
            menu.add(0,COPY_IMAGE,0, R.string.wv_menu_copy_image_link).setOnMenuItemClickListener(onClick);
        }
        if(result.getType()== HitTestResult.SRC_ANCHOR_TYPE||result.getType()== HitTestResult.SRC_IMAGE_ANCHOR_TYPE){
            menu.add(0,SHARE_LINK,0, R.string.wv_menu_share_link).setOnMenuItemClickListener(onClick);
            menu.add(0,COPY_LINK,0, R.string.wv_menu_copy_link).setOnMenuItemClickListener(onClick);
        }
    }

    private void saveImage(HitTestResult result){
        if(PermissionRequest.checkStore(activity,12)){
            Uri uri = Uri.parse(result.getExtra());
            DownloadManager.Request request = new DownloadManager.Request(uri);
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if(!dir.exists()){
                if(!dir.mkdirs()){
                    return;
                }
            }
            File destinationFile = new File(dir, Objects.requireNonNull(uri.getLastPathSegment()));
            request.setDestinationUri(Uri.fromFile(destinationFile));
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            ((DownloadManager) Objects.requireNonNull(activity.getSystemService(Context.DOWNLOAD_SERVICE))).enqueue(request);
        }

    }

    private void shareImage(HitTestResult result){
        if(PermissionRequest.checkStore(activity,12)){
            Uri uri = Uri.parse(result.getExtra());
            Glide.with(activity)
                    .asBitmap()
                    .load(uri)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            String savedImageURL
                                    = MediaStore.Images.Media.insertImage(
                                    activity.getContentResolver(),resource,getTitle(),"Image of" +getTitle());
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(savedImageURL));
                            activity.startActivity(intent);
                        }
                    });
            Toast.makeText( activity, R.string.wv_menu_share_img,Toast.LENGTH_SHORT).show();
        }

    }


}
