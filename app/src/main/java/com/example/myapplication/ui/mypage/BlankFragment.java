package com.example.myapplication.ui.mypage;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.FragmentBlankBinding;
import com.example.myapplication.utils.JsonHelper;
import com.example.myapplication.utils.MSSQL;
import com.example.myapplication.utils.StringFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

// 안드로이드 : 프래그먼트에 리스트뷰(ListView) 사용하기
//https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=jcosmoss&logNo=222006150792
public class BlankFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private FragmentBlankBinding binding;

    private JSONObject m_shop;
    private JSONObject m_userProfile;
    //----------------------------------------//
    // 2022.05.26.본사서버 IP변경
    //----------------------------------------//
    private String m_ip = "";
    private String m_port = "";
    //----------------------------------------//
    // 2021.12.21. 매장DB IP,PW,DB 추가
    //----------------------------------------//
    private String m_uuid = "";
    private String m_uupw = "";
    private String m_uudb = "";
    //----------------------------------------//

    private SimpleDateFormat m_dateFormatter;
    private Calendar m_dateCalender1;
    private Calendar m_dateCalender2;
    private int m_dateMode = 0;
    private NumberFormat m_numberFormat;
    private ProgressDialog dialog;

    // 레이아웃 컨트롤 정의
    private Button m_period1;
    private Button m_period2;
    private Button m_Search;

    private ListView listSlip;              // 데이타 리스트
    private View rootView;

    private TextView m_Samt;    //순매출
    private TextView m_Card;    // 카드
    private TextView m_Cash;    //현금
    private TextView m_Etca;    //기타

    String m_APP_USER_GRADE;    //앱권한
    String m_OFFICE_CODE = null;  //수수료매장거래처코드
    String m_viewOption;

    // 어댑터 정의
    private SimpleAdapter adapter;
    private List<HashMap<String, String>> mfillMaps = new ArrayList<HashMap<String, String>>();

    private BlankViewModel mViewModel;

    //MSSQL
    private static String ip = "125.133.201.71"; //접속할 서버측의 IP, 현재는 로컬에서 진행하니 이 컴퓨터의 IP주소를 할당하면 된다.
    private static String port = "11433"; //SQL 구성 관리자에서 TCP/IP 구성 중 모든 IP포트를 설정하는 구간에서 동적 포트를 적으면 된다.
    private static String Classes = "net.sourceforge.jtds.jdbc.Driver";
    private static String database = "okserver"; //접속할 데이터베이스 이름
    private static String username = "sa"; //서버의 ID PW
    private static String password = "pos4515";
    private static String url = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + database;

    private Connection connection = null;


    public static BlankFragment newInstance() {
        return new BlankFragment();
    }

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_blank, container, false);

        //binding = FragmentBlankBinding.inflate(inflater,container,false);
        //View root = binding.getRoot();
        // 레이아웃 컨트롤 정의
        m_period1 = (Button) rootView.findViewById(R.id.buttonSetDate1);
        m_period1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSetDate1(getView());
            }
        });
        m_period2 = (Button) rootView.findViewById(R.id.buttonSetDate2);
        m_period2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSetDate2(getView());
            }
        });
        m_Search = (Button) rootView.findViewById(R.id.buttonSearch);
        m_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doQuery();
                //Toast.makeText(rootView.getContext(), "조회 버튼을 누르셨습니다!",Toast.LENGTH_LONG).show();
            }
        });

        m_Samt = (TextView)rootView.findViewById(R.id.textview_samt);
        m_Card = (TextView)rootView.findViewById(R.id.textview_card);
        m_Cash = (TextView)rootView.findViewById(R.id.textview_cash);
        m_Etca = (TextView)rootView.findViewById(R.id.textview_etca);

        // 일자클래스 생성
        m_dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        m_dateCalender1 = Calendar.getInstance();
        m_dateCalender2 = Calendar.getInstance();

        m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
        m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));

        listSlip = (ListView) rootView.findViewById(R.id.listviewSlip);

        String[] from2 = new String[]{"일자", "순매출", "카드", "현금", "기타"};
        int[] to2 = new int[]{R.id.item1, R.id.item2, R.id.item3, R.id.item4, R.id.item5};

        adapter = new SimpleAdapter(getContext(), mfillMaps, R.layout.activity_listview_date_list, from2, to2);
        listSlip.setAdapter(adapter);
        listSlip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
            }
        });


        //return inflater.inflate(R.layout.fragment_blank, container, false);
        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BlankViewModel.class);
        // TODO: Use the ViewModel
    }

    // 자료 조회
    private void doQuery() {

        mfillMaps.removeAll(mfillMaps);
        adapter.notifyDataSetChanged();

        String period1 = m_period1.getText().toString().replace("-","");
        String period2 = m_period2.getText().toString().replace("-","");

        //if (doCoeenct() == false) return;

        String sql = "";
        sql += "SELECT ";
        sql += " SDAT 일자";
        sql += ",isnull(Sum(isnull(SAMT,0)),0) 순매출 ";  // 순매출
        sql += ",isnull(Sum(isnull(CARD,0)),0) 카드 ";    // 카드
        sql += ",isnull(Sum(isnull(CASH,0)),0) 현금 ";    // 현금

        sql += ",isnull((isnull(Sum(GIFT),0) ";
        sql += "+isnull(Sum(CRED),0) ";
        sql += "+isnull(Sum(CUPA),0) ";
        sql += "+isnull(Sum(USEA),0) ";
        sql += "+isnull(Sum(PONT),0) ";
        sql += "+isnull(Sum(CBAK),0) ";
        sql += "+isnull(Sum(ETCA),0) ";
        sql += "+isnull(Sum(ETC1),0) ";
        sql += "+isnull(Sum(ETC2),0)),0) 기타 ";  //기타매출 금액(상품권,외상,쿠폰,사용,포인트,캐쉬백,기타,기타1,기타2)

        sql += " From SRSEOD ";
        sql += " Where (SDAT >= '" + period1 + "' AND SDAT <= '" + period2 + "') ";
        sql += " group by SDAT ";

        // 로딩 다이알로그
        //dialog = new ProgressDialog(this);
        //dialog.setMessage("Loading....");
        //dialog.show();
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading....");
        dialog.show();

        // 콜백함수와 함께 실행
        new MSSQL(new MSSQL.MSSQLCallbackInterface() {
            @Override
            public void onRequestCompleted(JSONArray results) {
                dialog.dismiss();
                dialog.cancel();
                if (results.length() > 0) {
                    DataListView(results);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "조회 완료: " + results.length(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "자료가 없습니다!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onRequestFailed(int code, String msg) {
                dialog.dismiss();
                dialog.cancel();
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "자료를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        }).execute(ip + ":" + port, database, username, password, sql);

    }

//    // 연결 체크
//    private boolean doCoeenct() {
//
//        try {
//            //연결시도
//            Class.forName(Classes); //jdbc드라이버 클래스 적용
//            connection = DriverManager.getConnection(url, username, password);
//            Toast.makeText(rootView.getContext(), "연결성공", Toast.LENGTH_LONG).show();
//            return true;
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            Toast.makeText(rootView.getContext(), "Class를 찾을 수 없음", Toast.LENGTH_LONG).show();
//
//        } catch (SQLException e) {
//            Toast.makeText(rootView.getContext(), "연결실패", Toast.LENGTH_LONG).show();
//        }
//
//        return false;
//
//    }

    // 조회자료 출력
    private void DataListView(JSONArray results) {

        // 합계 계산
        float mRamt = 0;
        float mCard = 0;
        float mCash = 0;
        float mOther = 0;

        for (int index = 0; index < results.length(); index++) {

            try {
                JSONObject son = results.getJSONObject(index);
                HashMap<String, String> map = JsonHelper.toStringHashMap(son);

                String ramt = son.getString("순매출");
                String card = son.getString("카드");
                String cash = son.getString("현금");
                String other = son.getString("기타");

                String formatDate = map.get("일자").substring(0,4) + "-" + map.get("일자").substring(4,6) + "-" + map.get("일자").substring(6,8);
                map.put("일자", formatDate);
                map.put("순매출", StringFormat.convertTNumberFormat(map.get("순매출")));
                map.put("카드", StringFormat.convertTNumberFormat(map.get("카드")));
                map.put("현금", StringFormat.convertTNumberFormat(map.get("현금")));
                map.put("기타", StringFormat.convertTNumberFormat(map.get("기타")));

                mRamt += Float.parseFloat(ramt);
                mCard += Float.parseFloat(card);
                mCash += Float.parseFloat(cash);
                mOther += Float.parseFloat(other);

                mfillMaps.add(map);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //m_contents[20].setText(StringFormat.convertToNumberFormat(String.format("%.2f", cash)));	//현
        // 합계 출력
        m_Samt.setText(StringFormat.convertToNumberFormat(String.format("%.0f", mRamt)));
        m_Card.setText(StringFormat.convertToNumberFormat(String.format("%.0f", mCard)));
        m_Cash.setText(StringFormat.convertToNumberFormat(String.format("%.0f", mCash)));
        m_Etca.setText(StringFormat.convertToNumberFormat(String.format("%.0f", mOther)));

    }

    public void onClickSetDate1(View v){
        DatePickerDialog newDlg = new DatePickerDialog(getContext(),this,
                m_dateCalender1.get(Calendar.YEAR),
                m_dateCalender1.get(Calendar.MONTH),
                m_dateCalender1.get(Calendar.DAY_OF_MONTH));
        newDlg.show();

        m_dateMode = 1;
   }
    public void onClickSetDate2(View v){
        DatePickerDialog newDlg = new DatePickerDialog(getContext(),this,
                m_dateCalender2.get(Calendar.YEAR),
                m_dateCalender2.get(Calendar.MONTH),
                m_dateCalender2.get(Calendar.DAY_OF_MONTH));
        newDlg.show();

        m_dateMode = 2;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if (m_dateMode == 1) {
            m_dateCalender1.set(i, i1, i2);
            ;
            m_period1.setText(m_dateFormatter.format(m_dateCalender1.getTime()));
        } else if (m_dateMode == 2) {
            m_dateCalender2.set(i, i1, i2);
            ;
            m_period2.setText(m_dateFormatter.format(m_dateCalender2.getTime()));
        }
        m_dateMode = 0;

    }
}