package roommate.yapp.com.yapp13th_roommate.DetailInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roommate.yapp.com.yapp13th_roommate.DataModel.UserInfo;
import roommate.yapp.com.yapp13th_roommate.Function.ImageFunc;
import roommate.yapp.com.yapp13th_roommate.Global.GlobalVariable;
import roommate.yapp.com.yapp13th_roommate.R;
import roommate.yapp.com.yapp13th_roommate.SignUp.SignUpFirstActivity;
import roommate.yapp.com.yapp13th_roommate.ViewPager.RoomImagePagerAdapter;

public class DetailInfoActivity extends AppCompatActivity {

    private GlobalVariable global;
    private ImageFunc imageFunc;

    private int position;

    private ImageView ivProfile, pagerIndex1, pagerIndex2, pagerIndex3;
    private TextView tvName, tvBirth, tvLocation, tvInstarID, tvMonthly, tvPattern, tvDrink, tvSmoking, tvAllowFriend, tvPet, tvLike, tvDisLike, tvChatURL, tvIntroduceContent;

    private Button btnSelect, btnChat;

    private ViewPager viewPager;
    private RoomImagePagerAdapter roomImagePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        global = (GlobalVariable)getApplicationContext();
        imageFunc = new ImageFunc(this);
      
        ivProfile = (ImageView)findViewById(R.id.ivUser);
        pagerIndex1 = (ImageView)findViewById(R.id.viewPagerIndex1);
        pagerIndex2 = (ImageView)findViewById(R.id.viewPagerIndex2);
        pagerIndex3 = (ImageView)findViewById(R.id.viewPagerIndex3);

        tvName = (TextView)findViewById(R.id.tvName);
        tvBirth = (TextView)findViewById(R.id.tvBirth);
        tvLocation = (TextView)findViewById(R.id.tvLocation);
        tvInstarID = (TextView)findViewById(R.id.tvInstarID);
        tvMonthly = (TextView)findViewById(R.id.tvMonthly);
        tvPattern = (TextView)findViewById(R.id.tvPattern);
        tvDrink = (TextView)findViewById(R.id.tvDrink);
        tvSmoking = (TextView)findViewById(R.id.tvSmoking);
        tvAllowFriend = (TextView)findViewById(R.id.tvAllowFriend);
        tvPet = (TextView)findViewById(R.id.tvPet);
        tvLike = (TextView)findViewById(R.id.tvLike);
        tvDisLike = (TextView)findViewById(R.id.tvDisLike);
        tvChatURL= (TextView)findViewById(R.id.tvChatURL);
        tvIntroduceContent = (TextView)findViewById(R.id.tvIntroduceContent);

        btnSelect = (Button)findViewById(R.id.btnSelect);
        btnChat = (Button)findViewById(R.id.btnChat);

        Intent intent = getIntent();

        if(intent.getIntExtra("bottom", -1) != -1){
            position = intent.getIntExtra("bottom", -1);
            init(global.filterInfo.get(position));
        }else{
            position = intent.getIntExtra("like", -1);
            init(global.likeInfo.get(position));
        }

    }

    public void init(UserInfo userInfo){
        if(!(userInfo.getProfile_image() == null || userInfo.getProfile_image().isEmpty())){
            ivProfile.setImageBitmap(imageFunc.decodebase64ToBitmap(userInfo.getProfile_image()));
        }
        tvName.setText(userInfo.getName());
        tvBirth.setText(userInfo.getYear() + " 년생");
        tvLocation.setText(userInfo.getLocation());
        tvInstarID.setText(userInfo.getInstarID());
        tvMonthly.setText(userInfo.getMonthly() + " 만원");
        tvPattern.setText(userInfo.getPattern());
        tvDrink.setText(userInfo.getDrink());
        tvSmoking.setText(userInfo.getSmoking());
        tvAllowFriend.setText(userInfo.getAllow_friend());
        tvPet.setText(userInfo.getPet());
        tvLike.setText(userInfo.getLike());
        tvDisLike.setText(userInfo.getDisLike());
        tvChatURL.setText(userInfo.getOpenChatURL());
        tvIntroduceContent.setText(userInfo.getIntroduce());

        viewPager = (ViewPager)findViewById(R.id.viewPager);

        //방 사진 이미지가 하나도 없을 시 0으로 초기화 하는 작업 추가 해야됨

        if(!(userInfo.getRoom_image() == null || userInfo.getRoom_image().isEmpty())){
            ViewTreeObserver vto = viewPager.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    viewPager.getLayoutParams().height = (int)(viewPager.getWidth() * 0.8);
                    viewPager.requestLayout();
                }
            });
            //match_parent를 onCreate에서 이용하면 아직 뷰가 그려지기 전이라서 0으로 호출이 된다
            //뷰가 그려진 이후를 지켜보기 위해 트리옵저버를 이용하여 viewPager를 확인 및 그려지면 가로 : 세로 = 5 : 4 비율을 만들기 위해
            //레이아웃을 다시 그려준다

            int bitmapLength = userInfo.getRoom_image().size();
            Bitmap[] bitmaps = new Bitmap[bitmapLength];

            for(int i = 0; i < bitmaps.length; i++){
                bitmaps[i] = imageFunc.decodebase64ToBitmap(userInfo.getRoom_image().get("room" + i));
            }
            //뷰 페이저에 들어갈 방 사진 bitmap 만들기
            makeRoom(bitmapLength, bitmaps);
        }

        final String openChatURL = userInfo.getOpenChatURL();

        if(!userInfo.getOpenChatURL().equals("")){
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(openChatURL);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        Pattern pattern = Pattern.compile(".*");
        Linkify.addLinks(tvInstarID, pattern, "https://www.instagram.com/");
        //사용자가 입력한 아이디를 이용하여 인스타 링크를 걸어줌

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void makeRoom(int length, Bitmap[] bitmaps){
        final int bitmapLength = length;
        roomImagePagerAdapter = new RoomImagePagerAdapter(DetailInfoActivity.this, bitmaps);
        viewPager.setAdapter(roomImagePagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //페이저 뷰에서 지금 선택 된 인덱스를 표시해주는것 처리
                if(bitmapLength == 3){
                    if(position == 0){
                        pagerIndex1.setImageResource(R.drawable.oval_copy);
                        pagerIndex2.setImageResource(R.drawable.oval);
                        pagerIndex3.setImageResource(R.drawable.oval);
                        //●○○ 형태의 이미지
                    }else if(position == 1){
                        pagerIndex1.setImageResource(R.drawable.oval);
                        pagerIndex2.setImageResource(R.drawable.oval_copy);
                        pagerIndex3.setImageResource(R.drawable.oval);
                        //○●○ 형태의 이미지
                    }else if(position == 2){
                        pagerIndex1.setImageResource(R.drawable.oval);
                        pagerIndex2.setImageResource(R.drawable.oval);
                        pagerIndex3.setImageResource(R.drawable.oval_copy);
                        //○○● 형태의 이미지
                    }
                }else if(bitmapLength == 2){
                    if(position == 0){
                        pagerIndex1.setImageResource(R.color.transparent);
                        pagerIndex2.setImageResource(R.drawable.oval_copy);
                        pagerIndex3.setImageResource(R.drawable.oval);
                        //●○ 형태의 이미지
                    }else if(position == 1){
                        pagerIndex1.setImageResource(R.color.transparent);
                        pagerIndex2.setImageResource(R.drawable.oval);
                        pagerIndex3.setImageResource(R.drawable.oval_copy);
                        //○● 형태의 이미지
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}

