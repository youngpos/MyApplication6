package com.example.myapplication;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    ListViewAdapter adapter = new ListViewAdapter();
    ListView listView;


    private static String ip = "125.133.201.71"; //접속할 서버측의 IP, 현재는 로컬에서 진행하니 이 컴퓨터의 IP주소를 할당하면 된다.
    private static String port = "11433"; //SQL 구성 관리자에서 TCP/IP 구성 중 모든 IP포트를 설정하는 구간에서 동적 포트를 적으면 된다.
    private static String Classes = "net.sourceforge.jtds.jdbc.Driver";
    private static String database = "okserver"; //접속할 데이터베이스 이름
    private static String username = "sa"; //서버의 ID PW
    private static String password = "pos4515";
    private static String url ="jdbc:jtds:sqlserver://"+ip+":"+port+"/"+database;

    private Connection connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //listView = (ListView)findViewById(R.id.lv_studentList);



        //StrictMode는 네트워크 연결을 메인스레드에서 동작할 예정인데 이로 인해 속도가 느려지는 것을 감지하고
        //Android Not Response를 방지할 수 있도록 미리 탐지하는 역할을 함
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //연결시도
            Class.forName(Classes); //jdbc드라이버 클래스 적용
            connection = DriverManager.getConnection(url,username,password);
            Toast.makeText(this,"연결성공",Toast.LENGTH_LONG).show();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Class를 찾을 수 없음",Toast.LENGTH_LONG).show();

        }catch (SQLException e){
            Toast.makeText(this,"연결실패",Toast.LENGTH_LONG).show();
        }

        DisplayListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void DisplayListView() {


        String qry = "SELECT * FROM studentTbl";

        if(connection != null){
            Statement statement = null;
            try {
                statement = connection.createStatement();
                //쿼리문을 이용해 가져올 데이터값을 정한다.
                ResultSet resultSet = statement.executeQuery(qry);


                while(resultSet.next()){
                    //가져온 모든 값들을 next()메소드로 모두 순회하여 띄우도록 한다.
                    adapter.addItem(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3));

                    //맨 앞의 필드가 1, 그 다음이 2..
                }

                listView.setAdapter(adapter);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(this,"연결 없음 실패",Toast.LENGTH_LONG).show();
        }

    }
}