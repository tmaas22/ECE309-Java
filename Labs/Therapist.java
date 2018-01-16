// Name: Thomas Matrejek

import java.io.*;

public class Therapist
{
  public static void main(String[] args)
  {
    System.out.println("\n+===================+\n|  Thomas Matrejek  |\n+===================+\n");

    System.out.println("========= Welcome to the \"TAM On Line Therapy System\" (C) =========");
    System.out.println(" Instructions:\n  * Only submit Yes/No questions\n  * To exit your session type in \"END\"\n  * Submit commands/questions with the \"ENTER\" key\n");

    // Setting up variables and I/O devices
    String[] responses = {"I wouldn't bet a penny on it", "I don't think so", "\"It's a no from me\" - Simon Cowell", "You'd have a better chance at a slot machine", "N. O. No", "An outstanding yes", "fo' shizzle", "Heck yeah", "If you really want to know, yes", "Definitely", "You should ask Bowman", "Eventually", "<shrugs> Maybe"};
    String logTemplate = "Question was: '%s'. Answer was: '%s'.";
    String userInput;
    BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
    BufferedWriter sessionLog;
    try
    {
      sessionLog = new BufferedWriter(new FileWriter("TherapySessions.txt", true));
    }
    catch(IOException ioex)
    {
      System.out.println(ioex);
      return;
    }


    // The main program
    while(true)
    {
      System.out.print("# ");
      try
      {
        userInput = keyboard.readLine().trim();
      }
      catch(IOException ioex)
      {
        System.out.println(ioex);
        return;
      }

      //  Check for blank lines or if we are done
      if (userInput.equalsIgnoreCase("END")) break;
      if (userInput.length() == 0) continue;

      int index = (int) (responses.length * Math.random());
      String resp = responses[index];

      System.out.println(resp);

      try
      {
        sessionLog.write(String.format(logTemplate, userInput, resp));
        sessionLog.newLine();
      }
      catch(IOException ioex)
      {
        System.out.println(ioex);
        return;
      }
      // System.out.println(userInput);

    }

    try
    {
      sessionLog.close();
    }
    catch(IOException ioex)
    {
      System.out.println(ioex);
      return;
    }
  }
}
