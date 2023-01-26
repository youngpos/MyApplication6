package com.example.myapplication.utils;

import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MSSQL extends AsyncTask<String, Integer, JSONArray> {

    // 멤버
    public  interface  MSSQLCallbackInterface{
        public void onRequestCompleted(JSONArray results);
        public void onRequestFailed(int code, String msg);
    }

    private  MSSQLCallbackInterface mCallback;

    int errCode =0;
    String errMsg;

    // 생성자
    public MSSQL(MSSQLCallbackInterface callback){
        mCallback = callback;
    }

    // doInBackground 메소드가 실행되기 전에 실행되는 메소드
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // 오버라이드 실행 메소드
    @Override
    protected JSONArray doInBackground(String... strings) {

        Connection conn = null;
        JSONArray json = new JSONArray();

        String ip = strings[0];
        String dbname = strings[1];
        String dbid = strings[2];
        String dbpw = strings[3];
        String query = strings[4];

        //2019-11-14 IP/PORT 정보 확인
        Log.i("MSSQL","IP:" + ip);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            Log.i("MSSQL","MSSQL driver load");

            String url = "jdbc:jtds:sqlserver://" +ip +"/"+ dbname;
            conn = DriverManager.getConnection(url, dbid, dbpw);
            Log.i("MSSQL","MSSQL open: " + url);

            Statement stmt = conn.createStatement();
            ResultSet rs =null;
            Log.w("MSSQL","query: " + query );
            rs = stmt.executeQuery(query);

            json = ResultSetConverter.convert(rs);

            conn.close();
        } catch (SQLException e) {
            Log.e("Error connection","" + e.getMessage());
            errCode = 1;
            errMsg = e.getMessage();
        } catch (Exception e) {
            Log.e("Error connection","" + e.getMessage());
            errCode = 2;
            errMsg = e.getMessage();
        }

        return json;
    }

    // doInBackground 메소드가 실행 후에 실행되는 메소드
    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);

        switch (errCode) {
            case 0 :
                Log.w("MSSQL:", "onPostExecute: " +jsonArray.toString());
                mCallback.onRequestCompleted(jsonArray);
                break;
            case 1 :
            case 2 :
                mCallback.onRequestFailed(errCode, errMsg);
                break;
        }
    }
}
