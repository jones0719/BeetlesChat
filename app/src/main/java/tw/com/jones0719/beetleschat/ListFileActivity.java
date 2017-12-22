package tw.com.jones0719.beetleschat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ListFileActivity extends AppCompatActivity {
    private int x_index = 0;
    private String tgtFileName;
    private int tgtPoint;
    private File f;
    private String directoryName;
    private File directory;
    private ArrayList<File> files;
    private ArrayList<String> dspName;
    private File[] fList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listfile);

        /* 讀取存檔過的檔案名稱明細,顯示在ListView元件上供使用者選擇。*/
        getFileList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ListFileActivity.this, BrowseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("FILE_PATH", files.get(position).toString());
                bundle.putString("FILE_NAME", dspName.get(position).toString());
                intent.putExtras(bundle);
                startActivity(intent);
                //Toast.makeText(ListFileActivity.this, files.get(position) +" item no =" + position,Toast.LENGTH_SHORT).show();
            }
        });

        /* 長按ListView選單項目 */
        AdapterView.OnItemLongClickListener onItemLongSel = new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                x_index = position;
                //Toast.makeText(ListFileActivity.this, "長按LV選單", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ListFileActivity.this);
                builder.setTitle("刪除確認:");
                builder.setMessage("確認刪除[" + dspName.get(position).toString() + "]檔案嗎?");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    /* 按下[取消]不做任何事。*/
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    /* 按下[確認]按鈕,才真正執行刪除動作。*/
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file;
                        file = new File(directoryName + dspName.get(x_index).toString());

                        (file).delete();  /* 執行刪除動作 106/11/30 Jones */
                        if (file.exists()) {
                            Toast.makeText(getBaseContext(),
                                    "檔案[" + dspName.get(x_index).toString() + "]刪除失敗!",
                                    Toast.LENGTH_SHORT).show();
                        } else if (!file.exists()) {
                            Toast.makeText(getBaseContext(),
                                    "檔案[" + dspName.get(x_index).toString() + "]已刪除!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        /* 無論是否刪除成功,都重新顯示一次最新的檔案名稱在ListView元件上面,
                        讓使用者看到新的檔案明細狀況。106/11/30 15:40 Jones 於幼獅教室 */
                        getFileList();
                    }
                });
                builder.show();
                return true;
            }
        };
        listView.setOnItemLongClickListener(onItemLongSel);
    }

    /*-取得指定目錄下的檔案明細顯示在ListView元件上,短按開啟檔案內容在畫面上顯示;長按則刪除檔案。*/
    private void getFileList() {
        listView = (ListView) findViewById(R.id.listView);

        f = getFilesDir();
        directoryName = f.getAbsolutePath() + f.separator;
        directory = new File(directoryName);
        files = new ArrayList<>();
        dspName = new ArrayList<>();
        fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                tgtPoint = file.toString().indexOf(".txt");
                if (tgtPoint > 0) {
                    tgtFileName = file.toString().substring(tgtPoint - 14, tgtPoint + 4);
                    dspName.add(tgtFileName);
                }
                files.add(file);
            }
        }
        /* 把檔案明細丟入adapter,在ListView上做顯示。*/
        final ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                dspName);
        listView.setAdapter(adapter);
    }
}
