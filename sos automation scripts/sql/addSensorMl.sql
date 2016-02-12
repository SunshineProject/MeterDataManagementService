DO LANGUAGE plpgsql $$
DECLARE
	contProcedure integer;
	identifierProcedure text;
	FOI_x double precision;
	FOI_y double precision;
	inputStringOb text := ''; 
	outputStringOb text := ''; 
	proc record;
	observableprop record;
	BEGIN
		
		--ciclo sulle procedure che non hanno il sensorMl associato
		DROP TABLE IF EXISTS procedureToAddSensorMl;
		EXECUTE 'CREATE TEMP TABLE procedureToAddSensorMl AS select DISTINCT ON (procedure.identifier) featureofinterest.identifier as foiidentifier, procedure.identifier as procidentifier, procedure.procedureid
				from procedure join series on procedure.procedureid = series.procedureid join featureofinterest on featureofinterest.featureofinterestid = series.featureofinterestid
				where procedure.procedureid not in (select validproceduretime.procedureid from validproceduretime)';
		EXECUTE 'SELECT COUNT(*) FROM procedureToAddSensorMl' into contProcedure;
		raise INFO 'Processo di aggiunta sensorMl a  % procedure avviato.', contProcedure;
		--Loop sulla tabella appena creata
		FOR proc IN SELECT * FROM procedureToAddSensorMl
		LOOP
			raise info 'proc.procidentifier %', proc.foiidentifier;
			EXECUTE 'SELECT ST_X(geom)
					FROM featureofinterest
					WHERE identifier = ''' || proc.foiidentifier || '''' into FOI_x;
			
			EXECUTE 'SELECT ST_Y(geom)
					FROM featureofinterest
					WHERE identifier = ''' || proc.foiidentifier || '''' into FOI_y;
			raise INFO 'FOI_x  %', FOI_x;
			raise INFO 'FOI_y  %', FOI_y;
			
		
			DROP TABLE IF EXISTS tableObservableProperty;
			EXECUTE 'CREATE TEMP TABLE tableObservableProperty AS 
					select observableproperty.observablepropertyid, observationconstellation.offeringid, observableproperty.identifier
					from observationconstellation join observableproperty on observationconstellation.observablepropertyid = observableproperty.observablepropertyid
					where procedureid = ''' || proc.procedureid || ''''; 
		
		
			--creo tab temporanea per le observable property
			--CREATE TEMP TABLE tmp_table_observable_property (xml text);
			--Loop sulla tabella appena creata, per ogni proprieta osservata occorre inserire nell'xml
			FOR observableprop IN SELECT * FROM tableObservableProperty
			LOOP
				
				--INSERT INTO tmp_table_observable_property( xml )
				--VALUES (
				--	'<sml:input name="observable_property_'|| observableprop.observablepropertyid::text ||'">
				--			<swe:ObservableProperty definition="'|| observableprop.identifier ||'"/>
				--	</sml:input>'
				--	);	
					
				inputStringOb := inputStringOb || '<sml:input name="observable_property_'|| observableprop.observablepropertyid::text ||'">
							<swe:ObservableProperty definition="'|| observableprop.identifier ||'"/>
					</sml:input>';
			END LOOP;
			
			
			
			
			--creo tab temporanea per le observable property OUT
			--CREATE TEMP TABLE tmp_table_observable_property_out (xml text);
			--Loop sulla tabella appena creata, per ogni proprieta osservata occorre inserire nell'xml
			FOR observableprop IN SELECT * FROM tableObservableProperty
			LOOP
				
				--INSERT INTO tmp_table_observable_property_out( xml )
				--VALUES (
				--	'<sml:output name="test_observable_property_'|| observableprop.observablepropertyid::text ||'">
				--		<swe:Category definition="'|| observableprop.identifier ||'">
				--			<swe:codeSpace xlink:href="NOT_DEFINED"/>
				--		</swe:Category>
				--	</sml:output>'
				--	);	
					
				outputStringOb := outputStringOb || 
										'<sml:output name="test_observable_property_'|| observableprop.observablepropertyid::text ||'">
						<swe:Category definition="'|| observableprop.identifier ||'">
							<swe:codeSpace xlink:href="NOT_DEFINED"/>
						</swe:Category>
					</sml:output>';
					
			END LOOP;
			
			
			
			--ORA INSERISCO IL TEMPLATE
			INSERT INTO validproceduretime( validproceduretimeid, procedureid, proceduredescriptionformatid, starttime, endtime, descriptionxml)
				VALUES (
				nextval('validproceduretimeid_seq'::regclass),
				proc.procedureid,
				1,
				now()::date,
				null,
'<sml:SensorML xmlns:sml="http://www.opengis.net/sensorML/1.0.1" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.0.1">
<sml:member>
<sml:System>
 <!--optional; generated if not present-->
 <sml:identification>
<sml:IdentifierList>
<sml:identifier name="'|| proc.procidentifier ||'">
<sml:Term definition="urn:ogc:def:identifier:OGC:1.0:uniqueID">
<sml:value>'|| proc.procidentifier ||'</sml:value>
</sml:Term>
</sml:identifier>
<sml:identifier name="longName">
<sml:Term definition="urn:ogc:def:identifier:OGC:1.0:longName">
 <sml:value>'|| proc.procidentifier ||'</sml:value>
</sml:Term>
 </sml:identifier>
 <sml:identifier name="shortName">
<sml:Term definition="urn:ogc:def:identifier:OGC:1.0:shortName">
 <sml:value>'|| proc.procidentifier ||'</sml:value>
</sml:Term>
 </sml:identifier>
</sml:IdentifierList>
 </sml:identification>
 <sml:capabilities name="featuresOfInterest">
<swe:SimpleDataRecord>
  <swe:field name="featureOfInterestID">
<swe:Text>
 <swe:value>'|| proc.foiidentifier ||'</swe:value>
</swe:Text>
 </swe:field>
</swe:SimpleDataRecord>
 </sml:capabilities>
 <sml:position name="sensorPosition">
<swe:Position referenceFrame="urn:ogc:def:crs:EPSG::4326">
 <swe:location>
<swe:Vector gml:id="STATION_LOCATION">
 <swe:coordinate name="easting">
<swe:Quantity axisID="x">
 <swe:uom code="degree"/>
 <swe:value>'|| trim(to_char(FOI_x,'999D9999999999999')) ||'</swe:value>
</swe:Quantity>
 </swe:coordinate>
 <swe:coordinate name="northing">
<swe:Quantity axisID="y">
 <swe:uom code="degree"/>
 <swe:value>'|| trim(to_char(FOI_y,'999D9999999999999')) ||'</swe:value>
</swe:Quantity>
 </swe:coordinate>
 <swe:coordinate name="altitude">
<swe:Quantity axisID="z">
 <swe:uom code="m"/>
 <swe:value>20.0</swe:value>
</swe:Quantity>
 </swe:coordinate>
</swe:Vector>
 </swe:location>
</swe:Position>
 </sml:position>
 <sml:inputs>
<!--fenomeni osservati-->
<sml:InputList>
  ' || inputStringOb || '
</sml:InputList>
 </sml:inputs>
 <sml:outputs>
<sml:OutputList>
 ' || outputStringOb || '
</sml:OutputList>
 </sml:outputs>
</sml:System>
 </sml:member>
</sml:SensorML>'
);
		inputStringOb := '';
		outputStringOb := '';
		END LOOP;
		raise INFO 'ESECUZIONE TERMINATA';
	END $$;
