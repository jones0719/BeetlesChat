package tw.com.jones0719.beetleschat;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SprUsrSignActivity extends AppCompatActivity {
    private EditText et03, et04;
    private Intent intent;
    private Bundle bundle;
    String str_Channel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprusrsign);

        et03 = (EditText) findViewById(R.id.et03);
        et04 = (EditText) findViewById(R.id.et04);
        TextView tvSuperSign = (TextView) findViewById(R.id.tvSuperSgn);
        tvSuperSign.setOnClickListener(tvSuperSign_Listener);
    }

    /* 管理者帳號[管理者登入]監聽器*/
    TextView.OnClickListener tvSuperSign_Listener = new TextView.OnClickListener() {
        private String superUserName, superUserPwd;

        @Override
        public void onClick(View v) {
            superUserName = et03.getText().toString().trim();
            superUserPwd = et04.getText().toString().trim();
            if (!superUserName.equals("") && !superUserPwd.equals("")) {
                //et03.setText("");  /* 輸入完整,在進入主畫面前先把欄位清空。*/
                //et04.setText("");
                //Toast.makeText(SigninActivity.this,"按下管理者登入功能!", Toast.LENGTH_SHORT).show();
                if (superUserName.equals("SuperUser") && superUserPwd.equals("1234567890")) {
                    toChatRoom("1", superUserName, superUserPwd, str_Channel);
                }else{
                    Toast.makeText(
                            SprUsrSignActivity.this,
                            "帳號或密碼輸入錯誤!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(
                        SprUsrSignActivity.this,
                        "帳號或密碼未輸入,請輸入。",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_item, menu);
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

    /* 進入主畫面 */
    void toChatRoom(String level, String user_name, String user_pwd,String str_channel) {
        intent = new Intent();
        intent.setClass(SprUsrSignActivity.this, ChatRoomActivity.class);
        bundle = new Bundle();
        bundle.putString("USER_LEVEL", level);    /* 0:User 1:SuperUser */
        bundle.putString("USER_NAME", user_name); /* 匿稱及密碼置入Bundle物件。*/
        bundle.putString("USER_PWD", user_pwd);
        /* 使用者選擇的頻道置入Bundle物件。 106/11/26 Jones*/
        bundle.putString("USER_CHANNEL", str_channel);
        intent.putExtras(bundle);                 /* Bundle置入Intent。 */
        startActivity(intent);                    /* 進入主頁面。*/
    }
}
