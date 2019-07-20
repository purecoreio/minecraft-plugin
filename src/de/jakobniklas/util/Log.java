package de.jakobniklas.util;


/**
 * Logging class, used to write to {@link System#out}
 *
 * @author Jakob-Niklas See
 * @see #lastExecuted
 * @see #lastLogPrefixLength
 * @see #logFormat
 * @see #logPointer
 * @see #outputElapsedTime
 * @see #prefixLongest
 * @see #done()
 * @see #getLastLogPrefixLength()
 * @see #getLogFormat()
 * @see #getLogPointer()
 * @see #measureTime(String)
 * @see #measureTime(String, String)
 * @see #print(String)
 * @see #print(String, String)
 * @see #setLastLogPrefixLength(int)
 * @see #setLogFormat(String)
 * @see #setLogPointer(char)
 */
public class Log
{
    /**
     * Character which can be positioned with the tag '#logpointer' in the {@link #logFormat}
     *
     * @see #getLogPointer()
     * @see #setLogPointer(char)
     */
    private static char logPointer = '>';
    /**
     * String which defines the arrangement of values in the output of a 'Log.print()' method call.
     * <br><br>
     * Tags:
     * <br> #date - date in the format specified in '{@link de.jakobniklas.util.TimeUtil#getDate()
     * getDate()}'
     * <br> #time - time in the format specified in '{@link de.jakobniklas.util.TimeUtil#getTime()
     * getTime()}'
     * <br> #milliseconds - number with type long as return value from '{@link System#currentTimeMillis()}'
     * <br> #prefix - prefix value specified in the '{@link #print(String, String)}' method with 'String prefix' in the
     * constructor
     * <br> #logPointer - separating character ({@link #logPointer})
     * <br> #message - input form the '{@link #print(String)}' method
     * <br> #class - returns the name of the class which called the print method ({@link
     * de.jakobniklas.util.ClassUtil#getCallerCallerClassName() getCallerCallerClassName()})
     * <br> #thread - thread from which the message is logged
     *
     * @see #print(String, String)
     * @see #print(String)
     * @see #getLogFormat()
     * @see #setLogFormat(String)
     */
    private static String logFormat = "[#date; #time; #milliseconds] #prefix #logpointer #message (#class)";
    /**
     * internal value which controls spacing in logging
     */
    private static int prefixLongest = 0;
    /**
     * internal value which controls spacing in logging
     */
    private static int lastLogPrefixLength;
    /**
     * internal value which is the time in milliseconds in which the print method was last called
     *
     * @see #measureTime(String, String)
     * @see #measureTime(String)
     * @see #done()
     */
    private static long lastExecuted;
    /**
     * internal value which keeps record of executing and logging with mesure time
     *
     * @see #measureTime(String, String)
     * @see #measureTime(String)
     * @see #done()
     */
    private static boolean outputElapsedTime = false;

    /**
     * Method which starts measuring the time from the call to the call of the Log.done() method
     *
     * @param prefix Prefix which can be used in the logging output, specified by the '#prefix' tag in {@link
     *               #logFormat}
     * @param input  Message which should be used in the logging output, specified by the '#message' tag in {@link
     *               #logFormat}
     *
     * @return void
     *
     * @see #print(String, String)
     * @see #done()
     * @see #measureTime(String)
     */
    public static void measureTime(String prefix, String input)
    {
        print(prefix + " [m]", input);
        outputElapsedTime = true;
    }

    /**
     * Method which starts measuring the time from the call to the call of the Log.done() method
     *
     * @param input Message which should be used in the logging output, specified by the '#message' tag in {@link
     *              #logFormat}
     *
     * @return void
     *
     * @see #print(String)
     * @see #done()
     * @see #measureTime(String, String)
     */
    public static void measureTime(String input)
    {
        print("Log [m]", input);
        outputElapsedTime = true;
    }

    /**
     * Method which stops measuring the time, if Log.measureTime() was called before
     *
     * @return void
     *
     * @see #measureTime(String, String)
     * @see #measureTime(String)
     */
    public static void done()
    {
        if(outputElapsedTime)
        {
            String timeDifference = "";

            for(int i = 0; i < lastLogPrefixLength; i++)
            {
                timeDifference = timeDifference + " ";
            }

            timeDifference = timeDifference + logPointer + " Done in '" + (System.currentTimeMillis() - lastExecuted) + "ms'";

            System.out.println(timeDifference);
            outputElapsedTime = false;
        }
    }

    /**
     * Method which is logging the inputed parameters to the {@link System#out} stream (console) and uses the {@link
     * #logFormat} format the output
     *
     * @param prefix Prefix which can be used in the logging output, specified by the '#prefix' tag in {@link
     *               #logFormat}
     * @param input  Message which should be used in the logging output, specified by the '#message' tag in {@link
     *               #logFormat}
     *
     * @return void
     *
     * @see #print(String)
     */
    public static void print(String prefix, String input)
    {
        lastExecuted = System.currentTimeMillis();

        if(prefixLongest == 0)
        {
            prefixLongest = prefix.length();
        }
        else if(prefixLongest < prefix.length())
        {
            prefixLongest = prefix.length();
        }

        for(int i = prefix.length(); i < prefixLongest; i++)
        {
            prefix = prefix + " ";
        }

        Thread currentThread = Thread.currentThread();

        String output = new String
                (
                        logFormat.replaceFirst("#date", TimeUtil.getDate()).
                                replace("#time", TimeUtil.getTime()).
                                replace("#prefix", prefix).
                                replace("#message", input).
                                replace("#milliseconds", String.valueOf(System.currentTimeMillis())).
                                replace("#class", ClassUtil.getCallerCallerClassName()).
                                replace("#logpointer", String.valueOf(logPointer)).
                                replace("#thread", currentThread.getName())
                );

        lastLogPrefixLength = output.indexOf(logPointer);

        System.out.println(output);
    }

    /**
     * Method which is logging the inputed parameters to the System.out stream (console) and uses the {@link #logFormat}
     * to format the output ({@link #print(String, String)})
     * <br><br>
     * Note: The prefix value will be "Log" if not specified in the {@link #print(String, String)} method
     *
     * @param input Message which should be used in the logging output, specified by the '#message' tag in {@link
     *              #logFormat}
     *
     * @return void
     */
    public static void print(String input)
    {
        print("Log", input);
    }

    /**
     * @return {@link #lastLogPrefixLength}
     */
    public static int getLastLogPrefixLength()
    {
        return lastLogPrefixLength;
    }

    /**
     * @param lastLogPrefixLength {@link #lastLogPrefixLength}
     */
    public static void setLastLogPrefixLength(int lastLogPrefixLength)
    {
        Log.lastLogPrefixLength = lastLogPrefixLength;
    }

    /**
     * @return {@link #logPointer}
     */
    public static char getLogPointer()
    {
        return logPointer;
    }

    /**
     * @param logPointer {@link #logPointer}
     */
    public static void setLogPointer(char logPointer)
    {
        Log.logPointer = logPointer;
    }

    /**
     * @return {@link #logFormat}
     */
    public static String getLogFormat()
    {
        return logFormat;
    }

    /**
     * @param logFormat {@link #logFormat}
     */
    public static void setLogFormat(String logFormat)
    {
        Log.logFormat = logFormat;
    }
}
