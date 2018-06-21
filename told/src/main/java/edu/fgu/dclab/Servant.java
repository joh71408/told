package edu.fgu.dclab;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Servant implements Runnable {
    private ObjectOutputStream out = null;
    private String source = null;

    private Socket socket = null;

    private ChatRoom room = null;

    private int money=1000;//金錢
    private int catlove=50;//好感度
    private int catcandy=0;
    private int cattoy=0;
    private int catgreen=0;

    private String menu="\n＜主餐＞\n" +
            " 白醬海鮮義大利麵 $200 \n" +
            " 白酒蛤蠣義大利麵 $180 \n" +
            " 青醬海鮮義大利麵 $200 \n" +
            " 茄汁海鮮義大利麵 $200\n" +
            "＜輕食＞\n" +
            " 鮪魚三明治       $100\n" +
            " 燒肉三明治       $120\n"+
            "＜飲料＞ \n" +
            " 貓咪特調咖啡     $100 \n " +
            " 黑貓咖啡(美式)   $90 \n" +
            " 水果茶           $120\n " +
            "＜貓用相關用品＞\n" +
            " 貓零食           $40 \n" +
            " 逗貓棒           $120 \n" +
            " 貓薄荷           $200\n" +
            "----------------------------";
    private String help="\n---------------------\n"+
            " menu 菜單 \n help 幫助 \n order+餐點 點餐 \n money 錢包 \n love 貓咪好感度(一開始固定50) \n useforcat 貓咪用品"+
            "\n---------------------";
    private String cat1="麥可";
    private String cat2="黑仔";
    private String cat3="Coffee";
    private String catnow=null;

    private Random cat=new Random();

    private Timer uptime=new Timer();
    private Timer cattime=new Timer();
    private Timer waittime=new Timer();

    private boolean catten=false;
    private boolean ordertime=false;
    private boolean playtime=false;

    public static boolean bad1=false;
    public static boolean bad2=false;


    public Servant(Socket socket, ChatRoom room) {
        this.room = room;
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream(
                this.socket.getOutputStream()
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        greet();
    }

    public void process(Message message) {
        switch (message.getType()) {
            case Message.ROOM_STATE:
                this.write(message);
                break;

            case Message.CHAT:
                if ("menu".equals(((ChatMessage) message).MESSAGE)) {
                    this.write(new ChatMessage("菜單", MessageFormat.format("{0}", menu.toString())));
                }
                //-------------------------------------------
                else if("help".equals(((ChatMessage) message).MESSAGE)||"Help".equals(((ChatMessage) message).MESSAGE))
                {
                    this.write(new ChatMessage("指令", MessageFormat.format("{0}", help.toString())));
                }
                //-----------------------------------
                else if("order+白醬海鮮義大利麵".equals(((ChatMessage) message).MESSAGE)||"order+青醬海鮮義大利麵".equals(((ChatMessage) message).MESSAGE)
                        ||"order+茄汁海鮮義大利麵".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-200;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","好的，稍候為您送上。")));
                    dinnertime();
                }
                else if("order+白酒蛤蠣義大利麵".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-180;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","好的，稍候為您送上。")));
                    dinnertime();
                }
                else if("order+燒肉三明治".equals(((ChatMessage) message).MESSAGE)||"order+水果茶".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-120;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","好的，稍候為您送上。")));
                    dinnertime();
                }
                else if("order+鮪魚三明治".equals(((ChatMessage) message).MESSAGE)||"order+貓咪特調咖啡".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-100;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","好的，稍候為您送上。")));
                    dinnertime();
                }
                else if("order+黑貓咖啡(美式)".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-90;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","好的，稍候為您送上。")));
                    dinnertime();

                }
                else if("order+貓零食".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-40;
                    catcandy=catcandy+1;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","這是您點的貓零食。")));
                }
                else if("order+逗貓棒".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-120;
                    cattoy=cattoy+1;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","這是您要的逗貓棒。")));
                }
                else if("order+貓薄荷".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-120;
                    catgreen=catgreen+1;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","這是您點的貓薄荷。")));
                }
                //-----------------------------------------------------------
                else if("money".equals(((ChatMessage) message).MESSAGE)||"Money".equals(((ChatMessage) message).MESSAGE))
                {
                    this.write(new ChatMessage("錢包", MessageFormat.format("{0}", "你身上還有"+money+"元")));
                }
                //----------------------------------------------------------
                else if("love".equals(((ChatMessage) message).MESSAGE)||"Love".equals(((ChatMessage) message).MESSAGE))
                {
                    if(catlove<0)
                    {
                        this.write(new ChatMessage("貓咪好感度", MessageFormat.format("{0}", "\n---------------------\n" +
                                "目前貓咪們對您的好感度為0"+"\n---------------------")));
                    }
                    else if (catlove>100)
                    {
                        this.write(new ChatMessage("貓咪好感度", MessageFormat.format("{0}", "\n---------------------\n"+
                                "目前貓咪們對您的好感度為100"+"\n---------------------")));
                    }
                    else
                    {
                        this.write(new ChatMessage("貓咪好感度", MessageFormat.format("{0}", "\n---------------------\n"+
                                "目前貓咪們對您的好感度為"+catlove+"\n---------------------")));
                    }

                }
                else if("useforcat".equals(((ChatMessage) message).MESSAGE))
                {
                    this.write(new ChatMessage("道具", MessageFormat.format("{0}", "\n---------------------"+
                            "貓零食:"+catcandy+"包"+"\n逗貓棒:"+cattoy+"個"+"\n貓薄荷:"+catgreen+"個"+"\n---------------------")));
                }
                //----------------------------------------------------------
                else if("play+1".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    Random nb=new Random();
                    int i=nb.nextInt(2)+1;
                    if(catlove<30&&i==1)
                    {
                        this.write(new ChatMessage(catnow, MessageFormat.format("{0}",
                                "喵嘶~~~~~("+catnow+"咬了您的手，但它似乎稍微消氣了一點(恢復了點好感度))")));
                        this.room.multicast(new ChatMessage(
                                this.source,
                                MessageFormat.format("{0}", "阿阿")
                        ));
                        catlove=catlove+5;
                    }
                    else
                    {
                        catlove=catlove+8;
                        this.write(new ChatMessage(catnow, MessageFormat.format("{0}", "喵~~("+catnow+"覺得舒服)")));
                    }
                }
                else if("play+2".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    Random nb=new Random();
                    int i=nb.nextInt(2)+1;
                    if(catlove<30&&i==1)
                    {
                        write(new ChatMessage(catnow, "喵嘶~~~~~("+catnow+"伸出爪子，抓傷了您的手，但它似乎稍微消氣了一點(恢復了點好感度))"));
                        this.room.multicast(new ChatMessage(this.source, MessageFormat.format("{0}", "阿!好痛!")));
                        catlove=catlove+5;
                    }
                    else
                    {
                        catlove=catlove+12;
                        this.write(new ChatMessage(catnow, MessageFormat.format("{0}", "喵喵嗚嗚~~("+catnow+"開心!!)")));
                    }
                }
                else if("play+3".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    Random nb=new Random();
                    int i=nb.nextInt(2)+1;
                    if(catlove<30&&i==1)
                    {
                        catlove=catlove+5;
                        write(new ChatMessage(catnow, "喵嘶~~~~~("+catnow+"咬了您的手，但它似乎稍微消氣了一點(恢復了點好感度))"));
                        this.room.multicast(new ChatMessage(this.source, MessageFormat.format("{0}", "阿!好痛!")));
                    }
                    else
                    {
                        catlove=catlove+20;
                        this.write(new ChatMessage(catnow, MessageFormat.format("{0}", "喵嗚嗚~喵喵~("+catnow+"開心!!)")));
                    }
                }
                else if("play+4".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    catlove=catlove-12;
                    this.write(new ChatMessage(catnow, MessageFormat.format("{0}", "喵嘶嘶~~("+catnow+"不開心)")));
                }
                else if("play+5".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    catlove=catlove-20;
                    this.write(new ChatMessage(catnow, MessageFormat.format("{0}", "嘶嘶喵喵喵~~("+catnow+"生氣了)")));
                }
                else if("use+1".equals(((ChatMessage) message).MESSAGE)&&catten==true&&catcandy>0)
                {
                    catcandy=catcandy-1;
                    catlove=catlove+4;
                    this.write(new ChatMessage(catnow, MessageFormat.format("{0}","喵~(好感度增加了一點點)")));
                }
                else if("use+2".equals(((ChatMessage) message).MESSAGE)&&catten==true&&cattoy>0)
                {
                    cattoy=cattoy-1;
                    catlove=catlove+10;
                    this.write(new ChatMessage(catnow, MessageFormat.format("{0}","喵~(好感度增加了)")));
                }
                else if("use+3".equals(((ChatMessage) message).MESSAGE)&&catten==true&&catgreen>0)
                {
                    catgreen=catgreen-1;
                    catlove=catlove+30;
                    this.write(new ChatMessage(catnow, MessageFormat.format("{0}","喵~(好感度爆增了)")));
                }
                else if("use+3".equals(((ChatMessage) message).MESSAGE)||"use+2".equals(((ChatMessage) message).MESSAGE)||"use+1".equals(((ChatMessage) message).MESSAGE)&&catten==true&&catgreen==0)
                {
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","您似乎沒有擁有這道具")));
                }
                //------------------------------------------------------
                else if(catlove>100)
                {
                    Random nb=new Random();
                    int i=nb.nextInt(2)+1;
                    this.room.multicast(message);
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}", "因為貓咪們似乎非常喜歡您，所以店長特別為您準備好吃的點心喔!")));
                    if(i==1)
                    {
                        this.write(new ChatMessage("服務生", MessageFormat.format("{0}", "是特製貓咪造型的鬆餅塔，請享用!!")));
                    }
                    if(i==2)
                    {
                        this.write(new ChatMessage("服務生", MessageFormat.format("{0}", "是特製貓咪造型的聖代，請享用!!")));
                    }
                    this.room.multicast(new ChatMessage("服務生",MessageFormat.format("{0}","恭喜"+this.source+"獲得店長得特別點心")));
                }
                //-------------------------------------------------------

                else {
                    this.room.multicast(message);
                }

                break;

            case Message.LOGIN:
                if (this.source == null) {
                    this.source = ((LoginMessage) message).ID;
                    this.room.multicast(new ChatMessage(
                            "服務生",
                        MessageFormat.format("{0} 入座。", this.source)
                    ));
                    this.write(new ChatMessage(
                            "服務生",
                            MessageFormat.format("{0} ", "可使用help查看相關指令\n" +
                                    "本咖啡廳目前有麥可、黑仔、Coffee共三隻店貓在本店遊蕩，牠們可能會去找客人玩\n" +
                                    "本咖啡廳對本店貓的行為一律不負責，若有損失請自行吸收。")
                    ));
                    catplaytime();
                    this.room.multicast(new RoomMessage(
                        room.getRoomNumber(),
                        room.getNumberOfGuests()
                    ));
                }
                break;

            case Message.CATBAD1:
                this.room.multicast(message);
                this.write(new ChatMessage(this.source, MessageFormat.format("{0}", catnow+"! 你這樣不乖喔!("+this.source+"的餐點被"+catnow+"打翻了)")));
                bad1=false;
                break;
            case Message.CATBAD2:
                this.room.multicast(message);
                this.write(new ChatMessage(this.source, MessageFormat.format("{0}", "臭貓!把我的錢還來!("+this.source+"的錢被"+catnow+"偷了!!)")));
                bad2=false;
                break;

            default:
        }
    }
    public class SimpleTask extends TimerTask {

        public void run(){
            Random nb=new Random();
            Random lovenb=new Random();
            int j=nb.nextInt(3)+1;
            int i=cat.nextInt(3)+1;
            int t=lovenb.nextInt(3)+1;
            if(j==1||j==2)
            {
                if(i==1)
                {
                    catnow=cat1;
                    write(new ChatMessage("服務生","是"+catnow+"來了，快跟他們玩吧\n(1.搔肚子 2.捏臉頰 3.搔下巴 4.敲他頭 5.腳踢他。 請輸入play+數字\n" +
                            "1.餵食 2.拿出逗貓棒 3.給貓薄荷 請輸入use+數字)"));
                    if(catlove>80&&t==1)
                    {
                        write(new ChatMessage(catnow, "喵~~~~~("+catnow+"嘴咬著100元來到了你身邊，或許是誰的錢包裡叼來的.....)"));
                        money=money+100;
                    }
                    if(catlove>80&&t==2)
                    {
                        write(new ChatMessage(catnow, "喵喵!"));
                    }
                    if(catlove<20&&playtime&&t==3)
                    {
                        write(new ChatMessage(catnow, "喵~~~~~("+catnow+"似乎不打算理您，卻悄悄從您的錢包裡咬出100元跑掉了)"));
                        money=money-100;
                        catten=false;
                        bad2=true;
                        playtime=false;
                    }
                }
                else if(i==2)
                {
                    catnow=cat2;
                    write(new ChatMessage("服務生","是"+catnow+"來了，快跟他們玩吧\n(1.搔肚子 2.捏臉頰 3.抓屁股 4.敲他頭 5.腳踢他。 請輸入play+數字\n" +
                            "1.餵食 2.拿出逗貓棒 3.給貓薄荷 請輸入use+數字)"));
                    if(catlove>80&&t==1)
                    {
                        write(new ChatMessage(catnow, "喵~~~~~("+catnow+"嘴咬著100元來到了您身邊，或許是誰的錢包裡叼來的.....)"));
                        money=money+100;
                    }
                    else if(catlove>80&&t==2)
                    {
                        write(new ChatMessage(catnow, "^ↀᴥↀ^"));
                    }
                    if(catlove<20&&playtime&&t==3)
                    {
                        write(new ChatMessage(catnow, "喵~~~~~("+catnow+"似乎不打算理您，卻悄悄從您的錢包裡咬出100元跑掉了)"));
                        money=money-100;
                        catten=false;
                        bad2=true;
                        playtime=false;
                    }
                }
                else if(i==3)
                {
                    catnow=cat3;
                    write(new ChatMessage("服務生","是"+catnow+"來了，快跟他們玩吧\n(1.搔肚子 2.捏臉頰 3.抓屁股 4.敲他頭 5.腳踢他。 請輸入play+數字\n" +
                            "1.餵食 2.拿出逗貓棒 3.給貓薄荷 請輸入use+數字)"));
                    if(catlove>80&&t==1)
                    {
                        write(new ChatMessage(catnow, "喵~~~~~("+catnow+"嘴咬著100元來到了您身邊，或許是誰的錢包裡叼來的.....)"));
                        money=money+100;
                    }
                    else if(catlove>80&&t==2)
                    {
                        write(new ChatMessage("麥可", "ฅ●ω●ฅ"));
                    }
                    if(catlove<20&&playtime&&t==3)
                    {
                        write(new ChatMessage(catnow, "喵~~~~~("+catnow+"似乎不打算理您，卻悄悄從您的錢包裡咬出100元跑掉了)"));
                        money=money-100;
                        catten=false;
                        bad2=true;
                        playtime=false;
                    }
                }
                catten=true;
                playtime=true;
            }
            else if(j==2&&catgreen>0&&catlove<30)
            {
                catgreen=catgreen-1;
                catlove=catlove+20;
                write(new ChatMessage(catnow, catnow+"搶走了您身上的貓薄荷，似乎挺開心的(好感度增加)"));
            }
            else if(j==3){
                if(i==1)
                {
                    catnow=cat1;
                    write(new ChatMessage(catnow, catnow+"從你身旁走過了，似乎不打算理您"));
                }
                if(i==2)
                {
                    catnow=cat2;
                    write(new ChatMessage(catnow, catnow+"從你身旁走過了，似乎不打算理您"));
                }
                if(i==3)
                {
                    catnow=cat3;
                    write(new ChatMessage(catnow, catnow+"從你身旁走過了，似乎不打算理您"));
                }
            }
        }
    }
    public class daskTask extends TimerTask {

        public void run(){

                write(new ChatMessage("服務生", "這是您點的餐點，請享用"));
                if(catlove<40&&ordertime)
                {
                    bad1=true;
                    ordertime=false;
                }
                else
                {
                    ordertime=false;
                }
        }
    }
    public class waitTask extends TimerTask {

        public void run(){
            if(catten==true){
                write(new ChatMessage("服務生", catnow+"已經離開了"));
                catten=false;
                catplaytime();
            }
            else
            {
                catplaytime();
            }
        }
    }
    public void write(Message message) {
        try {
            this.out.writeObject(message);
            this.out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void greet() {
        String[] greetings = {
            "歡迎光臨貓咪咖啡廳",
            "請問該怎麼稱呼您【暱稱】呢?"
        };

        for (String msg : greetings) {
            write(new ChatMessage("服務生", msg));
        }
    }

    private void catplaytime(){
        cattime.schedule(new SimpleTask(), 40000);
        waittime();
    }
    private void dinnertime(){
        uptime.schedule(new daskTask(), 10000);
        ordertime=true;
    }
    private void waittime(){
        waittime.schedule(new waitTask(), 80000);

    }

    @Override
    public void run() {
        Message message;

        try (
            ObjectInputStream in = new ObjectInputStream(
                this.socket.getInputStream()
            )
        ) {
//            this.process((Message)in.readObject());

            while ((message = (Message) in.readObject()) != null) {
                this.process(message);
            } // od

            this.out.close();
        }
        catch (IOException e) {
            System.out.println("Servant: I/O Exc eption");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}


// Servant.java