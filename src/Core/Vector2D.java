/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.io.Serializable;

/**
 *
 * @author Rafael
 */
public class Vector2D implements Serializable{
    private double x;
    private double y;
    public Vector2D parent;
    
    public Vector2D(){
        x = y = 0.0;
        parent = null;
    }
    
    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
        parent = null;
    }
    
    public Vector2D (Vector2D v2){
        this.setTo(v2);
        parent = null;
    }
    
    public void translate(Vector2D v2){
        this.x += v2.x;
        this.y += v2.y;
    }
    
    public final void setTo(Vector2D v2){
        this.x = v2.x;
        this.y = v2.y;
    }
    
    public Vector2D scale (double s){
        return new Vector2D(this.x * s, this.y * s);
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public double getX(){
        double r = this.x;
        if (parent != null){
            r += parent.getX();
        }
        return r;
    }
    
    public double getY(){
        double r = this.y;
        if (parent != null){
            r += parent.getY();
        }
        return r;
    }
    
    public double getRelativeX(){
        return this.x;
    }
    
    public double getRelativeY(){
        return this.y;
    }
    
    //Determina o vetor pai, mantendo sua posição relativa
    public void setParent(Vector2D p){
        parent = p;
    }
    
    public double distanceEuclidian(Vector2D vOther){
        return Math.sqrt( Math.pow(x-vOther.x, 2) + Math.pow(y-vOther.y, 2) );
    }
}
