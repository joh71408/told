package edu.fgu.dclab;

public class ChatMessage extends AbstractMessage {
    public final String MESSAGE;

    public ChatMessage(String source, String message) {
        this.source = source;
        this.MESSAGE = message;
    }

    public int getType() {
        if(Servant.bad1==true)
        {
            return Message.CATBAD1;
        }
        else if(Servant.bad2==true)
        {
            return Message.CATBAD2;
        }
        else
        {
            return Message.CHAT;
        }

    }
}
