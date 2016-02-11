package it.sinergis.sunshine.util;

/**
 * Classe per la gestione degli errori sul servizio CreateFileCsvFromGreenButton.
 *
 * @author Rossana Bambili
 */
public class CreateFileCsvFromGBException extends Exception
{
    /** Serial. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ServiceException object.
     */
    public CreateFileCsvFromGBException()
    {
        super();
    }

    /**
     * Creates a new ServiceException object.
     *
     * @param  message  message
     */
    public CreateFileCsvFromGBException(String message)
    {
        super(message);
    }
}
