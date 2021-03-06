import java.math.BigDecimal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class RunningSS {
 public static void main(String[] args) {
  Process p;

  try {        
      List<String> cmdList = new ArrayList<String>();
      // adding command and args to the list
      cmdList.add("sh");
      cmdList.add("/home/nidm/stockquote/ticker.sh");
      cmdList.add("BABA");
      cmdList.add("SPY");
      cmdList.add("BTC-USD");
      ProcessBuilder pb = new ProcessBuilder(cmdList);
      p = pb.start();
                
      p.waitFor(); 
      BufferedReader reader=new BufferedReader(new InputStreamReader(
       p.getInputStream())); 

      String line;  
	    ArrayList<String[]> output = new ArrayList<String[]>();
      while((line = reader.readLine()) != null) { 
        System.out.println(line);
        output.add(line.split("\\s+"));
      } 
		int exitVal = p.waitFor();
		if (exitVal == 0) {
			System.out.println("Success!");
      for (String i[]: output) {
				System.out.println(i[0]+" "+i[1]+" "+i[2]+" "+i.length);
				System.out.println(i[0]+" "
				 +(new BigDecimal(i[1]))+" "
				 +(new BigDecimal(i[2]))
			                   	);
			
      }
			System.exit(0);
		} else {

			System.out.println("Fail!");
		}

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
 }

}
