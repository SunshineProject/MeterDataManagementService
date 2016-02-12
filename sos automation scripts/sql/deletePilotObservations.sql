CREATE TEMP TABLE tmp_table_params (keysearch text);
INSERT INTO tmp_table_params VALUES ( :keytosearch);



DO LANGUAGE plpgsql $$
DECLARE
	key_search text;
	rec record;
	contOb integer;

	BEGIN
		--#####################LETTURA PARAMETRI IN INPUT##############################
		SELECT keysearch INTO key_search FROM tmp_table_params LIMIT 1;
		--#####################FINE LETTURA PARAMETRI IN INPUT##############################
		
		EXECUTE 'CREATE TEMP TABLE observationsTodelete AS SELECT observationid FROM observation WHERE identifier LIKE (''%' || key_search || '%'')';
		EXECUTE 'SELECT COUNT(*) FROM observationsTodelete' into contOb;
		raise INFO 'Stai eliminando % osservazioni, hai 20secondi per poter fermare il processo con ctrl+c', contOb;
		SELECT pg_sleep(20);
		DELETE FROM observationhasoffering WHERE observationid in (select * from observationsTodelete);
		DELETE FROM numericvalue WHERE observationid in (select * from observationsTodelete);
		DELETE FROM observation WHERE observationid in (select * from observationsTodelete);
		raise INFO '% osservazioni cancellate', contOb;
	END $$;




