import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Author   : Adriano Alves
 * Date     : July/10/2016
 * File Name: DieHard.java
 *
 * Objective : Simple Java Server program to respond client request
 *             it will receive a name of state or capital and 
 *             will send back the capital or state of the requested name
 *
 *  for debug run java -DDEBUG DieHard <port number>
 */

public class DieHard
{
    /** main method**/
    public static void main(String args[])
    {
        // if no argument at command line than port number wil be 10001
        // make sure use the same port number for your app
        String portNumber = (args.length > 0) ? args[0] : "10001";

        if (!isInteger(portNumber))
        {
            System.err.println("USAGE: invalid port number: java Diehard || " +
                    "java DieHard <port number>");
            System.exit(1);
        }
        else
        {
            try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(portNumber)))
            {
                while (true)
                {
                    // Create the Socket to connect with client
                    Socket client = serverSocket.accept();
                    // Start the Runnable for multi thread
                    Runnable runnable = new Hulk(client);
                    Thread t = new Thread(runnable);
                    t.start();
                }
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    /**
     * method to check if port number entered by user is a valid int number
     **/
    public static boolean isInteger(String s)
    {
        try
        {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e) {return false;}

        return true;
    }
}// end of DieHard class

    /*** class to handle multi thread **/
class Hulk implements Runnable
{
    private Socket socket;

    // Constructor
    Hulk(Socket socket) { this.socket = socket; }

    @Override
    public void run()
    {
        String arStates[] = new String[50];
        String arCapitals[] = new String[50];
        fileToArrays(arStates,arCapitals);

        ///////////// DEBUG /////////////
        debug("********** Array with States Names *********");
        debugAr(arStates);
        debug("********** Array with Capital Names *********");
        debugAr(arCapitals);

        try
        {
            // get the output stream
            InputStream in = socket.getInputStream();
            // get the input stream
            OutputStream out = socket.getOutputStream();
            // create the scanner to get user request from input stream
            Scanner scanner = new Scanner(in);
            // create the print writer to write the answer to user
            PrintWriter writer = new PrintWriter(out,true);

            // boolean flag to stop server when job is completed
            boolean isJobCompleted = false;

            while (!isJobCompleted && scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                debug("DEBUG at line 90 :"+line);
                if (line.trim().equals("CLOSE")) isJobCompleted = true;
                else if (line.trim().equals("KILL!SERVER!")) System.exit(0);
                else
                {
                    debug("DEBUG at line 95 :"+line);
                    // sending answer to client
                    writer.println(searchAndReturn(arStates,arCapitals,line.toUpperCase()));
                    debug("DEBUG at line 97 :"+line);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /*** method to search the input on arrays and return result ***/
    private static String searchAndReturn(String[] sts, String[] caps, String target)
    {
        String str = "";

        debug("DEBUG inside searchAndReturn :"+target);

        for (int i=0; i < 50 ; i++)
        {
            // check if input was a state name
            if (sts[i].equals(target))
            {
                str = "Capital of "+target+" is "+caps[i];
                break;
            }
            // check if input was a capital name
            else if (caps[i].equals(target))
            {
                str = caps[i]+" Is the Capital of "+sts[i];
                break;
            }
            // check if input was a wrong capital or state name
            else str = "No Such State or Capital !";
        }
        return str;
    }
    /** Method to load file with data to array **/
    private static void fileToArrays(String[] states, String[] capitals)
    {
        try
        {
            // scan the file
            Scanner scanner = new Scanner(new File("US_states"));
            String line ;
            int i = 0 ;
            // skip 1st 2 header lines
            scanner.nextLine(); scanner.nextLine();

            while(scanner.hasNext())
            {
                // populate array with all upper to ignore case
                line = scanner.nextLine().toUpperCase();
                //split line by spaces
                String temp[] = line.split("\\s\\s+");

                if (temp.length>=2)
                {

                    if (temp.length==2)
                    {
                        states[i] = temp[0];
                        capitals[i++] = temp[1];
                    }
                    // check if capital or state is compost with multiples name
                    else
                    {
                        states[i] = temp[0]+" "+temp[1];
                        capitals[i++] = temp[2];
                    }
                }
            }
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
    }
    /****** method to debug array ******/
    private static void debugAr(String[] array)
    {
        if(System.getProperty("DEBUG") != null)
        {
            for(String str: array) println(str);
        }
    }
    /****** debug method ******/
    private static void debug(String msg)
    {
        if(System.getProperty("DEBUG") != null) println(msg);
    }
    /***** println ******/
    public static void println(Object o)
    {
        System.out.println(""+o);
    }

}// end of Hulk class
