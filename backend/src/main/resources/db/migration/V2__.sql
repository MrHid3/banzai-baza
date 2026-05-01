UPDATE member
    SET phone_number = '000 000 000'
    WHERE phone_number IS NULL;

ALTER TABLE member
    ALTER COLUMN location_id SET NOT NULL;

ALTER TABLE member
    ALTER COLUMN monthly_fee SET NOT NULL;

ALTER TABLE member
    ALTER COLUMN phone_number SET NOT NULL;