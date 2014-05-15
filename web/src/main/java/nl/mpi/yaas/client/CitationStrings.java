/*
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package nl.mpi.yaas.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @since Apr 15, 2014 3:31:49 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public interface CitationStrings extends Messages {

    @Key("nl.mpi.citation.paneltitle")
    String panelTitle();

    @Key("nl.mpi.citation.label")
    String citationLabel(String nodeLabel);

    @Key("nl.mpi.citation.description")
    String citationDescription();

    @Key("nl.mpi.citation.details")
    String details();

    @Key("nl.mpi.citation.internalid")
    String internalId();

    @Key("nl.mpi.citation.title")
    String title();

    @Key("nl.mpi.citation.formatid")
    String formatId();

    @Key("nl.mpi.citation.formatname")
    String formatName();

    @Key("nl.mpi.citation.format")
    String format();

    @Key("nl.mpi.citation.published")
    String published();

    @Key("nl.mpi.citation.handle")
    String handle();

    @Key("nl.mpi.citation.link")
    String link();

    @Key("nl.mpi.citation.url")
    String url();
}
