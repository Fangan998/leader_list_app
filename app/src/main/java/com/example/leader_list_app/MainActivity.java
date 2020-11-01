package com.example.leader_list_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

    Button badd , bedit, bdel , bsearch;
    EditText edit_name , edit_say;
    ListView LV;
    Cursor cursor;
    long myid; //儲存_id值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_name = findViewById(R.id.edit_name);
        edit_say= findViewById(R.id.edit_say);
        LV =  findViewById(R.id.LV);
        badd = findViewById(R.id.add);
        bedit= findViewById(R.id.edit);
        bdel = findViewById(R.id.delete);
        bsearch= findViewById(R.id.search);

        badd.setOnClickListener(listener);
        bedit.setOnClickListener(listener);
        bdel.setOnClickListener(listener);
        bsearch.setOnClickListener(listener);
        LV.setOnItemClickListener(LVlistener);

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
        edit_name.setText(c.getString(1)); //name
        edit_say.setText(""+c.getString(2)); //say
    }

    protected void onDestory(){
        super.onDestroy();
        db.close();
    }

    private Button.OnClickListener listener = new Button.OnClickListener(){
        public void onClick(View v){
            try {
                switch (v.getId()){
                    case R.id.add: {    //新增
                        String say =  edit_say.getText().toString();
                        String name = edit_name.getText().toString();
                        if (db.append(name, say)>0) {
                            cursor = db.getAll();
                            UpdateAdapter(cursor);
                            ClearEdit();
                        }
                        break; }
                    case R.id.edit:{    //修改
                        String say = edit_say.getText().toString();
                        String name = edit_name.getText().toString();
                        if(db.update(myid,name,say)) {
                            cursor = db.getAll();
                            UpdateAdapter(cursor);
                        }
                        break;
                    }case R.id.delete:{
                        if(cursor != null && cursor.getCount() >= 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("確定刪除");
                            builder.setMessage("確定要刪除" + edit_name.getText() + "這筆資料?");
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
                    }case R.id.search:{
                        String say = edit_say.getText().toString();
                        String name = edit_name.getText().toString();
                        cursor=db.getAll();
                        UpdateAdapter(cursor); // 載入資料表至 ListView 中
                        break;
                    }
                }
            }catch(Exception er){
                Toast.makeText(getApplicationContext(),"資料不正確!" ,
                        Toast.LENGTH_SHORT).show();
            }

        }
    };
    public void ClearEdit() { //函式
        edit_name.setText("");
        edit_say.setText("");
    }

    public void UpdateAdapter(Cursor cursor){
        if (cursor != null && cursor.getCount() >= 0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter
                    (this,android.R.layout.simple_list_item_2,
                            cursor,
                            new String[]{"name","say"},
                            new int[]{android.R.id.text1,android.R.id.text2},0);
            LV.setAdapter(adapter);
        }
    }
}