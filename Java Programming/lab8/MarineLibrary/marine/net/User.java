package marine.net;

import marine.Command;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;

public class User implements Serializable{


    public User(String nickname, int id){
        startSessionTime = System.currentTimeMillis();
        this.nickname = nickname;
        this.id = id;
        this.isSuperuser = false;

    }
    private String nickname;
    public String getNickname() {
        return nickname;
    }

    private int id;
    public int getId() {
        return id;
    }

    private boolean isSuperuser;
    public boolean isSuperuser(){return isSuperuser;}
    public void setSuperuser(boolean isSuper){isSuperuser = isSuper;}

    private Color color = Color.BLACK;

    public Color getColor(){return color;}
    public void setColor(Color color){this.color = color;}
    public void setColor(){
        Random rand = new Random();
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }


    private long startSessionTime;
    public long getSessionTime(){
        return System.currentTimeMillis() - startSessionTime;
    }

    private Command lastExecutedCommand;

    public Command getLastExecutedCommand() {
        return lastExecutedCommand;
    }

    public void setLastExecutedCommand(Command lastExecutedCommand) {
        this.lastExecutedCommand = lastExecutedCommand;
    }


    @Override
    public boolean equals(Object u){
        if(this == u)return true;
        if(u instanceof User user) {
            return nickname.equals(user.nickname);
        }
        return false;
    }
}
