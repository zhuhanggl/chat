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
import android.widget.Toast;

import com.example.chat.db.Friends;
import com.example.chat.gson.OneFriend;
import com.example.chat.gson.UserAccount;
import com.example.chat.gson.UserFriend;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/19.
 */

public class FriendListFragment extends Fragment implements View.OnClickListener {
    private TextView accountText;
    private Button backMainActivity;
    private List<Friend> mFriendList=new ArrayList<>();
    private List<Friends> friendsList;
    UserAccount userAccount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.friend_choose,container,false);
        accountText=(TextView)view.findViewById(R.id.title_account_text);
        backMainActivity=(Button)view.findViewById(R.id.back_MainActivity);
        backMainActivity.setOnClickListener(this);
        FriendChooseActivity friendChoose=(FriendChooseActivity) getActivity();
        userAccount=friendChoose.getUserAccount();
        accountText.setText(userAccount.getName());
        FriendInit();
        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.friend_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());//这里有可能是个BUG
        recyclerView.setLayoutManager(layoutManager);
        FriendAdapter friendAdapter=new FriendAdapter(mFriendList,userAccount);
        recyclerView.setAdapter(friendAdapter);
        return view;
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.back_MainActivity:
                getActivity().finish();
                break;
        }
    }
    private void FriendInit(){
        String FriendsId=userAccount.getFriendsId();
        friendsList= DataSupport.findAll(Friends.class);
        if(friendsList.size()>0){
            for (int i=0;i<friendsList.size();i++){
                Friend friend=new Friend(friendsList.get(i).getFriendId(),friendsList.get(i).getAvatar(),
                        friendsList.get(i).getName());
                mFriendList.add(friend);
            }
            showResponse("OK!(fromDB)");
        }else{
            showResponse("OK!(not fromDB)");
            HttpUtil.sendOkHttpRequest("http://192.168.1.109/"+FriendsId+"/"+FriendsId+".json",
                    new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response)throws IOException {
                    String responseData=response.body().string();
                    UserFriend userFriend=Utility.handleUserFriendResponse(responseData);
                    List<OneFriend> oneFriendList=userFriend.getOneFriendList();
                    for(int i=0;i<oneFriendList.size();i++){
                        OneFriend oneFriend=oneFriendList.get(i);
                        Friend friend=new Friend(userFriend.getFriendId(),
                                oneFriend.getAvatar(),oneFriend.getName());
                        mFriendList.add(friend);
                        Friends friends=new Friends();
                        friends.setFriendId(userFriend.getFriendId());
                        friends.setAccount(oneFriend.getAccount());
                        friends.setName(oneFriend.getName());
                        friends.setAvatar(oneFriend.getAvatar());
                        friends.save();
                    }
                }
                @Override
                public void onFailure(Call call,IOException e){
                    e.printStackTrace();
                }
            });
        }

        /*for (int i=0;i<2;i++){
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
        }*/
    }
    private void showResponse(final String response){
        Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
    }
}
