import java.io.*; 
import java.net.*; 
import java.util.Arrays;
import java.lang.Object;
import java.nio.file.*;
import java.lang.Exception;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.PrintWriter;

class TCPClient {  

    public static void main(String argv[]) throws Exception  {   

        String sentence = "";   
        String modifiedSentence;  
        //String name =  argv[2];
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
                        List<String> list = Files.readAllLines(path);
                        String[] fileArray = list.toArray(new String[list.size()]); 

                        outToServer.writeBytes("<StartOfFile>\n");
                        outToServer.writeBytes("<FileName>\n");
                        outToServer.writeBytes(array[1]+"\n");
                        //outToClient.writeBytes("<FileNameEnd>\n");
                        for(int i = 0; i<fileArray.length ;++i){
                            outToServer.writeBytes(fileArray[i]+"\n");
                        }
                        outToServer.writeBytes("<EndOfFile>\n");
                        outToServer.writeBytes("<EndOfStream>\n");
                    }
                }

                everything = new StringBuilder();
                line = "";
                
                while( !((line = inFromServer.readLine()).equals("<EndOfStream>")) && !sendingFile) {

                    if(line.equals("<StartOfFile>")){

                        line = inFromServer.readLine();
                        byte[] data = new byte[1];
                        print("DEBUG0: "+line);
                        int byteNum = 0;
                        if(line.equals("<NumberOfBytes>")){
                            line = inFromServer.readLine();
                            data = new byte[Integer.parseInt(line)];
                            byteNum = Integer.parseInt(line);
                        }
                        print("DEBUG1: "+line);
                        //PrintWriter inFile = new PrintWriter("TEMP_FILE_DELETE_WHEN_DONE.txt", "UTF-8");
                        //while( !((line = inFromServer.readLine()).equals("<EndOfFile>"))) {
                        line = inFromServer.readLine();
                        print("DEBUG2: "+line);
                        String name = "";
                        if(line.equals("<FileName>")){
                            line = inFromServer.readLine();
                            name = line;
                            //inFile = new PrintWriter(line, "UTF-8");
                            //line = inFromServer.readLine();
                        }
                        print("DEBUG3: "+line);
                        
                        print("DEBUG4: data length: "+ data.length);
                        /*for(int i = 0; i < data.length; ++i){
                            print("DATA : "+ bytesIn.read());
                        }*/
                        //inStream.read(data, 0, data.length);
                        //bytesIn.read(data);
                        print("DEBUG5");
                            //inFile.println(line);
                        //}
                        //inFile.close();
                        File fileCreated = new File(currDir(),name);
                        fileCreated.createNewFile();
                        FileOutputStream newFile = new FileOutputStream(fileCreated);
                        data = new byte[byteNum];
                        int count;
                        int i = 0 ;
                        print("DEBUG6 : " +byteNum);
                        while ((count = inStream.read(data, 0, 1)) > 0) {
                            print("DEBUG1829");
                            newFile.write(data, 0, count);
                            //print("COUNT: "+ count);
                            i += count;
                            print("i value : "+i + "  ---- byteNumber : " + byteNum);
                            if(i >= byteNum-1){ print("TRUE");break;}else{print("FALSE");}
                        }
                        //newFile.write(data, 0, data.length);
                        newFile.close();
                        
                        //print("DEBUG6"+inFromServer.readLine());
                        everything.append("File succesfully transfered.\n");
                        //print("DEBUG6"+inFromServer.readLine());
                        //Path path = Paths.get(currDir(), "TEMP_FILE_DELETE_WHEN_DONE.txt");
                        //Files.deleteIfExists(path);

                        break;
                    }
                    everything.append(line+"\n");
                    
                }
                
                //modifiedSentence = inFromServer.readLine();   
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