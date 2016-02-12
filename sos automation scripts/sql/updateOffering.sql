DO LANGUAGE plpgsql $$
DECLARE
	rec record;
	BEGIN
		--Loop sulla tabella appena creata
		FOR rec IN SELECT * FROM offering where identifier = <parameter>
		LOOP
			UPDATE offeringallowedobservationtype
				SET observationtypeid = 3
				WHERE offeringid = rec.offeringid;
		END LOOP;
		
		FOR rec IN select * from relatedfeature where relatedfeatureid = (select relatedfeatureid from offeringhasrelatedfeature where offeringid = (select offeringid from offering where identifier = <parameter>) LIMIT 1)
		LOOP	
			UPDATE observationconstellation
				SET observationtypeid = 3
				WHERE offeringid = (SELECT offeringid FROM offering where identifier = <parameter>);
		END LOOP;
	END $$;




