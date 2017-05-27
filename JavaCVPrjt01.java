import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.utils.Converters;
import org.opencv.video.BackgroundSubtractorMOG;


public class JavaCVPrjt01 {
	
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	static JLabel vidpanel = new JLabel();
	
	   
	
	static double post_x_circle = 0;
	static double post_y_circle = 0;
	static double post_x_mouse = 0;
	static double post_y_mouse = 0;
	static boolean hold = false;
	static int hold_frame = 0;
	static int frame_n = 0;

	public static void main(String[] args) {
		Robot bot = null;
		 try  {
		   bot = new Robot();
		   
		  
		 } catch (AWTException e) {
		     e.printStackTrace();
		 } 
		
		JFrame jframe = new JFrame("HUMAN MOTION DETECTOR FPS");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setContentPane(vidpanel);
		jframe.setSize(640,580) ;
		jframe.setVisible(true);
		int main = 0;
		VideoCapture cam = new VideoCapture(0);
		Mat frame = new Mat();
		while(cam.read(frame)){
			Mat og_frame = frame.clone();
			 Imgproc.GaussianBlur(frame, frame, new Size(5,5), 1);
		 	 Imgproc.medianBlur(frame, frame, 7);
		 	 Imgproc.medianBlur(frame, frame, 7);
		 	// Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
		 	 //Imgproc.erode(frame, frame, kernel);
		 	/* for(int i = 0 ; i < frame.rows(); i++) {
        		 for(int j = 0; j < frame.cols(); j++) {
        			 double[] data_rgb = frame.get(i, j);
        			 double total = data_rgb[0]+ data_rgb[1]+data_rgb[2];
        			 double red = data_rgb[2];double green = data_rgb[1];double bleu = data_rgb[0];
        			 double r2 = (red/total) * 255;
        			 double g2 = (green/total) * 255;
        			 double b2 = (bleu / total) * 255;
        			 double[] new_data = {b2,g2,r2};
        			 frame.put(i, j, new_data);
        		 
        		 }
        	 }*/
		 	/*for(int i = 0 ; i < frame.rows(); i++) {
		   		 for(int j = 0; j < frame.cols(); j++) {
		   			 double[] data_rgb = frame.get(i, j);
		   	
		   			 double total = data_rgb[0]+ data_rgb[1]+data_rgb[2];
		   			 double red = data_rgb[2];double green = data_rgb[1];double bleu = data_rgb[0];
		   			//double r2 = red/Math.sqrt(red*red + green*green + bleu*bleu);
		   		   // double g2 = green/Math.sqrt(red*red + green*green + bleu*bleu);
		   		    //double b2 = bleu/Math.sqrt(red*red + green*green + bleu*bleu);
		   			 double intensity = (red + green + bleu) /1;
		   			double r2 = (red/(total)/1) * intensity;
		   			 double g2 = (green/(total)/1) * intensity;
		   			 double b2 = (bleu /( total)/1) * intensity;
		   			 double[] new_data = {b2,g2,r2};
		   			 frame.put(i, j, new_data);
		   		 
		   		 }
				}*/

		 	// 
		for(int i = 0 ; i < frame.rows(); i++) {
        		 for(int j = 0; j < frame.cols(); j++) {
        			 double[] data_rgb = frame.get(i, j);
        			 Color c = new Color((int)data_rgb[0],(int)data_rgb[0],(int)data_rgb[0]);
        			 if(isSkin(c)){
        				 double[] data = {255,255,255} ;
        				 frame.put(i, j, data);
        			 } else {
        				 double[] data =  {0,0,0};
        				 frame.put(i, j, data);
        			 }
        		}
        	 }
		Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY); 
		 Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5));
	 	 Imgproc.erode(frame, frame, kernel);
	 	 Imgproc.erode(frame, frame, kernel);
	 	 Imgproc.erode(frame, frame, kernel);
	 	 Imgproc.erode(frame, frame, kernel);
	 	 
	 	 Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3));
	 	 Imgproc.dilate(frame, frame, kernel2);
	 	Imgproc.dilate(frame, frame, kernel2);
	 	Imgproc.dilate(frame, frame, kernel2);
	 	Imgproc.dilate(frame, frame, kernel2);
	 	Imgproc.GaussianBlur(frame, frame, new Size(7,7), 1);
	 	 Imgproc.medianBlur(frame, frame, 5);
	 	 
	 	// Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
	 	 
	 	 Mat canny_output = new Mat();
    	 Imgproc.Canny(frame, canny_output, 100, 200);
    	 List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    	 Mat hch = new Mat();
    	 Imgproc.findContours(canny_output, contours, hch, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));
     	Mat draw = new Mat();
     	draw = Mat.zeros(canny_output.size(), CvType.CV_8UC3);
     	
     	
     	int indice_max = 0;
     	int indice_max2 = 0;
     	double area_max2=0;
     	double area_max = 0;
     	for(int i = 0; i < contours.size(); i++) {
     		double current_area = Imgproc.contourArea(contours.get(i));
     		if(area_max < current_area){
     			area_max = current_area;
     			indice_max = i;
     		}           		
     	}
     	for(int i = 0; i < contours.size(); i++) {
     		double current_area = Imgproc.contourArea(contours.get(i));
     		if(area_max2 < current_area && i != indice_max){
     			area_max2 = current_area;
     			indice_max2 = i;
     		}           		
     	}
     	if(area_max < 8000 && area_max2<8000) {
     		ImageIcon image = new ImageIcon(Mat2bufferedImage(og_frame));//Mat2bufferedImage(frame));
            vidpanel.setIcon(image);
            vidpanel.repaint();
            continue;
     		
     	}
     	
     	if(area_max < 8000 || area_max2 < 8000) {
     		//System.out.println("Une main à l'écran");
     		main = 1;
     		
     	} else {
     		main = 2;
     	
     		//System.out.println("Deux main à l'écran");
     	}
     	
     	
     	//System.out.println(indice_max);
     	List<MatOfInt> hull = new ArrayList<MatOfInt>();
     	
     	if(contours.size() != 0){
     			Scalar color = new Scalar(0,255,0);
     			MatOfPoint convexHullMatOfPoint = new MatOfPoint();
     			ArrayList<MatOfPoint> convexHullMatOfPointArrayList = new ArrayList<MatOfPoint>();
     			
     			Imgproc.drawContours(draw, contours, indice_max, color,-1);
     			
     			MatOfInt hull2  = new MatOfInt();
     			
     			Imgproc.convexHull(contours.get(indice_max), hull2);
     			
     			hull.add(hull2);
     			
     			ArrayList<Point> convexHullPointArrayList = new ArrayList<Point>();
     			for(int j=0; j < hull2.toList().size(); j++)
                    convexHullPointArrayList.add(contours.get(indice_max).toList().get(hull2.toList().get(j)));
                convexHullMatOfPoint.fromList(convexHullPointArrayList);
                convexHullMatOfPointArrayList.add(convexHullMatOfPoint); 

                
                List<MatOfInt4> convdef = new ArrayList<MatOfInt4>();
                convdef.add(new MatOfInt4());
                
                //Imgproc.convexityDefects(contours.get(indice_max), hull.get(0), convdef.get(0));
               MatOfInt4 mConvexityDefectsMatOfInt4 = new MatOfInt4();

                    Imgproc.convexityDefects(contours.get(indice_max), hull.get(0), mConvexityDefectsMatOfInt4);
                    int[] mConvexityDefectsIntArrayList = new int[mConvexityDefectsMatOfInt4.toArray().length];
                    if(!mConvexityDefectsMatOfInt4.empty())
                    {
                       
                        mConvexityDefectsIntArrayList = mConvexityDefectsMatOfInt4.toArray();
                    }
                    
                   
               
                    ArrayList<Point> tipPts = new ArrayList<Point>();
                    ArrayList<Point> tip2Pts = new ArrayList<Point>();
                    ArrayList<Point> foldPts = new ArrayList<Point>();
                    ArrayList<Integer> depths = new ArrayList<Integer>();

                   ArrayList<Point> fingerTips = new ArrayList<Point>();

                  // System.out.println( mConvexityDefectsIntArrayList.length/4);
                    for (int i = 0; i < mConvexityDefectsIntArrayList.length/4; i++)
                    {
                    	//System.out.println(i);
                    	if(4*i > mConvexityDefectsIntArrayList.length) break;
                        tipPts.add(contours.get(indice_max).toList().get(mConvexityDefectsIntArrayList[4*i]));
                        tip2Pts.add(contours.get(indice_max).toList().get(mConvexityDefectsIntArrayList[4*i+1]));
                        //System.out.println("Before");
                        foldPts.add(contours.get(indice_max).toList().get(mConvexityDefectsIntArrayList[4*i+2]));
                       // System.out.println("after");
                        depths.add(mConvexityDefectsIntArrayList[4*i+3]);
                    }

               
                ArrayList<Point> valable_fold = new ArrayList<Point>();
                ArrayList<Point> valable_tips = new ArrayList<Point>();
                ArrayList<Point> valable_tips2 = new ArrayList<Point>();
                

                for(int i = 0; i < foldPts.size(); i++){
                	if(depths.get(i) <5000)
                		continue;
                
                	double t = (angleBetween(tipPts.get(i),foldPts.get(i),tip2Pts.get(i)));
                	if ( 40 < t && t < 90) {
                	valable_fold.add(foldPts.get(i));      
                	valable_tips.add(tipPts.get(i));
                	valable_tips2.add(tip2Pts.get(i));
                	}
                
                }
            
                
                Moments m = Imgproc.moments(frame,true);
                double m10 = m.get_m10();
                double m01 = m.get_m01();
                double m_area = m.get_m00();
               // m.
                int posX = (int) (m10/m_area);
                int posY = (int) (m01/m_area);
                
                Core.circle(og_frame, new Point(posX,posY), 50, new Scalar(255,0,255));
                ArrayList<Point> valable_tips3 = new ArrayList<Point>();
               
                
                
                
                for(int i = 0; i<valable_fold.size(); i++) {
                	Core.circle(og_frame, valable_fold.get(i), 10, new Scalar(0,0,255));
                	
                }
                for(int i = 0; i<valable_tips.size(); i++) {
                	Core.circle(og_frame, valable_tips.get(i), 20, new Scalar(0,255,0));
                	
                }
               for(int i = 0; i<valable_tips2.size(); i++) {
                	Core.circle(og_frame, valable_tips2.get(i), 20, new Scalar(0,0,255));
                	
                }
               
               
               
               
               
               
               
               
               
               
               
               
               
               
               
               
               
               
               
               
               
               
            
                
                
                double d_x = - (post_x_circle-posX)*10;
                double d_y = -(post_y_circle -posY)*10;
               // System.out.println(d_x+ "  "+d_y);
               
                
                if(main == 1){//!(d_x<15) && !(d_y<15)){
           //     System.out.println(valable_fold.size())
              if( valable_fold.size() == 0){ // move mouse
                	frame_n++;
					//bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);             	
                	bot.mouseMove(posX+1440,posY-100);
                } else if (valable_fold.size() == 1){ // double click
                	
                	if(! hold && frame_n - hold_frame > 2) {
                		bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                		hold = true;
                		hold_frame= frame_n;
                	} else if (frame_n - hold_frame > 2) {
                		bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                		hold_frame = frame_n;
                		hold = false;
                	}
                } else if (valable_fold.size() >=3) { // hold
                	//System.out.println("4");
                	bot.mousePress( InputEvent.BUTTON1_MASK ); 
                	bot.mouseRelease( InputEvent.BUTTON1_MASK ); 
                	try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
                	bot.mousePress( InputEvent.BUTTON1_MASK ); 
                	bot.mouseRelease( InputEvent.BUTTON1_MASK );
                }
     	
               
                
               
                
                
                
	     	
     	} else if (main == 2) {
     		if (valable_fold.size() == 0) {
    			 bot.keyRelease(KeyEvent.VK_CONTROL);
    		 }
     		 if (valable_fold.size() == 1) {
     			 bot.keyPress(KeyEvent.VK_CONTROL);
     			 bot.mouseWheel(-1);
     		 }
     		 if (valable_fold.size() == 2) {
     			 bot.keyPress(KeyEvent.VK_CONTROL);
     			 bot.mouseWheel(1);
     		 }
     		 
     		 
     	}
                Imgproc.drawContours(og_frame,convexHullMatOfPointArrayList,0,new Scalar(255,0,0),5);
     	
     /*	 for(int i =0; i < contours.size(); i++) {
     		// RNG r;
     		 Scalar color = new Scalar(255,255,255);
     		// Imgproc.drawContours(draw, contours, i, color, 2, 8, hch, 0, new Point(1,1) );
     		 Imgproc.drawContours(draw, contours, i, color);
     	 }*/
	 	 
		 	/* Mat Y = new Mat();
		 	for(int i = 0 ; i < frame.rows(); i++) {
       		 for(int j = 0; j < frame.cols(); j++) {
       			 double[] data_rgb = frame.get(i, j);
       			
       			 double red = data_rgb[2];double green = data_rgb[1];double bleu = data_rgb[0];
       			 double y, u,v;
       			 y = 0.257 * red + 0.504*green +0.098*bleu+16;
       			 u = -0.148 *red -0.291*green +0.439*bleu +128;
       			 v = 0.439*red -0.368*green -0.071*bleu+128;
       			boolean y_ok = (75<y) && (y<185);
    			boolean u_ok = (105<u) && (u<150);
    			boolean v_ok = (100<v) &&(v <180);
    			
    			 
    			 if( (u_ok && v_ok &&y_ok)){
    				 double[] data = {255,255,255};
    				 frame.put(i, j, data);
    			 }
       		 
       		 }
       	 }
		 	 */

		 	/* for(int i = 0 ; i < Y.rows(); i++) {
        		 for(int j = 0; j < Y.cols(); j++) {
        			 double[] data_YUV = Y.get(i, j);
        			 
        			 double U,V,y;
        			 
        			y = data_YUV[0];
        			 U = data_YUV[1];
        			 V = data_YUV[2];
        			 			 
        			boolean y_ok = (75<y) && (y<185);
        			boolean u_ok = (105<U) && (U<150);
        			boolean v_ok = (100<V) &&(V <180);
        			
        			 
        			 if( (u_ok && v_ok &&y_ok)){
        				 double[] data = {255,255,255};
        				 frame.put(i, j, data);
        			 }
        		
        			 
        			 
        		}
        	 }*/
		 	
		 	 /*for(int i = 0 ; i < Y.rows(); i++) {
        		 for(int j = 0; j < Y.cols(); j++) {
        			 double[] data_Y = Y.get(i, j);
        			 if ( !(data_Y[0] > 80) && (data_Y[1] < 180) && (data_Y[1] > 135) && (data_Y[2]) <135 && (data_Y[2] > 85)) {
        				 double[] data = {0,0,0};
        				 frame.put(i, j, data);
        			 }
        			 
        		}
        	 }*/
		 	 
		 	 
		 	 
		 	
			
			//Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2TSL);
			//frame.convertTo(frame, CvType.CV_32FC3);
		//Core.normalize(frame, frame, 0.0,255.0,Core.NORM_MINMAX, CvType.CV_32FC3);
			/*for(int r=0; r<frame.rows(); ++r){
				for(int c=0; c <frame.cols(); ++c){
					double[] data_hsv = frame.get(r, c);
					if( (data_hsv[0] > 5) && (data_hsv[0]<17) && (data_hsv[1]>38) && (data_hsv[1] < 250) && (data_hsv[2])> 51 &&(data_hsv[2]<242)) {
					 	
					} else {
						double[] data = {0,0,0};
						frame.put(r,c,data);
					}
				}
			}*/
			
		/*	Mat frame_gray = new Mat();
			Imgproc.cvtColor(frame, frame, Imgproc.COLOR_HSV2BGR);
			Imgproc.cvtColor(frame,frame_gray, Imgproc.COLOR_BGR2GRAY);
			Imgproc.threshold(frame_gray, frame_gray, 60, 255, Imgproc.THRESH_BINARY);
			
			Mat kernel1 = new Mat(3,3,1);
			Mat kernel2 = new Mat(7,7,1);
			Mat kernel3 = new Mat(9,9,1);
			
			kernel1.convertTo(kernel1, CvType.CV_8U);
			kernel2.convertTo(kernel3, CvType.CV_8U);
			kernel3.convertTo(kernel2, CvType.CV_8U);
			
			Imgproc.morphologyEx(frame_gray, frame_gray, Imgproc.MORPH_ERODE,kernel1,new Point(-1,-1),3);
			Imgproc.morphologyEx(frame_gray, frame_gray, Imgproc.MORPH_OPEN, kernel2,new Point(-1,-1),1);
			Imgproc.morphologyEx(frame_gray, frame_gray, Imgproc.MORPH_CLOSE, kernel3,new Point(-1,-1),1);
			
			frame_gray.convertTo(frame_gray, CvType.CV_8U);
			
			Imgproc.medianBlur(frame_gray, frame_gray, 15);
			BufferedImage desti = Mat2bufferedImage(frame_gray);*/
        	ImageIcon image = new ImageIcon(Mat2bufferedImage(og_frame));//Mat2bufferedImage(frame));
            vidpanel.setIcon(image);
            vidpanel.repaint();
			
     	}}
		
		
		
	}
	
	public static double angleBetween(Point center, Point current, Point previous) {

		  return Math.toDegrees(Math.atan2(current.x - center.x,current.y - center.y)-
		                        Math.atan2(previous.x- center.x,previous.y- center.y));
		}

/*
public class JavaCVPrjt01 {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
    }
    static JLabel vidpanel = new JLabel();
    public static void main(String[] args) throws InterruptedException {
        JFrame jframe = new JFrame("HUMAN MOTION DETECTOR FPS");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setSize(640,580) ;
        jframe.setVisible(true); 
        Mat frame = new Mat();
        Mat dst = new Mat();
        Mat outerBox = new Mat();
        Size sz = new Size(640,480);
        VideoCapture camera = new VideoCapture(0);
        BufferedImage img ;
        Color moyenne1 = new Color(0); 
        Color moyenne2 = new Color(0);
        Color moyenne3 = new Color(0);
        Color moyenne4 = new Color(0);
        Color moyenne5 = new Color(0);
        boolean calib  = false;
        int done = 0;
        while(!calib) {
        	if(camera.read(frame)) {
        		Core.rectangle(frame, new Point(150,100), new Point(350,300), new Scalar(0,255,0));
        		 Imgproc.resize(frame,frame, sz );
            	 //Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
            	 Imgproc.GaussianBlur(frame, frame, new Size(7,7), 0);
            	 //Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
        		img = Mat2bufferedImage(frame);
        		Color moyenne = medColor(150,100,350,300,img);
        		if(isSkin(moyenne)&& done < 100 ) {   
        			System.out.println("In IS_SKIN");
        			Core.rectangle(frame, new Point(190,200), new Point(215,225), new Scalar(0,0,255));
        			Core.rectangle(frame, new Point(225,250), new Point(250,275), new Scalar(0,0,255));
        			Core.rectangle(frame, new Point(175,250), new Point(200,275), new Scalar(0,0,255));
        			Core.rectangle(frame, new Point(200,125), new Point(225,150), new Scalar(0,0,255));        		
        			Core.rectangle(frame, new Point(250,180), new Point(275,205), new Scalar(0,0,255));     
        			wait_calib(camera);
        			moyenne1 = medColor(190,200,215,225,img);
        			moyenne2 = medColor(225,250,250,275,img);
        			moyenne3 = medColor(175,250,200,275,img);
        			moyenne4 = medColor(200,125,225,150,img);
        			moyenne5 = medColor(250,180,275,205,img);        			        			
        			img = Mat2bufferedImage(frame);
        			calib = true;
        		} 
        		
        		
        		ImageIcon image = new ImageIcon(img);
                vidpanel.setIcon(image);
                vidpanel.repaint();
        	}
        
        }
    	post_calib(camera);
    	
    	System.out.println(isSkin(moyenne1));
    	System.out.println(isSkin(moyenne2));
    	System.out.println(isSkin(moyenne3));
    	System.out.println(isSkin(moyenne4));
    	System.out.println(isSkin(moyenne5));
    	
    	
        while (true) {
            if (camera.read(frame)) {
            	 Imgproc.resize(frame,frame, sz );
            	// Mat fin = frame.clone();
            	 //Mat src_ycrcb = new Mat();
            	 //Mat src_hsv = new Mat();
            	 //BufferedImage dest = Mat2bufferedImage(fin);
            	 //Imgproc.cvtColor(frame, src_ycrcb, Imgproc.COLOR_BGR2YCrCb);
            	// frame.convertTo(src_hsv, CvType.CV_32FC3);
            	 //Imgproc.cvtColor(src_hsv, src_hsv, Imgproc.COLOR_BGR2HSV);
            	 //Imgproc.cvtColor(frame, dest, Imgproc.COLOR_BGR);
            	 //frame.convertTo(frame, CvType.CV_8UC1);
            	 //Core.normalize(src_hsv, src_hsv, 0.0,255.0,Core.NORM_MINMAX, CvType.CV_32FC3);
            	 Imgproc.GaussianBlur(frame, frame, new Size(3,3), 0);
            	 for(int i = 0 ; i < frame.rows(); i++) {
            		 for(int j = 0; j < frame.cols(); j++) {
            			 double[] data_rgb = frame.get(i, j);
            			 double total = data_rgb[0]+ data_rgb[1]+data_rgb[2];
            			 double red = data_rgb[2];double green = data_rgb[1];double bleu = data_rgb[0];
            			 double r2 = (red/total) * 255;
            			 double g2 = (green/total) * 255;
            			 double b2 = (bleu / total) * 255;
            			 double[] new_data = {b2,g2,r2};
            			 frame.put(i, j, new_data);
            		 
            		 }
            	 }
            	
            	 for(int i = 0 ; i < frame.rows(); i++) {
            		 for(int j = 0; j < frame.cols(); j++) {
            			 double[] data_rgb = frame.get(i, j); //Stores element in an array
            	         //double[] data_hsv = src_hsv.get(i, j);
            	        // double[] data_ycrcb = src_ycrcb.get(i, j);
            			 //Color c_rgb = new Color((int)data_rgb[0],(int) data_rgb[1],(int) data_rgb[2]);
            			Color c_rgb = new Color((int)data_rgb[2],(int)data_rgb[1],(int)data_rgb[0]);
            	         boolean a = isSkin(c_rgb);
            	    //    boolean b = isSkin2((int)data_ycrcb[0],(int) data_ycrcb[1],(int) data_ycrcb[2]);
            	     //    boolean c = isSkin3((int)data_hsv[0],(int) data_hsv[1],(int) data_hsv[2]);
            	         
            	        if(!a){
            	        	double[] new_data = {0,0,0};
            	       // 	dest.setRGB(j, i, Color.BLACK.getRGB());
            	        	frame.put(i, j, new_data);
            	        } else {
            	        	double[] new_data = {255,255,255};
            	       // 	dest.setRGB(j, i, Color.WHITE.getRGB());
            	        	frame.put(i, j, new_data);
            	        }
            	         
            		 }
            	 }
            	Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
            	//Imgproc.threshold(frame, frame, 250, 255, Imgproc.THRESH_BINARY);
            	 Imgproc.GaussianBlur(frame, frame, new Size(3,3), 0);
            	 Mat canny_output = new Mat();
            	 Imgproc.Canny(frame, canny_output, 100, 200);
            	 List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            	 Mat hch = new Mat();
            	 
            	// Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
            	// Imgproc.GaussianBlur(frame, frame, new Size(3,3), 0);
            	 
            	Imgproc.findContours(canny_output, contours, hch, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));
            	Mat draw = new Mat();
            	draw = Mat.zeros(canny_output.size(), CvType.CV_8UC3);
            	
            	/*
            	int indice_max = 0;
            	double area_max = 0;
            	for(int i = 0; i < contours.size(); i++) {
            		double current_area = Imgproc.contourArea(contours.get(i));
            		if(area_max < current_area){
            			area_max = current_area;
            			indice_max = i;
            		}           		
            	}
            	
            	
            	Scalar color = new Scalar(0,255,0);
            	Imgproc.drawContours(draw, contours, indice_max, color);
            	
            	
            	 for(int i =0; i < contours.size(); i++) {
            		// RNG r;
            		 Scalar color = new Scalar(255,255,255);
            		// Imgproc.drawContours(draw, contours, i, color, 2, 8, hch, 0, new Point(1,1) );
            		 Imgproc.drawContours(draw, contours, i, color);
            	 }
            
            	
            	 
            	 //Mat converted = new Mat();
            	 //Imgproc.cvtColor(frame, converted, Imgproc.COLOR_BGR2);
            	// Imgproc.GaussianBlur(frame, frame, new Size(7,7), 0);
            	/* Imgproc.medianBlur(frame, frame, 3);
            	 Imgproc.cvtColor(frame, frame, Imgproc.COLOR_HSV2BGR);
            	 Scalar lower = new Scalar(0,10,60);
            	 Scalar upper = new Scalar(20,150,255);
            	 Mat skinMask = new Mat();
            	 Core.inRange(frame, lower, upper, skinMask);
            	 Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(12,12));*/
            	 
            	// Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(11,11));
            	            	 
            	/* Imgproc.erode(skinMask, skinMask, kernel);
            	 Imgproc.erode(skinMask, skinMask, kernel);
            	 
            	 Imgproc.dilate(skinMask, skinMask, kernel);
            	 Imgproc.dilate(skinMask, skinMask, kernel);
            	 Imgproc.GaussianBlur(skinMask, skinMask, new Size(3,3), 0);
            	
            	
            	 Mat skin = new Mat();
            	 Core.bitwise_and(frame, frame, skin, skinMask);
            	 
            
            
            		
            		 Imgproc.erode(frame, frame, kernel);
                	 Imgproc.erode(frame, frame, kernel);
                	 
                	 Imgproc.dilate(frame, frame, kernel);
               	// Imgproc.dilate(frame, frame, kernel);
                	// Imgproc.GaussianBlur(frame, frame, new Size(3,3), 0);
                	// BufferedImage img2 = Mat2bufferedImage(frame);
                 		
                		
            	/*for(int i = 0; i < img2.getHeight(); i++){
            		for(int j = 0; j <img2.getWidth(); j++){
            			
            			if(!isSkin(new Color(img2.getRGB(j, i)))){
            				img2.setRGB(j, i, Color.BLACK.getRGB());
            			} else {
            				img2.setRGB(j, i, Color.WHITE.getRGB());
            			}
            			
            			
            		}
            	}*/
            	
                	 
                /*	 int rows = frame.rows(); //Calculates number of rows
                	 int cols = frame.cols(); //Calculates number of columns
                	 int ch = frame.channels(); //Calculates number of channels (Grayscale: 1, RGB: 3, etc.)

                	 for (int i=0; i<rows; i++)
                	 {
                	     for (int j=0; j<cols; j++)
                	     {
                	         double[] data = frame.get(i, j); //Stores element in an array
                	         Color c = new Color((int)data[2],(int) data[1],(int) data[0]);
                	         if(approach(c,moyenne1) || approach(c,moyenne2) || approach(c,moyenne3) || approach(c,moyenne4) || approach(c,moyenne5)) {
                	        	 for (int k = 0; k < ch; k++) //Runs for the available number of channels
                    	         {
                    	             data[k] = 255; //Pixel modification done here
                    	         }
                	         } else {
                	        	 for (int k = 0; k < ch; k++) //Runs for the available number of channels
                    	         {
                    	             data[k] = 0; //Pixel modification done here
                    	         }
                	         }
                	         
                	         frame.put(i, j, data); //Puts element back into matrix
                	     }
                	 }
            	ArrayList<MatOfPoint> ctn = new ArrayList<MatOfPoint>();
            	Mat hier = new Mat();
            	
            	BufferedImage img2 = Mat2bufferedImage(frame);*/
            	//Imgproc.findContours(frame, ctn, hier, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            	 
          /*
            	for(int r=0; r<frame.width(); ++r){
        			for(int c=0; c<frame.height(); ++c) {
        				// 0<H<0.25  -   0.15<S<0.9    -    0.2<V<0.95   
        				double[] array = frame.get(c, r);   			
        				if( (array[0]>2) && (array[0] < 9) && (array[1]>19) && (array[1]<145) && (array[2]>26) && (array[2]<121) ); // do nothing
        				else for(int i=0; i<3; ++i)	array[i] = 0;
        			}
            	}*/
            	
            	//Mat frame_gray = new Mat();
            /*	
            	Imgproc.cvtColor(frame, frame_gray, Imgproc.COLOR_BGR2GRAY);
            	Imgproc.threshold(frame_gray, frame_gray, 65, 230, Imgproc.THRESH_BINARY);
            	
            	
            	BufferedImage img2 = Mat2bufferedImage(frame_gray);
            	
                //Imgproc.resize(frame,frame, sz );
                outerBox = new Mat(frame.size(), CvType.CV_8UC1);
               // Imgproc.cvtColor(frame, outerBox, Imgproc.COLOR_RGB2HSV_FULL);
               // Imgproc.GaussianBlur(outerBox, outerBox, new Size(5, 5), 0);           
            	 BufferedImage desti = Mat2bufferedImage(draw);
            	ImageIcon image = new ImageIcon(desti);//Mat2bufferedImage(frame));
                vidpanel.setIcon(image);
                vidpanel.repaint();
            }
        }
    }
    
    public static boolean approach(Color c1, Color c2) {
    	if(c1.getBlue() > c2.getBlue() - 5 && 
    	   c1.getBlue() < c2.getBlue() + 5 && 
    	   c1.getGreen() > c2.getGreen() - 5 && 
    	   c1.getGreen() < c2.getGreen() + 5 && 
    	   c1.getRed() > c2.getRed() - 5 && 
    	   c1.getRed() < c2.getRed() + 5) {
    		return true;
    	}
    	
    	return false;
    
    }
    public static void wait_calib(VideoCapture cam){
    	Chrono c = new Chrono("Chrono", 1);
    	c.start();
    	Mat frame = new Mat();
    	BufferedImage img;
    	while(!c.get_done()) {
    		cam.read(frame);
    		 Imgproc.resize(frame,frame, new Size(640,480) );
        	// Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
        	 Imgproc.GaussianBlur(frame, frame, new Size(7,7), 0);
    		Core.rectangle(frame, new Point(150,100), new Point(350,300), new Scalar(255,0,0));
    		Core.rectangle(frame, new Point(190,200), new Point(215,225), new Scalar(0,0,255));
			Core.rectangle(frame, new Point(225,250), new Point(250,275), new Scalar(0,0,255));
			Core.rectangle(frame, new Point(175,250), new Point(200,275), new Scalar(0,0,255));
			Core.rectangle(frame, new Point(200,125), new Point(225,150), new Scalar(0,0,255));        		
			Core.rectangle(frame, new Point(250,180), new Point(275,205), new Scalar(0,0,255));     
    		img = Mat2bufferedImage(frame);
    		ImageIcon image = new ImageIcon(img);
    		vidpanel.setIcon(image);
    		vidpanel.repaint();
    	}
    	
    }
    
    public static void post_calib(VideoCapture cam){
    	Chrono c = new Chrono("Chrono", 1);
    	c.start();
    	Mat frame = new Mat();
    	BufferedImage img;
    	while(!c.get_done()) {
    		cam.read(frame);
    		 Imgproc.resize(frame,frame, new Size(640,480) );
        	 //Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
        	 Imgproc.GaussianBlur(frame, frame, new Size(7,7), 0);
    		Core.rectangle(frame, new Point(150,100), new Point(350,300), new Scalar(255,0,0));
    		Core.rectangle(frame, new Point(190,200), new Point(215,225), new Scalar(0,255,0));
			Core.rectangle(frame, new Point(225,250), new Point(250,275), new Scalar(0,255,0));
			Core.rectangle(frame, new Point(175,250), new Point(200,275), new Scalar(0,255,0));
			Core.rectangle(frame, new Point(200,125), new Point(225,150), new Scalar(0,255,0));        		
			Core.rectangle(frame, new Point(250,180), new Point(275,205), new Scalar(0,255,0));     
    		img = Mat2bufferedImage(frame);
    		ImageIcon image = new ImageIcon(img);
    		vidpanel.setIcon(image);
    		vidpanel.repaint();
    	}
    	
    }
    
    public static Color medColor(int x1, int y1,int x2, int y2, BufferedImage img) {
    	ArrayList<Color> cal = new ArrayList<Color>();
    	int R = 0 , G = 0, B = 0;
    	for(int i = x1+1; i < x2-1 ; i++) {
			for( int j = y1+1; j < y2-1 ; j++){
				cal.add( new Color(img.getRGB(j, i)));				
			}
		}    	
		for( int i = 0; i <cal.size(); i++) {
			R += cal.get(i).getRed();
			B += cal.get(i).getBlue();
			G += cal.get(i).getGreen();
		}
		
		Color moyenne = new Color( R/cal.size(), G/cal.size(), B/cal.size());
		return moyenne;
    }
    
    public static int maxRGB(Color c) {
    	int max =  Math.max(Math.max(c.getBlue(),c.getGreen()),c.getRed());
    	return max;
    }
    
    public static int minRGB(Color c) {
    	int min =  Math.min(Math.min(c.getBlue(),c.getGreen()),c.getRed());
    	return min;
    }
    
    public static boolean isSkin(Color c) {
    	  boolean e1 = (c.getRed()>95) && (c.getGreen()>40) && (c.getBlue()>15) && ((Math.max(c.getRed(),Math.max(c.getGreen(),c.getBlue())) - Math.min(c.getRed(), Math.min(c.getGreen(),c.getBlue())))>15) && (Math.abs(c.getRed()-c.getGreen())>15) && (c.getRed()>c.getGreen()) && (c.getRed()>c.getBlue());
    	  int R = c.getRed();
    	  int G = c.getGreen();
    	  int B = c.getBlue();
    	  boolean e2 = (R>220) && (G>210) && (B>170) && (Math.abs(R-G)<=15) && (R>B) && (G>B);
		
		return e1 || e2;
    }
    
   public  static boolean  isSkin3(float H, float S, float V) {
        return ( 0<=H && H<=180);
   }
   
    public static boolean isSkin2(float Y, float Cr, float Cb) {
      //  boolean e3 = Cr <= 1.5862*Cb+20;
     //   boolean e4 = Cr >= 0.3448*Cb+76.2069;
     //   boolean e5 = Cr >= -4.5652*Cb+234.5652;
     //   boolean e6 = Cr <= -1.15*Cb+301.75;
    //    boolean e7 = Cr <= -2.2857*Cb+432.85;
    //	boolean e3 = Cr > 140 && Cr < 165 && Cb > 140 && Cb < 195;
        return e3;
    }
*/
    
	
	public static boolean isSkin(Color c) {
  	/*  boolean e1 = (c.getRed()>95) && (c.getGreen()>40) && (c.getBlue()>15) && ((Math.max(c.getRed(),Math.max(c.getGreen(),c.getBlue())) - Math.min(c.getRed(), Math.min(c.getGreen(),c.getBlue())))>15) && (Math.abs(c.getRed()-c.getGreen())>15) && (c.getRed()>c.getGreen()) && (c.getRed()>c.getBlue());
  	  int R = c.getRed();
  	  int G = c.getGreen();
  	  int B = c.getBlue();
  	  boolean e2 = (R>220) && (G>210) && (B>170) && (Math.abs(R-G)<=15) && (R>B) && (G>B);*/
		//return ( c.getRed() < 55);
		//return e1 || e2;
		boolean e1;
		int R = c.getRed(); int G = c.getGreen() ; int B = c.getBlue();
		e1 = R < 62 && G < 62 && B < 62;
		boolean e2;
		e2 = Math.abs(R-G) < 15 && Math.abs(R-B) < 15 && Math.abs(B-G) <15 ;
		
		return e2 && e1;
	
	}
    public static BufferedImage Mat2bufferedImage(Mat image) {
        MatOfByte bytemat = new MatOfByte();
        Highgui.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return img;
    }
    
    public static ArrayList<Rect> detection_contours(Mat outmat) {
        Mat v = new Mat();
        Mat vv = outmat.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE); 
        double maxArea = 100;
        int maxAreaIdx = -1;
        Rect r = null;
        ArrayList<Rect> rect_array = new ArrayList<Rect>();
 
        for (int idx = 0; idx < contours.size(); idx++) { Mat contour = contours.get(idx); double contourarea = Imgproc.contourArea(contour); if (contourarea > maxArea) {
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                rect_array.add(r);
            } 
        } 
        v.release(); 
        return rect_array;
 
    }
    static public double sqr(double a) {
        return a*a;
    }
 
    static public double Distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(sqr(y2 - y1) + sqr(x2 - x1));
    }

}