package gui

import manager.Client
import manager.ClientCommandInterpreter
import manager.Program
import java.io.IOException
import java.util.concurrent.TimeUnit

fun main(){
    val win = ManagerApp();
    win.launch();
}
