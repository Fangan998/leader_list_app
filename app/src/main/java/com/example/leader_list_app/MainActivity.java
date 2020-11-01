package com.example.leader_list_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private  DB db = null;

    private final static String _ID   = "_id";
    private final static String NAME  = "name";
    private final static String SAY = "say";

    Button btn_add , btn_edit, btn_del , btn_re_search,btn_sec;
    EditText editText_id ;
    ListView ListV;
    Cursor cursor;
    long myid; //儲存_id值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button
        btn_add=(Button)findViewById(R.id.btn_add);
        btn_edit=(Button)findViewById(R.id.btn_edit);
        btn_del=(Button)findViewById(R.id.btn_del);
        btn_re_search=(Button)findViewById(R.id.btn_re_sec) ;
        btn_sec=(Button)findViewById(R.id.btn_sec);

        //EditText
        editText_id=(EditText)findViewById(R.id.edtxt_id);

        //listView
        ListV=(ListView)findViewById(R.id.leader_list);

        //btn_listener
        btn_add.setOnClickListener(listener);
        btn_edit.setOnClickListener(listener);
        btn_del.setOnClickListener(listener);
        btn_re_search.setOnClickListener(listener);

        //ListView_listener
        ListV.setOnItemClickListener(LVlistener);

        //DataBase
        db = new DB(this);
        db.open();
        cursor = db.getAll();
        UpdateAdapter(cursor);
    }

    private ListView.OnItemClickListener LVlistener = new ListView.OnItemClickListener() { //此處ListView是物件
        public void onItemClick(AdapterView<?> parent, View v,int position,long id) {
            ShowData(id);
            Cursor c=db.get(id);
            cursor.moveToPosition(position);
            String s= "id=" + id + "\r\n" + "name="
                    + c.getString(1) + "\r\n" + "say=" + c.getString(2);
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
        }
    };

    private void ShowData(long id)
    {
        Cursor c = db.get(id);
        myid = id;
        editText_id.setText(c.getString(1)); //name
    }

    protected void onDestory(){
        super.onDestroy();
        db.close();
    }

    private Button.OnClickListener listener = new Button.OnClickListener(){
        public void onClick(View v){
            try {
                switch (v.getId()){
                    case R.id.btn_add: {    //新增
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,add_MainActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.btn_edit:{    //修改
                        String set_ed="Edit";
                        long id= myid;

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,add_MainActivity.class);

                        Bundle bundle= new Bundle();
                        bundle.putString("SET",set_ed);


                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    }case R.id.btn_del:{
                        if(cursor != null && cursor.getCount() >= 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("確定刪除");
                            builder.setMessage("確定要刪除" + editText_id.getText() + "這筆資料?");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                }
                            });
                            builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    if (db.delete(myid)){
                                        cursor = db.getAll();
                                        UpdateAdapter(cursor);
                                        ClearEdit();
                                    }
                                }
                            });
                            builder.show();
                        }
                        break;
                    }case R.id.btn_sec:{
                        String say = editText_id.getText().toString();
                        cursor=db.getAll();
                        UpdateAdapter(cursor); // 載入資料表至 ListView 中
                        break;
                    } case R.id.btn_re_sec:{
                        ClearEdit();
                    }
                }
            }catch(Exception er){
                Toast.makeText(getApplicationContext(),"資料不正確!" ,
                        Toast.LENGTH_SHORT).show();
            }

        }
    };
    public void ClearEdit() { //函式
        editText_id.setText("");
    }

    public void UpdateAdapter(Cursor cursor){
        if (cursor != null && cursor.getCount() >= 0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter
                    (this,android.R.layout.simple_list_item_2,
                            cursor,
                            new String[]{"name","say"},
                            new int[]{android.R.id.text1,android.R.id.text2},0);
            ListV.setAdapter(adapter);
        }
    }

}

