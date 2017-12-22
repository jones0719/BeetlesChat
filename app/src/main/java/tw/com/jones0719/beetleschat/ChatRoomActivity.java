package tw.com.jones0719.beetleschat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class ChatRoomActivity extends AppCompatActivity {
    public static final String TAG = "Debug";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference("message");
    DatabaseReference myRef = null;
    private TextView tv1;
    private TextView tvUserName, tvChannel;
    private EditText et1;
    private CharSequence timeDate;
    private ScrollView scrollView;
    private String User_Name, User_passWord, User_level, User_Channel, value, saveFileName;
    private Calendar cal;
    private CharSequence timeDateToSave;
    private String str_channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        /*---------------------------------------------------------------*/

        tv1 = (TextView) findViewById(R.id.tv1);
        tv1.setMovementMethod(ScrollingMovementMethod.getInstance());
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        et1 = (EditText) findViewById(R.id.et1);
        cal = Calendar.getInstance();  /* 取得系統時間 */
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvChannel = (TextView) findViewById(R.id.tvChannel);
        timeDate = DateFormat.format("yyyy-MM-dd kk:mm:ss", cal.getTime());
        getIntentPara(); /* 取得由登入主頁SigninActivity.傳入的Intent參數。*/
        /* 判斷是否傳入選擇的頻道。是,則使用傳入的頻道;否,則使用預設的頻道。106/11/26 Jones */
        if (!User_Channel.equals("")) {
            myRef = database.getReference(User_Channel);
        } else {
            myRef = database.getReference("message");
        }
        /* 此行在控制一進到聊天室主頁面時,在EditText得到焦點時不自動帶出虛擬鍵盤。 106/11/15 Jones */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = dataSnapshot.getValue(String.class);
                if (!value.equals("")) {
        /*原來可以用Html.formHtml()方式把TextView內容改為HTML格式,呈現HTML效果,就可以有顏色效果。106/12/21*/
                    tv1.setText(Html.fromHtml(value));
                } else {
                    myRef.setValue(Html.fromHtml(timeDate.toString()));
                }
                /* 當資料內容改變時,讓ScrollView物件自動把內容上推,顯示出最新的發言內容。106/11/13 Jones */
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /*取得由Bundle傳入的匿名及密碼。*/
    void getIntentPara() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        User_level = bundle.getString("USER_LEVEL");
        User_Name = bundle.getString("USER_NAME");
        User_passWord = bundle.getString("USER_PWD");
        User_Channel = bundle.getString("USER_CHANNEL");
        StringBuilder sb_Msg = new StringBuilder();

        if ((!User_Name.equals("")) && (!User_passWord.equals(""))) {
            tvUserName.setText(User_Name);
            str_channel = "";
            switch (User_Channel) {
                case "":
                    str_channel = "主頻道";
                case "message":
                    str_channel = "主頻道";
                    break;
                case "message1":
                    str_channel = "頻道A";
                    break;
                case "message2":
                    str_channel = "頻道B";
                    break;
                case "message3":
                    str_channel = "頻道C";
                    break;
            }
            tvChannel.setText(str_channel);
            if (User_level.equals("1"))
                sb_Msg.append("管理者帳號及密碼傳入成功。");
            else
                sb_Msg.append("匿名使用者及密碼傳入成功。");
        } else {
            sb_Msg.append("匿名及密碼傳入空白。");
        }
        //Toast.makeText(ChatRoomActivity.this, sb_Msg, Toast.LENGTH_SHORT).show();
    }

    /* 在Activity出現在畫面之前，會自動呼叫Activity的onCreateOptionsMenu方法，
       方法的參數帶來一個menu物件。*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* 呼叫Activity的getMenuInflater方法得到MenuInflater物件,它是一個專門用來產生選單的類別,
           呼叫inflate方法可讀取一個Menu設計圖(XML檔),依照設計圖產生menu物件。
           因此,在專案中的/res/menu/menu_item.xml就是MainActivity的選單設計檔。
           用外部menu.xml資源檔建立item項目方式,必需用此行取得外部menu.xls定義檔。*/
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.item_back);
        /* 如果傳入的Leve=1,則代表用者為Super User,Menu的備份項目設定為可視狀態。*/
        if (User_level.equals("1")) {
            item.setVisible(true);
        }
        /* 用內部由程式建立Item項目。*/
        /*
        menu.add(0,10,0,"設定");
        */
        return super.onCreateOptionsMenu(menu);
    }

    /*當按下選單中的任一個項目時,會自動呼叫Activity的onOptionsItemSelected方法,
      並傳來被按下的項目MenuItem物件。 */
    @Override
    /*參數中傳來一個被按下的選項item。*/
    public boolean onOptionsItemSelected(MenuItem item) {
        /* 透過這個item的getItemId()方法可以取得被選項目的代號,為int型態,
           無論內部或外部定義的Menu,都用此方法取得選項,只是在判斷時有差別。
           內部用設定給選項的代號,一般為數字,如上方的10\20\30\40等,
           而外部則要判斷在Menu設定檔中的item id。*/
        int id = item.getItemId();
        StringBuilder str_msg = new StringBuilder();
        /* 用外部menu.xls檔判斷按下選項的方法式。*/
        switch (id) {
            case R.id.item_back:
                str_msg.append("[備份]");
                toBackup();   /* 備份功能 */
                break;
            case R.id.item_save:
                str_msg.append("存檔完成。");
                clickSave();  /* 存檔功能*/
                break;
            case R.id.item_brow:
                str_msg.append("[讀取]");
                clickRead();   /* 讀取功能 */
                break;
            default:
                str_msg.append("讀我說明!");
                new AlertDialog.Builder(this)
                        .setTitle("使用說明")
                        .setMessage("[儲存對話記錄]:\n把目前頻道內容存在本機文字檔中。\n檔案命名規則:\nyyyymmddhhMMss.txt 年月日時分秒.txt。\n\n" +
                                "[讀取歷史記錄]:\n可以選擇讀取或刪除歷史檔案。\n在檔案明細上點選檔名可以開啟瀏覽內容;長按則可以刪除檔案。\n"
                        )
                        .setPositiveButton("OK", null)
                        .show();
        }

        /*顯示訊息,用來驗證按下的項目執行邏輯是否正確。*/
        //Toast.makeText(this, str_msg, Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        if (!et1.getText().toString().equals("")) {
            cal = Calendar.getInstance();                     /* 取得系統時間 */
            timeDate = DateFormat.format("MM-dd kk:mm", cal.getTime());
            myRef.setValue(value + "[<font size='2' color='#0000C6'>" +
                    User_Name + "</font>]:&nbsp;" + et1.getText().toString() +
                    "&nbsp<font size='2' color='#E0E0E0'>" + timeDate + "</font><br>");
            tv1.setMovementMethod(ScrollingMovementMethod.getInstance());
            et1.setText("");
        } else {
            Toast.makeText(this, "請填入訊息後再送出。", Toast.LENGTH_SHORT).show();
        }

        /*每按下[送出發言]按鈕時自動讓ScrollView物件自動把內容上推,顯示出最新的發言內容。106/11/07 Jones */
        /* 在onDataChange中寫入本方法就會在資料有改變時自動往上推,此處就可以不必再推一次。 106/11/13 Jones */
/*        scrollView.post(new Runnable() {
            @Override
            public void run() {
               scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }); */
        hideKeyboard(); /* 在按下[發言]鈕之後自動收掉虛擬鍵盤。*/
    }

    /* 隱藏虛擬鍵盤,寫成單獨方法是為了方便在需要的地方呼叫使用。106/12/07 Jones */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /* 把畫面上的文字存檔到beetlechat.txt檔案中。106/11/08 Jones */
    public void clickSave() {
        File f = getFilesDir();
        cal = Calendar.getInstance();
        timeDateToSave = DateFormat.format("yyyyMMddkkmmss", cal.getTime());
        saveFileName = timeDateToSave + ".txt";
        //File myfile = new File(f.getAbsolutePath() + f.separator + "beetlechat.txt");
        File myfile = new File(f.getAbsolutePath() + f.separator + saveFileName);
        try {
            FileWriter fw = new FileWriter(myfile);
            BufferedWriter bw = new BufferedWriter(fw);
            /* 在存檔內容寫入頻道名稱。*/
            //String toWrite = str_channel + ":\n" + tv1.getText().toString();
            String toWrite = "<font color='#0000C6'>&nbsp;" + str_channel + "&nbsp;&nbsp;存檔時間:" +
                    timeDateToSave + "<br>========begin of file=======<br><br></font>" + value +
                    "<font color='#0000C6'><br>=======end of file========</font><br>";
            bw.write(toWrite);
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(ChatRoomActivity.this,
                "存檔完成,檔名 : " + saveFileName, Toast.LENGTH_LONG).show();
    }

    /* 從beetlechat.txt檔案中讀出資料到畫面上。106/11/08 Jones */
    public void clickRead() {
        Intent intent = new Intent();
        //intent.setClass(ChatRoomActivity.this, BrowseActivity.class);
        intent.setClass(ChatRoomActivity.this, ListFileActivity.class);
        /* 把顯示舊記錄的內容丟到另一個畫面,不影響到輸入訊息的畫面。106/11/12 Jones */
        startActivity(intent);
    }

    /* 管理者才有的功能,[備份]按鈕,按下後在FireBase中做備份資料。*/
    public void toBackup() {
        //DatabaseReference myRef1 = database.getReference("message" + timeDate);
        /* 因為分為數個聊天室,所以要動態傳入使用者所選擇的聊天室頻道,所以必需用變數方式傳入。106/11/26 Jones */
        DatabaseReference myRef1 = database.getReference(User_Channel + timeDate);
        myRef1.setValue("--- Begin of File ---<br>" + value + " --- End of File ---<br>");
        cal = Calendar.getInstance();  /* 取得系統時間 */
        timeDate = DateFormat.format("yyyy-MM-dd kk:mm:ss", cal.getTime());
        myRef.setValue("<font color='#ff0000'>公告:</font>清除版面。<font color='#ADADAD'>"
                + timeDate + "</font><br>");
    }
}
