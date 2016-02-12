DO LANGUAGE plpgsql $$
DECLARE
	rec record;
	t1 text;
	t2 text;
	BEGIN
		--Loop sulla tabella appena creata
		FOR rec IN SELECT * FROM observation where identifier like ('%<parameter>%') and identifier not LIKE ('%_LR%')
		LOOP
			EXECUTE 'select (substring ((select identifier from observation where observationid = '|| rec.observationid ||'), 0,60))' into t1;
			EXECUTE 'select (substring ((select identifier from observation where observationid = '|| rec.observationid ||'), 60))' into t2;
			UPDATE observation
				SET identifier = t1 || 'KWH_' || t2
				WHERE observationid = rec.observationid;
		END LOOP;
	END $$;


