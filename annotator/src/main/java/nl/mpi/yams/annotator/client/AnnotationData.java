/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.annotator.client;

/**
 * @since Jan 31, 2014 10:04 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class AnnotationData {

    private double inTime = 0;
    private double outTime = 0;
    private String annotationHtml = "";

    public AnnotationData() {
    }

    public AnnotationData(double inTime, double outTime, String annotationHtml) {
        this.inTime = inTime;
        this.outTime = outTime;
        this.annotationHtml = annotationHtml;
    }

    public double getInTime() {
        return inTime;
    }

    public void setInTime(double inTime) {
        this.inTime = inTime;
    }

    public double getOutTime() {
        return outTime;
    }

    public void setOutTime(double outTime) {
        this.outTime = outTime;
    }

    public String getAnnotationHtml() {
        return annotationHtml;
    }

    public void setAnnotationHtml(String annotationHtml) {
        this.annotationHtml = annotationHtml;
    }
}
