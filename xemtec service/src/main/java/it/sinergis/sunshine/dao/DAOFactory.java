package it.sinergis.sunshine.dao;

/**
 * Factory utilizzata per ottenere gli oggetti DAO.
 * 
 * @author Rossana Bambili
 */
public abstract class DAOFactory {
    /**
   
       
    
    /**
     * getXemtecDevicesDAO
     * @return XemtecDevicesDAO
     */
    public abstract XemtecDevicesDAO getXemtecDevicesDAO();
    
    /**
     * getXemtecValuesDAO
     * @return XemtecValuesDAO
     */
    public abstract XemtecValuesDAO getXemtecValuesDAO();
}
