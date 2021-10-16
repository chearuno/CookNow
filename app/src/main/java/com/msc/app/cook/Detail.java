package com.msc.app.cook;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;


import java.util.ArrayList;
import java.util.List;

public class Detail extends BaseActivity  {
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private RecyclerView recyclerView;
    //private ShoppingAdapter mAdapter;
    private RecyclerView recyclerViewPreparation;
   // private PreparationAdapter mAdapterPreparation;
    private RecyclerView recyclerViewComments;
    //private CommentsAdapter mAdapterComments;
    private CoordinatorLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        rootView = (CoordinatorLayout) findViewById(R.id.rootview);
        setupToolbar(R.id.toolbar, "DESSERTS", android.R.color.white, android.R.color.transparent, R.drawable.ic_arrow_back);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerShopping);

//        mAdapter = new ShoppingAdapter(generateShopping(), this);
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
//
//        recyclerViewPreparation = (RecyclerView) findViewById(R.id.recyclerPreparation);
//
//        mAdapterPreparation = new PreparationAdapter(getBaseContext(), generatePreparation(),this);
//        LinearLayoutManager mLayoutManagerPreparation = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerViewPreparation.setLayoutManager(mLayoutManagerPreparation);
//        recyclerViewPreparation.setItemAnimator(new DefaultItemAnimator());
//        recyclerViewPreparation.setAdapter(mAdapterPreparation);
//
//        recyclerViewComments = (RecyclerView) findViewById(R.id.recyclerComment);
//
//        mAdapterComments = new CommentsAdapter(generateComments(), this);
//        LinearLayoutManager mLayoutManagerComment = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerViewComments.setLayoutManager(mLayoutManagerComment);
//        recyclerViewComments.setItemAnimator(new DefaultItemAnimator());
//        recyclerViewComments.setAdapter(mAdapterComments);


//        final ImageView imageComment = (ImageView) findViewById(R.id.iv_user);
//        Glide.with(this)
//                .load(Uri.parse("https://randomuser.me/api/portraits/women/75.jpg"))
//                .transform(new CircleGlide(this))
//                .into(imageComment);

        final ImageView image = (ImageView) findViewById(R.id.image);
        Glide.with(this).load(Uri.parse("https://images.pexels.com/photos/140831/pexels-photo-140831.jpeg?w=1260&h=750&auto=compress&cs=tinysrgb")).into(image);

    }

//    @Override
//    public void onItemClicked(int position) {
//
//    }
//
//    @Override
//    public boolean onItemLongClicked(int position) {
//        toggleSelection(position);
//        return true;
//    }

//    private void toggleSelection(int position) {
//        mAdapterPreparation.toggleSelection(position);
//        final RelativeLayout rlShare = (RelativeLayout) findViewById(R.id.rl_share);
//        if (position == mAdapterPreparation.getItemCount()-1) {
//
//            Log.d("selected", "toggleSelection: "+position+" "+(mAdapterPreparation.getItemCount()-1));
//            rlShare.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
        menu.findItem(R.id.action_search).setIcon(drawable);
        return true;
    }

//    public List<ItemShopping> generateShopping(){
//        List<ItemShopping> itemList = new ArrayList<>();
//        String name[] = {"butter", "brown", "eggs", "flour", "baking powder", "of salt", "buttermilk", "orange juice"};
//        String pieces[] = {"200g", "200g", "4", "300g", "2tsp", "1 pinch", "100ml", "50ml"};
//        float rating[] = {3, 4, 4, 3, 5, 4, 4, 3};
//
//        for (int i = 0; i<name.length; i++){
//            ItemShopping item = new ItemShopping();
//            item.setPieces(pieces[i]);
//            item.setName(name[i]);
//            itemList.add(item);
//        }
//        return itemList;
//    }
//    public List<ItemComment> generateComments(){
//        List<ItemComment> itemList = new ArrayList<>();
//        String username[] = {"LAURA MAGNAGO"};
//        String date[] = {".27-01-2017"};
//        String comments[] = {"Made this for a BBQ today and it was amazing. Bought 2 Madiera cakes from Tesco and cut them Into wedges. Poured the coffee over the top. And used 75ml of Ameretti instead of masala in the cream. Will be making again next week for a gathering and probably many more times! :)"};
//        String userphoto[] = {"https://randomuser.me/api/portraits/women/20.jpg"};
//        String img1[] = {"https://images.pexels.com/photos/8382/pexels-photo.jpg?h=350&auto=compress&cs=tinysrgb"};
//        String img2[] = {"https://images.pexels.com/photos/134574/pexels-photo-134574.jpeg?h=350&auto=compress&cs=tinysrgb"};
//
//        for (int i = 0; i<username.length; i++){
//            ItemComment comment = new ItemComment();
//            comment.setUsername(username[i]);
//            comment.setUserphoto(userphoto[i]);
//            comment.setDate(date[i]);
//            comment.setComments(comments[i]);
//            comment.setImg1(img1[i]);
//            comment.setImg2(img2[i]);
//            itemList.add(comment);
//        }
//        return itemList;
//    }
//
//    public List<ItemPreparation> generatePreparation(){
//        List<ItemPreparation> itemList = new ArrayList<>();
//        String step[] = {"In a medium saucepan, whisk together egg yolks and sugar until well blended. Whisk in milk and cook over medium heat, stirring constantly, until mixture boils. Boil gently for 1 minute, remove from heat and allow to cool slightly. Cover tightly and chill in refrigerator 1 hour.",
//                "In a medium bowl, beat cream with vanilla until stiff peaks form. Whisk mascarpone into yolk mixture until smooth.",
//                "In a small bowl, combine coffee and rum. Split ladyfingers in half lengthwise and drizzle with coffee mixture.",
//                "Arrange half of soaked ladyfingers in bottom of a 7x11 inch dish. Spread half of mascarpone mixture over ladyfingers, then half of whipped cream over that. Repeat layers and sprinkle with cocoa. Cover and refrigerate 4 to 6 hours, until set."};
//
//        for (int i = 0; i<step.length; i++){
//            ItemPreparation item = new ItemPreparation();
//            item.setStep(step[i]);
//            item.setNumber(String.valueOf(i+1));
//            itemList.add(item);
//        }
//        return itemList;
//    }
}

