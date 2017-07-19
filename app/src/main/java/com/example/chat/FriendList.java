package com.example.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class FriendList extends Fragment {
    private TextView accountText;
    private Button backMainActivity;
    private List<Friend> mFriendList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.friend_choose,container,false);
        accountText=(TextView)view.findViewById(R.id.account_text);
        backMainActivity=(Button)view.findViewById(R.id.back_MainActivity);
        FriendInit();
        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.friend_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());//这里有可能是个BUG
        recyclerView.setLayoutManager(layoutManager);
        FriendAdapter friendAdapter=new FriendAdapter(mFriendList);
        recyclerView.setAdapter(friendAdapter);
        return view;
    }

    private void FriendInit(){
        for (int i=0;i<2;i++){
            Friend apple=new Friend(R.drawable.apple_pic,"apple");
            mFriendList.add(apple);
            Friend banana=new Friend(R.drawable.banana_pic,"banana");
            mFriendList.add(banana);
            Friend orange=new Friend(R.drawable.orange_pic,"orange");
            mFriendList.add(orange);
            Friend watermelon=new Friend(R.drawable.watermelon_pic,"watermelon");
            mFriendList.add(watermelon);
            Friend pear=new Friend(R.drawable.pear_pic,"pear");
            mFriendList.add(pear);
            Friend grape=new Friend(R.drawable.grape_pic,"grape");
            mFriendList.add(grape);
            Friend pineapple=new Friend(R.drawable.pineapple_pic,"pineapple");
            mFriendList.add(pineapple);
            Friend strawberry=new Friend(R.drawable.strawberry_pic,"strawberry");
            mFriendList.add(strawberry);
            Friend cherry=new Friend(R.drawable.cherry_pic,"cherry");
            mFriendList.add(cherry);
            Friend mango=new Friend(R.drawable.mango_pic,"mange");
            mFriendList.add(mango);
        }
    }
}
