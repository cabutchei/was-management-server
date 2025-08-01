package com.cabutchei;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class Testing {
    public static void main(String[] args) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/cabutchei/Documents/vs_code/was-vscode-project/socket-demo/java-server/sysout.log", true));
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                try{
                    Thread.sleep(8000);
                    System.out.println("SHUTDOWN");
                    writer.write("client stop request");
                    writer.flush();
                    writer.close();
                } catch(Exception e) {}})
                );
                while(true){
                    writer.write("hello\n");
                    writer.flush();  // immediately push to disk
                    Thread.sleep(2000);
     }
    }

}
