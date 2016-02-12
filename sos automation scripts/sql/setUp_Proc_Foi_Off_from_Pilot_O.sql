CREATE TEMP TABLE tmp_table_params (pilotcode text, nstation integer, x text, y text, typeFoi text, lNameFoi text,lOff text, lObProp text, iStartt integer, iIncr integer);
INSERT INTO tmp_table_params VALUES ( :pilot_code, :n_weather_stations, :xList, :yList, :typeOfFoi, :listNameFoi, :listOffering, :listObPropertyOffering, :iStart, :iIncrement);

--creo tab per contatori
CREATE TEMP TABLE tmp_table_cont (icont integer, jcont integer, zcont integer);
INSERT INTO tmp_table_cont VALUES (:iStart, 0, 0);


DO LANGUAGE plpgsql $$
DECLARE
	pilot text;
	nstat integer;
	xparam text;-- lista di x separate da ;
	yparam text;-- lista di y separate da ;
	type_of_foi text;
	list_offering text;
	list_observable_property text;
	list_name_foi text;
	i integer;
	j integer;
	z integer;
	i_Start integer;
	i_Incr integer;
	count_offering integer;
	count_name integer;
	codespaceid bigint;
	featId bigint;
	id_observableproperty bigint;
	procedure_id bigint;
	observation_type_id bigint;
	offering_id bigint;
	featureofinterest_type_id bigint;
	relatedfeature_id bigint;
	c bigint;
	o bigint;
	count_foi bigint;
	BEGIN
		--#####################LETTURA PARAMETRI IN INPUT##############################
		SELECT pilotcode INTO pilot FROM tmp_table_params LIMIT 1;
		SELECT nstation INTO nstat FROM tmp_table_params LIMIT 1;
		SELECT x INTO xparam FROM tmp_table_params LIMIT 1;
		SELECT y INTO yparam FROM tmp_table_params LIMIT 1;
		SELECT iStartt INTO i_Start FROM tmp_table_params LIMIT 1;
		SELECT iIncr INTO i_Incr FROM tmp_table_params LIMIT 1;
		SELECT typeFoi INTO type_of_foi FROM tmp_table_params LIMIT 1;
		SELECT lOff INTO list_offering FROM tmp_table_params LIMIT 1;
		SELECT lObProp INTO list_observable_property FROM tmp_table_params LIMIT 1;
		SELECT lNameFoi INTO list_name_foi FROM tmp_table_params LIMIT 1;
		--#####################FINE LETTURA PARAMETRI IN INPUT##############################
		
		
		--creazione delle procedure necessarie
		SELECT icont INTO i FROM tmp_table_cont LIMIT 1;
		--raise INFO 'valore della i %',i;
		raise INFO 'valore della nstat %',nstat;
		raise INFO 'valore della i_Incr %',i_Incr;
		raise INFO 'valore della i_Start %',i_Start;
		raise INFO 'nstat*i_Incr + i_Start %',nstat * i_Incr + i_Start;

		WHILE i < (nstat*i_Incr + i_Start)
		LOOP
			raise INFO 'valore della i %',i;
			--verifico che la procedure non esista gia'
			EXECUTE 'SELECT count(*) FROM procedure WHERE identifier = ''http://www.sunshineproject.eu/swe/procedure/'|| pilot || '-O' || trim(both ' ' from to_char(i, '00')) || '''' INTO c;
			raise INFO 'count procedure %',c;
			IF ( c = 0 )THEN
				INSERT INTO procedure( hibernatediscriminator, proceduredescriptionformatid,identifier, deleted, descriptionfile)
				VALUES (
					'T',
					1, 
					'http://www.sunshineproject.eu/swe/procedure/'|| pilot || '-O' || trim(both ' ' from to_char(i, '00')),
					'F',
					null);
			END IF;
			update tmp_table_cont set icont = (select icont from tmp_table_cont limit 1) + i_Incr ;
			SELECT icont INTO i FROM tmp_table_cont LIMIT 1;
		END LOOP;
		
		
		
		--creazione delle featureofinterest
		
		--prima devo ricavare il codespaceid
		EXECUTE 'SELECT codespaceid FROM codespace WHERE codespace = ''http://www.sunshineproject.eu/swe/project-pilot/' || pilot || ''' LIMIT 1' into codespaceid;

		
		SELECT zcont INTO z FROM tmp_table_cont LIMIT 1;
		
		update tmp_table_cont set icont = i_Start ;
		SELECT icont INTO i FROM tmp_table_cont LIMIT 1;
		WHILE i < (nstat*i_Incr + i_Start)
		LOOP
		
			
			EXECUTE 'select count (*) from unnest(string_to_array('''|| list_name_foi || ''', '';''))' INTO count_name;
			
			IF (count_name = 0) THEN 
				
				--verifico che la featureOfinterest esiste
				EXECUTE 'SELECT count(*) FROM featureofinterest WHERE identifier = ''http://www.sunshineproject.eu/swe/featureOfInterest/'|| pilot || '/' || type_of_foi ||'/' ||  trim(both ' ' from to_char(i, '00')) || '''' INTO c;
			
				IF ( c = 0 )THEN 
					INSERT INTO featureofinterest( hibernatediscriminator, featureofinteresttypeid, 
						identifier, codespaceid, name, geom, descriptionxml, url)
					VALUES (
					'T',
					6, 
					'http://www.sunshineproject.eu/swe/featureOfInterest/'|| pilot || '/'|| type_of_foi ||'/' ||  trim(both ' ' from to_char(i, '00')),
					codespaceid,
					null,
					ST_SetSRID(ST_MakePoint((split_part(xparam, ';' , z+1):: double precision),(split_part(yparam, ';' , z+1):: double precision)),4326),
					null,
					null);
				END IF;
				
				--creazione delle entry su reletedfeature
				--prima mi devo ricare l'id della featureofinterest appena committata
				EXECUTE 'SELECT featureofinterestid FROM featureofinterest WHERE identifier = ''http://www.sunshineproject.eu/swe/featureOfInterest/'|| pilot || '/'|| type_of_foi ||'/' ||  trim(both ' ' from to_char(i, '00')) || ''' LIMIT 1' into featId;
			ELSE --se invece ci sono i nomi come parametro di ingresso
				
				
				--verifico che la featureOfinterest esiste
				EXECUTE 'SELECT count(*) FROM featureofinterest WHERE identifier = ''http://www.sunshineproject.eu/swe/featureOfInterest/'|| pilot || '/'|| type_of_foi ||'/' ||  (split_part(list_name_foi, ';' , z+1):: text) || '''' INTO c;
				IF ( c = 0 )THEN
					INSERT INTO featureofinterest( hibernatediscriminator, featureofinteresttypeid, 
						identifier, codespaceid, name, geom, descriptionxml, url)
					VALUES (
					'T',
					6, 
					'http://www.sunshineproject.eu/swe/featureOfInterest/'|| pilot || '/'|| type_of_foi ||'/' ||  (split_part(list_name_foi, ';' , z+1):: text),
					codespaceid,
					null,
					ST_SetSRID(ST_MakePoint((split_part(xparam, ';' , z+1):: double precision),(split_part(yparam, ';' , z+1):: double precision)),4326),
					null,
					null);
				END IF;
				
				--prima mi devo ricare l'id della featureofinterest appena committata
				EXECUTE 'SELECT featureofinterestid FROM featureofinterest WHERE identifier = ''http://www.sunshineproject.eu/swe/featureOfInterest/'|| pilot || '/'|| type_of_foi ||'/' ||  (split_part(list_name_foi, ';' , z+1):: text) || ''' LIMIT 1' into featId;
			
			END IF;
			
			--CORRETTO DOPO
			EXECUTE 'select count (*) from relatedfeature where featureofinterestid = '|| featId INTO count_foi;
			IF (count_foi = 0 ) THEN
				INSERT INTO relatedfeature(featureofinterestid)
				VALUES (featId);
			END IF;
			--recupero l'id della relatedfeature
			SELECT relatedfeatureid INTO relatedfeature_id FROM relatedfeature WHERE featureofinterestid = featId ORDER BY relatedfeatureid DESC LIMIT 1;
			
			
			--recupero id della procedura
			SELECT procedure.procedureid INTO procedure_id FROM procedure WHERE identifier LIKE('%' || pilot || '-O' || trim(both ' ' from to_char(i, '00')) ||'%') LIMIT 1;
			
			--recupero id della observationtype
			SELECT observationtype.observationtypeid INTO observation_type_id FROM observationtype WHERE observationtype LIKE('%OM_Measurement%') LIMIT 1;
			
			--recupero dell'id della featureofinteresttype_id
			SELECT featureofinteresttypeid INTO featureofinterest_type_id FROM featureofinteresttype WHERE featureofinteresttype = 'http://www.sunshineproject.eu/swe/featureOfInterestType/Region' LIMIT 1;
			

			--aggirono il contatore j prima di riutilizzarlo
			update tmp_table_cont set jcont = 0 ;
			SELECT jcont INTO j FROM tmp_table_cont LIMIT 1;
			EXECUTE 'select count (*) from unnest(string_to_array('''|| list_offering || ''', '';''))' INTO count_offering;
			
			raise INFO 'valore di j %', j;
			
			
			WHILE j < count_offering
			LOOP
				raise INFO 'in while';
				--se non c'e' gia' inserisco la offering																						'-O' || trim(both ' ' from to_char(i, '00'))
				EXECUTE 'SELECT count(*) FROM offering WHERE identifier = ''http://www.sunshineproject.eu/swe/offering/' || pilot || '-O' || trim(both ' ' from to_char(j, '00')) || '_' || (split_part(list_offering, ';' , j+1):: text) || '''' INTO o;
				raise INFO 'valore di o %', o;
				IF ( o = 0 )THEN --creo la offering solo se non esiste gia'
					INSERT INTO offering( hibernatediscriminator, identifier, name)
					VALUES ('T','http://www.sunshineproject.eu/swe/offering/' || pilot || '-O' || trim(both ' ' from to_char(j, '00')) || '_' || (split_part(list_offering, ';' , j+1):: text), null);
				END IF;--o 
					
				EXECUTE 'SELECT count(*) FROM observableproperty WHERE identifier like (''%' || (split_part(list_observable_property, ';' , j+1):: text) || '%'')' INTO c;
				raise INFO 'valore di c %', c;
				IF ( c > 0 )THEN
					--recupero l'id della proprieta
					--raise INFO 'proprieta %', split_part(list_offering, ';' , j+1);
					SELECT observablepropertyid INTO id_observableproperty FROM observableproperty WHERE identifier LIKE('%' || (split_part(list_observable_property, ';' , j+1):: text)) LIMIT 1;
					raise INFO 'valore di id_observableproperty %', id_observableproperty;
					raise INFO 'valore di featId %', featId;
					raise INFO 'valore di procedure_id %', procedure_id;
					
					--se non c'e' gia' inserisco la series
					EXECUTE 'SELECT count(*) FROM series WHERE featureofinterestid = ' || featId || ' and  observablepropertyid = '|| id_observableproperty || ' and procedureid = '|| procedure_id INTO o;
					raise INFO 'questa altra o (select in series delle FOI) %', o;
					IF ( o = 0 )THEN
						INSERT INTO series (featureofinterestid , observablepropertyid, procedureid, deleted) VALUES (featId , id_observableproperty , procedure_id , 'F');
					END IF;--o
					--recupero l'offeringid appena inserito
					SELECT offeringid INTO offering_id FROM offering WHERE identifier = 'http://www.sunshineproject.eu/swe/offering/' || pilot || '-O' || trim(both ' ' from to_char(j, '00')) || '_' || (split_part(list_offering, ';' , j+1):: text) LIMIT 1;
					
					--se non c'e' gia' inserisco la obconstellation
					EXECUTE 'SELECT count(*) FROM observationconstellation WHERE observablepropertyid = ' || id_observableproperty || ' and  procedureid = '|| procedure_id || ' and offeringid = ' || offering_id INTO o;
					IF ( o = 0 )THEN		
						INSERT INTO observationconstellation(
						observablepropertyid, procedureid, 
						observationtypeid, offeringid, deleted, hiddenchild)
						VALUES (
						id_observableproperty,
						procedure_id, 
						observation_type_id,
						offering_id,
						'F',
						'F');
					
					END IF;--o
					
					--controllo esistenza su offeringallowedobservationtype
					EXECUTE 'SELECT count(*) FROM offeringallowedobservationtype WHERE offeringid = ' || offering_id || ' and  observationtypeid = '|| observation_type_id INTO o;
					IF ( o = 0 )THEN
						INSERT INTO offeringallowedobservationtype(
						offeringid, observationtypeid)
						VALUES (offering_id, observation_type_id);
					END IF;--o
					
					--controllo esistenza su offeringallowedfeaturetype
					EXECUTE 'SELECT count(*) FROM offeringallowedfeaturetype WHERE offeringid = ' || offering_id || ' and  featureofinteresttypeid = '|| featureofinterest_type_id INTO o;
					IF ( o = 0 )THEN
						INSERT INTO offeringallowedfeaturetype(
						offeringid, featureofinteresttypeid)
						VALUES (offering_id, featureofinterest_type_id);
					END IF;--o
					
					--controllo esistenza su offeringhasrelatedfeature
					EXECUTE 'SELECT count(*) FROM offeringhasrelatedfeature WHERE offeringid = ' || offering_id || ' and  relatedfeatureid = '|| relatedfeature_id INTO o;
					IF ( o = 0 )THEN
						INSERT INTO offeringhasrelatedfeature(
						relatedfeatureid, offeringid)
						VALUES (relatedfeature_id, offering_id);
					END IF;--o
				END IF;--c
				
				
				--aggiornamento del valore del contatore j
				update tmp_table_cont set jcont = (select jcont from tmp_table_cont limit 1) + 1 ;
				SELECT jcont INTO j FROM tmp_table_cont LIMIT 1;
			END LOOP;

			
			--aggiornamento del valore del contatore i
			update tmp_table_cont set icont = (select icont from tmp_table_cont limit 1) + i_Incr ;
			SELECT icont INTO i FROM tmp_table_cont LIMIT 1;
			--aggiornamento del valore del contatore z
			update tmp_table_cont set zcont = (select zcont from tmp_table_cont limit 1) + 1 ;
			SELECT zcont INTO z FROM tmp_table_cont LIMIT 1;
			
			
		END LOOP;
		raise INFO 'ESECUZIONE TERMINATA';
	END $$;


