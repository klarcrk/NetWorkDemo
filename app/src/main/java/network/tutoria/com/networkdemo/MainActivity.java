package network.tutoria.com.networkdemo;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import network.tutoria.com.networkdemo.network.RequestBuilder;
import network.tutoria.com.networkdemo.network.api.NetworkResultHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 112);
        }
        final TextView textView = (TextView) findViewById(R.id.textView);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> headers = new HashMap<String, String>();
                Map<String, String> params = new HashMap<String, String>();
                RequestBuilder.get("http://gank.io/api/data/休息视频/1/1").addHeaders(headers).addParams(params).execute(new NetworkResultHandler<GanHuoResult>() {
                    @Override
                    public void onLoadSuccess(GanHuoResult result) {
                        super.onLoadSuccess(result);
                        textView.setText(result.toString());
                    }

                    @Override
                    public void onError(Throwable error) {
                        super.onError(error);
                        textView.setText(error.getMessage());
                    }
                }, new TypeToken<GanHuoResult>() {
                }.getType());
            }
        });
    }

    private void downloadFile(final TextView textView) {
        File storageDirectory = Environment.getExternalStorageDirectory();
        String url = "http://dl-cdn.coolapkmarket.com/down/apk_file/2017/0818/com.coolapk.market-7.9.7-1708181.apk?_upt=5459fcd61506766821";
        File targetFile = new File(storageDirectory, "kuku.apk");
        RequestBuilder.download(url, targetFile).execute(new NetworkResultHandler<String>() {
            @Override
            public void onDownloadSuccess(File downloadFile) {
                textView.setText("下载进度完成");
                showToast("下载进度完成");
            }

            @Override
            public void onGetDownloadProgress(int progress) {
                textView.setText(String.format("下载进度%d", progress));
            }

            @Override
            public void onError(Throwable error) {
                textView.setText(error.getMessage());
            }
        }, null);
    }

    private void uploadFile(final TextView textView, File file) {
        File uploadFile = file;
        String url = "http://xxx.upload.com";
        HashMap<String, File> uploadFilePart = new HashMap<>();
        uploadFilePart.put("fileKey", uploadFile);
        RequestBuilder.upload(url, uploadFilePart)
                //.uploadFileMediaType("application/octet-stream")//标识上传文件的类型
                .execute(new NetworkResultHandler<String>() {
                    @Override
                    public void onGetUploadProgress(int progress) {
                        textView.setText(String.format("上传进度%d", progress));
                    }

                    @Override
                    public void onLoadSuccess(String result) {
                        textView.setText("上传完成");
                    }

                    @Override
                    public void onError(Throwable error) {
                        textView.setText(error.getMessage());
                    }
                }, new TypeToken<String>() {
                }.getType());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 112) {
            for (int grantResult : grantResults) {
                if (grantResult != PermissionChecker.PERMISSION_GRANTED) {
                    showToast("no permission");
                    break;
                }
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
