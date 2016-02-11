package it.sinergis.sunshine.dao;

import it.sinergis.sunshine.model.XemtecValues;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

/**
 * Interfaccia per la gestione dei dispostivi xemtec.
 *
 * @author Rossana Bambili
 */
public interface XemtecValuesDAO
{
   
   /**
     * Restituisce le letture associate ad un dispositivo
     * @param  id device
     * @return   Collection XemtecValues
     */
    public Collection<XemtecValues> findById(Long idDevice) throws SQLException, Exception;
    
    /**
     * Restituisce le letture associate ad un dispositivo maggiori della data
     * passata come parametro
     * @param  id device
     * @param  startdate
     * @return   Collection XemtecValues
     */
    public Collection<XemtecValues> findByDate(Long idDevice,Date startDate) throws SQLException, Exception;
    
   
}
