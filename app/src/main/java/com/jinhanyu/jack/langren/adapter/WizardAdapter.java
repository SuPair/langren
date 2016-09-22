package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/9.
 */
public class WizardAdapter extends CommonAdapter<UserInfo> implements ActionPerformer{

    private boolean timerCanceled;
    private boolean hasPoisoned;

    public WizardAdapter(Context context, List<UserInfo> data) {
        super(context, data);

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.wizard_item,null);
            viewHolder=new ViewHolder();
            viewHolder.head= (SimpleDraweeView) view.findViewById(R.id.gameWizard_item_head);
            viewHolder.username= (TextView) view.findViewById(R.id.gameWizard_item_name);
            viewHolder.state= (TextView) view.findViewById(R.id.gameWizard_item_state);
            viewHolder.poison= (TextView) view.findViewById(R.id.gameWizard_item_poison);
            viewHolder.type= (Spinner) view.findViewById(R.id.gameWizard_item_marker);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        final UserInfo info=data.get(i);
        viewHolder.head.setImageURI(info.getHead());
        viewHolder.username.setText(info.getUsername());
        if(info.getGameRole().isDead()){
            viewHolder.state.setText(R.string.isDead);
        }else{
            viewHolder.state.setText(R.string.isLiving);
        }

        viewHolder.poison.setText(R.string.wizardPoison);

        viewHolder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                info.getGameRole().setType(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(MainApplication.roomInfo.isHasPoisoned()){
            viewHolder.poison.setEnabled(false);
        }else {
            viewHolder.poison.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timerCanceled) {
                        Toast.makeText(context, "时间已经到啦，等待结果吧", Toast.LENGTH_SHORT).show();
                    }else if(hasPoisoned){
                        Toast.makeText(context, "你已经用过毒了", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("请做出您的选择：");
                        dialog.setMessage("您确定要毒死该玩家吗?");
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewHolder.poison.setEnabled(false);
                                viewHolder.poison.setBackgroundResource(R.drawable.button_clicked);
                                ((ActionPerformer) context).doAction(info.getUserId());
                                hasPoisoned = true;
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }});
        }


        return view;
    }

    @Override
    public void doAction(Object... params) {
         timerCanceled = true;
    }

    class ViewHolder{
        SimpleDraweeView head;
        TextView username,state,poison;
        Spinner type;
    }
}
