/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mybluemix.servlet;

import java.awt.Dimension;
//import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opencv.core.Size;
import org.opencv.core.Point;

/**
 *
 * @author Du0X
 */
public class PersonFace {
    Rectangle face;
    public PersonFace(Rectangle faceRect) { face = faceRect; }
    public Rectangle GetFace() { return face; }

    ArrayList<Rectangle> mouths = new ArrayList<Rectangle>();
        
    public void AddMouth(Rectangle mouth) { mouths.add(mouth); }

    Rectangle evaluatedMouth = new Rectangle(0, 0, 0, 0);
        
    public Rectangle GetMouth() { return evaluatedMouth; }

    ArrayList<Rectangle> eyes = new ArrayList<Rectangle>();
    ArrayList<Rectangle> evaluatedEyes = new ArrayList<Rectangle>();
    
    public void AddEye(Rectangle eye) { eyes.add(eye); }
    public Rectangle[] GetEyes() { 
        Rectangle[] evEyesArr = new Rectangle[evaluatedEyes.size()]; 
        for(int i = 0; i < evEyesArr.length; i++) 
            evEyesArr[i] = evaluatedEyes.get(i);
        return evEyesArr;
    }

    public Rectangle GetNose()
    {
        PointGenerator faceLinePoint = new PointGenerator(faceLineSlope, faceLineOffset);

        Size noseSize = new Size(face.width / 7, face.height / 7);

        Point projNosePos = faceLinePoint.GetFromY(evaluatedMouth.y - noseSize.height);

        Rectangle createdNose = new Rectangle((int)projNosePos.x - (int)noseSize.width / 2, (int)projNosePos.y - (int)noseSize.height / 2, 
                (int)noseSize.width, (int)noseSize.height);

        //createdNose.Offset(-createdNose.width / 2, -createdNose.height / 2);

        return createdNose;
    }

    boolean isValid = false;
    public boolean IsValid(){ return isValid; }

    double faceLineSlope = 0;
    double faceLineOffset = 0;
    public double[] GetFaceLineData() { return new double[] { faceLineSlope, faceLineOffset }; }


   //Function to cross all informations added to this face and evaluate the best values
   public void Evaluate()
   {
        //Evaluate mouth
        evaluatedMouth = new Rectangle(0, 0, 0, 0);

        //TODO must work a few on the mouth to choose the best one and proceed to histogram check for try to determinate skin color, eye color, hair color etc..

        for (Rectangle mouth : mouths)
        {
            if (mouth.y < face.y + face.height / 2)
                continue;

            if (evaluatedMouth.width > mouth.width)
                continue;

            evaluatedMouth = mouth;
        }

        //Evaluate eyes
        evaluatedEyes = new ArrayList<Rectangle>();
        ArrayList<Rectangle> rightCandidates = new ArrayList<Rectangle>();
        ArrayList<Rectangle> leftCandidates = new ArrayList<Rectangle>();

        for (Rectangle eye : eyes)
        {
            //Ensure the eyes are in the upper half of the img region
            if (eye.y + eye.height / 2 > face.y + face.height / 2)
                continue;

            if (eye.x + eye.width / 2 < face.x + face.width / 2)
                rightCandidates.add(eye);
            else
                leftCandidates.add(eye);
        }

        //get centers for each side weighted by their areas
        int totalAreas = 0;
        int totalX = 0;
        int totalY = 0;

            if(rightCandidates.size() > 0)
            {
                for (Rectangle eye : rightCandidates)
                {
                    int eyeArea = eye.width * eye.height;
                    totalAreas += eyeArea;

                    totalX += (eye.x + eye.width / 2) * eyeArea;
                    totalY += (eye.y + eye.height / 2) * eyeArea;
                }

                Point rightPoint = new Point(totalX / totalAreas, totalY / totalAreas);

                int rightEyeSide = (int)Math.sqrt((double)totalAreas / (double)rightCandidates.size());

                Rectangle rightEye = new Rectangle((int)rightPoint.x - rightEyeSide / 2,(int)rightPoint.y - rightEyeSide/ 2, 
                        rightEyeSide, rightEyeSide);
                //rightEye.Offset(-rightEye.Width / 2, -rightEye.Height / 2);

                evaluatedEyes.add(rightEye);
            }

            if(leftCandidates.size() > 0)
            {
                totalAreas = 0;
                totalX = 0;
                totalY = 0;

                for (Rectangle eye : leftCandidates)
                {
                    int eyeArea = eye.width * eye.height;
                    totalAreas += eyeArea;

                    totalX += (eye.x + eye.width / 2) * eyeArea;
                    totalY += (eye.y + eye.height / 2) * eyeArea;
                }

                Point leftPoint = new Point(totalX / totalAreas, totalY / totalAreas);

                int leftEyeSide = (int)Math.sqrt((double)totalAreas / (double)leftCandidates.size());

                Rectangle leftEye = new Rectangle((int)leftPoint.x - leftEyeSide/2,(int)leftPoint.y - leftEyeSide/2, leftEyeSide, leftEyeSide);

                //leftEye.Offset(-leftEye.Width / 2, -leftEye.Height / 2);

                evaluatedEyes.add(leftEye);
            }

            //Check if it is valid
            isValid = false;

            //if (evaluatedEyes.size() > 2)
                //throw new Exception("Eyes count must be equal or less than two");

            if (evaluatedEyes.size() == 2)
            {
                isValid = true;

                //Get the face line data

                Point eye1Center = new Point(evaluatedEyes.get(0).x + evaluatedEyes.get(0).width / 2,
                    evaluatedEyes.get(0).y + evaluatedEyes.get(0).height / 2);

                Point eye2Center = new Point(evaluatedEyes.get(1).x + evaluatedEyes.get(1).width / 2,
                    evaluatedEyes.get(1).y + evaluatedEyes.get(1).height / 2);

                int xOffset = ((int)eye2Center.x - (int)eye1Center.x) / 2;
                int yOffset = ((int)eye2Center.y - (int)eye1Center.y) / 2;

                Point eyeLineCenter = new Point(eye1Center.x + xOffset, eye1Center.y + yOffset);

                int zeroDivFac = eye1Center.x == eye2Center.x ? 1 : 0;

                //Generate face line slope and offset
                double aFact = (double)(eye1Center.y - eye2Center.y) /
                    (double)(eye1Center.x - eye2Center.x + zeroDivFac);

                aFact = Math.atan(aFact) + Math.PI / 2;
                aFact = Math.tan(aFact);

                double bFact = eyeLineCenter.y - aFact * eyeLineCenter.x;

                faceLineSlope = aFact;
                faceLineOffset = bFact;

                //If the mouth is invalid, project a new based on the face line
                if (evaluatedMouth.width == 0)
                {
                    PointGenerator faceLinePoint = new PointGenerator(aFact, bFact);

                    Point projMouthPos = faceLinePoint.GetFromY(face.y + face.height * 0.8);

                    evaluatedMouth = new Rectangle((int)projMouthPos.x - (face.width / 3) / 2, (int)projMouthPos.y - (face.height/5) / 2, 
                            face.width / 3, face.height/5);

                    //evaluatedMouth.Offset(-evaluatedMouth.width / 2, -evaluatedMouth.height / 2);
                }
            }

            if (evaluatedEyes.size() == 1 && evaluatedMouth.width > 0)
            {
                isValid = true;

                //Project the other eye based on the mouth

                //Get the bottom mouth coords
                Point mouthBottomCenter = new Point(evaluatedMouth.width / 2 +
                    evaluatedMouth.x,
                    evaluatedMouth.y + evaluatedMouth.height);

                //get the facetop coords
                Point faceTopCenter = new Point(face.width / 2 +
                    face.x, face.y);

                //Apply an experimental correct factor to the values
                int correctFact = (int)mouthBottomCenter.x - (int)faceTopCenter.x;
                //correctFact = (int)(correctFact * 0.5);

                mouthBottomCenter.x += correctFact;
                faceTopCenter.x -= correctFact;

                //Get the slope of the faceline

                //In case they are the same value, add a pixel to prevent division by 0
                int zeroDivFac = mouthBottomCenter.x == faceTopCenter.x ? 1 : 0;

                double a = (double)(mouthBottomCenter.y - faceTopCenter.y) /
                        (double)(mouthBottomCenter.x - faceTopCenter.x + zeroDivFac);

                //Get the offset of the face line
                double b = mouthBottomCenter.y - a * mouthBottomCenter.x;

                faceLineSlope = a;
                faceLineOffset = b;

                //Get the line function of the face
                PointGenerator faceLinePoint = new PointGenerator(a, b);

                //Get the reference of the existing eye and its center point
                Rectangle eyeRef = evaluatedEyes.get(0);
                Point eyeCenter = new Point(eyeRef.x + eyeRef.width / 2, eyeRef.y + eyeRef.height / 2);

                //Get the slope of the eye line (it must be normal to the face line, so we turn it Pi/2
                double aEyeFact = Math.atan(a) + Math.PI / 2;
                aEyeFact = Math.tan(aEyeFact);

                //Get the eye line offset
                double bEyeFact = eyeCenter.y - aEyeFact * eyeCenter.x;

                //Get the line function of the eye
                PointGenerator eyeLinePoint = new PointGenerator(aEyeFact, bEyeFact);

                //Get the horizontal difference between the center of the existing eye and the face line
                int diff = (int)faceLinePoint.GetFromY(eyeCenter.y).x - (int)eyeCenter.x;

                //Get the project eye coords
                Point projEyePoint = eyeLinePoint.GetFromX(eyeCenter.x + diff * 2);
                
                //Get the project eye rectangle
                Rectangle projEyeRect = new Rectangle((int)projEyePoint.x - eyeRef.width / 2, (int)projEyePoint.y - eyeRef.height / 2, 
                        eyeRef.width, eyeRef.height);
                //projEyeRect.Offset(-eyeRef.Width / 2, -eyeRef.Height / 2);

                evaluatedEyes.add(projEyeRect);
            }              

            //If the face keep invalid, put the face line on the middle of the face square
            if(!isValid)
            {
                faceLineSlope = -face.height/0.01;
                faceLineOffset = face.y - faceLineSlope * face.x + face.width / 2;
            }
        }

    
}
