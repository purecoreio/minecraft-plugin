package de.jakobniklas.util;

/**
 * Class used to interact with the {@link Thread Thread} of the class which is calling this classes methods
 *
 * @author Jakob-Niklas See
 * @see #defaultSleep
 * @see #sleep()
 * @see #sleep(int)
 * @see #getDefaultSleep()
 * @see #setDefaultSleep(int)
 */
public class ThreadUtil
{
    /**
     * Default sleep duration in milliseconds. Used in {@link #sleep()}
     *
     * @see #getDefaultSleep()
     * @see #setDefaultSleep(int)
     */
    private static int defaultSleep = 10;

    /**
     * Pauses the {@link Thread Thread} of the calling class for an amount of milliseconds
     * <br> exceptions handled by {@link de.jakobniklas.util.Exceptions Exceptions} class
     *
     * @param ms sleeping amount in milliseconds
     *
     * @return void
     *
     * @see #sleep()
     */
    public static void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException e)
        {
            Exceptions.handle(e);
        }
    }

    /**
     * Pauses the {@link Thread Thread} of the calling class for the {@link #defaultSleep} amount of milliseconds
     *
     * @return void
     *
     * @see #sleep(int)
     */
    public static void sleep()
    {
        sleep(defaultSleep);
    }

    /**
     * @return {@link #defaultSleep}
     */
    public static int getDefaultSleep()
    {
        return defaultSleep;
    }

    /**
     * @param defaultSleep {@link #defaultSleep}
     */
    public static void setDefaultSleep(int defaultSleep)
    {
        ThreadUtil.defaultSleep = defaultSleep;
    }
}
