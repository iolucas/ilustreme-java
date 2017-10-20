/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mybluemix.servlet;

import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author Du0X
 */
public class FaceDetector {
    ArrayList<CascadeClassifier> frontFaceCascades = new ArrayList<CascadeClassifier>();
    ArrayList<CascadeClassifier> eyesCascades = new ArrayList<CascadeClassifier>();
    ArrayList<CascadeClassifier> nosesCascades = new ArrayList<CascadeClassifier>();
    ArrayList<CascadeClassifier> mouthsCascades = new ArrayList<CascadeClassifier>();

    public FaceDetector(String[] frontFaceCascadeFiles,
        String[] eyesCascadeFiles,
        String[] nosesCascadeFiles,
        String[] mouthsCascadeFiles)
    {
    	
    	final String cascadePath = "/cascades/";
    	
        for (String cascadeFile : frontFaceCascadeFiles)
        {
            frontFaceCascades.add(new CascadeClassifier(getResourcePath(cascadePath + cascadeFile)));
        }

        for (String cascadeFile : eyesCascadeFiles)
        {
            eyesCascades.add(new CascadeClassifier(getResourcePath(cascadePath + cascadeFile)));
        }

        for (String cascadeFile : nosesCascadeFiles)
        {
            nosesCascades.add(new CascadeClassifier(getResourcePath(cascadePath + cascadeFile)));
        }

        for (String cascadeFile : mouthsCascadeFiles)
        {
            mouthsCascades.add(new CascadeClassifier(getResourcePath(cascadePath + cascadeFile)));
        }
    }
    
    private String getResourcePath(String path) {
    	//System.out.print(path);
    	URL resourceUrl = this.getClass().getResource(path);
    	String absoluteFileName = resourceUrl.getPath();
    	absoluteFileName = absoluteFileName.replaceFirst("/", "");
    	System.out.print(" -- " + absoluteFileName + " -- ");
    	return absoluteFileName;
    }

    public ArrayList<PersonFace> Detect(Mat image)
    {
        ArrayList<PersonFace> personFaces = new ArrayList<PersonFace>();

        Mat ugray = new Mat();
        
        cvtColor(image, ugray, Imgproc.COLOR_BGR2GRAY);
        
        //normalizes brightness and increases contrast of the image
        equalizeHist(ugray, ugray);
        
        for (CascadeClassifier faceCascade : frontFaceCascades)
        {
            MatOfRect detectedFaces = new MatOfRect();
            
            faceCascade.detectMultiScale(ugray, detectedFaces, 1.1, 3, 0, new Size(20, 20), new Size());
            //Rectangle[] detectedFaces = faceCascade.detectMultiScale(ugray,1.1,3,new Size(20, 20),new Size());

            for (Rect face : detectedFaces.toArray())
            {
                PersonFace personFace = new PersonFace(new Rectangle(face.x,face.y,face.width,face.height));

                personFaces.add(personFace);

                //Get the region of interest on the faces
                Mat faceRegion = new Mat(ugray, face);
                        
                //Detect eyes
                for (CascadeClassifier eyeCascade : eyesCascades)
                {
                    MatOfRect detectedEyes = new MatOfRect();
                    
                    eyeCascade.detectMultiScale(faceRegion, detectedEyes, 1.1, 3, 0, new Size(10, 10), new Size());

                    for(Rect eye : detectedEyes.toArray())
                    {
                        //eye.Offset(face.X, face.Y);
                        personFace.AddEye(new Rectangle(eye.x + face.x, eye.y + face.y, eye.width, eye.height));
                    }
                }

                //Detect mouths
                for (CascadeClassifier mouthCascade : mouthsCascades)
                {
                    MatOfRect detectedMouths = new MatOfRect();
                    mouthCascade.detectMultiScale(faceRegion, detectedMouths, 1.1, 3, 0, new Size(25, 15), new Size());

                    for (Rect mouth : detectedMouths.toArray())
                    {
                        //mouth.Offset(face.X, face.Y);
                        personFace.AddMouth(new Rectangle(mouth.x + face.x, mouth.y + face.y, mouth.width, mouth.height));
                    }
                }

                //Detect noses
                ArrayList<Rectangle> noses = new ArrayList<Rectangle>();
                
                for(CascadeClassifier noseCascade : nosesCascades)
                {
                    MatOfRect detectedNoses = new MatOfRect();
                    noseCascade.detectMultiScale(faceRegion, detectedNoses, 1.1, 10, 0, new Size(25, 15), new Size());

                    for (Rect nose : detectedNoses.toArray())
                    {
                        //nose.Offset(face.X, face.Y);
                        noses.add(new Rectangle(nose.x + face.x, nose.y + face.y, nose.width, nose.height));
                    }
                }                      
            }                    
        }

        return personFaces;
    }    
}
