package com.jinhanyu.jack.langren.ui;

import com.jinhanyu.jack.langren.R;

public class VoteActivity extends CommonActivity {

    private int type = -1;

    @Override
    protected void prepareViews() {
        type = getIntent().getIntExtra("type",-1);
        setContentView(R.layout.vote);
    }

    @Override
    protected void prepareSocket() {

    }

    @Override
    protected void unbindSocket() {

    }
}
