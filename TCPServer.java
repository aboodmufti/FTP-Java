import java.io.*; 
import java.net.*; 
import java.util.Arrays;
import java.lang.Object;
import java.nio.file.*;
import java.lang.Exception;
//import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;


class TCPServer {    
    public static void main(String argv[]) throws Exception
    {
        ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(argv[0]));
        System.out.println("Server is listening on port: "+argv[0]+" , and IP address: "+InetAddress.getLocalHost().getHostAddress());
        while(true){
            server(argv,welcomeSocket);
        }

    }

    private static void server(String[] argv, ServerSocket welcomeSocket) throws Exception{
            String orignalPath = currDir();
            File workingDir = new File(orignalPath);

            String clientSentence; 
            String capitalizedSentence; 

            
            try{
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("A new client is now connected to your machine. Client IP address is : "+ connectionSocket.getInetAddress().toString());

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                //DataOutputStream outToClient2 = new DataOutputStream(new BufferedOutputStream(connectionSocket.getOutputStream()));

                String[] array;
                while(true)
                {

                    clientSentence = inFromClient.readLine();
                    array = clientSentence.split("[ ]+");

                    if(array[0].equals("")){
                        array = removeFirst(array);
                    } 

                    if(array[0].equals("ls")){

                        System.out.println("client is listing the files in your directory");
                        String[] fileList = workingDir.list();
                        for(int i = 0; i<fileList.length ;++i){
                            outToClient.writeBytes(fileList[i]+"\n");
                        }
                        outToClient.writeBytes("<EndOfStream>\n");
                    }else if(array[0].equals("cd")){

                        System.out.println("client is changing directory");

                        String filename = "";
                        for(int i = 1; i<array.length ;++i){
                            filename += array[i];
                            if(i != array.length-1){
                                filename += " ";
                            }
                        }

                        
                        try{
                            workingDir = ChangeDirectory(workingDir,filename);
                            outToClient.writeBytes("Current Directory: "+workingDir.getPath()+"\n");
                            outToClient.writeBytes("<EndOfStream>\n");
                        }catch(Throwable e){
                            System.out.println(e.getMessage());
                            outToClient.writeBytes(e.getMessage()+"\n");
                            outToClient.writeBytes("<EndOfStream>\n");
                        }
                        
                    }else if(array[0].equals("mkdir")){
                        System.out.println("client is creating a new directory");
                        File tempDir = new File(workingDir.getPath(),array[1]);
                        boolean didCreate = tempDir.mkdir();
                        if(didCreate){
                            //workingDir = tempDir;
                            outToClient.writeBytes("Directory \""+array[1]+"\" was created succesfully.\n");
                            outToClient.writeBytes("<EndOfStream>\n");
                        }else{
                            outToClient.writeBytes("Couldn't create directory \""+array[1]+"\".\n");
                            outToClient.writeBytes("<EndOfStream>\n");
                        }
                    }else if(array[0].equals("get")){
                        System.out.println("client is getting a file from your system");
                        Path path = Paths.get(workingDir.getPath(), array[1]);

                        if (!Files.exists(path)) {
                            outToClient.writeBytes("No such file \""+array[1]+"\".\n");
                            outToClient.writeBytes("<EndOfStream>\n");
                            
                        }else{
                            File inputFile = new File(workingDir.getPath(), array[1]);
                            byte[] data = new byte[(int) inputFile.length()];
                            FileInputStream fis = new FileInputStream(inputFile);
                            //DataOutputStream outToClient2 = new DataOutputStream(new BufferedOutputStream(connectionSocket.getOutputStream()));
                            OutputStream outToClient2= connectionSocket.getOutputStream();
                            outToClient.writeBytes("<StartOfFile>\n");
                            outToClient.writeBytes("<NumberOfBytes>\n");
                            outToClient.writeBytes(data.length+"\n");
                            outToClient.writeBytes("<FileName>\n");
                            outToClient.writeBytes(array[1]+"\n");

                            TimeUnit.SECONDS.sleep(1);
                            int count;
                            while ((count = fis.read(data,0,data.length)) > 0) {
                                outToClient2.write(data, 0, count);
                            }
                            outToClient2.flush();
                            fis.close();

                        }

                    }else if(array[0].equals("put")){
                        System.out.println("client is sending a file");
                        String line = inFromClient.readLine();

                        if(line.equals("<StartOfFile>")){
                            InputStream bytesIn2 =connectionSocket.getInputStream();
                            line = inFromClient.readLine();
                            byte[] data = new byte[1];
                            int byteNum = 0;
                            if(line.equals("<NumberOfBytes>")){
                                line = inFromClient.readLine();
                                data = new byte[Integer.parseInt(line)];
                                byteNum = Integer.parseInt(line);
                            }
                            line = inFromClient.readLine();
                            String name = "";
                            if(line.equals("<FileName>")){
                                line = inFromClient.readLine();
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
                            outToClient.writeBytes("<EndOfStream>\n");
                            System.out.println("File succesfully transfered.\n");
                        }

                    }else if(array[0].equals(".exit")){
                        System.out.println("Client has diconnected");
                        outToClient.writeBytes("<EndOfStream>\n");
                        //System.exit(0);
                        break;
                    }else if(array[0].equals("pwd")){
                        outToClient.writeBytes("Current Directory: "+workingDir.getPath()+"\n");
                        outToClient.writeBytes("<EndOfStream>\n");
                        
                    }else{
                        System.out.println("Wrong input: " +clientSentence);
                        outToClient.writeBytes("Please enter a valid command.\n");
                        outToClient.writeBytes("<EndOfStream>\n");
                    }
                }
                
            }catch(NullPointerException e) {
                System.out.println("Client has diconnected");
                //System.exit(0);
                return;
            }

    }
    //takes an array of strings
    private static String executeCommand(String[] command) {

        StringBuffer output = new StringBuffer();

        Process p;

        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";           
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    } 
    //Returns the current directory that the program is running in
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

    //Changes directory on server's machine
    private static File ChangeDirectory(File oldDir,String location) throws Exception{
        
        File newDir = new File(oldDir.getPath());

        if(location.equals("..")){
             newDir = new File(oldDir.getParent());
        }else{
            Path path = Paths.get(oldDir.getPath(), location);
            if (!Files.exists(path)) {
                Exception e = new Exception("No such Directory.");
                throw e;
                
            }else{
                newDir = new File(oldDir.getPath(), location);
            }
        }


        return newDir;

    }

    //Removes first element of an array
    private static String[] removeFirst(String[] array) {
            int length = array.length;
            
            String[] array2 = new String[length];

            //System.arraycopy(array, 1, array2, 0, length );
            for(int i = 0; i<length ;++i){
                array2[i] =array[i+1];
                if(i+1 == length-1){
                    break;
                }

            }
            return array2;
        }

    public static void print(String stuff){
        System.out.println(stuff);
    }

} 