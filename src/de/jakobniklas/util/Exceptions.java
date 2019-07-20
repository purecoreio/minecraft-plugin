package de.jakobniklas.util;

/**
 * Class to handle any {@link Exception Exception} occurring
 *
 * @author Jakob-Niklas See
 * @see #handle(Exception)
 */
public class Exceptions
{
    /**
     * Handles exception occurring (accepting {@link Exception Exception} object) by printing the stacktrace to {@link
     * System#out}
     *
     * @param e {@link Exception Exception} object
     *
     * @return void
     */
    public static void handle(Exception e)
    {
        e.printStackTrace();
    }
}
