/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.shared;

import java.io.Serializable;

/**
 * Created on : Jan 29, 2013, 6:06:24 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class YaasDataNode /* extends AbstractDataNode */ implements Serializable {

    String nodeID = null;
    String nodeName = null;
    String nodeUrl = null;
    String nodeIconId = null;

    public YaasDataNode() {
    }

//    List<FieldGroup> fieldGroups;
//    AbstractDataNode[] childArray;
//    @Override
    public String getName() {
        return nodeName;
    }

//    @Override
    public String getID() {
        return nodeID;
    }

//    @Override
    public String getUrlString() {
        return nodeUrl;
    }

//    @Override
    public String getIconId() {
        return nodeIconId;
    }
//    @Override
//    public List<FieldGroup> getFieldGroups() {
//        return fieldGroups;
//    }
//    @Override
//    public AbstractDataNode[] getChildArray() {
//        return childArray;
//    }
}
