/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import nl.mpi.flap.model.DataNodeType;
import nl.mpi.flap.model.FieldGroup;
import nl.mpi.flap.model.SerialisableDataNode;

/**
 * Created on : Jan 29, 2013, 6:06:24 PM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
@Deprecated
public class YaasDataNode extends SerialisableDataNode implements Serializable {

    String nodeID = null;
    String nodeName = null;
    DataNodeType dataNodeType = null;
    String nodeIconId = null;
    List<FieldGroup> fieldGroups = Collections.EMPTY_LIST;

    public YaasDataNode() {
    }

    public YaasDataNode(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String getLabel() {
        return nodeName;
    }

    @Override
    public void setLabel(String label) {
        nodeName = label;
    }

    @Override
    public String getID() {
        return nodeID;
    }

    @Override
    public void setID(String id) {
        nodeID = id;
    }

    @Override
    public DataNodeType getType() {
        return dataNodeType;
    }

    @Override
    public void setType(DataNodeType dataNodeType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FieldGroup> getFieldGroups() {
        return fieldGroups;
    }

    @Override
    public void setFieldGroups(List<FieldGroup> fieldGroups) {
        this.fieldGroups = fieldGroups;
    }

    @Override
    public void setChildIds(List<String> idString) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getChildIds() {
        return Collections.EMPTY_LIST;
    }
}
