package manager;

import marine.Command;

public class User {


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
}
