package it.sinergis.sunshine.util;

/**
 * Classe per la gestione degli errori sul servizio ImportMeteoDataSOS.
 *
 * @author Rossana Bambili
 */
public class ImportMeteoDataException extends Exception
{
    /** Serial. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ServiceException object.
     */
    public ImportMeteoDataException()
    {
        super();
    }

    /**
     * Creates a new ServiceException object.
     *
     * @param  message  message
     */
    public ImportMeteoDataException(String message)
    {
        super(message);
    }
}
