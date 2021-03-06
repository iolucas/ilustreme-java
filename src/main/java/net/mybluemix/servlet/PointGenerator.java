/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mybluemix.servlet;

//import java.awt.Point;
import org.opencv.core.Point;

/**
 *
 * @author Du0X
 */
public class PointGenerator {
    double aFact;
    double bFact;

    public PointGenerator(double aFact, double bFact)
    {
        this.aFact = aFact;
        this.bFact = bFact;
    }

    public PointGenerator(Point p1, Point p2)
    {
        if (p1.x == p2.x)
            p1.x += 1;

        aFact = (double)(p1.y - p2.y) / (double)(p1.x - p2.x);
        bFact = p1.y - aFact * p1.x;
    }

    public Point GetFromX(double xValue)
    {
        return new Point((int)xValue, (int)(aFact * xValue + bFact));
    }

    public Point GetFromY(double yValue)
    {
        return new Point((int)((yValue - bFact) / aFact), (int)yValue);
    }    
}
