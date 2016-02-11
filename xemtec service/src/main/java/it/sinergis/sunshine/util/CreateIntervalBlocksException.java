package it.sinergis.sunshine.util;

/**
 * Classe per la gestione degli errori sul servizio CreateIntervalBlocks.
 *
 * @author Rossana Bambili
 */
public class CreateIntervalBlocksException extends Exception
{
    /** Serial. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ServiceException object.
     */
    public CreateIntervalBlocksException()
    {
        super();
    }

    /**
     * Creates a new ServiceException object.
     *
     * @param  message  message
     */
    public CreateIntervalBlocksException(String message)
    {
        super(message);
    }
}
