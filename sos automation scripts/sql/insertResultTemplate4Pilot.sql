CREATE TEMP TABLE tmp_table_params (pilotcode text);
INSERT INTO tmp_table_params VALUES (:pilot_code);



DO LANGUAGE plpgsql $$
DECLARE
	pilot text;
	codeOffering text;
	offeringIdentifier text;
	offeringIdentifierBaseName text;
	posSubOff int;
	nOff int;
	nTemplate int;
	count_foi int;
	maxidTemplate bigint;
	observableP bigint;
	foiId bigint;
	countreletedf bigint;
	unitCode text;
	rec record;
	recProc record;
	observablePident text;
	
	
	BEGIN
		--#####################LETTURA PARAMETRI IN INPUT##############################
		SELECT pilotcode INTO pilot FROM tmp_table_params LIMIT 1;
		--#####################FINE LETTURA PARAMETRI IN INPUT##############################
		
		
		
		--per ogni procedure del pilot in input
		DROP TABLE IF EXISTS procPilotTempTable;
		EXECUTE 'CREATE TEMP TABLE procPilotTempTable AS SELECT * FROM procedure WHERE identifier LIKE (''%' || pilot || '-%'')';
		--Loop sulla tabella appena creata
		FOR rec IN SELECT * FROM procPilotTempTable
		LOOP
			--per ogni procedure del pilot in input
			DROP TABLE IF EXISTS constellationProcTempTable;
			EXECUTE 'CREATE TEMP TABLE constellationProcTempTable AS SELECT * FROM observationconstellation WHERE procedureid = ' || rec.procedureid;
			--Loop sulla tabella appena creata
			FOR recProc IN SELECT * FROM constellationProcTempTable
			LOOP
				SELECT identifier INTO offeringIdentifier FROM offering WHERE offeringid = recProc.offeringid LIMIT 1;
				EXECUTE 'select position(''' || pilot || ''' in ''' || offeringIdentifier || ''')' INTO posSubOff;
				EXECUTE 'select substring('''|| offeringIdentifier ||''' from ' || posSubOff || ')' INTO offeringIdentifierBaseName;
				--EXECUTE 'SELECT count(*) FROM observation WHERE identifier like (''%' || offeringIdentifierBaseName || '%'')' INTO nOff;
				EXECUTE 'SELECT count(*) FROM resulttemplate WHERE identifier like (''%' || offeringIdentifierBaseName || '%'')' INTO nTemplate;
				
				--controllo che ci siano osservazione per un determinato offering e che il template non sia gia' stato creato
				if (nTemplate = 0) THEN
					raise info 'insertr';
				    --recupero l'id per il template
					EXECUTE 'select max(resulttemplateid) from resulttemplate' INTO maxidTemplate;
					--recupero l'observableproperty
					EXECUTE 'select observablepropertyid from observationconstellation WHERE offeringid = '|| recProc.offeringid ||' LIMIT 1' INTO observableP;
					EXECUTE 'select identifier from observableproperty where observablepropertyid = '||observableP into observablePident;
						


					
					
					
					raise info 'obsep %',observableP;
					raise info 'offeid %',recProc.offeringid;		 
					--recupero la FOI
					--EXECUTE 'select featureofinterestid from relatedfeature where relatedfeatureid = (select relatedfeatureid from offeringhasrelatedfeature where offeringid = '|| recProc.offeringid ||') LIMIT 1' INTO foiId;
					 EXECUTE 
							'select featureofinterestid from procedure 
							left join series on procedure.procedureid = series.procedureid
							where  procedure.procedureid  =' || rec.procedureid INTO foiId; 
					raise info 'foiid %',foiId;
					--recupero la unit
					--EXECUTE 'select unit from unit WHERE unitid = (SELECT unitid FROM observation WHERE identifier like (''%' || offeringIdentifierBaseName || '%'') LIMIT 1)' INTO unitCode;
					
					EXECUTE 'select count (*) from featureofinterest where featureofinterestid = '|| foiId INTO count_foi;
					raise info 'start if ';
					
					EXECUTE 'select 
							substring (offering.identifier from 57 for 4)
							from offering  where offeringid = '||recProc.offeringid into unitCode ;
							

					
					
					
					
					

					IF count_foi > 0 THEN
						if maxidTemplate is null then 
							maxidTemplate = 0;
							raise info '@@@@@@@@@@@@@@@@@@nullo % ',unitCode;
						end if;
					
						INSERT INTO resulttemplate(
							resulttemplateid,
							 offeringid, observablepropertyid, procedureid, 
							featureofinterestid, identifier, resultstructure, resultencoding)
							VALUES (maxidTemplate +1 , 
							recProc.offeringid, 
							observableP, 
							recProc.procedureid, 
							foiId, 
							'http://www.sunshineproject.eu/swe/template/' ||offeringIdentifierBaseName ,
							'<swe:DataRecord xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:om="http://www.opengis.net/om/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:sf="http://www.opengis.net/sampling/2.0" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:swe="http://www.opengis.net/swe/2.0" xmlns:sams="http://www.opengis.net/samplingSpatial/2.0" xmlns:sos="http://www.opengis.net/sos/2.0">
							<swe:field name="phenomenonTime">
							<swe:Time definition="http://www.opengis.net/def/property/OGC/0/PhenomenonTime">
							<swe:uom code="ms"/>
							</swe:Time>
							</swe:field>
							<swe:field name="value">
							<swe:Quantity definition="'|| observablePident ||'">
							<swe:uom code="' || unitCode || '"/>
							</swe:Quantity>
							</swe:field>
							</swe:DataRecord>',	
							'<swe:TextEncoding xmlns:swes="http://www.opengis.net/swes/2.0" xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:om="http://www.opengis.net/om/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:sf="http://www.opengis.net/sampling/2.0" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:swe="http://www.opengis.net/swe/2.0" xmlns:sams="http://www.opengis.net/samplingSpatial/2.0" xmlns:sos="http://www.opengis.net/sos/2.0" tokenSeparator="," blockSeparator="@@"/>'
							);
							
							--verifico se il record gia esiste
							/*EXECUTE 'SELECT count(*) FROM featurerelation WHERE parentfeatureid = '|| foiId ||' and  childfeatureid = ' || foiId  INTO countreletedf;
							if (countreletedf = 0 )THEN 
								INSERT INTO featurerelation(
								parentfeatureid, childfeatureid)
								VALUES (foiId, foiId);
							END IF;*/
					END IF;
				END IF;
			END LOOP;
		END LOOP;
	END $$;




