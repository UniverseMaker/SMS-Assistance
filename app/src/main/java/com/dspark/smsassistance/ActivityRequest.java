package com.dspark.smsassistance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.dspark.smsassistance.utility.*;

public class ActivityRequest  extends Fragment implements View.OnClickListener{
    static MainActivity ma;
    View v;

    RequestRecyclerAdapter adapter;
    ArrayList<RequestRecyclerItem> mItems = new ArrayList<>();
    RecyclerView recyclerView;

    Map<Integer, String> memberlist = new HashMap<>();

    String mParam1;
    String mParam2;

    public ActivityRequest() {
        // Required empty public constructor
    }

    public static ActivityRequest newInstance(MainActivity _ma, String param1, String param2) {
        ma = _ma;
        ActivityRequest fragment = new ActivityRequest();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("SMS Assistance");

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업을 합니다.
        v = inflater.inflate(R.layout.content_request, container, false);
        //((Button)v.findViewById(R.id.btnLottery1Start)).setOnClickListener(this);

        ma.activityRequest = this;

        //리스트초기화 중요
        setRecyclerView();
        refreshData();

        //mItems.add(new RequestRecyclerItem("01083648316", "반가워요~!"));
        //adapter.notifyDataSetChanged();
        return v ;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    public void refreshData(){
        DBHelper dbHelper = new DBHelper(ma, "SMSASSISTANCE", null, 1);
        Map<Integer, ArrayList<String>> data = dbHelper.getRecord();

        if(data.size() == 0) {
            TextView tv = (TextView) v.findViewById(R.id.txtNoDataRequestRecycler);
            tv.setVisibility(View.VISIBLE);
        }
        else{
            TextView tv = (TextView) v.findViewById(R.id.txtNoDataRequestRecycler);
            tv.setVisibility(View.GONE);
        }

        mItems.clear();
        Set keyset = data.keySet();
        for (Iterator iterator = keyset.iterator(); iterator.hasNext(); ) {
            int key = (int) iterator.next();
            ArrayList<String> value = (ArrayList<String>) data.get(key);

            //String[] d1 = value.get(1).split("/_/");
            mItems.add(new RequestRecyclerItem(key, value.get(0), value.get(1)));
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();
    }

    private void setRecyclerView(){
        //ActivityMainBinding mainBinding;

        recyclerView = (RecyclerView) v.findViewById(R.id.requestRecyclerView);

        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        recyclerView.setHasFixedSize(true);

        // RecyclerView에 Adapter를 설정해줍니다.
        adapter = new RequestRecyclerAdapter(mItems);
        recyclerView.setAdapter(adapter);

        // 다양한 LayoutManager 가 있습니다. 원하시는 방법을 선택해주세요.
        // 지그재그형의 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        // 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        // 가로 또는 세로 스크롤 목록 형식
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setItemClick(listRequestRecycler_Clicked);

    }

    @Override
    public void onClick(View view) {
        //switch (view.getId()) {
        //    case R.id.btnLottery1Start :
        //
        //        break ;
        //}
    }

    RequestRecyclerAdapter.ItemClick listRequestRecycler_Clicked = new RequestRecyclerAdapter.ItemClick() {
        @Override
        public void onClick(View view, final int position) {
            //클릭시 실행될 함수 작성

            final String id = String.valueOf(mItems.get(position).getId());

            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ma);
            dlgAlert.setMessage("선택대상\n" + mItems.get(position).getTitle() + "\n" + mItems.get(position).getContents());
            dlgAlert.setTitle("원하는 동작을 선택하세요");
            dlgAlert.setNeutralButton("삭제",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            //DBHelper dbHelper = new DBHelper(ma, "MINISLOTTERY", null , 1);
                            //dbHelper.removeMember(id);
                            //onStart();
                            DBHelper dbHelper = new DBHelper(ma, "SMSASSISTANCE", null, 1);
                            dbHelper.removeRecord(id);
                            refreshData();
                            Snackbar.make(v, "삭제됨", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    });
            dlgAlert.setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.setPositiveButton("발송",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //ma.getSupportFragmentManager().beginTransaction().replace(R.id.container, ActivityMemberEdit.newInstance(ma, am, Integer.parseInt(id), memberset[0], memberset[1])).addToBackStack(null).commit();
                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                String contents = mItems.get(position).getContents();
                                if(contents.length() > 70)
                                    contents = contents.substring(0, 70);
                                smsManager.sendTextMessage(mItems.get(position).getTitle(), null, contents, null, null);
                                DBHelper dbHelper = new DBHelper(ma, "SMSASSISTANCE", null, 1);
                                dbHelper.removeRecord(id);
                                refreshData();
                                Snackbar.make(v, "발송됨", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }catch (Exception e){
                                e.printStackTrace();
                                Snackbar.make(v, "발송오류", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();


        }
    };

}
