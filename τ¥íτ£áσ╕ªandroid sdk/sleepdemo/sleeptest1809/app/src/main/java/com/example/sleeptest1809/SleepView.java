
package com.example.sleeptest1809;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * 睡眠图
 * wyl
 */

public class SleepView extends View {
    //区域颜色属性
    private int white = Color.WHITE;//白色
    private int red = Color.RED;//红色


    private int wakeColor = Color.parseColor("#FC7B3A");//清醒的颜色
    private int shallowColor = Color.parseColor("#76C4E8");//Rem的颜色
    private int deepColor = Color.parseColor("#00CC60");//浅度睡眠的颜色
    private int wakeColorER = Color.parseColor("#C25CFF");//深度睡眠的颜色
    private int textGrayColor = Color.parseColor("#768E96");//文字灰色
    private int bottomColor = Color.parseColor("#013E5D");//底部颜色
    private int backgrondColor = Color.parseColor("#003c5b");//背景的颜色
    //背景颜色
    private int BGzero = Color.parseColor("#70FF0000");
    private int BGone = Color.parseColor("#3B4A53");
    private int BGtwo = Color.parseColor("#185777");
    private int BGthere = Color.parseColor("#045052");
    private int BGfour = Color.parseColor("#70C25CFF");

    private Context context;
    List<List<Sleepfordata>> ALLdata = new ArrayList<>();//数据源
    int duration;//总共有多少分钟睡眠数据


    /* ********************* 内部使用的属性 *********************** */

    private Paint linePaint;//画线的画笔
    private Paint textPaint;//画文字画笔
    private Paint backPaint;//画底部背景画笔
    private Paint backimgshowdow;//画底部背景画笔
    private Paint backimgline;//画底部背景画笔


    private Paint p_indexPaint;

    float yuliustart_endtime;//预留20像素绘制开始和结束的时间
    float yuliu;//预留30像素下部空间
    float getbitmapleft;//下部滑动图标的第一次距离左后的像素点
    float getbitmapleftbg;//下部滑动图标的第一次距离左后的像素点
    float mapleft;//下部滑动图标一版的大小

    float getViewheight;//每一项区域高度是固定的
    float avgtime;//X所需要的分数

    float linewith;//连接线宽度


    public SleepView(Context context) {
        this(context, null);
        this.context = context;
    }

    public SleepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public SleepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();

    }
    public SleepView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }


    @Override
    public void onWindowFocusChanged(boolean hasFoucus) {
        super.onWindowFocusChanged(hasFoucus);
        //View焦点变化
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    float startx;
    float endx;
    float entop;
    float enbottom;
    Bitmap mBitmap;
    Bitmap lineBitmap;
    int myndex = 0;
    float getAnimateValue;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画15.5像素背景
        if (getHeight() > 0&&getWidth()>0) {
            //每一份的高度计算 默认下部颜色高度+预留部分高度+绘制开始和借宿时间高度 平均高度除以7份最终得到每一块的高度
            //getViewheight = (getHeight() - (dip2px(context, 17.5f) + yuliu + yuliustart_endtime)) / 7f;
            getViewheight = ((getHeight() - (dip2px(context, 17.5f) + yuliu + yuliustart_endtime)) -(dip2px(context, 30f)))/5f;
            startx = dip2px(context, 30.5f);//开始的X抽坐标
            //计算区域坐标点
            if (this.ALLdata != null ) {//如果小于10小时的话
                if(this.ALLdata.size() > 0&&ALLdata.size() ==1){
                    avgtime = (Integer.valueOf(getScreenWidth(context) - dip2px(context, 61f)).floatValue()) / duration  ;
                }else {
                    int cc=dip2px(context, 30.5f)*(ALLdata.size()-1);
                    avgtime = ((Integer.valueOf((getScreenWidth(context) - dip2px(context, 61f)-cc)).floatValue())) / duration;
                }
                avgtime = avgtime*getAnimateValue;
            }


            if ( ALLdata== null||this.ALLdata.size()== 0){//绘制无睡眠数据提示
                canvas.save();
                TextPaint tp = new TextPaint();
                tp.setDither(true); // 防抖动
                tp.setAntiAlias(true);// 抗锯齿
                tp.setStrokeWidth(1);//画笔宽度
                tp.setTextAlign(Paint.Align.CENTER);
                tp.setTextSize(dip2px(context, 14f));
                tp.setColor(Color.parseColor("#86878C"));
                tp.setStyle(Paint.Style.FILL);
                String message = "no_sleep";
                StaticLayout myStaticLayout = new StaticLayout(message, tp, (int)tp.measureText(message), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                canvas.translate(getWidth()/2,getHeight()/2);
                myStaticLayout.draw(canvas);
                canvas.restore();
            }else{
                backPaint.setColor(bottomColor);
                canvas.drawRect(0f, getHeight()-dip2px(context, 17.5f) , getWidth(), getWidth(), backPaint);
            }

        }




        if (getWidth() > 0 && getHeight() > 0 && this.ALLdata != null && this.ALLdata.size() > 0) {

            //计算区块坐标
            for (int j = 0; j < ALLdata.size(); j++) {
                for (int i = 0; i < ALLdata.get(j).size(); i++) {
                    int index = ALLdata.get(j).get(i).getIndex();
                    //离床 0 //清醒4 //rem 3 //浅睡 2  深睡 1,
                    switch (index) {
                        case 0:
                            entop = yuliustart_endtime;
                            enbottom = yuliustart_endtime + getViewheight;
                            break;
                        case 4:
                            entop = yuliustart_endtime + getViewheight+dip2px(context, 10f);
                            enbottom = yuliustart_endtime + getViewheight*2+dip2px(context, 10f);
                            break;
                        case 3:
                            entop = yuliustart_endtime + getViewheight*2+dip2px(context, 20f);
                            enbottom = yuliustart_endtime +getViewheight*3+dip2px(context, 20f);
                            break;
                        case 2:
                            entop = yuliustart_endtime + getViewheight*3+dip2px(context, 30f);
                            enbottom = yuliustart_endtime + getViewheight*4+dip2px(context, 30f);
                            break;
                        case 1:
                            entop = yuliustart_endtime + getViewheight*4+dip2px(context, 40f);
                            enbottom = yuliustart_endtime + getViewheight*5+dip2px(context, 40f);
                            break;

                    }
                    if (j == 1||j == 2||j == 3||j == 4) {//最多睡四次
                        if (i == 0) {
                            startx = endx + dip2px(context, 30.5f);
                            endx = startx + (ALLdata.get(j).get(i).getCount()  * avgtime);
                        } else {
                            startx = endx;
                            endx = startx + (ALLdata.get(j).get(i).getCount()* avgtime);
                        }
                    } else {
                        if (i != 0) {
                            startx = endx;
                            endx = endx + (ALLdata.get(j).get(i).getCount()  * avgtime);
                        } else {
                            startx = dip2px(context, 30.5f);
                            endx = startx + (ALLdata.get(j).get(i).getCount()  * avgtime);
                        }
                    }
                    ALLdata.get(j).get(i).setStartX(startx);
                    ALLdata.get(j).get(i).setStartY(entop);
                    ALLdata.get(j).get(i).setStopX(endx);
                    ALLdata.get(j).get(i).setStopY(enbottom);
                    ALLdata.get(j).get(i).setId(myndex);
                    myndex++;
                }
            }




            //划线
            for (int j = 0; j < ALLdata.size(); j++) {
                for (int i = 0; i < ALLdata.get(j).size(); i++) {
                    Sleepfordata data = ALLdata.get(j).get(i);
                    //绘制开始时间
                    if (i == 0) {
                        canvas.save();
                        String message = ALLdata.get(j).get(i).getBegintime() ;
                        // canvas.translate(ALLdata.get(j).get(i).getStartX(), -getTexthight(textPaint,message));
                        canvas.translate(0,0);//-dip2px(context, 15f)-getTexthight(textPaint,message)
                        if(ALLdata.size()==1){
                            textPaint.setTextSize(dip2px(context, 10f));
                        }else{
                            if(isZh(context)){
                                textPaint.setTextSize(dip2px(context, 8f));
                            }else{
                                textPaint.setTextSize(dip2px(context, 6.8f));
                            }

                        }
                        textPaint.setColor(textGrayColor);
                        textPaint.setTextAlign(Paint.Align.LEFT);
                        canvas.drawText(message,ALLdata.get(j).get(i).getStartX(), Integer.valueOf((getTexthight(textPaint,message))).floatValue(),textPaint);

                        Bitmap systolicBP = BitmapFactory.decodeResource(getResources(), R.drawable.sleep);
                        Bitmap systolicBPmBitmap = Bitmap.createScaledBitmap(systolicBP, dip2px(context, 19), dip2px(context, 14), false); //自定义
                        canvas.drawBitmap(systolicBPmBitmap, ALLdata.get(j).get(i).getStartX()+(getTextWith(textPaint,message)/2)-(dip2px(context, 19)/2),getTexthight(textPaint,message)+dip2px(context, 3),null);
                        systolicBP.recycle();
                        systolicBPmBitmap.recycle();
                        canvas.restore();

                    }
                    //绘制结束时间
                    if ((i + 1) == ALLdata.get(j).size()) {
                        canvas.save();

                        //判断开始和结束时间距离是否相交
                        int isLorR=0;
                        if(ALLdata.get(j).get(0).getStartX()+getTextWith(textPaint, ALLdata.get(j).get(0).getBegintime())>=
                                ALLdata.get(j).get(ALLdata.get(j).size()-1).getStopX()-getTextWith(textPaint, ALLdata.get(j).get(0).getBegintime())){
                            isLorR=2;
                        }else{
                            isLorR=0;
                        }
                        String message = ALLdata.get(j).get(ALLdata.get(j).size() - 1).getEndtime();
                        canvas.translate(0,0);
                        if(ALLdata.size()==1){
                            textPaint.setTextSize(dip2px(context, 10f));
                        }else{
                            if(isZh(context)){
                                textPaint.setTextSize(dip2px(context, 8f));
                            }else{
                                textPaint.setTextSize(dip2px(context, 6.8f));
                            }
                        }
                        BlurMaskFilter maskFilter = new BlurMaskFilter(0.5f, BlurMaskFilter.Blur.SOLID);
                        textPaint.setMaskFilter(maskFilter);
                        textPaint.setColor(textGrayColor);
                        textPaint.setTextAlign(isLorR==2? Paint.Align.LEFT: Paint.Align.RIGHT);
                        canvas.drawText(message,ALLdata.get(j).get(ALLdata.get(j).size() - 1).getStopX() , Integer.valueOf((getTexthight(textPaint,message))).floatValue(),textPaint);


                        Bitmap systolicBP = BitmapFactory.decodeResource(getResources(), R.drawable.getup);
                        Bitmap systolicBPmBitmap = Bitmap.createScaledBitmap(systolicBP, dip2px(context, 19), dip2px(context, 14), false); //自定义
                        float value=isLorR==2?ALLdata.get(j).get(ALLdata.get(j).size() - 1).getStopX()+dip2px(context, 19f):ALLdata.get(j).get(ALLdata.get(j).size() - 1).getStopX();
                        canvas.drawBitmap(systolicBPmBitmap, value-(getTextWith(textPaint,message)),getTexthight(textPaint,message)+dip2px(context, 3),null);

                        systolicBP.recycle();
                        systolicBPmBitmap.recycle();

                        canvas.restore();
                    }
                    if (i + 1 != ALLdata.get(j).size()) {
                        int index = ALLdata.get(j).get(i).getIndex();
                        int indexer = ALLdata.get(j).get(i + 1).getIndex();
                        drawNext(canvas, indexer, index, ALLdata.get(j).get(i).getStopX(), ALLdata.get(j).get(i).getStopX());

                    }
                }
            }

            //画当前的背景
            if (ALLdata.size() > 0) {
                //判断当前在哪个下标
                for (int j = 0; j < ALLdata.size(); j++) {
                    for (int i = 0; i < ALLdata.get(j).size(); i++) {
                        if (ALLdata.get(j).get(i).getStartX() <= getbitmapleftbg && ALLdata.get(j).get(i).getStopX() >= getbitmapleftbg) {
                            switch (ALLdata.get(j).get(i).getIndex()) {
                                case 0://离床
                                    backimgshowdow.setColor(BGzero);
                                    lineBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.bg_linezero));
                                    break;
                                case 4://清醒4
                                    //深睡 1, //浅睡 2 //清醒4 //rem 3
                                    backimgshowdow.setColor(BGone);
                                    lineBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.bg_line1));
                                    break;
                                case 3://rem 3
                                    backimgshowdow.setColor(BGtwo);
                                    lineBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.bg_line4));
                                    break;
                                case 2://浅睡 2
                                    backimgshowdow.setColor(BGthere);
                                    lineBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.bg_line3));
                                    break;
                                case 1://深睡
                                    backimgshowdow.setColor(BGfour);
                                    lineBitmap = drawableToBitmap(getResources().getDrawable(R.drawable.bg_line2));
                                    break;
                            }
                            //画阴影的话 ，从区域的一半开始画,左右减去线的一半
                            canvas.drawRect(ALLdata.get(j).get(i).getStartX()+linewith, ALLdata.get(j).get(i).getStopY()-getViewheight/2f, ALLdata.get(j).get(i).getStopX()-linewith/2f, getHeight()-dip2px(context, 17.5f), backimgshowdow);
                            //画线啊
                            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                            canvas.drawBitmap(Bitmap.createScaledBitmap(lineBitmap, dip2px(context, 0.5f), dip2px(context, 104f), false),
                                    getbitmapleftbg, (ALLdata.get(j).get(i).getStopY()) - dip2px(context, 53f), backimgline);

                        } else {
                            backimgshowdow.setShader(null);
                            backimgline.setShader(null);
                        }
                    }
                }
            }


            //绘制区块
            if (ALLdata.size() > 0) {
                for (int j = 0; j < ALLdata.size(); j++) {
                    for (int i = 0; i < ALLdata.get(j).size(); i++) {
                        int index = ALLdata.get(j).get(i).getIndex();
                        linePaint.setShader(null);

                        //离床 0 //清醒4 //rem 3 //浅睡 2  深睡 1,
                        switch (index) {  //判断当前在哪个下标
                            case 0:
                                linePaint.setColor(red);
                                break;
                            case 4:
                                linePaint.setColor(wakeColor);
                                break;
                            case 3:
                                linePaint.setColor(shallowColor);
                                break;
                            case 2:
                                linePaint.setColor(deepColor);
                                break;
                            case 1:
                                linePaint.setColor(wakeColorER);
                                break;
                        }
                        //区块的话也需要加连接线的宽度，不加，不好看-linewith/2f
                        RectF rect = new RectF( ALLdata.get(j).get(i).getStartX()-linewith,ALLdata.get(j).get(i).getStartY() , ALLdata.get(j).get(i).getStopX()+linewith, ALLdata.get(j).get(i).getStopY());
                        canvas.drawRoundRect(rect, dip2px(context, 4f), dip2px(context, 4f), linePaint);
                        //绘制 p _idex
                        if(null!=ALLdata.get(j).get(i).getP_INDEX()){
                            p_indexPaint.setTextSize(dip2px(context, 8f));
                            p_indexPaint.setColor(white);
                           canvas.drawText(ALLdata.get(j).get(i).getP_INDEX(),
                                 ALLdata.get(j).get(i).getStartX(),
                                   yuliustart_endtime- getTexthight(p_indexPaint,ALLdata.get(j).get(i).getP_INDEX())/2,p_indexPaint);
                        }

                    }}
            }


        }



        //画图片
        if (getHeight() > 0) {
            if ( ALLdata!= null&&this.ALLdata.size()>0){
                Bitmap hartRate = BitmapFactory.decodeResource(getResources(), R.drawable.seekbar);
                mBitmap = Bitmap.createScaledBitmap(hartRate, dip2px(context, 61f), dip2px(context, 31f), false); //自定义
                // 画出原图像
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                canvas.drawBitmap(mBitmap, getbitmapleftbg - mapleft, getHeight() - dip2px(context, 30.5f), backPaint);
            }
        }
        super.onDraw(canvas);
    }


    /**
     * 初始化
     */

    private void init() {
        //初始化画笔
        linePaint = new Paint();
        linePaint.setAntiAlias(true); // 抗锯齿
        linePaint.setDither(true); // 防抖动
        //linePaint.setStrokeCap(Paint.Cap.ROUND); // 把每段圆弧改成圆角的
        linePaint.setStyle(Paint.Style.FILL);
        //文字画笔
        textPaint = new Paint();
        textPaint.setDither(true); // 防抖动
        textPaint.setAntiAlias(true);// 抗锯齿
        textPaint.setStrokeWidth(1);//画笔宽度
        textPaint.setTextSize(dip2px(context, 9f));
        textPaint.setColor(textGrayColor);
        textPaint.setTextAlign(Paint.Align.LEFT);

        backPaint = new Paint();
        backPaint.setDither(true); // 防抖动
        backPaint.setAntiAlias(true);// 抗锯齿
        backPaint.setStyle(Paint.Style.FILL);

        //阴影绘制
        backimgshowdow = new Paint();
        backimgshowdow.setDither(true); // 防抖动
        backimgshowdow.setAntiAlias(true);// 抗锯齿
        backimgshowdow.setStyle(Paint.Style.FILL);


        backimgline = new Paint();
        backimgline.setDither(true); // 防抖动
        backimgline.setAntiAlias(true);// 抗锯齿


        p_indexPaint = new Paint();
        p_indexPaint.setDither(true); // 防抖动
        p_indexPaint.setAntiAlias(true);// 抗锯齿


        yuliu = dip2px(context, 25f);//预留30像素下部空间
        getbitmapleft = getScreenWidth(context) - dip2px(context, 61f);
        getbitmapleftbg = getScreenWidth(context) - dip2px(context, 30.5f);
        mapleft = dip2px(context, 30.5f);
        yuliustart_endtime = dip2px(context, 35f);//绘制开始结束时间预留距离
        linewith=dip2px(context, 0.43f);//连接线宽度

    }


    /**
     * 绘制两点间隔线
     */
    private void drawNext(Canvas canvas, int indexer, int index, Float Left, Float right) {
        int endColor = wakeColor;
        Float top = 0f, bottom = 0f;
        switch (index) {
            case 0://离床 0 //清醒4 //rem 3 //浅睡 2  深睡 1,
                switch (indexer) {
                    case 4:
                        top = yuliustart_endtime +(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*2+dip2px(context, 10f)-(getViewheight/2);
                        endColor = red;
                        break;
                    case 3:
                        top = yuliustart_endtime + getViewheight+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight * 3f+dip2px(context, 20f)-(getViewheight/2);
                        endColor = red;
                        break;
                    case 2:
                        top = yuliustart_endtime + getViewheight+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight * 4f+dip2px(context, 30f)-(getViewheight/2);
                        endColor = red;
                        break;
                    case 1:
                        top = yuliustart_endtime + getViewheight+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight * 5f+dip2px(context, 40f)-(getViewheight/2);
                        endColor = red;
                        break;
                }

            case 4://离床 0 //清醒4 //rem 3 //浅睡 2  深睡 1,
                switch (indexer) {
                    case 3://1
                        top = yuliustart_endtime + getViewheight+dip2px(context, 10f)+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*3+dip2px(context, 20f) -(getViewheight/2);
                        endColor = wakeColor;
                        break;
                    case 2://1
                        top = yuliustart_endtime + getViewheight+dip2px(context, 10f) +(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*4+dip2px(context, 30f)-(getViewheight/2);
                        endColor = wakeColor;
                        break;
                    case 1://1
                        top = yuliustart_endtime + getViewheight+dip2px(context, 10f)+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*4+dip2px(context, 40f) -(getViewheight/2);
                        endColor = wakeColor;
                        break;
                    case 0://1
                        top = yuliustart_endtime +(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*2+dip2px(context, 10f)-(getViewheight/2);
                        endColor = red;
                        break;
                }
                break;
            case 3:// //离床 0 //清醒4 //rem 3 //浅睡 2  深睡 1,
                switch (indexer) {
                    case 4://1
                        top = yuliustart_endtime + getViewheight+dip2px(context, 10f)+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*3+dip2px(context, 20f) -(getViewheight/2);
                        endColor = wakeColor;
                        break;
                    case 2://1
                        top = yuliustart_endtime + getViewheight*2+dip2px(context, 20f)+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*3+dip2px(context, 30f)+(getViewheight/2);
                        endColor = shallowColor;
                        break;
                    case 1://1
                        top = yuliustart_endtime + getViewheight*2+dip2px(context, 20f)+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*5+dip2px(context, 40f)-(getViewheight/2);
                        endColor = shallowColor;
                        break;
                    case 0://ok
                        top = yuliustart_endtime + getViewheight+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight * 3f+dip2px(context, 20f)-(getViewheight/2);
                        endColor = red;
                        break;
                }
                break;
            case 2://  //离床 0 //清醒4 //rem 3 //浅睡 2  深睡 1,
                switch (indexer) {
                    case 4://1
                        top = yuliustart_endtime + getViewheight+dip2px(context, 10f) +(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*4+dip2px(context, 30f)-(getViewheight/2);
                        endColor = wakeColor;
                        break;
                    case 3://1s
                        top = yuliustart_endtime + getViewheight*2+dip2px(context, 20f)+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*3+dip2px(context, 30f)+(getViewheight/2);
                        endColor = shallowColor;
                        break;
                    case 1://1
                        top = yuliustart_endtime + getViewheight*3+dip2px(context, 30f) +(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*5+dip2px(context, 40f) -(getViewheight/2);

                        endColor = deepColor;
                        break;
                    case 0:
                        top = yuliustart_endtime + getViewheight+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight * 4f+dip2px(context, 30f)-(getViewheight/2);
                        endColor = red;
                        break;
                }
                break;
            case 1: //离床 0 //清醒4 //rem 3 //浅睡 2  深睡 1,
                switch (indexer) {
                    case 4://1
                        top = yuliustart_endtime + getViewheight+dip2px(context, 10f)+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*4+dip2px(context, 40f) -(getViewheight/2);
                        endColor = wakeColor;
                        break;
                    case 3://1
                        top = yuliustart_endtime + getViewheight*2+dip2px(context, 20f)+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*5+dip2px(context, 40f)-(getViewheight/2);
                        endColor = shallowColor;
                        break;
                    case 2://1
                        top = yuliustart_endtime + getViewheight*3+dip2px(context, 30f) +(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight*5+dip2px(context, 40f) -(getViewheight/2);
                        endColor = deepColor;
                        break;
                    case 0://ok
                        top = yuliustart_endtime + getViewheight+(getViewheight/2);
                        bottom = yuliustart_endtime + getViewheight * 5f+dip2px(context, 40f)-(getViewheight/2);
                        endColor = red;
                        break;
                }
                break;

        }
        textPaint.setColor(endColor);
        //设置竖线宽度
        textPaint.setStrokeWidth(linewith);
        canvas.drawLine(Left, top , right, bottom, textPaint);
    }


    /**
     * 设置数据源
     */
    ObjectAnimator objectAnimator=null;
    @SuppressLint("ObjectAnimatorBinding")
    public void setDataSource(List<List<Sleepfordata>> timeArray, Xylistener xylistener, int myduration) {
        this.Xylistener = xylistener;
        this.duration = myduration;
        getbitmapleftbg =  dip2px(context, 25f);
        if (timeArray != null) {
            this.ALLdata = timeArray;
            if(null==objectAnimator){
                objectAnimator = ObjectAnimator.ofFloat(this, "phaseX", 0f, 1f);
                objectAnimator.setDuration(600);
                objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        getAnimateValue=(float) animation.getAnimatedValue();
                        invalidate();
                    }
                });
            }
            if(null!=objectAnimator){
                objectAnimator.start();
            }

        }
    }


    /**
     * 清空画布
     */

    public void clearView() {
        this.ALLdata = null;
        p_indexPaint.setShader(null);
        linePaint.setShader(null);
        textPaint.setShader(null);//画文字画笔
        backimgshowdow.setShader(null);//画底部背景画笔
        backimgline.setShader(null);//画底部背景画笔
        invalidate();
    }

    boolean isbaohan=false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //每次回调onTouchEvent的时候，我们获取触摸点的代码
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (null != mBitmap) {
                    getbitmapleftbg = x;
                    invalidate();
                    isbaohan=false;
                    if (ALLdata != null && ALLdata.size() > 0) {
                        for (int j = 0; j < ALLdata.size(); j++) {
                            for (int i = 0; i < ALLdata.get(j).size(); i++) {
                                if (ALLdata.get(j).get(i).getStartX() <= x && x <= ALLdata.get(j).get(i).getStopX()) {
                                    if (null != Xylistener) {
                                        Xylistener.ReadEcgData(ALLdata.get(j).get(i));
                                    }
                                    isbaohan=true;
                                    break;
                                }
                            }
                        }
                    }
                    //当前X 在所有数据中都不包含
                    if(!isbaohan){
                        if (null != Xylistener) {
                            Xylistener.ReadEcgData(null);
                        }
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_DOWN://获取屏幕上点击的坐标
                break;
            case MotionEvent.ACTION_UP://点击抬起后，回复初始位置。
                break;
        }
        return true;
    }


    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return
     */

    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public interface Xylistener {
        void ReadEcgData(Sleepfordata X);
    }

    public static Xylistener Xylistener = null;


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 传入文字计算文字宽度
     * @param paint
     * @param str
     * @return
     */
    private static  int getTextWith(Paint paint, String str){
        paint.measureText(str);
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.width();
    }

    private static  int getTexthight(Paint paint, String str){
        paint.measureText(str);
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.height();
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}

