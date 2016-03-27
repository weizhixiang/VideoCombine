package com.wzx.wildworld.videocombine;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        ListView listView = (ListView) findViewById(R.id.fileListView);
        Intent intent=getIntent();
        final String fileName=intent.getStringExtra("fileName");
        File file=new File(fileName);
        File[] files=file.listFiles();
        final ArrayList<String> items=new ArrayList<String>();
        if(files!=null){
            int count=files.length;
            for (int i=0;i<count;i++){
                items.add(files[i].getName());
            }
        }
        ArrayAdapter<String>adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //弹出视频播放
                Intent intent=new Intent(Intent.ACTION_VIEW);
                String type="video/*";
                Uri uri= Uri.parse("file://"+fileName+"/" + items.get(position).toString());
//                System.out.println(fileName+"/" + items.get(position).toString());
                intent.setDataAndType(uri,type);
                startActivity(intent);
            }
        });
    }
}
