/**
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
package nl.mpi.yams.common.data;

/**
 * Created on : Feb 4, 2013, 10:22:27 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class QueryDataStructures {

    public enum SearchOption {

        contains(SearchType.contains, SearchNegator.is, "contains"),
        notcontains(SearchType.contains, SearchNegator.not, "does not contain"),
        equals(SearchType.equals, SearchNegator.is, "equals"),
        notequals(SearchType.equals, SearchNegator.not, "does not equal"),
        fuzzy(SearchType.fuzzy, SearchNegator.is, "fuzzy match");

        private SearchOption(SearchType searchType, SearchNegator searchNegator, String displayName) {
            this.searchType = searchType;
            this.searchNegator = searchNegator;
            this.displayName = displayName;
        }
        final SearchType searchType;
        final SearchNegator searchNegator;
        final String displayName;

        public SearchType getSearchType() {
            return searchType;
        }

        public SearchNegator getSearchNegator() {
            return searchNegator;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum SearchType {

        contains,
        equals,
        //        like,
        fuzzy,
//        regex
    }

    public enum SearchNegator {

        is, not
    }

    public enum CriterionJoinType {

        union("Union (match any)"), intersect("Intersection (match all)"); //, except("Difference");
        final private String displayName;

        private CriterionJoinType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
