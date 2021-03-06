
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


//The following class is for returning value of message delay
//based on sender's sending time and receiver's receiving time

 class message_handling
{

      public double average_delivery_time(double receive, double send)

        {
            double diff = receive - send;
            return diff;
        }


}

public class AverageTimeDisplay  {




    public static void main(String[] args) {


    message_handling m =   new message_handling();


    List<String> listing1 = new ArrayList<String>();
    List<String> hourpart1 = new ArrayList<String>();
    List<String> minutepart1 = new ArrayList<String>();
    List<String> secondpart1 = new ArrayList<String>();

    // The file includes the delivery time of sending messages of all clients

    File file = new File("D:\\test1.txt");

    if(file.exists())

    {
        try
        {
            listing1 = Files.readAllLines(file.toPath(),Charset.defaultCharset());
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
      if(listing1.isEmpty())
          return;
    }

    for(String line : listing1)   //For message sending  clients
    {

        //Spliting the values of each line in the text which is separated by":"
        //in terms of hour,minute and second


        String [] res = line.split(":");

        hourpart1.add(res[0]);
        minutepart1.add(res[1]);
        secondpart1.add(res[2]);
    }




    List<String> listing2 = new ArrayList<String>();
    List<String> hourpart2 = new ArrayList<String>();
    List<String> minutepart2 = new ArrayList<String>();
    List<String> secondpart2 = new ArrayList<String>();

    // The file includes the delivery time of receiving messages of all clients

    File file2 = new File("D:\\test2.txt");

    if(file2.exists())
    {
        try
        {
            listing2 = Files.readAllLines(file2.toPath(),Charset.defaultCharset());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

      if(listing2.isEmpty())
          return;
    }
    for(String line : listing2) //For message receiving  clients

    {
        //Spliting the values of each line in the text which is separated by":"
        //in terms of hour,minute and second

        String [] res = line.split(":");
        hourpart2.add(res[0]);
        minutepart2.add(res[1]);
        secondpart2.add(res[2]);
    }





                double [] client_id = new double[10001]  ;
                double[] totalsecondsender = new double[10001]  ;
                double[] totalsecondreceiver = new double[10001]  ;

                for(int j=0;j<10000;j++)

                  {
                      //Calculating the the whole time in seconds of all the sender clients.
                      totalsecondsender[j]   =   (Double.parseDouble(hourpart1.get(j))*3600) + (Double.parseDouble(minutepart1.get(j))*60)+ Double.parseDouble((secondpart1.get(j)));
                      //Calculating the the whole time in seconds of all the receiving clients.
                      totalsecondreceiver[j]   =   (Double.parseDouble(hourpart2.get(j))*3600) + (Double.parseDouble(minutepart2.get(j))*60)+ Double.parseDouble((secondpart2.get(j)));

                      //Calling the method "average_delivery_time" to get the time diffrence in seconds.

                      client_id[j] = m.average_delivery_time(totalsecondreceiver[j], totalsecondsender[j]);

                      System.out.println("Message delay time for number " +j+ " pair of clients:"+client_id[j] );
                  }

                double sum=0;

                  double [] d = new double[10001]  ;
                  int [] c = new int[10001]  ;
                  int j=1;

                for(int k=0;k<10000;k++)

                  {
                        sum=sum+client_id[k];

                        if((k+1)%10 == 10)
                        {
                            // d[] array is tracking the average "Time per number of connections"
                            // c[] array is tracking the number ofconnections
                            d[j]=sum/(k+1);
                            c[j]=k+1;
                            System.out.println("Average time for "+(k+1)+" connections are : "+d[j]+" seconds");
                            j++;
                        }
                        else if((k+1)%10 == 100)
                        {
                            // d[] array is tracking the average "Time per number of connections"
                            // c[] array is tracking the number ofconnections
                            d[j]=sum/(k+1);
                            c[j]=k+1;
                            System.out.println("Average time for "+(k+1)+" connections are : "+d[j]+" seconds");
                            j++;
                        }
                        else if((k+1)%10 == 1000)
                        {
                            // d[] array is tracking the average "Time per number of connections"
                            // c[] array is tracking the number ofconnections
                            d[j]=sum/(k+1);
                            c[j]=k+1;
                            System.out.println("Average time for "+(k+1)+" connections are : "+d[j]+" seconds");
                            j++;
                        }
                        else if((k+1)%10 == 10000)
                        {
                            // d[] array is tracking the average "Time per number of connections"
                            // c[] array is tracking the number ofconnections
                            d[j]=sum/(k+1);
                            c[j]=k+1;
                            System.out.println("Average time for "+(k+1)+" connections are : "+d[j]+" seconds");
                            j++;
                        }
                  }
    }
}

