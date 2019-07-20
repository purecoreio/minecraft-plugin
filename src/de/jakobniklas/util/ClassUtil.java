package de.jakobniklas.util;

/**
 * Utilities class
 *
 * @author Jakob-Niklas See
 * @see #getCallerCallerClassName()
 * @see #getCallerClassName()
 */
public class ClassUtil
{
    /**
     * @return The classname of the class which called the class which called this method (stacktrace)
     *
     * @see Log
     */
    public static String getCallerCallerClassName()
    {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String callerClassName = null;

        for(int i = 1; i < stElements.length; i++)
        {
            StackTraceElement ste = stElements[i];
            if
            (
                    !ste.getClassName().equals(ClassUtil.class.getName()) &&
                            ste.getClassName().indexOf("java.lang.Thread") != 0
            )
            {
                if(callerClassName == null)
                {
                    callerClassName = ste.getClassName();
                }
                else if(!callerClassName.equals(ste.getClassName()))
                {
                    return ste.getClassName();
                }
            }
        }

        return null;
    }

    /**
     * @return The classname of the class which called this method (stacktrace)
     */
    public static String getCallerClassName()
    {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

        for(int i = 1; i < stElements.length; i++)
        {
            StackTraceElement ste = stElements[i];

            if(!ste.getClassName().equals(ClassUtil.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0)
            {
                return ste.getClassName();
            }
        }

        return null;
    }
}
