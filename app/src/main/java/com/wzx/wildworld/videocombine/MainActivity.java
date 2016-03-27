package com.wzx.wildworld.videocombine;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.mp4parser.Container;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.AppendTrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    private String sdcardString;
    private int videoCombineIs=1;//合并是否成功
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sdcardString = Environment.getExternalStorageDirectory().getPath();
        File path = new File(sdcardString + "/Android/data/com.tencent.qqlive/files/videos/");
        File qqMovie = new File(sdcardString + "/QQMovie");

        if (!qqMovie.exists()) {
            try {
                qqMovie.mkdirs();
            } catch (Exception e) {

            }
        }
        File[] files = path.listFiles();
        System.out.println("ok");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.tishi), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        final VideoFileComputer fileComputer = new VideoFileComputer();

        if(!path.exists()){
            System.out.println("ok!");
        }else{
        for (File file : files) {
            if (file.isDirectory()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemTitle", file.getName());
                map.put("path", file.getPath());
                Date date = new Date(file.lastModified());
                map.put("date", fileComputer.formateDate(date));

                try {
                    map.put("fileLen", fileComputer.formatFileSize(fileComputer.getFilesSize(file)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listItem.add(map);
            }
        }}

        SimpleAdapter listItemAdaper = new SimpleAdapter(this, listItem, R.layout.item_list_main, new String[]{"ItemTitle", "path", "date", "fileLen"}, new int[]{R.id.fileTextView, R.id.pathTextView, R.id.fileDateTextView, R.id.fileLenTextView});
        listView.setAdapter(listItemAdaper);
//        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add(0,0,0,"合并");
//                menu.add(0,0,0,"删除");
//                menu.add(0,0,0,"取消");
//            }
//
//        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int itemNumber = i;
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("如何处理？");
                builder.setItems(getResources().getStringArray(R.array.itemArray), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent=new Intent(MainActivity.this,FileListActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("fileName",listItem.get(itemNumber).get("path").toString());
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }else if (which == 1) {
                            doProgressUpdate(listItem.get(itemNumber).get("path").toString());//显示合并进度，开始合并Thread
//                            fileComputer.copyFile(sdcardString + "/QQMovie/sd.mp4",sdcardString + "/QQMovie/sd1.mp4",sdcardString + "/QQMovie/sd2.mp4");
                        } else if (which == 2) {
                            //删除缓存
                            if (new FileDelete().deleteFolder(listItem.get(itemNumber).get("path").toString())) {
                                listItem.remove(itemNumber);
                                SimpleAdapter listItemAdaper = (SimpleAdapter) listView.getAdapter();
                                listItemAdaper.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
//                builder.setMessage("是否合并？？？");
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        doProgressUpdate(listItem.get(itemNumber).get("path").toString());
//                    }
//                });
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
                builder.setCancelable(true);
                builder.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("关于")
                    .setMessage("反馈博客：\nblog.sina.com/weizhixiang")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            return true;
        }
        if(id==R.id.goToQQMovie){
            Intent intent=new Intent(MainActivity.this,FileListActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("fileName",sdcardString + "/QQMovie");
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void doProgressUpdate(String string) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("正在处理。。。。");
        progressDialog.setMessage("正在检查文件");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
        File path = new File(string);
        VideoFileComputer fileComputer = new VideoFileComputer();
        final String[] videoUris = fileComputer.getFileName(path);
//        System.out.println(Arrays.toString(filePathVideo));
        final File[] files = path.listFiles();
//        String[] videoUris = new String[files.length];
//        int i = 0;
//        for (File file : files) {
//            if (!file.isDirectory()) {
//                videoUris[i] = file.getPath();
//                i++;
//            }
//        }
        final Runnable afterTreaad = new Runnable() {
            @Override
            public void run() {

            }
        };
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 20) {
                    progressDialog.setMessage("正在读取文件。。。");
                }
                if (msg.what == 60) {
                    progressDialog.setMessage("正在合并文件。。。");
                }
                if (msg.what == 80) {
                    progressDialog.setMessage("正在写入文件。。。");
                }
                if (msg.what == 99) {
                    progressDialog.setMessage("合并成功");
                }
                if (msg.what == 98) {
                    progressDialog.setMessage("合并失败");
                    progressDialog.setCancelable(true);
                }
                if (msg.what >= 100) {
                    afterTreaad.run();
                    progressDialog.cancel();
                    progressDialog.dismiss();
                    if(videoCombineIs<0){
                        Toast.makeText(MainActivity.this, "合并失败，错误代码" + videoCombineIs, Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(MainActivity.this, "合并成功" , Toast.LENGTH_LONG).show();
                    }
                }
                progressDialog.setProgress(msg.what);

                super.handleMessage(msg);
            }
        };
        Runnable doTread = new Runnable() {
            @Override
            public void run() {

                List<Movie> inMovies = new ArrayList<Movie>();
                for (String file : videoUris) {
                    try {
                        inMovies.add(MovieCreator.build(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                        videoCombineIs=-1;
                    }
                }
                handler.sendEmptyMessage(20);
                List<Track> videoTracks = new LinkedList<Track>();
                List<Track> audioTracks = new LinkedList<Track>();

                for (Movie m : inMovies) {
                    for (Track t : m.getTracks()) {
                        if (t.getHandler().equals("soun")) {
                            audioTracks.add(t);
                        }
                        if (t.getHandler().equals("vide")) {
                            videoTracks.add(t);
                        }
                    }
                }
                Movie result = new Movie();
                handler.sendEmptyMessage(40);
                if (audioTracks.size() > 0) {
                    try {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    } catch (IOException e) {
                        e.printStackTrace();
                        videoCombineIs=-2;
                    }
                }
                handler.sendEmptyMessage(60);
                if (videoTracks.size() > 0) {
                    try {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    } catch (IOException e) {
                        e.printStackTrace();
                        videoCombineIs=-3;
                    }
                }
                Container out = new DefaultMp4Builder().build(result);
                handler.sendEmptyMessage(80);
                FileChannel fc;
                try {
                    fc = new RandomAccessFile(String.format(sdcardString + "/QQMovie/" + files[0].getParentFile().getName() + ".mp4"), "rw").getChannel();
                    out.writeContainer(fc);
                    fc.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(98);
                    videoCombineIs=-4;
                } catch (IOException e) {
                    e.printStackTrace();
                    videoCombineIs=-5;
                }
//                handler.sendEmptyMessage(99);
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                handler.sendEmptyMessage(100);

            }
        };
        new Thread(doTread).start();

    }
}
