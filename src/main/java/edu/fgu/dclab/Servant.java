package edu.fgu.dclab;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class Servant implements Runnable {
    private ObjectOutputStream out = null;
    private String source = null;

    private Socket socket = null;

    private ChatRoom room = null;

    private int money=1000;//金錢
    private int catlove=50;//好感度

    private String menu="\n＜主餐＞\n 白醬海鮮義大利麵  $200 \n 白酒蛤蠣義大利麵 $180 \n青醬海鮮義大利麵 $200 \n茄汁海鮮義大利麵 $200\n" +
            "＜輕食＞\n 鮪魚三明治 $100\n 燒肉三明治 $120\n"+"＜飲料＞ \n貓咪特調咖啡 $100 \n黑貓咖啡(美式) $90 \n水果茶 $120\n " +
            "＜貓用相關用品＞\n 貓零食 $40 \n逗貓棒 $120 \n 貓薄荷 $200\n----------------------------\n";
    private String help="\n menu 菜單 \n help 幫助 \n order+餐點 點餐 \n money 錢包 \n love 貓咪好感度(一開始固定50)";

    private Timer uptime=new Timer();
    private Timer cattime=new Timer();
    private Timer waittime=new Timer();

    private boolean catten=false;


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
        catplaytime();

    }

    public void process(Message message) {
        switch (message.getType()) {
            case Message.ROOM_STATE:
                this.write(message);
                break;

            case Message.CHAT:
                if ("menu".equals(((ChatMessage) message).MESSAGE)) {
                    this.write(new ChatMessage("菜單：", MessageFormat.format("{0}", menu.toString())));
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
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","這是點的你的貓零食。")));
                }
                else if("order+逗貓棒".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-120;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","這是要的你的逗貓棒。")));
                }
                else if("order+貓薄荷".equals(((ChatMessage) message).MESSAGE))
                {
                    money=money-120;
                    this.write(new ChatMessage("服務生", MessageFormat.format("{0}","這是點的你的貓薄荷。")));
                }
                //-----------------------------------------------------------
                else if("money".equals(((ChatMessage) message).MESSAGE)||"Money".equals(((ChatMessage) message).MESSAGE))
                {
                    this.write(new ChatMessage("錢包", MessageFormat.format("{0}", "你身上還有"+money+"元")));
                }
                //----------------------------------------------------------
                else if("love".equals(((ChatMessage) message).MESSAGE)||"Love".equals(((ChatMessage) message).MESSAGE))
                {
                    this.write(new ChatMessage("貓咪好感度", MessageFormat.format("{0}", "目前對貓咪的好感度為"+catlove)));
                }
                //----------------------------------------------------------
                else if("play+1".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    if(catlove<30)
                    {
                        write(new ChatMessage("麥可", "喵嘶~~~~~(麥可咬了你的手，氣稍微消了一點(恢復了點好感度))"));
                        catlove=catlove+5;
                    }
                    else
                    {
                        catlove=catlove+8;
                        this.write(new ChatMessage("貓咪", MessageFormat.format("{0}", "喵~~(貓咪覺得舒服)")));
                    }
                }
                else if("play+2".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    if(catlove<30)
                    {
                        write(new ChatMessage("麥可", "喵嘶~~~~~(麥可咬了你的手，氣稍微消了一點(恢復了點好感度))"));
                        catlove=catlove+5;
                    }
                    else
                    {
                        catlove=catlove+12;
                        this.write(new ChatMessage("貓咪", MessageFormat.format("{0}", "喵喵嗚嗚~~(貓咪開心!!)")));
                    }
                }
                else if("play+3".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    if(catlove<30)
                    {
                        catlove=catlove+5;
                        write(new ChatMessage("麥可", "喵嘶~~~~~(麥可咬了你的手，氣稍微消了一點(恢復了點好感度))"));
                    }
                    else
                    {
                        catlove=catlove+20;
                        this.write(new ChatMessage("貓咪", MessageFormat.format("{0}", "喵嗚嗚~喵喵~(醉了!!)")));
                    }
                }
                else if("play+4".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    catlove=catlove-12;
                    this.write(new ChatMessage("貓咪", MessageFormat.format("{0}", "喵嘶嘶~~(貓咪不開心)")));
                }
                else if("play+5".equals(((ChatMessage) message).MESSAGE)&&catten==true)
                {
                    catlove=catlove-20;
                    this.write(new ChatMessage("貓咪", MessageFormat.format("{0}", "嘶嘶喵喵喵~~(貓咪生氣了)")));
                }
                //------------------------------------------------------
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
                    this.room.multicast(new ChatMessage(
                            "服務生",
                            MessageFormat.format("{0} ", "可使用help查看相關指令")
                    ));

                    this.room.multicast(new RoomMessage(
                        room.getRoomNumber(),
                        room.getNumberOfGuests()
                    ));
                }

                break;

            default:
        }
    }
    public class SimpleTask extends TimerTask {

        public void run(){

            write(new ChatMessage("服務生","貓咪來了，快跟貓咪玩吧\n(1.搔肚子 2.捏臉頰 3.給貓薄荷 4.敲他頭 5.腳踢他。 請輸入play+數字)"));
            write(new ChatMessage("麥可", "喵喵!"));
            catten=true;
            if(catlove>80)
            {
                write(new ChatMessage("麥可", "喵~~~~~(麥可咬著100元來了)"));
                money=money+100;
            }
            if(catlove<20)
            {
                write(new ChatMessage("麥可", "喵~~~~~(麥可從你的錢包咬出100元跑掉了)"));
                money=money-100;
                catten=false;
            }

        }
    }
    public class daskTask extends TimerTask {

        public void run(){

                write(new ChatMessage("服務生", "這是你點的餐點，請享用"));
                if(catlove<40)
                {
                    write(new ChatMessage("服務生", "麥可!你這樣不乖喔!(你的餐點被打翻了)"));
                }
        }
    }
    public class waitTask extends TimerTask {

        public void run(){
            if(catten==true){
                write(new ChatMessage("服務生", "貓咪離開了"));
                catten=false;
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
        cattime.schedule(new SimpleTask(), 50000);
        waittime();
    }
    private void dinnertime(){
        uptime.schedule(new daskTask(), 10000);
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
