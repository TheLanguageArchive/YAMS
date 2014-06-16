--
-- Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
--
-- This program is free software; you can redistribute it and/or
-- modify it under the terms of the GNU General Public License
-- as published by the Free Software Foundation; either version 2
-- of the License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
--

--CREATE DATABASE "BasexComparisonDB" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'C' LC_CTYPE = 'C';


--\connect "BasexComparisonDB"

-- This file is exists only to be used as a quick comparison of a relation db's speed compared to basex's on an equivalent sized data set and similar query

DROP TABLE document_basex_test;
DROP TABLE document_link_basex_test;

CREATE TABLE document_basex_test (
    document_id SERIAL PRIMARY KEY UNIQUE NOT NULL,
    external_id text UNIQUE NOT NULL,
    time_stamp timestamp with time zone default now()
);

CREATE TABLE document_link_basex_test (
    document_link_id SERIAL PRIMARY KEY UNIQUE NOT NULL,
    external_id text UNIQUE NOT NULL,
    time_stamp timestamp with time zone default now()
);


INSERT INTO document_basex_test (external_id)
SELECT x.counter
  FROM generate_series(100,20001) AS x(counter);

INSERT INTO document_link_basex_test (external_id)
SELECT x.counter
  FROM generate_series(1,20000) AS x(counter);

SELECT external_id FROM document_link_basex_test WHERE external_id NOT IN (SELECT external_id FROM document_basex_test);

