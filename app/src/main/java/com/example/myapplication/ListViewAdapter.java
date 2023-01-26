package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    TextView tvStudentName, tvAddress; //아이템 레이아웃에 존재하는 텍스트뷰 2개를 담아올 객체

    ArrayList<ListViewAdapterData> arryListDO = new ArrayList<ListViewAdapterData>(); //데이터베이스에서 받아온 값들을 리스트뷰에 띄우기 위해 배열화 시켜 저장


    @Override
    public int getCount() {
        return arryListDO.size(); //배열의 크기 반환
    }

    @Override
    public Object getItem(int position) {
        return arryListDO.get(position); //리스트뷰를 클릭할때 사용하는데 클릭한 리스트뷰의 포지션값을 받아서 배열에서 해당하는 아이템을 가져옴
    }

    @Override
    public long getItemId(int position) {
        return position; //위치값 반환
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final Context context = viewGroup.getContext(); //이 리스트뷰가 작동될 컨텍스트를 가져오기

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //리스트뷰에 아이템을 인플레이트 해야함

//        view = inflater.inflate(R.layout.item,viewGroup,false); //인플레이트 후 해당 아이템을 view 에 담고
//
//        tvStudentName = (TextView) view.findViewById(R.id.tv_studentName); //해당 아이템의 텍스트 뷰들을 가져옴
//        tvAddress = (TextView) view.findViewById(R.id.tv_studentAddress);

        ListViewAdapterData ldo = arryListDO.get(position); //위치에 따라 배열로부터 가져올 값을 가져와 ListViewAdapterData객체 1개에 담음

        tvStudentName.setText(ldo.getsStudentName()); //그 ldo에 들어있는 값들을 각 뷰에 적용시킴
        tvAddress.setText(ldo.getsAddress());

        return view;
    }


    //데이터베이스로부터 값을 받았다면 그 레코드 수만큼 순회하며 배열에 add를 해줘야함
    public void addItem(int studentNum, String studentName, String address){

        ListViewAdapterData ldo = new ListViewAdapterData(); //arryListDO 배열에 add할 AdapterData객체 하나를 만듦.

        ldo.setiStudentNum(studentNum); //AdapterData객체에 적용
        ldo.setsStudentName(studentName);
        ldo.setsAddress(address);

        arryListDO.add(ldo); //arryListDO배열에 AdapterData객체를 추가

    }
}
