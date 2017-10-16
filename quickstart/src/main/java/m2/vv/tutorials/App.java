package m2.vv.tutorials;

import java.lang.Throwable;
import javassist.*;

public class App 
{
    public static void main(String[] args )
    {
        try {
            //TODO: Your code goes here
            throw new Throwable();
        }

        catch(Throwable exc) {
            System.out.println("Oh, no! Something went wrong.");
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }

    }
}
