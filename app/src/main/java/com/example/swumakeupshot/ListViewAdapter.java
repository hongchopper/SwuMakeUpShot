package com.example.swumakeupshot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    ArrayList<ListItem> list = new ArrayList<ListItem>();

    @Override
    public int getCount() {
        return list.size(); //그냥 배열의 크기를 반환하면 됨
    }

    @Override
    public Object getItem(int i) {
        return list.get(i); //배열에 아이템을 현재 위치값을 넣어 가져옴
    }

    @Override
    public long getItemId(int i) {
        return i; //그냥 위치값을 반환해도 되지만 원한다면 아이템의 num 을 반환해도 된다.
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final Context context = viewGroup.getContext();

        //리스트뷰에 아이템이 인플레이트 되어있는지 확인한후
        //아이템이 없다면 아래처럼 아이템 레이아웃을 인플레이트 하고 view객체에 담는다.
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.main_list_item,viewGroup,false);
        }

        //이제 아이템에 존재하는 텍스트뷰 객체들을 view객체에서 찾아 가져온다
        TextView name = (TextView)view.findViewById(R.id.makeup_name);
        TextView caution = (TextView)view.findViewById(R.id.caution);
        TextView allergy = (TextView)view.findViewById(R.id.allergy);
        TextView good = (TextView)view.findViewById(R.id.good);

        //현재 포지션에 해당하는 아이템에 글자를 적용하기 위해 list배열에서 객체를 가져온다.
        ListItem listdata = list.get(i);

        //가져온 객체안에 있는 글자들을 각 뷰에 적용한다
        name.setText(listdata.getCos_name()); //원래 int형이라 String으로 형 변환
        caution.setText(listdata.getCaution_count());
        allergy.setText(listdata.getAllergy_count());
        good.setText(listdata.getGood_count());


        return view;
    }

    //ArrayList로 선언된 list 변수에 목록을 채워주기 위함 다른방시으로 구현해도 됨
    public void addItemToList(String name, String caution,String allergy, String good){
        ListItem listdata = new ListItem();

        listdata.setCos_name(name);
        listdata.setCaution_count(caution);
        listdata.setAllergy_count(allergy);
        listdata.setGood_count(good);

        //값들의 조립이 완성된 listdata객체 한개를 list배열에 추가
        list.add(listdata);

    }
}
