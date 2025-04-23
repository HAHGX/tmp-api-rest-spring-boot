-- V2__update_id_column_type.sql

-- Convertir SERIAL a BIGSERIAL implica:
-- 1. Cambiar el tipo de la columna
-- 2. Actualizar la secuencia asociada

-- 1. Cambiar el tipo de la columna id de INTEGER a BIGINT
ALTER TABLE api_call_history
  ALTER COLUMN id TYPE BIGINT;

-- 2. Renombrar la secuencia antigua (si existe)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
      FROM pg_sequences
     WHERE schemaname = 'public'
       AND sequencename = 'api_call_history_id_seq'
  ) THEN
    ALTER SEQUENCE api_call_history_id_seq RENAME TO api_call_history_id_seq_old;
  END IF;
END
$$;

-- 3. Crear una nueva secuencia BIGINT
CREATE SEQUENCE IF NOT EXISTS api_call_history_id_seq
  AS BIGINT
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

-- 4. Obtener el último valor de la secuencia antigua y establecerlo en la nueva
DO $$
DECLARE
  seq_last_val BIGINT;
BEGIN
  IF EXISTS (
    SELECT 1
      FROM pg_sequences
     WHERE schemaname = 'public'
       AND sequencename = 'api_call_history_id_seq_old'
  ) THEN
    SELECT last_value
      INTO seq_last_val
      FROM api_call_history_id_seq_old;

    -- Ajustar la nueva secuencia al valor anterior
    PERFORM setval('api_call_history_id_seq', seq_last_val, true);

    -- 5. Eliminar el DEFAULT que apuntaba a la secuencia vieja
    ALTER TABLE api_call_history
      ALTER COLUMN id DROP DEFAULT;

    -- 6. Fijar el DEFAULT para usar la nueva secuencia
    ALTER TABLE api_call_history
      ALTER COLUMN id SET DEFAULT nextval('api_call_history_id_seq');

    -- 7. Borrar la secuencia antigua ya sin dependencias
    DROP SEQUENCE api_call_history_id_seq_old;
  END IF;
END
$$;

-- 8. Asegurar que la secuencia nueva esté 'owned' por la columna id
ALTER SEQUENCE api_call_history_id_seq
  OWNED BY api_call_history.id;