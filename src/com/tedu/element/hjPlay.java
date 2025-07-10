package com.tedu.element;

import com.tedu.manager.GameLoad;

import javax.swing.ImageIcon;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class hjPlay extends ElementObj{
    /**
     * 移动属性：双属性 上下和左右
     */
    private boolean left=false; //左
    private boolean up=false;   //上
    private boolean right=false;//右
    private boolean down=false; //下

    private ImageIcon icon2; //新增一个下半身图像
    private String fx="right";//当前主角方向状态
    private boolean pkType=false;//攻击状态
    private int jumpVelocity = 0; // 跳跃速度
    private int gravity = 1; // 重力加速度
    private int jumpPower = 15; // 跳跃初始速度
    private int groundLevel = 200; // 地面高度

    private int frameIndex = 0;      // 当前动画帧索引
    private int frameCount = 0;      // 帧计数器（控制动画速度）
    private static final int FRAME_DELAY = 6; // 帧延迟（数值越大动画越慢）
    private int attackFrameIndex = 0; // 攻击动画帧索引

    private int squatFrameIndex=0;
    private int jumpFrameIndex=0;
    private Map<String, List<ImageIcon>> animationMap = new HashMap<>(); // 存储下半身动画序列
    private Map<String, List<ImageIcon>> animationMap2 = new HashMap<>(); // 存储上半身动画序列


    @Override
    public void showElement(Graphics g) {
        if(down){
            g.drawImage(this.getIcon().getImage(),
                this.getX(), this.getY()+10,
                this.getIcon().getIconWidth(), this.getIcon().getIconHeight(), null);
        }else if(!down){
            g.drawImage(this.getIcon().getImage(),
                    this.getX(), this.getY(),
                    this.getIcon().getIconWidth(), this.getIcon().getIconHeight(), null);
        }

        if(this.fx=="right"){
            g.drawImage(this.getIcon2().getImage(),
                    this.getX()+10, this.getY()+this.getIcon().getIconHeight()-10,
                    this.getIcon2().getIconWidth(), this.getIcon2().getIconHeight(),null);
        }else if(up){
            g.drawImage(this.getIcon2().getImage(),
                    this.getX()+10, this.getY()+this.getIcon().getIconHeight()-10,
                    this.getIcon2().getIconWidth(), this.getIcon2().getIconHeight(),null);
        }else{
            g.drawImage(this.getIcon2().getImage(),
                    this.getX()+this.getIcon().getIconWidth()/2, this.getY()+this.getIcon().getIconHeight()-10,
                    this.getIcon2().getIconWidth(), this.getIcon2().getIconHeight(),null);
        }

    }

    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        this.setX(Integer.parseInt(split[0]));
        this.setY(Integer.parseInt(split[1]));
        ImageIcon icon0 = GameLoad.imgMap2.get(split[2]);
        ImageIcon icon1 = GameLoad.imgMap4.get(split[3]);
        this.setW(icon0.getIconWidth());
        this.setH(icon0.getIconHeight());
        this.setIcon(icon0);
        this.setIcon2(icon1);
        loadAnimations();
        loadAttackAnimations();
        loadSquatAnimations();
        loadJumpAnimations();
        return this;
    }

    @Override   // 注解 通过反射机制，为类或者方法或者属性 添加的注释(相当于身份证判定)
    public void keyClick(boolean bl,int key) {
//		System.out.println("测试："+key);
        if(bl) {//按下
            switch(key) {  //怎么优化 大家中午思考;加 监听会持续触发；有没办法触发一次
                case 65:
                    this.right=false;this.left=true; this.fx="left"; break;
                case 87:
                    if (!this.up) { // 防止二次跳跃
                        this.up = true;
                        jumpVelocity = jumpPower; // 设置初始跳跃速度
                        jumpFrameIndex = 0; // 重置跳跃动画
                    }
                    break;
                case 68:
                    this.left=false; this.right=true;this.fx="right";break;
                case 83:
                    this.down=true;
                    this.squatFrameIndex=0;
                    break;
                case 74:
                    this.pkType=true;break;//开启攻击状态
            }
        }else {
            switch(key) {
                case 65: this.left=false;  break;
                case 68: this.right=false; break;
                case 83: this.down=false; break;
            }
            //a a
        }
    }

    @Override
    public void move() {
        if (this.left && this.getX()>0) {
            this.setX(this.getX() - 3);
        }
        if (this.up  && this.getY()>50 ) {
            this.setY(this.getY() - 2);
        }
        if (this.right && this.getX()<900-this.getW()) {  //坐标的跳转由大家来完成
            this.setX(this.getX() + 3);
        }
        if(this.up){
            // 更新Y坐标
            int newY = this.getY() - jumpVelocity;
            this.setY(newY);

            // 应用重力
            jumpVelocity -= gravity;

            // 检查是否落地
            if (newY >= groundLevel) {
                this.setY(groundLevel);
                up = false; // 重置跳跃状态
            }
        }

    }

    @Override
    protected void updateImage(){
//        System.out.println("被调用");
        // 更新帧计数器
        frameCount++;
        if (frameCount < FRAME_DELAY) return;
        frameCount = 0;


        // 获取当前动画序列
        String animationKey = "walk_"+fx;
        List<ImageIcon> sequence = animationMap.get(animationKey);
        String squatKey = "squat_" + this.fx; // 使用当前方向
        List<ImageIcon> squatSequence = animationMap.get(squatKey);
        String jumpKey="jump_"+this.fx;
        List<ImageIcon> jumpSequence1=animationMap.get(jumpKey);
        List<ImageIcon> jumpSequence2=animationMap2.get(jumpKey);
//        System.out.println(jumpSequence2);
        if(up){
            jumpFrameIndex = (jumpFrameIndex + 1) % jumpSequence1.size();
            // 更新上半身跳跃动画
            if (jumpSequence2 != null && !jumpSequence2.isEmpty()) {
                this.setIcon(jumpSequence2.get(jumpFrameIndex));
            }
            // 更新下半身跳跃动画
            if (jumpSequence1 != null && !jumpSequence1.isEmpty()) {
                this.setIcon2(jumpSequence1.get(jumpFrameIndex));
            }
        }else if (!this.up&&this.down&&(this.left||this.right)&&squatSequence != null && !squatSequence.isEmpty()) {
            squatFrameIndex = (squatFrameIndex + 1) % squatSequence.size();
            this.setIcon2(squatSequence.get(squatFrameIndex));
            if(fx=="right"){
                ImageIcon icon=GameLoad.imgMap2.get("attack1");
                this.setIcon(icon);
            }else{
                ImageIcon icon=GameLoad.imgMap.get("attack1");
                this.setIcon(icon);
            }
        }else if (!this.down&&(this.left||this.right)&&sequence != null && !sequence.isEmpty()) {
            // 更新下半身图像
            frameIndex = (frameIndex + 1) % sequence.size();

            this.setIcon2(sequence.get(frameIndex));
            if(fx=="right"){
                ImageIcon icon=GameLoad.imgMap2.get("attack1");
                this.setIcon(icon);
            }else{
                ImageIcon icon=GameLoad.imgMap.get("attack1");
                this.setIcon(icon);
            }
//            System.out.println("已设置");
        }else if(this.down){//不进行操作时的人物状态
            if(fx=="left") {
                ImageIcon icon=GameLoad.imgMap3.get("squat_stand");
                this.setIcon2(icon);
                ImageIcon icon2=GameLoad.imgMap.get("attack1");
                this.setIcon(icon2);
            } else {
                ImageIcon icon=GameLoad.imgMap4.get("squat_stand");
                this.setIcon2(icon);
                ImageIcon icon2=GameLoad.imgMap2.get("attack1");
                this.setIcon(icon2);
            }
        }else{if(fx=="left") {
                ImageIcon icon=GameLoad.imgMap3.get("stand");
                this.setIcon2(icon);
                ImageIcon icon2=GameLoad.imgMap.get("attack1");
                this.setIcon(icon2);
            } else {
                ImageIcon icon=GameLoad.imgMap4.get("stand");
                this.setIcon2(icon);
                ImageIcon icon2=GameLoad.imgMap2.get("attack1");
                this.setIcon(icon2);
            }
        }
        //更新上半身攻击动画
        String attackKey = "attack_" + this.fx; // 使用当前方向
        List<ImageIcon> attackSequence = animationMap2.get(attackKey);
        if(!up&&pkType==true&&attackSequence != null && !attackSequence.isEmpty()) {
            attackFrameIndex = (attackFrameIndex + 1) % attackSequence.size();
            // 更新攻击动画帧
            if (attackFrameIndex < attackSequence.size()) {
                this.setIcon(attackSequence.get(attackFrameIndex));
                attackFrameIndex++;
            }
            if(attackFrameIndex==attackSequence.size()) pkType=false;
        }

    }

    private void loadAnimations() {
        // 加载向右下半身行走动画
        List<ImageIcon> walkRightDown = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            String key = "walk" + i;
            ImageIcon icon = GameLoad.imgMap4.get(key);
            if (icon != null) walkRightDown.add(icon);
        }
        animationMap.put("walk_right", walkRightDown);
//        System.out.println(animationMap.keySet());

        //加载向左下半身行走动画
        List<ImageIcon> walkLeftDown = new ArrayList<>();//加载向左下半年身行走动作
        for (int i = 1; i <= 9; i++) {
            String key = "walk" + i;
            ImageIcon icon = GameLoad.imgMap3.get(key);
            if (icon != null) walkLeftDown.add(icon);
        }
        animationMap.put("walk_left", walkLeftDown);

    }
    private void loadAttackAnimations() {//加载攻击动画
        // 向右攻击动画
        List<ImageIcon> attackRight = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String key = "attack" + i;
            ImageIcon icon = GameLoad.imgMap2.get(key);
            if (icon != null) attackRight.add(icon);
        }
        animationMap2.put("attack_right", attackRight);

        // 向左攻击动画
        List<ImageIcon> attackLeft = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String key = "attack" + i;
            ImageIcon icon = GameLoad.imgMap.get(key);
            if (icon != null) attackLeft.add(icon);
        }
        animationMap2.put("attack_left", attackLeft);
    }

    private void loadSquatAnimations(){
        // 加载向右下蹲伏动画
        List<ImageIcon> squatRightDown = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String key = "squat" + i;
            ImageIcon icon = GameLoad.imgMap4.get(key);
            if (icon != null) squatRightDown.add(icon);
        }
        animationMap.put("squat_right", squatRightDown);

        List<ImageIcon> squatLeftDown = new ArrayList<>();//加载向左下半年身行走动作
        for (int i = 1; i <= 5; i++) {
            String key = "squat" + i;
            ImageIcon icon = GameLoad.imgMap3.get(key);
//            System.out.println(icon);
            if (icon != null) squatLeftDown.add(icon);
        }
        animationMap.put("squat_left",squatLeftDown);
    }

    private void loadJumpAnimations(){
        //加载向右跳跃下半身动画
        List<ImageIcon> jumpRightDown=new ArrayList<>();
        for(int i=0;i<=7;i++){
            String key = "jump"+i;
            ImageIcon icon=GameLoad.imgMap4.get(key);
            if(icon!=null)  jumpRightDown.add(icon);
        }
        animationMap.put("jump_right",jumpRightDown);
        //加载向左跳跃下半身动画
        List<ImageIcon> jumpLeftDown=new ArrayList<>();
        for(int i=0;i<=7;i++){
            String key = "jump"+i;
            ImageIcon icon=GameLoad.imgMap3.get(key);
            if(icon!=null)  jumpLeftDown.add(icon);
        }
        animationMap.put("jump_left",jumpLeftDown);

        List<ImageIcon> jumpLeftUp=new ArrayList<>();
        for(int i=0;i<=7;i++){
            String key = "jump"+i;
            ImageIcon icon=GameLoad.imgMap.get(key);
            if(icon!=null)  jumpLeftUp.add(icon);
        }
        animationMap2.put("jump_left",jumpLeftUp);

        List<ImageIcon> jumpRightUp=new ArrayList<>();
        for(int i=0;i<=7;i++){
            String key = "jump"+i;
            ImageIcon icon=GameLoad.imgMap2.get(key);
            if(icon!=null)  jumpRightUp.add(icon);
        }
        animationMap2.put("jump_right",jumpRightUp);
    }

    public ImageIcon getIcon2() {
        return icon2;
    }

    public void setIcon2(ImageIcon icon2) {
        this.icon2 = icon2;
    }
}
