package it.sinergis.sunshine.dao;

import it.sinergis.sunshine.model.XemtecDevices;

import java.sql.SQLException;

/**
 * Interfaccia per la gestione dei dispostivi xemtec.
 *
 * @author Rossana Bambili
 */
public interface XemtecDevicesDAO
{
   
   /**
     * Restituisce il dispositivo associato ad un determinato id
     * @param  id device
     * @return  XemtecDevices
     */
    public XemtecDevices findById(Long idDevice) throws SQLException, Exception;
    
   
}
