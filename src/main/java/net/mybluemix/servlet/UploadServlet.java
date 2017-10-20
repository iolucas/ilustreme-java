/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mybluemix.servlet;

//import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;

import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Du0X
 */
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
@WebServlet("/upload")
public class UploadServlet extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadServlet() {
        //System.load("C:\\Users\\Du0X\\Documents\\NetBeansProjects\\PipaFace\\opencv_java310.dll");
    }  
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	//System.out.print(System.getProperty("user.dir"));
        response.getWriter().write(":D");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        

    	
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        //System.load("opencv_java310.dll");
        
        FaceDetector faceDetector = new FaceDetector(new String[]
        {
            "haarcascade_frontalface_alt2.xml"
        }, new String[] {
            "haarcascade_lefteye_2splits.xml",
            "haarcascade_righteye_2splits.xml"
        }, new String[] {
           "Nose.xml",
           "haarcascade_mcs_nose.xml"
        }, new String[] {
            "Mouth.xml"
        });
        
        //------ Begin Getting Sent Image ------       
        
	Mat receivedImage = getPostImage(request);
        
        //------ End Getting Sent Image
        
        //Create sky background
        /*Mat skyImage = new Mat(receivedImage.height(),receivedImage.width(),CvType.CV_8UC3);

        int totalBytes = (int)(skyImage.total() * skyImage.elemSize());
        byte buffer[] = new byte[totalBytes];
        skyImage.get(0, 0,buffer);
        for(int i=0;i<totalBytes;i++){
            switch(i%3) {
                case 0:
                    buffer[i] = (byte)232;
                    break;
                case 1:
                    buffer[i] = (byte)162;
                    break;
                case 2:
                    buffer[i] = (byte)0;
                    break;
            }
        }
        skyImage.put(0, 0, buffer);*/
        
        //Mat skyImage = receivedImage;
        
        //-----------------------------------------------------------------
        
        
       
        
        JSONArray jsonFaces = new JSONArray();
        
        ArrayList<PersonFace> personFaces = faceDetector.Detect(receivedImage);

        for (PersonFace personFace : personFaces)
        {
            personFace.Evaluate();

            if (!personFace.IsValid())
                continue;
            
            

            Rectangle faceRect = personFace.GetFace();

            Rectangle mouthRect = personFace.GetMouth();
            Rectangle noseRect = personFace.GetNose();
            Rectangle[] eyesRects = personFace.GetEyes();

            //Draw face division line
            double[] faceLineData = personFace.GetFaceLineData();
            //PointGenerator faceLine = new PointGenerator(faceLineData[0], faceLineData[1]);
            //Point faceTopPoint = faceLine.GetFromY(faceRect.y);
            //Point faceBottomPoint = faceLine.GetFromY(faceRect.y + faceRect.height);
            
            JSONObject faceRectJson = rectangleToJson(faceRect);
            JSONObject mouthRectJson = rectangleToJson(mouthRect);
            JSONObject noseRectJson = rectangleToJson(noseRect);
            JSONArray eyesJson = new JSONArray();
            
            for(Rectangle eye : eyesRects) {
   
                JSONObject eyeJson = rectangleToJson(eye);
                eyesJson.put(eyeJson);
            }
           
            JSONArray faceLineJson = new JSONArray();
            faceLineJson.put(faceLineData[0]);
            faceLineJson.put(faceLineData[1]);

            
            JSONObject faceJson = new JSONObject();
            faceJson.put("face", faceRectJson);
            faceJson.put("mouth", mouthRectJson);
            faceJson.put("nose", noseRectJson);
            faceJson.put("eyes", eyesJson);
            faceJson.put("line", faceLineJson);
            
            jsonFaces.put(faceJson);
            
            continue;
            

            //Imgproc.line(skyImage, faceTopPoint,faceBottomPoint, new Scalar(0,0,0),1);

            //CvInvoke.Line(image, faceTopPoint, faceBottomPoint, new Bgr(Color.Orange).MCvScalar, 3);

            //Draw rect around the face
            //Imgproc.rectangle(skyImage, new Point(faceRect.x, faceRect.y), 
                    //new Point(faceRect.x + faceRect.width, faceRect.y + faceRect.height), new Scalar(0,0,0));
            //CvInvoke.Rectangle(image, faceRect, new Bgr(Color.Yellow).MCvScalar, 2);

            //Draw rect around the mouth
            //Imgproc.rectangle(skyImage, new Point(mouthRect.x, mouthRect.y), 
                    //new Point(mouthRect.x + mouthRect.width, mouthRect.y + mouthRect.height), new Scalar(0,0,0));
            //CvInvoke.Rectangle(image, mouthRect, new Bgr(Color.Blue).MCvScalar, 2);

            //Draw rect around the nose
            //Imgproc.rectangle(skyImage, new Point(noseRect.x, noseRect.y), 
                    //new Point(noseRect.x + noseRect.width, noseRect.y + noseRect.height), new Scalar(0,0,0));
            //CvInvoke.Rectangle(image, noseRect, new Bgr(Color.Green).MCvScalar, 2);

            //Draw eyes rect and circles
            //for (Rectangle eye : eyesRects)
            //{
                //Imgproc.rectangle(skyImage, new Point(eye.x, eye.y), 
                    //new Point(eye.x + eye.width, eye.y + eye.height), new Scalar(255,255,255));
                /*CvInvoke.Rectangle(image, eye, new Bgr(Color.White).MCvScalar, 2);
                CvInvoke.Circle(image, new Point(eye.X + eye.Width / 2, eye.Y + eye.Height / 2), eye.Width / 2, new Bgr(Color.White).MCvScalar, 2);*/
            //}

            //Get face feature angle
            //double faceFeatureAngle = Math.atan(faceLineData[0]);
            //faceFeatureAngle = RadianToDegree(faceFeatureAngle);
            //faceFeatureAngle += faceFeatureAngle > 0 ? -90 : 90;


            //Draw circle around face
            /*Point faceCenter = new Point(faceRect.X + faceRect.Width / 2, 
                    faceRect.Y + faceRect.Height / 2);
                Size faceSize = new Size(faceRect.Width / 2, faceRect.Height / 2);
                CvInvoke.Ellipse(image, faceCenter,faceSize,0,0,360,
                    new Bgr(172, 203, 227).MCvScalar,0);
                CvInvoke.Ellipse(image, faceCenter, faceSize, 0, 0, 360, 
                    new Bgr(Color.Black).MCvScalar, 1);*/

            //Draw face lateral boundaries lines
            //Detect right and left eye
            /*Rectangle rightEye, leftEye;
            if(eyesRects[0].x > eyesRects[1].x)
            {
                rightEye = eyesRects[1];
                leftEye = eyesRects[0];
            }
            else
            {
                rightEye = eyesRects[0];
                leftEye = eyesRects[1];
            }

            //get eye line generator
            PointGenerator eyeLines = new PointGenerator(
                getRectCenter(rightEye), getRectCenter(leftEye));

            Point leftFacePoint = eyeLines.GetFromX(getRectCenter(leftEye).x + leftEye.width);
            Point rightFacePoint = eyeLines.GetFromX(getRectCenter(rightEye).x - rightEye.width);*/

            //Imgproc.circle(skyImage, leftFacePoint, 10, new Scalar(0,255,0), -1);
            //Imgproc.circle(skyImage, rightFacePoint, 10, new Scalar(255,0,0), -1);

            /* CvInvoke.Circle(image, leftFacePoint, 20,
                new Bgr(Color.Green).MCvScalar, -1);

            CvInvoke.Circle(image, rightFacePoint, 20,
                new Bgr(Color.Blue).MCvScalar, -1);*/



            //Get line generators for each side of the face
            /*double faceLineSlope = faceLineData[0];

            //Left side
            double leftFaceSideOffset = leftFacePoint.y - leftFacePoint.x * faceLineSlope;
            PointGenerator leftFaceLine = new PointGenerator(faceLineSlope, leftFaceSideOffset);

            Point startPointL = leftFaceLine.GetFromY(0);
            Point endPointL = leftFaceLine.GetFromY(receivedImage.height());

            //Right side
            double rightFaceSideOffset = rightFacePoint.y - rightFacePoint.x * faceLineSlope;
            PointGenerator rightFaceLine = new PointGenerator(faceLineSlope, rightFaceSideOffset);

            Point startPointR = rightFaceLine.GetFromY(0);
            Point endPointR = rightFaceLine.GetFromY(receivedImage.height());*/

            //CvInvoke.Line(image, startPointL, endPointL, new Bgr(Color.Green).MCvScalar, 3);
            //Imgproc.line(skyImage, startPointL, endPointL, new Scalar(0,255,0));
            //CvInvoke.Line(image, startPointR, endPointR, new Bgr(Color.Blue).MCvScalar, 3);
            //Imgproc.line(skyImage, startPointR, endPointR, new Scalar(255,0,0));


            //Draw mouth line
            //Put center on the top for the mouth stay in the middle of the mouth square
            /*Point mouthCenter = new Point(mouthRect.x + mouthRect.width / 2, mouthRect.y);
            Size mouthSize = new Size(mouthRect.width / 2, mouthRect.height / 2);


            Point mCenter = getRectCenter(mouthRect);

            //Get mouth line generator
            double aFactMouth = Math.tan(Math.atan(faceLineSlope) + Math.PI / 2);
            double bfactMouth = mCenter.y - mCenter.x * aFactMouth;
            PointGenerator mouthLine = new PointGenerator(aFactMouth, bfactMouth);

            double leftFaceMouthCrossX = (bfactMouth - leftFaceSideOffset) / (faceLineSlope - aFactMouth);

            double rightFaceMouthCrossX = (bfactMouth - rightFaceSideOffset) / (faceLineSlope - aFactMouth);

            Point leftFaceMouthCross = mouthLine.GetFromX(leftFaceMouthCrossX);
            Point rightFaceMouthCross = mouthLine.GetFromX(rightFaceMouthCrossX);

            //Get face top line
            double afactTopFace = aFactMouth;   //use the mouth line since this uses the same slope
            double bfactTopFace = faceTopPoint.y - faceTopPoint.x * afactTopFace;
            PointGenerator faceTopLine = new PointGenerator(afactTopFace, bfactTopFace);

            double leftTopFaceCrossX = (bfactTopFace - leftFaceSideOffset) / (faceLineSlope - afactTopFace);

            double rightTopFaceCrossX = (bfactTopFace - rightFaceSideOffset) / (faceLineSlope - afactTopFace);

            Point leftTopFaceCross = faceTopLine.GetFromX(leftTopFaceCrossX);
            Point rightTopFaceCross = faceTopLine.GetFromX(rightTopFaceCrossX);*/

            //CvInvoke.Circle(image, leftTopFaceCross, 5, new Bgr(Color.Black).MCvScalar, -1);
            //CvInvoke.Circle(image, rightTopFaceCross, 5, new Bgr(Color.Black).MCvScalar, -1);
            //CvInvoke.Circle(image, leftFaceMouthCross, 5, new Bgr(Color.Black).MCvScalar, -1);
            //CvInvoke.Circle(image, rightFaceMouthCross, 5, new Bgr(Color.Black).MCvScalar, -1);
            //CvInvoke.Circle(image, faceBottomPoint, 5, new Bgr(Color.Black).MCvScalar, -1);

            /*Point[] facePoints = new Point[]
            {
                leftTopFaceCross,
                rightTopFaceCross,
                rightFaceMouthCross,
                faceBottomPoint,
                leftFaceMouthCross
            };*/

            /*MatOfPoint facePointsMat = new MatOfPoint(leftTopFaceCross,
                rightTopFaceCross,
                rightFaceMouthCross,
                faceBottomPoint,
                leftFaceMouthCross);*/

            //CvInvoke.Polylines(image, facePointsVector, true, new Bgr(172, 203, 227).MCvScalar, 1);
            //Imgproc.fillConvexPoly(skyImage, facePointsMat, new Scalar(172, 203, 227));

            //Imgproc.ellipse(skyImage, mouthCenter, mouthSize, faceFeatureAngle, 0, 180, new Scalar(0,0,0), 2);

            /*Point p1 = faceTopLine.GetFromX(0);
            Point p2 = faceTopLine.GetFromX(receivedImage.width());

            //Draw nose line
            Point noseCenter = new Point(noseRect.x + noseRect.width / 2, noseRect.y + noseRect.height / 2);
            Size noseSize = new Size(noseRect.width / 2, noseRect.height / 2);
            double noseAngle = Math.atan(faceLineData[0]);
            noseAngle = RadianToDegree(noseAngle);
            //Imgproc.ellipse(skyImage, noseCenter, noseSize, noseAngle, 0, 180, new Scalar(0,0,0), 2);


            //Draw eyes ellipses
            for(Rectangle eye : personFace.GetEyes())
            {
                Point eyeCenter = new Point(eye.x + eye.width / 2, eye.y + eye.height / 2);
                Size elipseSize = new Size(eye.width / 5, eye.height / 2);
                //Imgproc.ellipse(skyImage, eyeCenter, elipseSize, faceFeatureAngle, 0, 360, new Scalar(0,0,0), -1);
            }*/

            //Imgproc.line(skyImage, faceBottomPoint, new Point(skyImage.width() / 2, skyImage.height()), new Scalar(0,0,0));

        }
        
      
        //---------------------------------------------------
        
        JSONObject imageSize = new JSONObject();
        imageSize.put("width", receivedImage.width());
        imageSize.put("height", receivedImage.height());
        
        JSONObject imageMetaData = new JSONObject();
        imageMetaData.put("faces", jsonFaces);
        imageMetaData.put("size", imageSize);
  
        response.getWriter().write(imageMetaData.toString());
        
        
        //sendJpegImage(response, skyImage);
        
    }
    
    JSONObject rectangleToJson(Rectangle rect) {
        JSONObject jsonRect = new JSONObject();
        jsonRect.put("x", rect.x);
        jsonRect.put("y", rect.y);
        jsonRect.put("width", rect.width);
        jsonRect.put("height", rect.height);
        
        return jsonRect;
    }
    
    /*private Point getRectCenter(Rectangle rect) {
        return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
    }
    
    private double RadianToDegree(double angle)
    {
        return angle * (180.0 / Math.PI);
    }*/
    
    void sendJpegImage(HttpServletResponse response, Mat image) throws IOException {
        MatOfByte outBuffer = new MatOfByte();
        
        
        Imgcodecs.imencode(".jpg", image, outBuffer);

        response.setContentType("image/jpeg");  
        ServletOutputStream out;  
        out = response.getOutputStream();

        out.write(outBuffer.toArray());
    }
    
    Mat getPostImage(HttpServletRequest request) throws IOException, ServletException {
        InputStream is = (InputStream) request.getPart("file").getInputStream();
		
        BufferedInputStream bin = new BufferedInputStream(is);  
        ByteArrayOutputStream osbuffer = new ByteArrayOutputStream();  
        int ch =0; 
	while((ch=bin.read())!=-1)  
	{  
            osbuffer.write(ch);  
	}  
	osbuffer.flush();
	bin.close();  
		
	byte[] encodedImage = osbuffer.toByteArray();
        Mat encodedMat = new Mat(encodedImage.length,1,CvType.CV_8U);
	encodedMat.put(0, 0,encodedImage);
        
        
	Mat receivedImage = Imgcodecs.imdecode(encodedMat, Imgcodecs.CV_LOAD_IMAGE_ANYCOLOR);
        
        return receivedImage;
    }
    
}
