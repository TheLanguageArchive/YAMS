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

import com.google.gwt.user.client.ui.Anchor;

/**
 * @since May 16, 2014 3:16:09 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class StatisticsLink extends Anchor implements HistoryListener {

    private final HistoryController historyController;

    public StatisticsLink(HistoryController historyController) {
        super("View Database Statistics");
        this.historyController = historyController;
    }

    private void setStatisticsLink() {
        final String databaseName = historyController.getDatabaseName();
        final String dbStatsHref = (databaseName == null || databaseName.isEmpty()) ? "DatabaseStats.jsp" : "DatabaseStats.jsp?databaseName=" + databaseName;
        this.setHref(dbStatsHref);
    }

    public void historyChange() {
        setStatisticsLink();
    }

    public void userSelectionChange() {
        setStatisticsLink();
    }
}
