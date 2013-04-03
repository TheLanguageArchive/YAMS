/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.shared;

/**
 * Created on : Jan 31, 2013, 11:58:57 AM
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class WebQueryException extends Exception {

    public WebQueryException() {
    }

    public WebQueryException(Throwable thrwbl) {
        super(thrwbl);
    }

    public WebQueryException(String string) {
        super(string);
    }
}
