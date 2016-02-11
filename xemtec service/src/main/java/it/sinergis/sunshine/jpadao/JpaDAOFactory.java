package it.sinergis.sunshine.jpadao;

import it.sinergis.sunshine.dao.DAOFactory;
import it.sinergis.sunshine.dao.XemtecDevicesDAO;
import it.sinergis.sunshine.dao.XemtecValuesDAO;

/**
 * {@inheritDoc}
 * 
 * @author Rossana Bambili
 * @see it.sinergis.sunshine.dao.DAOFactory
 */
public class JpaDAOFactory extends DAOFactory {
    /**
     * Costruttore di default.
     */
    public JpaDAOFactory() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see getXemtecDevicesDAO()
     */
    @Override
    public XemtecDevicesDAO getXemtecDevicesDAO() {
        return new JpaXemtecDevicesDAO();
    }

    /**
     * {@inheritDoc}
     * 
     * @see getXemtecValuesDAO()
     */
    @Override
    public XemtecValuesDAO getXemtecValuesDAO() {
        return new JpaXemtecValuesDAO();
    }

}
