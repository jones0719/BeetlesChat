package tw.com.jones0719.beetleschat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BrowseActivity extends AppCompatActivity {
    private TextView tv1;
    private Intent intent;
    private Bundle bundle;
    private String file_path, file_name;
    private File f;
    private File myfile;
    private TextView tvFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        /*---------------------------------------------*/
        tv1 = (TextView) findViewById(R.id.tv1);
        tvFileName = (TextView) findViewById(R.id.tvFileName);
        readFile();    /* 讀取歷史資料。*/
    }

    /* 讀取歷史資料。106/11/12 Jones */
    private void readFile() {
        intent = getIntent();
        bundle = intent.getExtras();
        /* 傳入的參數包含了完整的路徑及檔名。106/11/14 Jones*/
        file_path = bundle.getString("FILE_PATH");
        file_name = bundle.getString("FILE_NAME");  /* 檔名 106/11/15 Jones */
        tvFileName.setText(file_name);
        f = getFilesDir();
        //myfile = new File(f.getAbsolutePath() + f.separator + file_path);
        myfile = new File(file_path);
        if (myfile.exists()) {
            try {
                FileReader fr = new FileReader(myfile);
                BufferedReader br = new BufferedReader(fr);
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str + "\n"); /*因為存檔時不會存入換行符號,所以讀出時需自行加入,讓換行正確。*/
                }
                //tv1.setText(files.toString());
                tv1.setText(Html.fromHtml(sb.toString())); /*改為Html格式。106/12/22 Jones */
                br.close();
                fr.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(BrowseActivity.this, "檔案尚未建立", Toast.LENGTH_SHORT).show();
        }
    }
}
