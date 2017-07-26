package com.example.chat;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
    private UserAccount userAccount;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FriendAdapter friendAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.friend_choose,container,false);
        accountText=(TextView)view.findViewById(R.id.title_account_text);//在碎片中，所以要用view
        backMainActivity=(Button)view.findViewById(R.id.back_MainActivity);
        backMainActivity.setOnClickListener(this);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        if (getActivity() instanceof FriendChooseActivity){
            FriendChooseActivity friendChoose=(FriendChooseActivity) getActivity();
            userAccount=friendChoose.getUserAccount();
            accountText.setText(userAccount.getName());
        }else {
            ChatActivity chatActivity=(ChatActivity) getActivity();
            userAccount=chatActivity.getUserAccount();
            accountText.setText(userAccount.getName());//这句话是bug所在！！！
        }
        FriendInit();
        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.friend_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());//这里有可能是个BUG
        recyclerView.setLayoutManager(layoutManager);
        Activity activity=getActivity();
        friendAdapter=new FriendAdapter(mFriendList,userAccount,activity);//注意，这里的userAccount的
        // 传递顺序，顺序出错程序会崩溃！！尤其要注意fragment是复用的，而且其中的量userAccount是会用到两次的
        //on a null object reference，为空对象的引用！注意
        recyclerView.setAdapter(friendAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataSupport.deleteAll(Friends.class);
                FriendInit();
                swipeRefreshLayout.setRefreshing(false);//让等待图标消失
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
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
        mFriendList.clear();
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
            HttpUtil.sendOkHttpRequest("http://192.168.1.111/"+FriendsId+"/"+FriendsId+".json",
                    new okhttp3.Callback(){//内部属于子线程
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
                    getActivity().runOnUiThread(new Runnable() {//需要得到活动才能执行runOnUi
                        @Override
                        public void run() {
                            friendAdapter.notifyDataSetChanged();//这也属于UI操作，还有该函数放在这里而不是放在
                            //刷新函数中FriendInit();的后面的原因是，该方法必须放在子线程中等待被调用，否则没等
                            //FriendInit()执行完，friendAdapter.notifyDataSetChanged()就执行结束了
                        }
                    });
                }
                @Override
                public void onFailure(Call call,IOException e){
                    e.printStackTrace();
                }
            });
            showResponse("OK!(not fromDB)");
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
