package tw.com.jones0719.beetleschat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity {
    /* 類別層級物件變數宣告。*/
    private EditText et01, et02;
    private Button btnSign;
    String str_Channel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        /*------------------------------------------------------*/
        findViews();     /* 取得畫面輸入欄位物件,用以取得輸入內容值。106/11/05 */
        setListeners();  /* 設定按鈕物件的監聽器。106/11/05 */
        rtoShePrf();     /* 取出曾經輸入過的匿名及密碼顯示在對應的欄位裏,省去再次輸入的麻煩。*/
    }

    /* 取得畫面輸入欄位物件,用以取得輸入內容值。106/11/05 */
    void findViews() {
        et01 = (EditText) findViewById(R.id.et01);              // 使用者帳號。
        et02 = (EditText) findViewById(R.id.et02);              // 使用者密碼。
        //et03 = (EditText) findViewById(R.id.et03);              // 管理者帳號。
        //et04 = (EditText) findViewById(R.id.et04);              // 管理者密碼。

        btnSign = (Button) findViewById(R.id.btnSignin);        // 使用者登入按鈕。
        //tvSuperSign = (TextView) findViewById(R.id.tvSuperSgn); // 管理者登入標籤。
    }

    /* 設定使用者 [進入聊天] 及 [管理者登入] 2個物件按下的監聽動作*/
    void setListeners() {
        btnSign.setOnClickListener(btnSign_Listener);
        //tvSuperSign.setOnClickListener(tvSuperSign_Listener);
    }

    /* 一般使用者[進入聊天]按鈕監聽器 */
    Button.OnClickListener btnSign_Listener = new Button.OnClickListener() {
        String userName, userPwd;

        @Override
        public void onClick(View v) {
            userName = et01.getText().toString();    /* 使用者帳號 */
            userPwd = et02.getText().toString();     /* 使用者密碼 */
            /* 判斷使用者是否輸入匿名及密碼,任一欄位空白時則發出訊息。*/
            if ("".equals(userName.trim()) || "".equals(userPwd.trim())) {
                Toast.makeText(
                        SigninActivity.this,
                        "匿名或密碼空白,請填寫。",
                        Toast.LENGTH_SHORT).show();
            } else if (!"".equals(userName.trim()) && !"".equals(userPwd.trim())) {
                /* 匿名及密碼寫入SharedPreferences檔案中。*/
                writesfs(userName, userPwd);
                /* 完成輸入者,則把匿稱及密碼打包傳入,進入聊天主頁面。*/
                toChatRoom("0", userName, userPwd, str_Channel);
            }
        }
    };

    /* 進入主畫面 */
    void toChatRoom(String level, String user_name, String user_pwd, String str_channel) {
        Intent intent = new Intent();
        intent.setClass(SigninActivity.this, ChatRoomActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("USER_LEVEL", level);           /* 0:User 1:SuperUser */
        bundle.putString("USER_NAME", user_name);        /* 匿稱置入Bundle物件。*/
        bundle.putString("USER_PWD", user_pwd);          /* 密碼置入Bundle物件。*/
        /* 使用者選擇的頻道置入Bundle物件。 106/11/26 Jones*/
        bundle.putString("USER_CHANNEL", str_channel);
        intent.putExtras(bundle);                        /* Bundle置入Intent。 */
        startActivity(intent);                           /* 進入主頁面。*/
    }

    /* 寫入帳號及密碼到SharedPreferences檔案中*/
    protected void writesfs(String userName, String userPWD) {
        SharedPreferences sps = getSharedPreferences("USER_ACCOUNT", 0);
        sps.edit().putString("USER_NAME", userName).commit();
        sps.edit().putString("USER_PWD", userPWD).commit();
    }

    /*取出曾經輸入過的匿名及密碼顯示在對應的欄位裏,省去再次輸入的麻煩。*/
    void rtoShePrf() {
        SharedPreferences sps = getSharedPreferences("USER_ACCOUNT", 0);
        String userName = sps.getString("USER_NAME", "");
        String userPwd = sps.getString("USER_PWD", "");
        //Log.d("Debug", p_UserName + " - " + p_UserPwd);
        et01.setText(userName);
        et02.setText(userPwd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_item, menu);

        menu.add(0, 10, 0, "管理員登入");
        menu.add(0, 20, 0, "主頻道");
        menu.add(0, 30, 0, "頻道A");
        menu.add(0, 40, 0, "頻道B");
        menu.add(0, 50, 0, "頻道C");
        menu.add(0, 90, 0, "讀我說明");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        StringBuilder str_msg = new StringBuilder();

        switch (id) {
            case 10:
                str_msg.append("管理員登入");
                toSprUsrSign();
                break;
            case 20:
                str_msg.append("主頻道");
                str_Channel = "message";
                break;
            case 30:
                str_msg.append("頻道A");
                str_Channel = "message1";
                break;
            case 40:
                str_msg.append("頻道B");
                str_Channel = "message2";
                break;
            case 50:
                str_msg.append("頻道C");
                str_Channel = "message3";
                break;
            case 90:
                str_msg.append("[讀我說明");
                new AlertDialog.Builder(this)
                        .setTitle("使用說明")
                        .setMessage("[管理員登入]:\n切換為系統管理員角色登入系統,有主機端對話內容備份及清除權限。\n\n" +
                                "[頻道A~C]:\n可以選擇登入不同頻道,模擬不同的聊天室空間;" +
                                "不選擇預設登入主頻道。\n"
                        )
                        .setPositiveButton("OK", null)
                        .show();
                break;
            default:
                str_msg.append("未選擇項目!");
        }
        //Toast.makeText(this, str_msg, Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    void toSprUsrSign() {
        Intent intent = new Intent();
        intent.setClass(SigninActivity.this, SprUsrSignActivity.class);
        startActivity(intent);
    }
}
