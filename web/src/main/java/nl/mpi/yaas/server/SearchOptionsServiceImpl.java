/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;
import nl.mpi.yaas.client.SearchOptionsService;
import nl.mpi.yaas.common.db.ArbilDatabase.SearchOption;

/**
 * Created on : Jan 30, 2013, 5:23:13 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@SuppressWarnings("serial")
public class SearchOptionsServiceImpl extends RemoteServiceServlet implements SearchOptionsService {

    public String[] getSearchOptions() {
        ArrayList<String> returnList = new ArrayList<String>();
        for (SearchOption searchOption : SearchOption.values()) {
            returnList.add(searchOption.toString());
        };
        return returnList.toArray(new String[0]);
    }
}
