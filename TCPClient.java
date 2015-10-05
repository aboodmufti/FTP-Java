import java.io.*; 
import java.net.*; 
import java.util.Arrays;
import java.lang.Object;
import java.nio.file.*;
import java.lang.Exception;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

class TCPClient {  

    public static void main(String argv[]) throws Exception  {   

        String sentence = "";   
        String modifiedSentence;  
        StringBuilder everything;
        String line;
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
        try{
            Socket clientSocket = new Socket(argv[0], Integer.parseInt(argv[1]) ); 
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
            //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  
            BufferedInputStream bytesIn = new BufferedInputStream(clientSocket.getInputStream());
            DataInputStream inStream = new DataInputStream(bytesIn);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(bytesIn, StandardCharsets.UTF_8));
            boolean sendingFile = false; 
            String[] array;
            while(!sentence.equals(".exit")){
                sendingFile = false;
                System.out.print("client> ");
                sentence = inFromUser.readLine();   

                outToServer.writeBytes(sentence + '\n');   

                array = sentence.split("[ ]+");

                if(array[0].equals("put")){
                    sendingFile = true;
                    Path path = Paths.get(currDir(), array[1]);

                    if (!Files.exists(path)) {
                        System.out.println("File doesnt not exist");
                        sendingFile = false;
                    }else{
                        File inputFile = new File(currDir(), array[1]);
                        byte[] data = new byte[(int) inputFile.length()];
                        FileInputStream fis = new FileInputStream(inputFile);
                        //DataOutputStream outToClient2 = new DataOutputStream(new BufferedOutputStream(connectionSocket.getOutputStream()));
                        OutputStream outToClient2 = clientSocket.getOutputStream();
                        outToServer.writeBytes("<StartOfFile>\n");
                        outToServer.writeBytes("<NumberOfBytes>\n");
                        outToServer.writeBytes(data.length+"\n");
                        outToServer.writeBytes("<FileName>\n");
                        outToServer.writeBytes(array[1]+"\n");

                        TimeUnit.SECONDS.sleep(1);
                        int count;
                        while ((count = fis.read(data,0,data.length)) > 0) {
                            outToClient2.write(data, 0, count);
                        }
                        outToClient2.flush();
                        fis.close();
                        sendingFile = true;
                    }
                }
                everything = new StringBuilder();
                line = "";
                while( !((line = inFromServer.readLine()).equals("<EndOfStream>")) && !sendingFile) {
                    print("DEBUG inside WHILE");
                    if(line.equals("<StartOfFile>")){
                        InputStream bytesIn2 =clientSocket.getInputStream();
                        line = inFromServer.readLine();
                        byte[] data = new byte[1];
                        int byteNum = 0;
                        if(line.equals("<NumberOfBytes>")){
                            line = inFromServer.readLine();
                            data = new byte[Integer.parseInt(line)];
                            byteNum = Integer.parseInt(line);
                        }
                        line = inFromServer.readLine();
                        String name = "";
                        if(line.equals("<FileName>")){
                            line = inFromServer.readLine();
                            name = line;
                        }
                        File fileCreated = new File(currDir(),name);
                        fileCreated.createNewFile();
                        FileOutputStream newFile = new FileOutputStream(fileCreated);
                        int count;
                        int i = 0 ;
                        while ((count = bytesIn2.read(data, 0, data.length)) > 0) {
                            newFile.write(data, 0, count);
                            i += count;
                            print("i value : "+i + "  ---- byteNumber : " + byteNum);
                            if(i >= byteNum-1){ print("TRUE");break;}else{print("FALSE");}
                        }
                        newFile.close();
                        
                        everything.append("File succesfully transfered.\n");
                        break;
                    }
                    everything.append(line+"\n");
                    
                }
                System.out.println(everything.toString());   
            }
            clientSocket.close();
        }catch(ConnectException e) {
            System.out.println("Error: "+e.getMessage()+"\nPlease make sure you put the right server info and that the server is listening.");
            System.exit(1);
        }
    } 


    private static String currDir() {

        StringBuffer output = new StringBuffer();

        Process p;

        try {
            p = Runtime.getRuntime().exec("pwd");
            p.waitFor();
            BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";           
            while ((line = reader.readLine())!= null) {
                output.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public static void print(String stuff){
        System.out.println(stuff);
    }

} 